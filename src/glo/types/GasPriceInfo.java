package glo.types;

import glo.db.SchemaDefinition;
import glo.db.SchemaManager;
import glo.db.StdDatabaseable;
import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;
import glo.schema.GLSchemaManager;
import glo.sys.GLSettings;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.io.http.HttpDateParser;

/**
 * This class represents a single gas price for a gas company. It attaches a gas type to
 * itself for further definition of what it represents. A gas station can have up to 3 gas prices.
 * Each gas price is defined here with all necessary features.
 * 
 * @author rahibbert
 *
 */
public class GasPriceInfo extends StdDatabaseable {	
	
	/**
	 * Name of the gas price
	 */
	private String name = null;
	
	/**
	 * Alias of the gas price
	 */
	private String alias = null;
	
	/**
	 * The cost of the gas price
	 */
	private double cost;
	
	/**
	 * Time stamp to when the gas price was last updated
	 */
	private String lastUpdated = null; 
	
	/**
	 * Gas Station ID to which the gas price belongs
	 */
	private int gasCompanyID;

	/**
	 * Gas type id to which the gas price belongs
	 */
	private int gasTypeID;
	
	/**
	 * Decides whether the description should use the full name or the alias 
	 */
	public boolean verbose = false;
	
	/**
	 * 
	 */
	public static Vector lastAddedPrices = new Vector();
	
	/**
	 * Function to construct a gas price object with only a name and the price
	 * @param id - The gas station id to which this price belongs
	 * @param name - Name of the gas price
	 * @param cost - Cost of the gas price
	 */
	public GasPriceInfo(int id, String name, double cost){
		this(id,name,cost,null);
	}
	
	/**
	 * Constructor function to create a gas price object with the name cost and
	 * when the price was last updated
	 * @param stationId - The gas station id to which this price belongs
	 * @param name - Name of the gas price
	 * @param cost - Cost of the gas price
	 * @param lastUpdated - Date when the gas price was last updated
	 */
	public GasPriceInfo(int stationId, String name, double cost, String lastUpdated){
		this.gasCompanyID = stationId;
		this.name = name;
		this.cost = cost;
		this.lastUpdated = lastUpdated;
	}

	/**
	 * Retruns the name of the gas price	
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the gas price
	 * @param name - Name of the gas price
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the alias of the gas price	
	 * @return String
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias of the gas price
	 * @param alias - Alias of the gas price
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Returns the cost of the gas price
	 * @return double
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Sets the cost of the gas price
	 * @param cost
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

	/**
	 * Returns the date when the gas price was last updated
	 * @return Date
	 */
	public String getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * Sets the date when the gas price was last updated
	 * @param lastUpdated - Date when gas price was last updated
	 */
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * Returns the gas station id to which the gas price belongs
	 * @return int
	 */
	public int getGasCompanyID() {
		return gasCompanyID;
	}

	/**
	 * Sets the gas station id to which the gas price belongs
	 * @param gasCompanyID - Gas company to which the gas price belongs
	 */
	public void setGasCompanyID(int gasCompanyID) {
		this.gasCompanyID = gasCompanyID;
	}

	/**
	 * Returns the gas type to which the gas price belongs
	 * @return int
	 */
	public int getGasTypeID() {
		return gasTypeID;
	}

	/**
	 * Sets the id of the gas type to which the gas price belongs
	 * @param gasTypeID - The id of the gas type to which the gas price belongs
	 */
	public void setGasTypeID(int gasTypeID) {
		this.gasTypeID = gasTypeID;
	}

	/**
	 * Parses JSON Data to create GasPriceInfo Objects and add the necessary
	 * additional information to the objects. All objects are placed in a
	 * static array to which stores all gas price information.
	 * @param arr - JSONArray with gas price information
	 */
	public static void fromJSON(JSONArray arr){
		System.out.println("GL [II] Parsing JSON gas price info");
		try {
			JSONArray outer = arr;
			if(outer != null){
				int outerLength = outer.length();
				lastAddedPrices.removeAllElements();
				for(int a=0; a<outerLength;  a++){
					JSONObject inner = (JSONObject) outer.get(a);
					if(inner != null){
						int gasCompanyID = inner.getInt(GLSettings.GP_GS_ID);
						String date_added = inner.getString(GLSettings.GP_DATE);
						int gas_type_id = inner.getInt(GLSettings.GP_GT_ID);
						double gasCost = inner.getDouble(GLSettings.GP_COST);
						GasPriceInfo currentGasPrice;
						if(!date_added.equals(null)){
							currentGasPrice = new GasPriceInfo(gasCompanyID,"Type Not Available",gasCost,date_added);
						}
						else{ currentGasPrice = new GasPriceInfo(gasCompanyID,"Type Not Available",gasCost); }
						
						currentGasPrice.setGasTypeID(gas_type_id);
						GasDataManager.getInstance().addGasPrices(currentGasPrice);
						lastAddedPrices.addElement(currentGasPrice);
					}
				}
				// Used to minimize storage of vector
				GasDataManager.getInstance().getGasPrices().trimToSize();
			}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Hashtable getData() {
		Hashtable data = new Hashtable();
		data.put("gs_id",new Integer(getGasCompanyID()));
		data.put("gst_id",new Integer(getGasTypeID()));

		return data;
	}

	public SchemaDefinition getSchema() {
		SchemaManager sm = SchemaManager.getManager();
		if(sm != null){
			return sm.getSchema(GLSchemaManager.SCHEMA_GAS_STATION_PRICE);
		} else {
			System.out.println("GL [EE] GasPriceInfo could not find Schema Manager");
		}
		return null;
	}

	public String getTableName() {
		return "GSPrices";
	}
	
	public String toString() {
		if(verbose){
			return name + ": " + "$" + cost;		
		}
		else{
			return alias + ": " + "$" + cost;
		}
	}
}
