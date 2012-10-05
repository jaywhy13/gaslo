package glo.types;

import glo.db.SchemaDefinition;
import glo.db.SchemaManager;
import glo.db.StdDatabaseable;
import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;
import glo.schema.GLSchemaManager;
import glo.sys.GLSettings;
import glo.ui.GLPriceInfo;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.util.IntVector;

/**
 * This class represents the attributes a gas company object should have
 * to be modeled correctly. It includes essential items such as gas prices,
 * gas locations and features of a gas station. All information about a gas
 * station can be found in this class.
 * @author rahibbert
 *
 */
public class GasCompany extends StdDatabaseable {

	/**
	 * The name of the gas station
	 */
	private String companyName;
	
	/**
	 * The id of the gas station
	 */
	private int companyId;
	
	/**
	 * The geographic id of the gas station which is the same as the gas station
	 */
	private int geoID;
	
	/**
	 * The gas station features object that stores all the features of the gas station
	 */
	public Features GSFeatures;
	
	/**
	 * Stores the gas station's current distance from the user
	 */
	private double distanceFromUser = Integer.MAX_VALUE;
	
	/**
	 * The gas station geographic data object
	 */
	private GasGeoData GSGeoLocation= null;
	
	/**
	 * Tells whether the gas station has an geographic data object
	 */
	public boolean geoDataSet = false;

	/**
	 * Stores the prices that belong to the gas station
	 */
	private Vector prices = new Vector();
	
	/**
	 * The number of gas prices that can be added to a gas station
	 */
	private int limit = 3;
	
	/**
	 * The date that the prices become effective
	 */
	private String dateOfPrices = null;
	
	/**
	 * A summary of the gas station including prices if available
	 */
	protected String summary = "";
	
	/**
	 * The parish in which the gas staion belongs
	 */
	private String parish = null;
	
	/**
	 * 
	 */
	private double averagePrices = 0;
	
	/**
	 * 
	 */
	public static Vector lastAddedStations = new Vector();
	
	/**
	 * Constructor function to create gas station objects using its name
	 * and id only
	 * @param name - Name of the gas station
	 * @param id - id of the gas station
	 */
	public GasCompany(String name, int id) {
		this.companyName = name;
		this.companyId = id;
	}

	/**
	 * Constructor function to create gas station objects using
	 * the gas station name, id, geographic location, parish and prices. 
	 * @param companyName - Name of gas station
	 * @param companyId - id of gas station
	 * @param location - gas station geographic data object
	 * @param parish - the parish the gas station is in
	 * @param prices - vector which stores the prices that belong to the gas station
	 */
	public GasCompany(String companyName, int companyId, GasGeoData location,
			String parish, Vector prices) {
		super();
		this.companyName = companyName;
		this.companyId = companyId;
		this.GSGeoLocation = location;
		this.setParish(parish);
		this.prices = prices;
		geoDataSet = true;
	}
	
	/**
	 * Returns the gas station name
	 * @return String
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Sets the gas station name
	 * @param companyName - The gas station name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Returns the gas station id
	 * @return int
	 */
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the gas station id
	 * @param companyId - The gas station id
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Returns the gas station geographic data object
	 * @return the GSGeoLocation
	 */
	public GasGeoData getLocation() {
		return GSGeoLocation;
	}

	/**
	 * Sets the gas station geographic data object
	 * @param GSGeoLocation - the gas station geographic data object
	 */
	public void setLocation(GasGeoData GSGeoLocation) {
		this.GSGeoLocation = GSGeoLocation;
	}

	/**
	 * Returns the vector storing the gas price info object(s)
	 * @return Vector
	 */
	public Vector getPrices() {
		return prices;
	}

	/**
	 * Sets the vector storing the gas price info object(s)
	 * @param prices - he vector storing the gas price info object(s)
	 */
	public void setPrices(Vector prices) {
		this.prices = prices;
	}

	/**
	 * Returns the gas station geographic data id
	 * @return int
	 */
	public int getGeoID() {
		return geoID;
	}

	/**
	 * Sets the gas station geographic id
	 * @param geoID - the gas station geographic data id
	 */
	public void setGeoID(int geoID) {
		this.geoID = geoID;
		geoDataSet = true;
	}
	
	/**
	 * Returns the current distance from the user
	 * @return double
	 */
	public double getDistanceFromUser() {
		return distanceFromUser;
	}

	/**
	 * Sets the current distance from the user
	 * @param distanceFromUser - the current distance from the user
	 */
	public void setDistanceFromUser(double distanceFromUser) {
		this.distanceFromUser = distanceFromUser;
	}
	
	/**
	 * Returns the gas station's features object
	 * @return Features
	 */
	public Features getFeature(){
		return GSFeatures;
	}
	
	/**
	 * Returns the current date of the prices
	 * @return String
	 */
	public String getDateOfPrices() {
		return dateOfPrices;
	}

	/**
	 * Sets the current date of the prices
	 * @param dateOfPrices - Date in which the prices take effect
	 */
	public void setDateOfPrices(String dateOfPrices) {
		this.dateOfPrices = dateOfPrices;
	}
	
	/**
	 * Sets the parish in which the gas station belongs
	 * @param parish the parish to set
	 */
	public void setParish(String parish) {
		this.parish = parish;
	}

	/**
	 * Returns the parish in which the gas station belongs
	 * @return String
	 */
	public String getParish() {
		return parish;
	}
	
	/**
	 * Adds the features to the gas station by creating an new features object
	 * @param airPump - sets to true if gas station has an air-pump, false otherwise
	 * @param atm - sets to true if gas station has an ATM, false otherwise
	 * @param bathroom - sets to true if gas station has a bathroom, false otherwise
	 * @param miniMart - sets to true if gas station has a mini-mart, false otherwise
	 */
	public void addFeatures(boolean airPump, boolean atm, boolean bathroom, boolean miniMart){
		GSFeatures = new Features(airPump, atm, bathroom, miniMart);
	}
	
	/**
	 * Returns a summary of the gas station
	 * @return String
	 */
	public String getSummary() {
		if(getLocation() != null){
			this.summary = companyName + ", " + getLocation().getParish();
		}
		else{
			this.summary = companyName + ", " + getParish();
		}
		return summary;
	}
	
	/**
	 * Adds a gas price info object to the vector prices
	 * @param gpi - gas price info object
	 */
	public void addPrice(GasPriceInfo gpi) {
		prices.addElement(gpi);
		if(prices.size() == limit){
			computeAveragePrice();
		}
	}
	
	public double getAveragePrices() {
		return averagePrices;
	}

	public void setAveragePrices(double averagePrices) {
		this.averagePrices = averagePrices;
	}
	
	public void computeAveragePrice() {
		int priceLength = prices.size();
		for(int a=0; a<priceLength; a++) {
			GasPriceInfo price = (GasPriceInfo) prices.elementAt(a);
			averagePrices += price.getCost();
		}	
		averagePrices = averagePrices / priceLength;
	}

	/**
	 * Returns true if the gas prices vector has reached it limit and false if not
	 * @return boolean
	 */
	public boolean gasPricesAllAdded() {
		if(prices.size() == limit){
			return true;
		}
		else{ return false; }
	}
	
	/**
	 * Sorts the Gas Price Information in the prices array in the order Unleaded 87, Unleaded 90
	 * and Diesel. 
	 */
	private void sortPrices() { // TODO: Check to see if this method can be improved or eliminated
		int allPrices = prices.size();
		if(allPrices > 1){
			GasPriceInfo currentPrice = null;
			for(int b=0; b<allPrices; b++){
				currentPrice = (GasPriceInfo) prices.elementAt(b);
				if(currentPrice.getGasTypeID() == 1){
					if(b != 0){ 
						prices.removeElementAt(b);
						prices.insertElementAt(currentPrice,0);
					}
				}
				else if(currentPrice.getGasTypeID() == 2){
					if(b != 1){
						prices.removeElementAt(b);
						if(allPrices == 3) { prices.insertElementAt(currentPrice,1); }
						else { prices.insertElementAt(currentPrice,0); }
					}
				}
				else if(currentPrice.getGasTypeID() == 3){
					if(b != 2){
						prices.removeElementAt(b);
						if(allPrices == 3) { prices.insertElementAt(currentPrice,2); }
						else { prices.insertElementAt(currentPrice,1); }
					}
				}
				else{
					// Do nothing
				}
			}
		}
	}
	
	/**
	 * Parses JSON Data to create Gas Station Objects and add the necessary
	 * additional information to the objects. All objects are placed in a
	 * static array.
	 * @param arr - JSONArray with gas station information
	 */
	public static void fromJSON(JSONArray arr) {
		try {
			JSONArray outer = arr;	
			if(outer != null){
				int outerLength = outer.length();
				lastAddedStations.removeAllElements();
				for(int a=0; a < outerLength; a++){
					// Get the inner objects and Parse the data
					JSONObject inner = (JSONObject) outer.get(a);
					if(inner != null){

						int gasStationID = inner.getInt(GLSettings.GS_ID);
						String gasStationName = inner.getString(GLSettings.GS_NAME);
						GasCompany currentGS = new GasCompany(gasStationName, gasStationID);
						double latitude = inner.getDouble(GLSettings.GS_LATITUDE);
						double longitude = inner.getDouble(GLSettings.GS_LONGITUDE);
						GasGeoData currentGGD = new GasGeoData(gasStationID, longitude, latitude);
						
						String parish = inner.getString(GLSettings.GS_PARISH);
						String address = inner.getString(GLSettings.GS_ADDRESS);
						
						currentGS.setGeoID(gasStationID);
						if(!address.equals(null)) {
							currentGGD.setAddress(address);
						}
						if(!parish.equals(null)) {
							currentGGD.setParish(parish);
						}
				
						JSONArray stationDatails = inner.getJSONArray(GLSettings.GS_FEATURES);
						currentGS.addFeatures(stationDatails.getBoolean(1), stationDatails.getBoolean(2), stationDatails.getBoolean(0), stationDatails.getBoolean(3));
						
						GasDataManager.getInstance().addGasStation(currentGS);
//						GasDataManager.getInstance().addGasStationWithIndex(currentGS, gasStationID);
						lastAddedStations.addElement(currentGS);

						GasDataManager.getInstance().addGasGeoData(currentGGD);
//						GasDataManager.getInstance().addGasGeoDataWithIndex(currentGGD, gasStationID);
						GasGeoData.lastAddedGeoData.addElement(currentGGD);
					}
				}
				// Used to minimize storage of vector
				GasDataManager.getInstance().getGasStations().trimToSize();
				GasDataManager.getInstance().getGasGeoData().trimToSize();
			}
		}
		catch (JSONException e) {
			System.out.println("GL [EE] Error parsing JSON: " + e.getMessage());
//			e.printStackTrace();
		} 
	}
	
	public Hashtable getData() {
		Hashtable data = new Hashtable();
		data.put("id", new Integer(getCompanyId()));
		data.put("name",getCompanyName());
		data.put("parish",getLocation().getParish());
		data.put("x",new Double(getLocation().getLongitude()));
		data.put("y",new Double(getLocation().getLatitude())); 
		data.put("price_update",getDateOfPrices());
		
		return data;
	}

	public SchemaDefinition getSchema() {
		SchemaManager sm = SchemaManager.getManager();
		if(sm != null){
			return sm.getSchema(GLSchemaManager.SCHEMA_GAS_STATIONS);
		} else {
			System.out.println("GL [EE] GasCompany could not find Schema Manager");
		}
		return null;
	}

	public String getTableName() {
		return "GStation";
	}
	
	public String toString() {
		String description = "";
		int allPrices = prices.size();
		if(allPrices != 0){
			sortPrices();
			for(int a=0; a<allPrices; a++) {
				((GasPriceInfo) prices.elementAt(a)).verbose = false;
				if(a == 0) { description += prices.elementAt(a).toString(); }
				else {description += ", "+prices.elementAt(a).toString(); }
			}
		}
		else {
			description = " Prices Unavailable: " + companyName + ", " + getLocation().getCity();
		}
		
		description += " Avg: " + Math.ceil(averagePrices);
		
		return description;
	}
	


	/**
	 * This class stores all the features that a gas station has
	 * @author rahibbert
	 *
	 */
	public class Features extends StdDatabaseable {
		
		/**
		 * Says whether a gas station has a mini-mart or not
		 */
		boolean miniMart = false;
		
		/**
		 * Says whether a gas station has a bathroom or not
		 */
		boolean bathroom = false;
		
		/**
		 * Says whether a gas station has an ATM or not
		 */
		boolean atm = false;
		
		/**
		 * Says whether a gas station has an air-pump or not
		 */
		boolean airPump = false;
		
		/**
		 * Stores all features in a readable form
		 */
		public String allFeatures = "";
		
		/**
		 * Constructor function which to create gas station features with the 
		 * four features available.
		 * @param airPump - sets to true if gas station has an air-pump, false otherwise
		 * @param atm - sets to true if gas station has an ATM, false otherwise
		 * @param bathroom - sets to true if gas station has a bathroom, false otherwise
		 * @param miniMart - sets to true if gas station has a mini-mart, false otherwise
		 */
		Features(boolean airPump, boolean atm, boolean bathroom, boolean miniMart){
			this.miniMart = miniMart;
			this.bathroom = bathroom;
			this.atm = atm;
			this.airPump = airPump;
			featuresText();
		}
		
		public boolean isMiniMart() {
			return miniMart;
		}

		public void setMiniMart(boolean miniMart) {
			this.miniMart = miniMart;
		}

		public boolean isBathroom() {
			return bathroom;
		}

		public void setBathroom(boolean bathroom) {
			this.bathroom = bathroom;
		}

		public boolean isAtm() {
			return atm;
		}

		public void setAtm(boolean atm) {
			this.atm = atm;
		}

		public boolean isAirPump() {
			return airPump;
		}

		public void setAirPump(boolean airPump) {
			this.airPump = airPump;
		}

		/**
		 * Transforms gas station features in to a readable form
		 */
		private void featuresText() {
			if(miniMart) { allFeatures += " Mini-Mart "; }
			if(bathroom) { allFeatures += " Bathroom "; }
			if(atm) { allFeatures += " A.T.M. "; }
			if(airPump) { allFeatures += " Air Pump "; }
		}
		
		public Hashtable getData() {
			Hashtable data = new Hashtable();
			data.put("gs_id", new Integer(getCompanyId()));
			data.put("has_atm",new Boolean(isAtm()));
			data.put("has_mini_mart",new Boolean(isMiniMart()));
			data.put("has_bathroom",new Boolean(isBathroom()));
			data.put("has_air_pump",new Boolean(isAirPump()));
			
			return data;
		}
		
		public SchemaDefinition getSchema() {
			SchemaManager sm = SchemaManager.getManager();
			if(sm != null){
				return sm.getSchema(GLSchemaManager.SCHEMA_GAS_STATION_PROPS);
			} else {
				System.out.println("GL [EE] GasCompany->Features could not find Schema Manager");
			}
			return null;
		}

		public String getTableName() {
			return "GSProps";
		}
		
		public String toString(){
			return allFeatures;
		}
	}
	
}
