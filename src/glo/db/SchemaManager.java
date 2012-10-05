package glo.db;

import glo.misc.GLPrerequisite;
import glo.misc.GLPrerequisites;
import glo.ui.GLApp;
import glo.ui.GLSplashScreen;

import java.util.Vector;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.Database;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.Statement;
import net.rim.device.api.system.ControlledAccessException;

public abstract class SchemaManager implements DbManagerListener {

	public static final int DB_SQLLITE_V3 = 3;
	public static final int DB_SQLLITE_V2 = 2;

	private static Vector tmpListeners = new Vector();

	protected int databaseType = DB_SQLLITE_V3;

	/**
	 * Ensure that the schema is created
	 */
	public void init() {
		if (isInitialized())
			return;
		System.out.println("GL [II] Intializing the schema manager");
		GLSplashScreen.setMessage("Initializing database");
		defineSchema();
		installSchema();
		setInitialized(true);
		schemaReady();
		GLSplashScreen.setMessage("Database ready");
		
	}

	/**
	 * Called to define the schema
	 */
	protected abstract void defineSchema();

	/**
	 * Adds a schema to the list schemas
	 * 
	 * @param tableName
	 * @param schema
	 */
	public abstract void addSchema(String tableName, SchemaDefinition schema);

	public abstract boolean hasSchema(String schemaName);

	/**
	 * Internal method for adding listeners... called by the static version
	 * without the underscore
	 * 
	 * @param sml
	 */
	protected abstract void _addSchemaManagerListener(SchemaManagerListener sml);

	protected abstract void _addSchemaManagerListeners(Vector v);

	protected abstract Vector getSchemaManagerListeners();

	public static void addSchemaManagerListener(SchemaManagerListener sml) {
		if (sManager != null) {
			sManager._addSchemaManagerListener(sml);
		} else {
			tmpListeners.addElement(sml);
		}
	}

	/**
	 * Called to notify all that the schema is ready
	 */
	protected void schemaReady() {
		Vector listeners = getSchemaManagerListeners();
		for (int i = 0; i < listeners.size(); i++) {
			SchemaManagerListener sml = (SchemaManagerListener) listeners
					.elementAt(i);
			sml.schemaReady(this);
		}
	}

	/**
	 * Called to notify all that a schema was created
	 * 
	 * @param schema
	 */
	protected void schemaCreated(SchemaDefinition schema) {
		Vector listeners = getSchemaManagerListeners();
		for (int i = 0; i < listeners.size(); i++) {
			SchemaManagerListener sml = (SchemaManagerListener) listeners
					.elementAt(i);
			sml.schemaCreated(this);
		}
	}

	/**
	 * Retrieves a schema from the application
	 * 
	 * @param schemaName
	 * @return
	 */
	public abstract SchemaDefinition getSchema(String schemaName);

	public abstract Vector getAllSchema();

	private static SchemaManager sManager;

	/**
	 * Registers the schema and initializes it and the DbManager
	 * 
	 * @param className
	 *            - the qualified class name of the schema
	 */
	public static void registerManager(String className) {
		GLSplashScreen.setMessage("Initializing database");
		GLPrerequisites.prerequisiteStarted(GLApp.SCHEMA_REGISTRATION);
		
		try {
			System.out.println("GL [II] Registering schema manager: "
					+ className);
			Class classs = Class.forName(className);
			Object o = classs.newInstance();
			sManager = (SchemaManager) o;
			System.out.println("GL [II] Schema manager registered");
			GLPrerequisites.prerequisiteComplete(GLApp.SCHEMA_REGISTRATION);

			// Subscribe to the database loading mechanism
			DbManager.addListener(sManager);
			// Initialize the database
			DbManager.init(); // tell the gas db to initialize itself.. quickly

			// When this is complete... databaseActivity gets called...

		} catch (ClassCastException e) {
			System.out.println("GL [EE] Could not cast schema class: "
					+ className);
			for (int i = 0; i < tmpListeners.size(); i++) {
				SchemaManagerListener sml = (SchemaManagerListener) tmpListeners
						.elementAt(i);
				sml.schemaRegistrationFailed(className, e);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("GL [EE] Could not find schema class: "
					+ className);
			for (int i = 0; i < tmpListeners.size(); i++) {
				SchemaManagerListener sml = (SchemaManagerListener) tmpListeners
						.elementAt(i);
				sml.schemaRegistrationFailed(className, e);
			}
		} catch (InstantiationException e) {
			System.out.println("GL [EE] Could not instantiate schema class: "
					+ className);
			for (int i = 0; i < tmpListeners.size(); i++) {
				SchemaManagerListener sml = (SchemaManagerListener) tmpListeners
						.elementAt(i);
				sml.schemaRegistrationFailed(className, e);
			}
		} catch (IllegalAccessException e) {
			System.out
					.println("GL [EE] Illegal access exception occurred trying to instantiate: "
							+ className);
			for (int i = 0; i < tmpListeners.size(); i++) {
				SchemaManagerListener sml = (SchemaManagerListener) tmpListeners
						.elementAt(i);
				sml.schemaRegistrationFailed(className, e);
			}
		} catch(ControlledAccessException cae){
			System.out.println("GL [EE] Controll access exception occurred when trying to open db.");
			for (int i = 0; i < tmpListeners.size(); i++) {
				SchemaManagerListener sml = (SchemaManagerListener) tmpListeners
						.elementAt(i);
				sml.schemaRegistrationFailed(className, cae);
			}
		}
	}

	public static SchemaManager getManager() {
		return sManager;
	}

	public int getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(int databaseType) {
		this.databaseType = databaseType;
	}

	public static String getDatabaseTypeName(int type) {
		String result = null;
		switch (type) {
		case SchemaField.TYPE_DATE:
			result = "DATE";
			break;
		case SchemaField.TYPE_INTEGER:
			result = "INTEGER";
			break;
		case SchemaField.TYPE_DECIMAL:
			result = "REAL";
			break;
		case SchemaField.TYPE_TEXT:
			result = "TEXT";
			break;
		case SchemaField.TYPE_VARCHAR:
			result = "VARCHAR";
			break;
		case SchemaField.TYPE_BOOLEAN:
			result = "BOOLEAN";
			break;
		}
		return result;
	}

	/**
	 * This method is called after the DbManager fires the DB fires either the
	 * database created or database opened events. This method also transfers
	 * all schema listeners on the static instance to the actual instance that
	 * has been registered. Then it calls init on it
	 */
	private void postDbInit() {
		if (isInitialized())
			return;
		System.out.println("GL [II] Schema manager initializing ...");
		for (int i = 0; i < tmpListeners.size(); i++) {
			SchemaManagerListener sml = (SchemaManagerListener) tmpListeners
					.elementAt(i);
			sml.schemaRegistered(sManager);
		}
		sManager._addSchemaManagerListeners(tmpListeners);
		tmpListeners.removeAllElements();
		sManager.init();
		System.out.println("GL [II] Schema manager initialized");
	}

	/**
	 * Method is called whenever the DbManager fires an event
	 */
	public void databaseActivity(DbManagerEvent evt) {
		if (evt.getType() == DbManagerEvent.DATABASE_CREATED) { // call init
			// after the
			// database is
			// created
			if (!isInitialized()) {
				postDbInit();
			}
		} else if (evt.getType() == DbManagerEvent.DATABASE_OPENED) {
			if (!isInitialized()) {
				postDbInit();
			}
		}
	}

	public abstract boolean isInitialized();

	protected abstract void setInitialized(boolean initialized);

	/**
	 * Returns true if the schema is installed. PS: Database needs to be opened
	 * at this point.
	 * 
	 * @param sd
	 * @return
	 */
	public static boolean isSchemaInstalled(SchemaDefinition sd) {
		boolean result = false;
		String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='"
				+ sd.getTableName() + "'";
		if (DbManager.isOpen()) {
			try {
				Statement stmt = DbManager.getDatabase().createStatement(sql);
				stmt.prepare();
				Cursor c = stmt.getCursor();
				result = !c.isEmpty();
			} catch (DatabaseException e) {
				System.out.println("GL [EE] Unable to determine if table: "
						+ sd.getTableName() + " exists.\nReason: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Installed all schema into the database
	 * 
	 * @param sd
	 */
	protected void installSchema(SchemaDefinition sd) {
		Database db = DbManager.getDatabase();
		// Return if the schema is installed
		if (SchemaManager.isSchemaInstalled(sd)) {
			System.out
					.println("GL [II] Skipping re-installation of schema for: "
							+ sd.getTableName());
			return;
		}

		try {
			DbManager.beginTransaction();
			System.out.println("GL [II] Attempting to install schema: " + sd.getTableName() + "\nSQL: " + sd.asSQL());
			Statement stmt = db.createStatement(sd.asSQL());
			stmt.prepare();
			stmt.execute();
			DbManager.commitTransaction();
			System.out.println("GL [II] Schema: " + sd.getTableName()
					+ " installed successfully");
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			DbManager.rollbackTransaction();
			System.out.println("GL [EE] Schema: " + sd.getTableName()
					+ " installation failed");
			e.printStackTrace();
		}
	}

	/**
	 * Installs if necessary all the schema created by defineSchema method
	 */
	protected void installSchema() {
		Vector allSchema = getAllSchema();
		for (int i = 0; i < allSchema.size(); i++) {
			SchemaDefinition sd = (SchemaDefinition) allSchema.elementAt(i);
			installSchema(sd);
		}
	}

}
