package glo.types;

import glo.gps.UpdateGPSLocations;
import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;
import glo.sys.GLEventListener;
import glo.sys.GLEvents;
import glo.sys.GLSettings;
import glo.ui.GLApp;

import java.util.Date;
import java.util.Vector;

import net.rim.device.api.util.IntVector;

/**
 * This class manages the data that is received from the server by manipulating
 * it so that gas stations properly relates to gas prices and gas geographic data and 
 * gas prices properly relate to gas types.
 * @author rahibbert
 *
 */
public class GasDataManager implements GLEventListener {

	/**
	 * Stores all gas station objects
	 */
	private Vector stations = new Vector();
	
	/**
	 * Stores all gas type objects
	 */
	private Vector gasTypes = new Vector();
	
	/**
	 * Stores all gas price objects
	 */
	private Vector gasPrice = new Vector();
	
	/**
	 * Stores all gas station geographic data objects
	 */
	private Vector gasGeoData = new Vector();
	
	/**
	 * Stores all gas station objects related to a search
	 */
	private Vector searchStations = new Vector();
	
	/**
	 * Stores all gas station objects that are stored as favourites
	 */
	private Vector favouriteStations = new Vector();
	
	private static int minCapacity = 10;
	
	/**
	 * Tells whether the application has already done its initial setup
	 */
	private static boolean initialSetup = false;
	
	/**
	 * The only instance of this class
	 */
	private static GasDataManager gasDataManager = new GasDataManager();
	
	/**
	 * Private constructor, only gasDataManager must be used to get an instance of this class
	 */
	private GasDataManager() {
		GLEvents.addEventListener(GLApp.EVENT_SORT_TYPE_CHANGED, this);
	}
	
	/**
	 * Returns true if the application has already done an initial setup and false otherwise
	 * @return boolean
	 */
	public boolean isInitialSetup() {
		return GasDataManager.initialSetup;
	}

	/**
	 * Sets the initial setup variable for the application
	 * @param initialSetup - initial setup for the life of the application
	 */
	public void setInitialSetup(boolean initialSetup) {
		GasDataManager.initialSetup = initialSetup;
	}

	/**
	 * Returns the Vector that stores the list of favourite gas stations
	 * @return Vector
	 */
	public Vector getFavouriteStations() {
		return favouriteStations;
	}

	/**
	 * Sets the Vector that stores the list of favourite gas stations
	 * @param favouriteStations - Vector that stores the list of favourite gas stations
	 */
	public void setFavouriteStations(Vector favouriteStations) {
		this.favouriteStations = favouriteStations;
	}

	/**
	 * Returns the Vector that stores the list of gas stations that were searched for
	 * @return Vector
	 */
	public Vector getSearchStations() {
		return searchStations;
	}

	/**
	 * Sets the Vector that stores the list of gas stations that were searched for
	 * @param searchStations - Stores the Vector that stores the list of gas stations that were searched for
	 */
	public void setSearchStations(Vector searchStations) {
		this.searchStations = searchStations;
	}

	/**
	 * Returns the only instance of this class
	 * @return GasDataManager
	 */
	public static GasDataManager getInstance() {
		return gasDataManager;
	}
	
	/**
	 * Adds an element to the list of gas stations
	 * @param gasStation
	 */
	public void addGasStation(GasCompany gasStation) {
		stations.addElement(gasStation);
	}
	
	/**
	 * 
	 * @param gasStation
	 * @param index
	 */
	public void addGasStationWithIndex(GasCompany gasStation, int index) {
		int length = stations.size();
		if(index > length) {
			if(length == 0) {
				for(int a=0; a<index; a++) {			
					stations.addElement(null);
				}
			}
			else {
				for(int a=length; a<index; a++) {			
					stations.addElement(null);
				}
			}
		}
		else if(index < length){
			stations.setElementAt(gasStation, index);
			return;
		}
		
		stations.insertElementAt(gasStation, index);
	}
	

	/**
	 * Returns the list of gas stations
	 * @return Vector
	 */
	public Vector getGasStations() {
		return stations;
	}
	
	/**
	 * Search for a gas station by id
	 * @param id - Gas station id
	 * @return GasCompany
	 */
	public GasCompany lookupGasStationById(int id) {
		for(int i = 0; i < stations.size(); i++){
			GasCompany station = (GasCompany) stations.elementAt(i);
			if(id == station.getCompanyId()){
				return station;
			}
		}
		return null;
	}
	
	/**
	 * Adds a gas type to the list of gas types
	 * @param gasType - A gas type
	 */
	public void addGasType(GasType gasType) {
		gasTypes.addElement(gasType);
	}
	
	/**
	 * 
	 * @param gasType
	 * @param index
	 */
	public void addGasTypeWithIndex(GasType gasType, int index) {
		int length = gasTypes.size();
		if(index > length) {
			if(length == 0) {
				for(int a=0; a<index; a++) {			
					gasTypes.addElement(null);
				}
			}
			else {
				for(int a=length; a<index; a++) {			
					gasTypes.addElement(null);
				}
			}
		}
		else if(index < length) { 
			gasTypes.setElementAt(gasType, index);
			return;
		}
		
		gasTypes.insertElementAt(gasType, index);
	}
	
	/**
	 * Returns the list of gas types
	 * @return Vector
	 */
	public Vector getGasTypes() {
		return gasTypes;
	}
	
	/**
	 * Searches for a gas type by id
	 * @param id - A gas type id
	 * @return GasType
	 */
	public GasType lookupGasTypesById(int id) {
		for(int i = 0; i < gasTypes.size(); i++){
			GasType gasType = (GasType) gasTypes.elementAt(i);
			if(id == gasType.getId()){
				return gasType;
			}
		}
		return null;
	}
	
	/**
	 * Adds a gas price to the list of gas prices
	 * @param gasPrices - A gas price object
	 */
	public void addGasPrices(GasPriceInfo gasPrices) {
		gasPrice.addElement(gasPrices);
	}
	
	/**
	 * 
	 * @param gasPrices
	 * @param index
	 */
	public void addGasPricesWithIndex(GasPriceInfo gasPrices, int index) {
		int length = gasPrice.size();
		if(index > length) {
			if(length == 0) {
				for(int a=0; a<index; a++) {			
					gasPrice.addElement(null);
				}
			}
			else {
				for(int a=length; a<index; a++) {			
					gasPrice.addElement(null);
				}
			}
		}	
		else if(index < length){ 
			gasPrice.setElementAt(gasPrices, index);
			return;
		}
		gasPrice.insertElementAt(gasPrices, index);
	}
	
	/**
	 * Returns the list of gas prices
	 * @return Vector
	 */
	public Vector getGasPrices(){
		return gasPrice;
	}
	
	/**
	 * Searches for a gas price by id
	 * @param id - A gas price info
	 * @return GasPriceInfo
	 */
	public GasPriceInfo lookupGasPricesById(int id){
		for(int i = 0; i < gasPrice.size(); i++){
			GasPriceInfo currentGasPrice = (GasPriceInfo) gasPrice.elementAt(i);
			if(id == currentGasPrice.getGasCompanyID()){
				return currentGasPrice;
			}
		}
		System.out.println("GL [II] No gas price found with id: " + id);
		return null;
	}
	
	/**
	 * Adds gas station geographic data to the list of geographic data 
	 * @param gasGeoData
	 */
	public void addGasGeoData(GasGeoData GeoData){
		gasGeoData.addElement(GeoData);
	}
	
	/**
	 * 
	 * @param GeoData
	 * @param index
	 */
	public void addGasGeoDataWithIndex(GasGeoData GeoData, int index) {
		int length = gasGeoData.size();
		if(index > length) {
			if(length == 0) {
				for(int a=0; a<index; a++) {			
					gasGeoData.addElement(null);
				}
			}
			else {
				for(int a=length; a<index; a++) {			
					gasGeoData.addElement(null);
				}
			}
		}	
		else if(index < length){ 
			gasGeoData.setElementAt(GeoData, index);
			return;
		}
		
		gasGeoData.insertElementAt(GeoData, index);
		
	}
	
	/**
	 * Returns the list of gas station geographic data
	 * @return Vector
	 */
	public Vector getGasGeoData(){
		return gasDataManager.gasGeoData;
	}
	
	/**
	 * Searches for a gas geographic data by id
	 * @param id - a gas geographic data id
	 * @return GasGeoData
	 */
	public GasGeoData lookupGasGeoData(int id){
		for(int i = 0; i < gasGeoData.size(); i++){
			GasGeoData gasGeo = (GasGeoData) gasGeoData.elementAt(i);
			if(id == gasGeo.getGeoID()){
				return gasGeo;
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	public static void addGeoDatatoGasStation() {
		Vector gasStationsHolder = gasDataManager.getGasStations();
		int gasStationLength = gasDataManager.getGasStations().size();
		for(int e=0; e<gasStationLength; e++) {
			GasCompany currentCompany = (GasCompany) gasStationsHolder.elementAt(e);
			if(currentCompany != null) {
				if(currentCompany.geoDataSet) {
					int geoid = currentCompany.getGeoID();
					GasGeoData geoObject =  (GasGeoData) gasDataManager.getGasGeoData().elementAt(geoid);
					currentCompany.setLocation(geoObject);  
					System.out.println("GL [II] Added Geo Data: " + geoObject.getParish() + " to gas Station: " + currentCompany.getCompanyName());
				}
			}
		}
	}
	
	
	/**
	 * 
	 */
	public static void addPricetoGasStation() {
		Vector gasPricesHolder = gasDataManager.getGasPrices();
		int gasPricesLength = gasPricesHolder.size();
		for(int e=0; e<gasPricesLength; e++) {
			GasPriceInfo currentPrice = (GasPriceInfo) gasPricesHolder.elementAt(e);
			int gasStationId = currentPrice.getGasCompanyID();
			// TODO: Find a more efficient way to search
			GasCompany currentCompany = gasDataManager.lookupGasStationById(gasStationId);
//			GasCompany currentCompany = (GasCompany) gasDataManager.getGasStations().elementAt(gasStationId);
			if(!currentCompany.gasPricesAllAdded()) {
				currentCompany.addPrice(currentPrice);
				currentCompany.setDateOfPrices(currentPrice.getLastUpdated());
			}
		}
	}
	
	public static void addGasTypetoPrice() {
		Vector gasPricesHolder = gasDataManager.getGasPrices();
		int gasPricesLength = gasPricesHolder.size();
		for(int e=0; e<gasPricesLength; e++) {
			GasPriceInfo currentPrice = (GasPriceInfo) gasPricesHolder.elementAt(e);
			int typeId = currentPrice.getGasTypeID();
			GasType typeObject =  (GasType) gasDataManager.getGasTypes().elementAt(typeId);
			currentPrice.setName(typeObject.getName());
			currentPrice.setAlias(typeObject.getAlias());
			System.out.println("GL [II] Added GS Type: " + typeObject.getName() + " to GS Price: " +  currentPrice.getCost());
		}
	}
	
	/**
	 * Parses JSON data to extract the id's of gas stations that were returned
	 * from the search done by a user. The id's are matched with the current listing of gas stations
	 * and the applicable ones are updated in a new list. 
	 * @param arr - JSONArray which holds gas station id's that were returned from the search
	 */
	public static void searchFromJSON(JSONArray arr) {
		try{
			JSONArray outer = arr;
			if(outer != null){
				int outerLength = outer.length();
				GasDataManager.getInstance().getSearchStations().removeAllElements();
				for(int d=0; d < outerLength; d++){		
					JSONObject inner = (JSONObject) outer.get(d);
					if(inner != null){
						int stationID = inner.getInt("station_id");
						double distance = 0;
						if(inner.has("distance")) {
							distance = inner.getDouble("distance"); 
						}
						searchStationsUpdate(stationID, distance);
					}
				}
			}
			// Used to minimize storage of vector
			GasDataManager.getInstance().getSearchStations().trimToSize();		
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Searches for the particular id in the list of gas stations 
	 * and adds the gas station to the list of search stations if the
	 * id is found.
	 * @param id - A gas station ID
	 * @param distance - The current distance the gas station is from the user
	 */
	private static void searchStationsUpdate(int id, double distance) {
		Vector gasStations = GasDataManager.getInstance().getGasStations();
		int gasStationLength = gasStations.size();
		for(int e=0; e<gasStationLength; e++){
			GasCompany gasStation = (GasCompany) gasStations.elementAt(e);
			if(gasStation != null) {
				int stationID = gasStation.getCompanyId();
				if(id == stationID){
					gasStation.setDistanceFromUser(distance);
					GasDataManager.getInstance().getSearchStations().addElement(gasStation);
					return;
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public static void populateFavourites(){
		
	}
	
	/**
	 * 
	 * @param gc
	 */
	public void addFavourite(GasCompany gc) {
		if(!gasDataManager.getFavouriteStations().contains(gc)){		
			gasDataManager.getFavouriteStations().addElement(gc);
		}
	}
	
	/**
	 * 
	 * @param gc
	 */
	public void removeFavourite(GasCompany gc) {
		if(gasDataManager.getFavouriteStations().contains(gc)){		
			gasDataManager.getFavouriteStations().removeElement(gc);
		}
	}
	
	/**
	 * 
	 * @param gc
	 * @return
	 */
	public boolean inFavourites(GasCompany gc){
		if(gasDataManager.getFavouriteStations().contains(gc)){
			return true;
		}
		else{
			return false;
		}
	}

	public void eventOccurred(String eventName, Object data) {
		if(eventName == GLApp.EVENT_SORT_TYPE_CHANGED){
			Thread resort = new Thread(new Runnable(){
				public void run(){
					System.out.println("GL [II] Performing restart based on new sort mode of: " + GLSettings.getSortMode());
					UpdateGPSLocations doUpdate = new UpdateGPSLocations();
					doUpdate.run();
				}
			});
			
			resort.start();
		}
		
	}
}
