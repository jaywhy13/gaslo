package glo.net;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.io.HttpConnection;

import net.rim.device.api.database.DatabaseException;

import glo.db.DBOperations;
import glo.gps.GPSHandle;
import glo.gps.UpdateGPSLocations;
import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;
import glo.misc.GLPrerequisites;
import glo.misc.GLUser;
import glo.sys.GLEventListener;
import glo.sys.GLEvents;
import glo.sys.GLSettings;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.types.GasGeoData;
import glo.types.GasPriceInfo;
import glo.types.GasType;
import glo.ui.GLApp;

public class ServerDataManager implements GLEventListener {
	/**
	 * 
	 */
	private Timer fetchMoreStationsTimer = new Timer();
	
	/**
	 * 
	 */
	protected TimerTask fetchMoreStationsTask;
	
	/**
	 * 
	 */
	protected static Vector dbStations = new Vector();
	
	/**
	 * 
	 */
	protected static Vector dbPrices = new Vector();
	
	/**
	 * 
	 */
	public ServerDataManager() {
		GLEvents.addEventListener(GLApp.EVENT_GPS_POSITION_UPDATED, this);
	}


	private int completeStationFetches = 0;
	
	public int getCompleteStationFetches() {
		return completeStationFetches;
	}

	protected void setCompleteStationFetches(int completeStationFetches) {
		this.completeStationFetches = completeStationFetches;
	}

	/**
	 * Gets the relevant gas station(s) based on the search requirement that was
	 * made by the user. Gas Stations are returned based on the proximity to the
	 * user, starting from the closest to the farthest.
	 * 
	 * @param search
	 * @param limit
	 * @throws JSONException 
	 */
	public boolean networkSearch(String search, int limit) throws JSONException { 
		GLNetworkNavigate request = new GLNetworkNavigate();
		GLNetworkAction dataAction = new GLNetworkAction();
		if(request.canConnect()) { 
			Hashtable searchParams = new Hashtable();
			searchParams.put("search", search);
			searchParams.put("limit", new Integer(limit));
			String searchURL = GLSettings.getUrlByName(GLSettings.URL_SEARCH,searchParams);
			JSONArray jsonString = getJsonFromUrl(searchURL, request, dataAction);
			GasDataManager.searchFromJSON(jsonString);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param url
	 * @throws JSONException
	 */
	public boolean getSettings() throws JSONException {
		GLNetworkNavigate request = new GLNetworkNavigate();
		GLNetworkAction dataAction = new GLNetworkAction();
		if(request.canConnect()) {
			String settingsURL = GLSettings.getUrlByName(GLSettings.URL_HELLO);
			System.out.println("GL [II] Sayin hello: " + settingsURL);
			GLPrerequisites.prerequisiteStarted(GLApp.SAY_HELLO);
			JSONArray jsonString = getJsonFromUrl(settingsURL, request, dataAction);
			if(jsonString != null){
				GLPrerequisites.prerequisiteComplete(GLApp.SAY_HELLO);
				GLUser.fromJSON(jsonString);
				return true;
			} else {
				GLPrerequisites.prerequisiteFailed(GLApp.SAY_HELLO);
			}
			return false;
		} else {
			GLPrerequisites.prerequisiteFailed(GLApp.SAY_HELLO);
			return false;
		}
	}
	
	
	/**
	 * 
	 * @param limit
	 * @throws JSONException
	 */
	public boolean getGasStations(int limit, boolean addDB)throws JSONException {
		GLNetworkNavigate request = new GLNetworkNavigate();
		GLNetworkAction dataAction = new GLNetworkAction();
		if(request.canConnect()) {
			Hashtable params = new Hashtable();
			params.put("limit", new Integer(limit));
			String stationURL = GLSettings.getUrlByName(GLSettings.URL_STATIONS,params);
			JSONArray jsonString = getJsonFromUrl(stationURL, request, dataAction);
			GasCompany.fromJSON(jsonString);
			if(addDB) {
				Thread addToDb = new Thread(new Runnable() {
					public void run(){
						// add fetched stations to db
						try {
							DBOperations.populateStations(GasCompany.lastAddedStations);
						} catch (DatabaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				addToDb.start();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param limit
	 * @throws JSONException
	 */
	public boolean getGasPrices(int limit, boolean addDB)throws JSONException {
		GLNetworkNavigate request = new GLNetworkNavigate();
		GLNetworkAction dataAction = new GLNetworkAction();
		if(request.canConnect()) {
			Hashtable params = new Hashtable();
			params.put("limit", new Integer(limit));
			String pricesURL = GLSettings.getUrlByName(GLSettings.URL_PRICES,params);
			JSONArray jsonString = getJsonFromUrl(pricesURL, request, dataAction);		
			GasPriceInfo.fromJSON(jsonString);
			if(addDB) {
				Thread addToDb = new Thread(new Runnable() {
					public void run(){
						// add fetched prices to db
						try {
							DBOperations.populateGasPrices(GasPriceInfo.lastAddedPrices);
						} catch (DatabaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				addToDb.start();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @throws JSONException
	 */
	public boolean getGasTypes(boolean addDB)throws JSONException {
		GLNetworkNavigate request = new GLNetworkNavigate();
		GLNetworkAction dataAction = new GLNetworkAction();
		
		if(request.canConnect()) {
			String typesURL = GLSettings.getBasicUrlByName(GLSettings.URL_GASTYPES);
			JSONArray jsonString = getJsonFromUrl(typesURL, request, dataAction);
			GasType.fromJSON(jsonString);
			if(addDB) {
				Thread addToDb = new Thread(new Runnable() {
					public void run(){
						// add fetched types to db
						try {
							DBOperations.populateGasTypes(GasType.lastAddedTypes);
						} catch (DatabaseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				addToDb.start();
			}
			return true;
		} else {
			return false;
		}	
	}

	/**
	 * 
	 * @param limit
	 * @throws JSONException
	 */
	public boolean getGeoData(int limit)throws JSONException {
		GLNetworkNavigate request = new GLNetworkNavigate();
		GLNetworkAction dataAction = new GLNetworkAction();
		if(request.canConnect()) {
			Hashtable params = new Hashtable();
			params.put("limit", new Integer(limit));
			String geoURL = GLSettings.getUrlByName(GLSettings.URL_GEODATA,params);
			JSONArray jsonString = getJsonFromUrl(geoURL, request, dataAction);
			GasGeoData.fromJSON(jsonString);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method is responsible for fetching x amount of gas stations from
	 * the server and doing the following:
	 * - parse the listing
	 * 		-	extract gas stations and their prices
	 * 		- 	add stations to GasDataManager
	 * - insert into database in a thread
	 */
	public boolean fetchStations(final int numberOfStations) {
		if(!GPSHandle.isValid()) {
			System.out.println("GL [II] Skipping fetch stations, no point available");
			return false;
		}

		Hashtable params = new Hashtable();
		params.put("limit", new Integer(numberOfStations));
		
		String url = GLSettings.getUrlByName(GLSettings.URL_STATIONS_PRICES, params);
		GLNetworkNavigate request = new GLNetworkNavigate();
		System.out.println("GL [II] Will fetch " + numberOfStations + " stations from url: " + url);
		GLNetworkAction parseDataAction = new GLNetworkAction() {
			public void success(Object data, int status) {
				if(data == null){
					System.out.println("GL [EE] Got a NULL response from the server");
					return;
				}
				data = ((String) data).trim();
				if(((String) data).charAt(0) != '['){
					data = "[" + data + "]";
				}
				String jString = new String((String) data);
				System.out.println("GL [II] Response from fetchStations url: " + jString);
				int l=0;
				l++;

				// parse the data from the server				
				try {
					JSONArray outer = new JSONArray(jString);	
					if(outer != null) {
						dbStations.removeAllElements();
						dbPrices.removeAllElements();
						
						int outerLength = outer.length();
						for(int a=0; a < outerLength; a++){
							// Get the inner objects and Parse the data
							JSONObject inner = (JSONObject) outer.get(a);
							if(inner != null) {
								String last_update = inner.getString(GLSettings.GAS_PRICE_UPDATE);
								JSONArray gas_data = (JSONArray) inner.getJSONArray(GLSettings.GS_DATA);
								int data_length = gas_data.length();
								for(int e=0; e < data_length; e++) {
									JSONObject gasInfo = (JSONObject) gas_data.get(e);
									int gasStationID = gasInfo.getInt(GLSettings.GS_ID);
									String gasStationName = gasInfo.getString(GLSettings.GS_NAME);
									GasCompany currentGS = new GasCompany(gasStationName, gasStationID);
									if(currentGS == null) {
										System.out.println("GL [EE] Skipping NULL gas station with id=" + gasStationID);
										continue;
									}
									if(!last_update.equals(null)) {
										currentGS.setDateOfPrices(last_update);
									}
									
									double latitude = gasInfo.getDouble(GLSettings.GS_LATITUDE);
									double longitude = gasInfo.getDouble(GLSettings.GS_LONGITUDE);
									
									if(latitude == 0 || longitude == 0) continue; // don't add these stations 
									
									GasGeoData currentGGD = new GasGeoData(gasStationID, longitude, latitude);
									
									String parish = gasInfo.getString(GLSettings.GS_PARISH);
									String address = gasInfo.getString(GLSettings.GS_ADDRESS);
									
									currentGS.setGeoID(gasStationID);
									if(!address.equals(null)) {
										currentGGD.setAddress(address);
									}
									if(!parish.equals(null)) {
										currentGGD.setParish(parish);
										currentGS.setParish(parish);
									}
									currentGS.setLocation(currentGGD);
									if(GPSHandle.isValid()) {
										double userLat = GPSHandle.getLatitude();
										double userLon = GPSHandle.getLongitude();
										double distanceFromUser = Math.abs(UpdateGPSLocations.distance(userLat, userLon, latitude, longitude));
										currentGS.setDistanceFromUser(distanceFromUser);
									}
									JSONArray stationDatails = gasInfo.getJSONArray(GLSettings.GS_FEATURES);
									currentGS.addFeatures(stationDatails.getBoolean(1), stationDatails.getBoolean(2), stationDatails.getBoolean(0), stationDatails.getBoolean(3));
									
									JSONArray price_list = gasInfo.getJSONArray(GLSettings.GS_PRICE_ARRAY);
									int price_list_length = price_list.length();
									if(price_list_length != 0 ){
										for(int i=0; i<price_list_length; i++) {
											JSONObject price = (JSONObject) price_list.get(i);
											int gas_type_id = price.getInt(GLSettings.GP_GT_ID);
											double gasCost = price.getDouble(GLSettings.GP_COST);
											GasPriceInfo currentGasPrice;
											if(!last_update.equals(null)) {
												currentGasPrice = new GasPriceInfo(gasStationID,"Type Not Available",gasCost,last_update);
											}
											else{ currentGasPrice = new GasPriceInfo(gasStationID,"Type Not Available",gasCost); }
											
											currentGasPrice.setGasTypeID(gas_type_id);
											currentGS.addPrice(currentGasPrice);
											GasDataManager.getInstance().addGasPrices(currentGasPrice);
											dbPrices.addElement(currentGasPrice);
										}
									} else {
										continue;
									}
//									GasDataManager.getInstance().addGasStationWithIndex(currentGS, gasStationID);
//									GasDataManager.getInstance().addGasGeoDataWithIndex(currentGGD, gasStationID);
									GasDataManager.getInstance().addGasStation(currentGS);
									dbStations.addElement(currentGS);
									GasDataManager.getInstance().addGasGeoData(currentGGD);			
								}
							}
						}
						// Used to minimize storage of vector
						GasDataManager.getInstance().getGasStations().trimToSize();
						GasDataManager.getInstance().getGasGeoData().trimToSize();
						GasDataManager.getInstance().getGasPrices().trimToSize();
						// add gas types to the gas prices
						GasDataManager.addGasTypetoPrice();
						// Gas stations all downloaded from server
						completeStationFetches++;
						
						UpdateGPSLocations doUpdate = new UpdateGPSLocations();
						doUpdate.run();
						
					} else {
						System.out.println("GL [EE] The JSONArray received from fetch stations was NULL!");
						fetchStations(numberOfStations);
					}
				}
				catch (JSONException e) {
					String msg = e.getMessage();
					System.out.println("GL [EE] Error parsing gas station and prices information. " + msg + " (JSONException)");
				} catch(Exception e) {
					String msg = e.getMessage() + e.getClass().getName();
					System.out.println("GL [EE] General error parsing gas station and prices information. " + msg);
				}
				
				Thread addToDb = new Thread(new Runnable() {
					public void run(){
						// add fetched stations to db
						try {
							DBOperations.populateStations(dbStations);
							DBOperations.populateGasPrices(dbPrices);
						} catch (DatabaseException e) {
							System.out.println("GL [EE] Error while trying to populate db" + e.getMessage());
						}
						startBackgroundFetchStations();
					}
				});
				addToDb.start();
				GLEvents.triggerEvent(GLApp.EVENT_GAS_STATIONS_FETCHED);
			}
		};
		parseDataAction.setAutoRetryOnFailure(true);
		
		if(request.canConnect()) {
			request.get(url, parseDataAction);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Asks the server for the data at the given url
	 * @param url
	 * @param request
	 * @param dataAction
	 * @return
	 * @throws JSONException
	 */
	protected JSONArray getJsonFromUrl(String url, GLNetworkNavigate request, GLNetworkAction dataAction) throws JSONException {
		System.out.println("GL [II] Fetching data...");
		System.out.println("GL [II]  >> Url: " + url);
		int numberOfRetries = 3;
		int retryCount = 0;
		while(retryCount < numberOfRetries && !dataAction.isResposnseSuccess()){
			request.get(url, dataAction);
			retryCount++;
			if(!dataAction.isResposnseSuccess()){
				System.out.println("GL [EE] Got response: " + dataAction.getStatus() + " from server. Will retry getJsonFromUrl: " + url);
			}
		}
		
		if (dataAction.isResposnseSuccess()) {
			String serverData = (String) dataAction.getData();
			int serverResponse = dataAction.getStatus();
			if (serverResponse == 200) {
				if (!serverData.equals("")) {
					serverData = serverData.trim();
					if(serverData.charAt(0) != '['){
						serverData = "[" + serverData + "]";
					}
					JSONArray jsonData = new JSONArray(serverData);
					return jsonData;
				}
			}
		} 
		return null;
	}

	/**
	 * Slowly increments the database gas stations by asking for more stations 
	 * every now and then 
	 */
	protected void startBackgroundFetchStations() {
		if(fetchMoreStationsTask == null) {
			fetchMoreStationsTask = new TimerTask() {
				public void run() {
					fetchStations(GLSettings.getNumberOfBackgroundStationsToFetch());
				}
			};
			fetchMoreStationsTimer.scheduleAtFixedRate(fetchMoreStationsTask, 0,GLSettings.getFetchStationDelay());
		}
	}
	
	/**
	 * Stop the background fetching 
	 */
	protected void stopBackgroundFetchStations() {
		if(fetchMoreStationsTimer != null) {
			fetchMoreStationsTimer.cancel();
			fetchMoreStationsTask = null;
		}
	}

	public void eventOccurred(String eventName, Object data) {
		if(eventName == GLApp.EVENT_GPS_POSITION_UPDATED && completeStationFetches == 0){
			System.out.println("GL [II] Calling fetch because we have a new position and we have not done a complete fetch yet");
			fetchStations(GLSettings.getNumberOfStationsToFetch());
		}
	}
}
