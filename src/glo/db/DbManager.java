package glo.db;

import glo.schema.GLSchemaManager;
import glo.types.GasCompany;
import glo.types.GasPriceInfo;
import glo.types.GasType;
import glo.ui.GLUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javacard.framework.ISOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.DatabaseFactory;
import net.rim.device.api.database.DatabaseIOException;
import net.rim.device.api.database.DatabasePathException;
import net.rim.device.api.database.Row;
import net.rim.device.api.database.Statement;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.io.MalformedURIException;
import net.rim.device.api.io.URI;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.util.Arrays;

public class DbManager {

	/**
	 * Used to keep a list of database listeners
	 */
	private static Vector dbListeners = new Vector();

	/**
	 * Used to keep a list of schema file location that should be auto
	 * installed. Use addSchemaLocation to add a schema file.
	 */
	private static Vector schemaFileLocations = new Vector();

	private static boolean firstLoad = false;

	/*
	 * Name of the database
	 */
	public static final String DB_NAME = "gasdb.db";

	public static boolean dbSupported = true;

	/**
	 * Private connection to the database. One connection per application
	 * lifetime.
	 */
	private static Database _database = null;

	private static boolean open = false;

	/**
	 * Add a database listener
	 * 
	 * @param dbml
	 */
	public static void addListener(DbManagerListener dbml) {
		dbListeners.addElement(dbml);
	}

	/**
	 * Returns true if the database has just been created
	 * 
	 * @return
	 */
	public static boolean isFirstLoad() {
		return firstLoad;
	}

	/**
	 * Returns true if the database has already been created. The database is
	 * stored on the SD card.
	 * 
	 * @return
	 */
	public static boolean dbExists() {
		if (!dbSupported)
			return false;

		try {
			FileConnection fc = (FileConnection) Connector
					.open("file:///SDCard/" + DB_NAME);
			return fc.exists();
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Opens a connection to the database and returns the database object. The
	 * static database variable is also updated with this value.
	 * 
	 * @return
	 */
	public static Database openDatabase() {
		System.out.println("GL [II] Asked to open database");

		if (!dbSupported) {
			System.out
					.println("GL [II] Database is not supported, cannot open database");
			return null;
		}

		if (!dbExists()) {
			System.out
					.println("GL [II] Database does not exist, cannot open. Will attempt to create");
			if (!createDatabase()) {
				System.out
						.println("GL [EE] Database creation failed, cannot open database");
				return null;
			}
		}

		if (isOpen()) {
			System.out
					.println("GL [II] Database exists, no need to open... returning");
			return _database;
		}

		if (dbExists()) {
			URI dbURI = getDbURI();
			if (dbURI == null) {
				System.out
						.println("GL [EE] Null database URI received, cannot open database");
				return null;
			}
			try {
				System.out.println("GL [II] Attempting to open database...");
				Database d = DatabaseFactory.open(dbURI);
				System.out.println("GL [II] Database successfully opened");
				_database = d;
				open = true;
				System.out.println("GL [II] Database ready");
				// Notify all listeners
				DbManagerEvent evt = new DbManagerEvent(
						DbManagerEvent.DATABASE_OPENED);
				notifyDbListeners(evt);
				return d;
			} catch (ControlledAccessException e) {
				System.out.println("GL [EE] CAException: " + e.getMessage());
			} catch (DatabaseIOException e) {
				System.out.println("GL [EE] DbIOException: " + e.getMessage());
			} catch (DatabasePathException e) {
				System.out.println("GL [EE] DbPException: " + e.getMessage());
				dbSupported = false;
				System.out.println("GL [II] Disabling DB for app");
			}
		}
		return null;
	}

	/**
	 * Returns the database, if the database exists.
	 * 
	 * @return
	 */
	public static Database getDatabase() {
		if (!dbSupported) {
			System.out
					.println("GL [II] Database is not supported, cannot get database");
			return null;
		}

		if (dbExists() && !isOpen()) {
			openDatabase();
		}

		if (dbExists() && isOpen()) {
			return _database;
		}

		if (_database == null) {
			System.out.println("GL [EE] Database is null, cannot return it");
		}

		return null;
	}

	/**
	 * Returns a URI for connecting to the database
	 * 
	 * @return
	 */
	public static URI getDbURI() {

		URI dbURI = null;
		try {
			dbURI = URI.create("file:///SDCard/" + DB_NAME);
		} catch (IllegalArgumentException e) {
			System.out.println("GL [EE] IAException: " + e.getMessage());
		} catch (MalformedURIException e) {
			System.out.println("GL [EE] MURIException: " + e.getMessage());
		}
		return dbURI;
	}

	/**
	 * Reads a text file from inside the package structure and returns a string
	 * with the contents. Method returns null if we cannot find the text file.
	 * 
	 * @param fileName
	 * @return
	 */
	private static String readTextFileFromPkg(String fileName) {
		String result = null;
		try {
			Class classs = Class.forName("glo.db.GasDb");
			InputStream is = classs.getResourceAsStream(fileName);
			if (is != null) {
				byte[] data = IOUtilities.streamToBytes(is);
				result = new String(data);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("GL [EE] CNFException: " + e.getMessage());
		} catch (IOException ioe) {
			System.out.println("GL [EE] CNFException: " + ioe.getMessage());
		}
		return result;
	}

	/**
	 * Installs schema from a text file in the package. NB: Only one table can
	 * be created at a time. Store one table per file.
	 * 
	 * @param d
	 * @param fileLocation
	 * @return
	 */
	private static boolean installSchemaFromPackageFile(Database d,
			String fileLocation) {
		String schemaSql = readTextFileFromPkg(fileLocation);
		if (schemaSql != null) {
			System.out.println("GL [II] Installing schema: \n" + schemaSql);
			try {
				Statement stmt = d.createStatement(schemaSql);
				stmt.prepare();
				stmt.execute();
				System.out.println("GL [II] Schema installed from "
						+ fileLocation);
				return true;

			} catch (DatabaseException e) {
				System.out.println("GL [EE] Error creating database schema");
				System.out.println("GL [EE] DException: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	/**
	 * Creates the database at the URI location.
	 * 
	 * @return
	 */
	public static boolean createDatabase() {

		if (dbExists()) {
			return true;
		}

		URI dbURI = getDbURI();

		if (dbURI != null) {
			try {
				Database d = DatabaseFactory.create(dbURI);
				firstLoad = true;
				_database = d;
				open = true;
				System.out.println("GL [II] Database successfully created");

				// Notify all listeners
				DbManagerEvent evt = new DbManagerEvent(
						DbManagerEvent.DATABASE_CREATED);
				notifyDbListeners(evt);

				for (int i = 0; i < getSchemaFileLocationsAsArray().length; i++) {
					String schemaLocation = getSchemaFileLocationsAsArray()[i];
					if (!installSchemaFromPackageFile(d, schemaLocation)) {
						return false;
					}
				}
				return true;
			} catch (DatabaseIOException e) {
				System.out.println("GL [EE] DIOException: " + e.getMessage());
			} catch (DatabasePathException e) {
				System.out.println("GL [EE] DPException:  " + e.getMessage());
				dbSupported = false;
				System.out.println("GL [II] Disabling DB for app");
			} catch (Exception e) {
				System.out.println("GL [EE] Could not create database: "
						+ e.getMessage());
				dbSupported = false;
			}
		}

		return false;
	}

	private static void notifyDbListeners(DbManagerEvent evt) {
		for (int j = 0; j < dbListeners.size(); j++) {
			DbManagerListener dbml = (DbManagerListener) dbListeners
					.elementAt(j);
			dbml.databaseActivity(evt);
		}
	}

	/**
	 * Call this method to initialize the database
	 */
	public static void init() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("GL [II] Initiailizing database");
				openDatabase(); // this should check if the database exists or
				// not and create it
			}
		});
		t.start();
	}

	/**
	 * Tells whether the database is open
	 * 
	 * @return
	 */
	public static boolean isOpen() {
		return open && dbSupported;
	}

	/**
	 * Returns true if the database is supported
	 * 
	 * @return
	 */
	public static boolean isDbSupported() {
		return dbSupported;
	}

	/**
	 * Adds a schema location to the list of files that should be parsed for
	 * database creation
	 * 
	 * @param fileLocation
	 */
	public static void addSchemaFileLocation(String fileLocation) {
		schemaFileLocations.addElement(fileLocation);
	}

	/**
	 * Returns the schema locations as an array of strings
	 * 
	 * @return
	 */
	public static String[] getSchemaFileLocationsAsArray() {
		String[] locations = new String[schemaFileLocations.size()];
		for (int i = 0; i < schemaFileLocations.size(); i++) {
			locations[i] = schemaFileLocations.elementAt(i).toString();
		}
		return locations;
	}

	// =============================================
	// Code for interaction with the database
	public static void runTests() {
		System.out.println("GL [II] Running tests");
		Hashtable data = new Hashtable();
		data.put("id", new Integer(-1));
		data.put("name", "MGI Gas Station");

		beginTransaction();
		if (insert("GStation", data)) {
			System.out.println("GL [II] Test insertion succeeded");
			// Now try the select
			// Cursor results = select("GStation", -1);
			// if (results == null) {
			// System.out.println("GL [EE] Null result retrieved from query");
			// } else {
			// try {
			// if (!results.isEmpty()) {
			// if(results.first()){
			// Row r = results.getRow();
			// System.out.println("GL [II] Retrieved row: " + r.toString());
			// } else {
			// System.out.println("GL [EE] Could not advance cursor to first row");
			// }
			// } else {
			// System.out.println("GL [EE] Result set is empty");
			// }
			// } catch (DatabaseException e) {
			// System.out.println("GL [EE] Database exception occurred while attempting select on test data.\nReason: "
			// + e.getMessage());
			// }
			// }

		} else {
			System.out
					.println("GL [EE] Tests failed, database possibly not ready");
		}
		commitTransaction();
		// rollbackTransaction();
	}

	public static Cursor getTableData(String tblName) {
		Cursor results = select(tblName);
		if (results == null) {
			System.out
					.println("GL [EE] Null result retrieved from querying the table "
							+ tblName);
			return null;
		} else {
			try {
				if (!results.isEmpty()) {
					return results;
				} else {
					System.out.println("GL [EE] Result set from the table "
							+ tblName + " is empty");
					return null;
				}
			} catch (DatabaseException e) {
				System.out
						.println("GL [EE] Database exception occurred while attempting select on table"
								+ tblName + ".\nReason: " + e.getMessage());
				return null;
			}
		}
	}

	public static String sqlCleanString(String str) {
		String newString = str.replace('\"', ' ');
		return newString;
	}
	
	public static void updatePriceDate() throws DatabaseException {
		
	}
	
	/**
	 * Given a vector filled with types/rows of something e.g. Gas stations
	 * we will filter out the ones that already exist in the database
	 * @param rows
	 * @return
	 * @throws DatabaseException 
	 */
	public static Vector filterExistingInstances(Vector rows) throws DatabaseException {
		if(rows.size() == 0){
			return rows;
		} else {
			Object o = rows.elementAt(0);
			StdDatabaseable firstRow = (StdDatabaseable) o;
			SchemaDefinition def = firstRow.getSchema();
			SchemaField pKey = def.getPrimaryKey();
			if(pKey != null) {
				Vector validRows = new Vector();
				for(int i = 0; i < rows.size(); i++){
					Object o1 = rows.elementAt(i);
					if(o1 == null) continue;
					StdDatabaseable row = (StdDatabaseable) o1;
					
					int pkValue = Integer.valueOf( row.getData().get(pKey.getName()).toString() ).intValue();
					if(select(def.getTableName(),pkValue).isEmpty()) {
						validRows.addElement(rows.elementAt(i));
					}
				}
				return validRows;
			}
			else {
				Vector validRows = new Vector();
				for(int i = 0; i < rows.size(); i++){
					Object o1 = rows.elementAt(i);
					if(o1 == null) continue;
					StdDatabaseable row = (StdDatabaseable) o1;	
	
					if(select(def.getTableName(),row.getData()).isEmpty()) {
						validRows.addElement(rows.elementAt(i));
					}
				}
			}
		}
		return rows;
	}

	protected static String[] getColumnsAndValueString(Hashtable data) {
		return getColumnsAndValueString(data, false, ",");
	}

	protected static String[] getColumnsAndValueString(Hashtable data,
			boolean useEquals, String valueDelimiter) {
		String colStr = new String();
		String valueStr = new String();
		Enumeration keys = data.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			String value = data.get(key).toString();
			colStr += key;
			if (useEquals) {
				valueStr += key + " = '" + value + "'";
			} else {
				valueStr += "\"" + value + "\"";
			}

			if (keys.hasMoreElements()) {
				colStr += ", ";
				valueStr += valueDelimiter + " ";
			}
		}

		String[] result = new String[2];
		result[0] = colStr;
		result[1] = valueStr;
		return result;
	}

	
	public static int count(String tableName) {
		SchemaManager sm = SchemaManager.getManager();
		if (sm != null) {
			SchemaDefinition sd = sm.getSchema(tableName);
			if (sd == null) {
				System.out.println("GL [EE] Could not find schema with name: "
						+ tableName + ". Cannot count");
				return 0;
			}
			if (sd.hasPrimaryKey()) {
				String pkName = sd.getPrimaryKey().getName();
				String sql = "SELECT COUNT(*) AS count FROM " + tableName;
				try {
					Statement stmt = getDatabase().createStatement(sql);
					stmt.prepare();
					Cursor c = stmt.getCursor();
					if (c != null) {
						if (!c.isEmpty()) {
							c.first();
							return c.getRow().getInteger(0);
						} else {
							return 0;
						}
					} else {
						return 0;
					}
				} catch (DatabaseException e) {
					System.out
							.println("GL [EE] Database exception occurred while trying to select. \nReason: "
									+ e.getMessage());
				} catch (NullPointerException npe) {
					System.out
							.println("GL [EE] Database not ready, cannot perform select for: "
									+ tableName);
				} catch (DataTypeException e) {
					System.out
							.println("GL [EE] Database exception while trying to get count for: "
									+ tableName
									+ ".\nReason: "
									+ e.getMessage());
				}
			}
		}
		return 0;

	}
	
	/**
	 * Tells us whether a table is empty 
	 * @param tableName
	 * @return
	 */
	public static boolean isEmpty(String tableName){
		try {
			Cursor c = select(tableName);
			return c.isEmpty();
		} catch (DatabaseException e) {
			return true;
		}
	}
	
	

	public static Cursor select(String tableName) {
		SchemaManager sm = SchemaManager.getManager();
		if (sm != null) {
			SchemaDefinition sd = sm.getSchema(tableName);
			if (sd == null) {
				System.out.println("GL [EE] Could not find schema with name: "
						+ tableName + ". Cannot select");
				return null;
			}

			if (sd.hasPrimaryKey()) {
				String pkName = sd.getPrimaryKey().getName();
				String sql = "SELECT * FROM " + tableName;
				try {
					Statement stmt = getDatabase().createStatement(sql);
					stmt.prepare();
					return stmt.getCursor();
				} catch (DatabaseException e) {
					System.out
							.println("GL [EE] Database exception occurred while trying to select. \nReason: "
									+ e.getMessage());
				} catch (NullPointerException npe) {
					System.out
							.println("GL [EE] Database not ready, cannot perform select for: "
									+ tableName);
				}
			} else {
				String sql = "SELECT * FROM " + tableName;
				try {
					Statement stmt = getDatabase().createStatement(sql);
					stmt.prepare();
					return stmt.getCursor();
				} catch (DatabaseException e) {
					System.out
							.println("GL [EE] Database exception occurred while trying to select. \nReason: "
									+ e.getMessage());
				} catch (NullPointerException npe) {
					System.out
							.println("GL [EE] Database not ready, cannot perform select for: "
									+ tableName);
				}
			}
		}
		return null;
	}

	public static Cursor select(String tableName, int id) {
		SchemaManager sm = SchemaManager.getManager();
		if (sm != null) {
			SchemaDefinition sd = sm.getSchema(tableName);
			if (sd == null) {
				System.out.println("GL [EE] Could not find schema with name: "
						+ tableName + ". Cannot select");
			} else {
				if (sd.hasPrimaryKey()) {
					String pkName = sd.getPrimaryKey().getName();
					String sql = "SELECT * FROM " + tableName + " WHERE "
							+ pkName + " = '" + id + "'";
					try {
						Statement stmt = getDatabase().createStatement(sql);
						stmt.prepare();
						return stmt.getCursor();
					} catch (DatabaseException e) {
						System.out
								.println("GL [EE] Database exception occurred while trying to select. \nReason: "
										+ e.getMessage());
					} catch (NullPointerException npe) {
						System.out
								.println("GL [EE] Database not ready, cannot perform select for: "
										+ tableName + " with id=" + id);
					}
				}
			}

		}
		return null;
	}

	public static Cursor select(String tableName, Hashtable data) {
		SchemaManager sm = SchemaManager.getManager();
		if (sm != null) {
			SchemaDefinition sd = sm.getSchema(tableName);
			if (sd == null) {
				System.out.println("GL [EE] Could not find schema with name: "
						+ tableName + ". Cannot select");
			} else {
				if (data == null) {
					System.out.println("GL [EE] Cannot select null data from "
							+ tableName);
					return null;
				}
				if (data.size() == 0) {
					System.out
							.println("GL [EE] No data for Selection, cannot select.");
					return null;
				}
				String[] colAndStrs = getColumnsAndValueString(data);
				String colStr = colAndStrs[0];
				String valueStr = colAndStrs[1];

				String sql = "SELECT * FROM " + tableName + " WHERE "
						+ valueStr;
				try {
					Statement stmt = getDatabase().createStatement(sql);
					stmt.prepare();
					return stmt.getCursor();
				} catch (DatabaseException e) {
					System.out
							.println("GL [EE] Database exception occurred while trying to select. \nReason: "
									+ e.getMessage());
				} catch (NullPointerException npe) {
					System.out
							.println("GL [EE] Database not ready, cannot perform select for: "
									+ tableName);
				}

			}

		}
		return null;
	}
	
	public static boolean insert(String tableName, Hashtable data) {
		if (data == null) {
			System.out.println("GL [EE] Cannot insert null data into "
					+ tableName);
			return false;
		} else {
			if (data.size() == 0) {
				System.out
						.println("GL [EE] No data for insertion, cannot insert.");
				return false;
			}
		}

		if (tableName == null) {
			System.out
					.println("GL [EE] No table name provided, no insertions will be done");
			return false;
		}

		boolean result = false;
		String sql = "INSERT INTO " + tableName + " ( ";
		String[] colAndStrs = getColumnsAndValueString(data);
		String colStr = colAndStrs[0];
		String valueStr = colAndStrs[1];

		sql += colStr + ") VALUES ( " + valueStr + " )";
		result = exec(sql);
		return result;
	}

	public static boolean update(String tableName, Hashtable data) {
		if (data == null) {
			System.out.println("GL [EE] Cannot update null data in " + tableName);
			return false;
		} else {
			if (data.size() == 0) {
				System.out.println("GL [EE] No data for updating, cannot update.");
				return false;
			}
		}
		
		if (tableName == null) {
			System.out.println("GL [EE] No table name provided, no updates will be done");
			return false;
		}
		
		boolean result = false;
		
		String sql = "Update " + tableName + " set( ";
		String[] colAndStrs = getColumnsAndValueString(data);
		String colStr = colAndStrs[0];
		String valueStr = colAndStrs[1];

		sql += colStr + ") VALUES ( " + valueStr + " )";
		result = exec(sql);
		return result;
	}
	
	public static boolean remove(String tableName, Hashtable data) {
		if (tableName == null) {
			System.out
					.println("GL [EE] No table name provided, no removals will be done");
			return false;
		}

		boolean result = false;
		String sql = "DELETE FROM " + tableName;

		if (data != null) {
			String[] colAndStrs = getColumnsAndValueString(data, true, "AND");
			String colStr = colAndStrs[0];
			String valueStr = colAndStrs[1];

			sql += " WHERE  " + valueStr + " ";
		}

		result = exec(sql);
		return result;
	}

	private static boolean exec(String sql) {
		boolean result = false;
		System.out.println("GL [II] Creating statement for: " + sql);
		if (isOpen()) {
			try {
				Database d = getDatabase();
				if (d != null) {
					Statement stmt = d.createStatement(sql);
					stmt.prepare();
					stmt.execute();
					stmt.close();
					result = true;
				} else {
					System.out.println("GL [EE] Null database, cannot insert");
				}
			} catch (DatabaseException e) {
				System.out.println("GL [EE] Could not create statement: " + sql
						+ "\n" + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("GL [EE] Cannot insert data, database not open");
		}
		return result;
	}

	/**
	 * Begins a transaction
	 * 
	 * @return
	 */
	public static boolean beginTransaction() {
		boolean result = false;
		if (isOpen()) {
			try {
				System.out.println("GL [II] Beginning transaction");
				Database d = getDatabase();
				if (d != null) {
					d.beginTransaction();
					System.out.println("GL [II] Transaction started");
					result = true;
				} else {
					System.out
							.println("GL [EE] Null database, cannot begin transaction");
				}

			} catch (DatabaseException e) {
				System.out.println("GL [EE] Error starting transaction. "
						+ e.getMessage());
			}
		}
		return result;
	}

	/**
	 * Commits the transaction
	 * 
	 * @return
	 */
	public static boolean commitTransaction() {
		boolean result = false;
		if (isOpen()) {
			System.out.println("GL [II] Committing ....");
			Database d = getDatabase();
			if (d != null) {
				try {
					d.commitTransaction();
					System.out.println("GL [II] Committed!");
					result = true;
				} catch (DatabaseException e) {
					System.out.println("GL [EE] Error attempting to commit... "
							+ e.getMessage());
				}
			} else {
				System.out.println("GL [EE] Could not commit, null database");
			}
		} else {
			System.out.println("GL [EE] Database is not open, cannot commmit");
		}
		return result;
	}

	/**
	 * Rolls back the transaction
	 * 
	 * @return
	 */
	public static boolean rollbackTransaction() {
		boolean result = false;
		if (isOpen()) {
			System.out.println("GL [II] Rolling back ....");
			Database d = getDatabase();
			if (d != null) {
				try {
					d.rollbackTransaction();
					System.out.println("GL [II] Rolled back!");
					result = true;
				} catch (DatabaseException e) {
					System.out
							.println("GL [EE] Error attempting to rollback... "
									+ e.getMessage());
				}

			} else {
				System.out.println("GL [EE] Could not rollback, null database");
			}
		} else {
			System.out.println("GL [EE] Database is not open, cannot rollback");
		}
		return result;
	}

}
