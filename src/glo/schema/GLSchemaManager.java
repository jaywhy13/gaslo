package glo.schema;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.util.IntIntHashtable;

import glo.db.SchemaDefinition;
import glo.db.SchemaFKeyDefinition;
import glo.db.SchemaField;
import glo.db.SchemaManager;
import glo.db.SchemaManagerListener;
import glo.db.StdSchemaDefinition;

/**
 * The GLSchemaManager subclasses the SchemaManager. It holds schema definitions
 * for all the different types that need them.
 * 
 * @author JMWright
 * 
 */
public class GLSchemaManager extends SchemaManager {

	private boolean initialized = false;

	public static final String SCHEMA_GAS_STATIONS = "GStation";

	public static final String SCHEMA_GAS_STATION_PRICE = "GSPrice";

	public static final String SCHEMA_GAS_STATION_PROPS = "GSProps";

	public static final String SCHEMA_GAS_TYPE = "GSType";

	public static final String SCHEMA_GAS_FAVOURITES = "GSFavourites";
	
	public static final String SCHEMA_GAS_SETTINGS = "GSSettings";
	
	public Hashtable schemaDict = new Hashtable();

	private Vector listeners = new Vector();

	
	/**
	 * Adds a schema to the dictionary
	 */
	public void addSchema(String tableName, SchemaDefinition schema) {
		System.out.println("GL [II] Schema definition added for: " + tableName);
		schemaDict.put(tableName, schema);
	}

	public boolean hasSchema(String schemaName) {
		return schemaDict.get(schemaName) != null;
	}

	public SchemaDefinition getSchema(String schemaName) {
		if (hasSchema(schemaName)) {
			return (SchemaDefinition) schemaDict.get(schemaName);
		}
		return null;
	}
	
	public Vector getAllSchema() {
		Vector result = new Vector();
		Enumeration e = schemaDict.elements();
		while(e.hasMoreElements()){
			SchemaDefinition sd = (SchemaDefinition) e.nextElement();
			result.addElement(sd);
		}
		return result;
	}

	
	protected void defineSchema() {
		// Define ALL our schema
		// =======================================================================================
		// Defining GStation
		SchemaDefinition gs = new StdSchemaDefinition(SCHEMA_GAS_STATIONS);
		SchemaField gsId = new SchemaField("id", SchemaField.TYPE_INTEGER);
		SchemaField gsName = new SchemaField("name", SchemaField.TYPE_VARCHAR);
		SchemaField gsParish = new SchemaField("parish", SchemaField.TYPE_VARCHAR);
		SchemaField gsX = new SchemaField("x", SchemaField.TYPE_DECIMAL);
		SchemaField gsY = new SchemaField("y", SchemaField.TYPE_DECIMAL);
		SchemaField gsUpdateAt = new SchemaField("price_update", SchemaField.TYPE_DATE);
		gsId.setPrimary(true);
		
		// Construct it
		gs.addField(gsId);
		gs.addField(gsName);
		gs.addField(gsParish);
		gs.addField(gsX);
		gs.addField(gsY);
		gs.addField(gsUpdateAt);
		addSchema(SCHEMA_GAS_STATIONS, gs);

		// =======================================================================================
		// Defining Gas Types
		SchemaDefinition gt = new StdSchemaDefinition(SCHEMA_GAS_TYPE);
		SchemaField gtId = new SchemaField("id", SchemaField.TYPE_INTEGER);
		SchemaField gtName = new SchemaField("name", SchemaField.TYPE_TEXT);
		SchemaField gtAlias = new SchemaField("alias", SchemaField.TYPE_TEXT);
		gtId.setPrimary(true);
		
		// Construct it
		gt.addField(gtId);
		gt.addField(gtName);
		gt.addField(gtAlias);
		addSchema(SCHEMA_GAS_TYPE, gt);

		// =======================================================================================
		// Defining GSPrices
		// CREATE TABLE GSPrices (gs_id integer,gst_id integer,double price,
		// foreign key (gs_id) references GStation (id) on update cascade on
		// delete cascade,
		// foreign key (gst_id) references GSType (id) on update cascade on
		// delete cascade );

		// NB: Add ALLLL fields before adding foreign keys
		SchemaDefinition gsPrice = new StdSchemaDefinition(SCHEMA_GAS_STATION_PRICE);
//		SchemaField gspId = new SchemaField("id", SchemaField.TYPE_INTEGER);
		SchemaField gspGsId = new SchemaField("gs_id", SchemaField.TYPE_INTEGER); // gas station id
		SchemaField gspGstId = new SchemaField("gst_id", SchemaField.TYPE_INTEGER); 
		SchemaField gsCost = new SchemaField("cost", SchemaField.TYPE_DECIMAL); // cost
//		SchemaField gsDateAdded = new SchemaField("date_added", SchemaField.TYPE_DATE); // date added
		SchemaField gsDateEffective = new SchemaField("date_effective", SchemaField.TYPE_DATE); // date effective
		
		// Construct it
//		gsPrice.addField(gspId);
		gsPrice.addField(gspGsId);
		gsPrice.addField(gspGstId);
		gsPrice.addField(gsCost);
//		gsPrice.addField(gsDateAdded);
		gsPrice.addField(gsDateEffective);
		gsPrice.addForeignKey(new SchemaFKeyDefinition(gspGsId, gsId)); // foreign key refences
		gsPrice.addForeignKey(new SchemaFKeyDefinition(gspGstId, gtId));
		
		addSchema(SCHEMA_GAS_STATION_PRICE, gsPrice);

		// =======================================================================================
		// Defining GSProps
		SchemaDefinition gsProps = new StdSchemaDefinition(
				SCHEMA_GAS_STATION_PROPS);
		SchemaField gspropsGsId = new SchemaField("gs_id",
				SchemaField.TYPE_INTEGER);
		SchemaField hasAtm = new SchemaField("has_atm",
				SchemaField.TYPE_BOOLEAN);
		SchemaField hasMiniMart = new SchemaField("has_mini_mart",
				SchemaField.TYPE_BOOLEAN);
		SchemaField hasBathroom = new SchemaField("has_bathroom",
				SchemaField.TYPE_BOOLEAN);
		SchemaField hasAirPump = new SchemaField("has_air_pump",
				SchemaField.TYPE_BOOLEAN);
		gspropsGsId.setPrimary(true);
		gsProps.addField(gspropsGsId);
		gsProps.addField(hasAtm);
		gsProps.addField(hasMiniMart);
		gsProps.addField(hasBathroom);
		gsProps.addField(hasAirPump);
		gsProps.addForeignKey(new SchemaFKeyDefinition(gspropsGsId, gsId));
		addSchema(SCHEMA_GAS_STATION_PROPS, gsProps);
		
		// =======================================================================================
		// Defining GSFavourites
		SchemaDefinition gsFav = new StdSchemaDefinition(SCHEMA_GAS_FAVOURITES);
		SchemaField gsFavId = new SchemaField("id",SchemaField.TYPE_INTEGER);
		gsFavId.setPrimary(true);
		gsFav.addField(gsFavId);
		gsFav.addForeignKey(new SchemaFKeyDefinition(gsFavId,gsId));
		addSchema(SCHEMA_GAS_FAVOURITES, gsFav);
		
		// =======================================================================================
		// Defining GSSettings
		SchemaDefinition gsSet = new StdSchemaDefinition(SCHEMA_GAS_SETTINGS);
		SchemaField price_date = new SchemaField("price_date",SchemaField.TYPE_TEXT);
		SchemaField db_version = new SchemaField("db_version",SchemaField.TYPE_TEXT);
		SchemaField hello_url = new SchemaField("hello_url",SchemaField.TYPE_TEXT);
		db_version.setPrimary(true);
		gsSet.addField(price_date);
		gsSet.addField(db_version);
		gsSet.addField(hello_url);
		addSchema(SCHEMA_GAS_SETTINGS, gsSet);
	}

	protected void _addSchemaManagerListener(SchemaManagerListener sml) {
		listeners.addElement(sml);
	}

	protected void _addSchemaManagerListeners(Vector v) {
		for (int i = 0; i < v.size(); i++) {
			SchemaManagerListener sml = (SchemaManagerListener) v.elementAt(i);
			_addSchemaManagerListener(sml);
		}
	}

	protected Vector getSchemaManagerListeners() {
		return listeners;
	}

	public boolean isInitialized() {
		return initialized;
	}
	
	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

}
