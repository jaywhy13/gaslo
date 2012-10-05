package glo.sys;

import java.util.Enumeration;
import java.util.Hashtable;

import glo.gps.GLUpdateStationListing;
import glo.misc.GLUser;
import glo.ui.GLApp;

public class GLSettings {

	private static final Hashtable settingsHash = new Hashtable();

	public static final String URL_SEARCH = "search_url";
	public static final String URL_ADS = "ads_url";
	public static final String URL_GEODATA = "geo_data_url";
	public static final String URL_BASE = "base_url";
	public static final String URL_STATIONS = "gas_stations_url";
	public static final String URL_PRICES = "prices_url";
	public static final String URL_GASTYPES = "types_url";
	public static final String URL_HELLO = "hello_url";

	public static final String URL_STATIONS_PRICES = "station_prices_url";


	public static final String GAS_PRICE_UPDATE = "dt"; 
	public static final String GS_DATA = "data";
	
	public static final String GS_LATITUDE = "lat";
	public static final String GS_LONGITUDE = "lon";
	public static final String GS_NAME = "na";
	public static final String GS_ID = "id";
	public static final String GS_ADDRESS = "addr";
	public static final String GS_FEATURES = "fe";
	public static final String GS_PARISH = "pa";
	public static final String GS_PRICE_ARRAY = "pr";
	
	public static final String GP_GS_ID = "gsid";
	public static final String GP_COST = "c";
	public static final String GP_GT_ID = "gtid";
	public static final String GP_DATE = "dt";
		
	public static final String GT_NAME = "na";
	public static final String GT_ALIAS = "a";
	public static final String GT_DESCRIPTION = "des";
	public static final String GT_ID = "gtid";
	
	public static final int SORT_BY_CLOSEST = 0;
	public static final int SORT_BY_LOWEST = 1;
	public static final int SORT_BY_BOTH = 2;

	public static boolean search = false;

	private static int sortMode = 2;
	private static int timeInSeconds = 1;
	private static int distanceUpdate = 0;
	private static int groupStationsWithin = 10000;

	private static int numberOfStationsToFetch = 20;
	private static int numberOfBackgroundStationsToFetch = 10;
	private static int fetchStationDelay = 120 * 1000; 
	private static int numberOfPricesToFetch = 200;
	private static int distanceThreshold = 1000;
	private static int rotateAdsEvery = 30 * 1000;
	
	
	
	public static int getRotateAdsEvery() {
		return rotateAdsEvery;
	}

	public static void setRotateAdsEvery(int rotateAdsEvery) {
		GLSettings.rotateAdsEvery = rotateAdsEvery;
	}

	public static int getFetchStationDelay() {
		return fetchStationDelay;
	}

	public static void setFetchStationDelay(int fetchStationDelay) {
		GLSettings.fetchStationDelay = fetchStationDelay;
	}

	public static int getNumberOfStationsToFetch() {
		return numberOfStationsToFetch;
	}

	public static int getNumberOfPricesToFetch() {
		return numberOfPricesToFetch;
	}

	public static void setNumberOfPricesToFetch(int numberOfPricesToFetch) {
		GLSettings.numberOfPricesToFetch = numberOfPricesToFetch;
	}

	public static void setNumberOfStationsToFetch(int numberOfStationsToFetch) {
		GLSettings.numberOfStationsToFetch = numberOfStationsToFetch;
	}

	public static int getNumberOfBackgroundStationsToFetch() {
		return numberOfBackgroundStationsToFetch;
	}

	public static void setNumberOfBackgroundStationsToFetch(
			int numberOfBackgroundStationsToFetch) {
		GLSettings.numberOfBackgroundStationsToFetch = numberOfBackgroundStationsToFetch;
	}

	public static int getDistanceThreshold() {
		return distanceThreshold;
	}

	public static void setDistanceThreshold(int distanceThreshold) {
		GLSettings.distanceThreshold = distanceThreshold;
	}

	public static void setSortMode(int sortMode) {
		GLSettings.sortMode = sortMode;
		GLEvents.triggerEvent(GLApp.EVENT_SORT_TYPE_CHANGED);
	}

	public static int getGroupStationsWithin() {
		return groupStationsWithin;
	}

	public static void setGroupStationsWithin(int groupStationsWithin) {
		GLSettings.groupStationsWithin = groupStationsWithin;
	}

	public static int getSortMode() {
		return sortMode;
	}

	public static int getTimeInSeconds() {
		return timeInSeconds;
	}

	public static int getDistanceUpdate() {
		return distanceUpdate;
	}

	public static void setDistanceUpdate(int distanceUpdate) {
		GLSettings.distanceUpdate = distanceUpdate;
		final int meters;
		if (GLSettings.distanceUpdate == 0) {
			meters = 500;
		} else if (GLSettings.distanceUpdate == 1) {
			meters = 1000;
		} else if (GLSettings.distanceUpdate == 2) {
			meters = 3000;
		} else {
			meters = 5000;
		}
		GLUpdateStationListing.setDistanceMove(meters);
	}

	public static void setTimeInSeconds(int timeInSeconds) {
		GLSettings.timeInSeconds = timeInSeconds;
		final int seconds;
		if (GLSettings.timeInSeconds == 8) {
			seconds = 450000;
		} else if (GLSettings.timeInSeconds == 5) {
			seconds = 270000;
		} else if (GLSettings.timeInSeconds == 3) {
			seconds = 150000;
		} else if (GLSettings.timeInSeconds == 2) {
			seconds = 90000;
		} else {
			seconds = 60000;
		}

		if (seconds != GLUpdateStationListing.getInstance().getTimeOut()) {
			Thread refreshStations = new Thread(new Runnable() {
				public void run() {
					GLUpdateStationListing.getInstance().setTimeOut(seconds);
					GLUpdateStationListing.periodChange = true;
				}
			});
			refreshStations.start();
		}
	}

	/**
	 * Adds a property
	 * 
	 * @param key
	 * @param value
	 */
	public static void addProperty(String key, Object value) {
		if(hasProperty(key)){
			System.out.println("GL [II] Updating property: " + key + " = " + value);
			settingsHash.put(key, value);
			GLEvents.triggerEvent(GLApp.EVENT_PROPERTY_UPDATED);
		} else {
			System.out.println("GL [II] Adding property: " + key + " = " + value);
			settingsHash.put(key, value);
			GLEvents.triggerEvent(GLApp.EVENT_SETTING_ADDED);
		}
		
	}

	/**
	 * Returns a property as a string
	 * 
	 * @param key
	 * @return
	 */
	public static String getPropertyAsStr(String key) {
		Object value = getProperty(key);
		if (value != null) {
			return value.toString();
		}
		return null;
	}

	/**
	 * Retrieves a property as an object
	 * 
	 * @param key
	 * @return
	 */
	public static Object getProperty(String key) {
		if (hasProperty(key)) {
			return settingsHash.get(key);
		}
		return null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static boolean hasProperty(String key) {
		return settingsHash.containsKey(key);
	}

	public static boolean hasValue(Object value) {
		return settingsHash.contains(value);
	}

	public static void addDefaultSettings() {
		addProperty(URL_BASE, "http://demo.monagis.com/gaslo_staging/");
		addProperty(URL_STATIONS, "list/stations/");
		addProperty(URL_PRICES, "list/prices/");
		addProperty(URL_GASTYPES, "list/types/");
		addProperty(URL_GEODATA, "list/geodata/");
		addProperty(URL_SEARCH, "search/");
		addProperty(URL_ADS, "list/ads/");
		addProperty(URL_HELLO, "hello/");
//		addProperty(URL_STATIONS_PRICES, "list/stations_prices");

		Hashtable params = new Hashtable();
		params.put("search", "liguanea");
	}



	public static String[] getAllUrlParams(Hashtable params) {
		String[] defaultParams = GLUser.getDefaultURLParameters();

		String[] allParams = new String[defaultParams.length + params.size()];
		for (int i = 0; i < defaultParams.length; i++) {
			allParams[i] = defaultParams[i];
		}

		Enumeration keys = params.keys();
		int j = 0;
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			String value = params.get(key).toString();

			if (!(key == null || value == null)) {
				String paramStr = key + "=" + value;
				allParams[j + defaultParams.length] = paramStr;
			}
			j++;
		}
		
		return allParams;
	}
	
	
	/**
	 * Returns the url given by the url name
	 * 
	 * @param urlName
	 * @return
	 */
	public static String getUrlByName(String urlName) {
		return getUrlByName(urlName,new Hashtable());
	}

	public static String getUrlByName(String urlName, Hashtable params) {
		return getUrlByNameWithSuffix(urlName, "", params);
	}
	
	public static String getBasicUrlByName(String urlName){
		String suffixUrl = getPropertyAsStr(urlName);
		String url = getUrlWithBase(suffixUrl);
		return url;
	}
	
	public static String getUrlByNameWithSuffix(String urlName, String suffix, Hashtable params){
		String suffixUrl = getPropertyAsStr(urlName);
		String url = getUrlWithBase(suffixUrl);
		url += suffix;
		if (!url.endsWith("?")) {
			url += "?";
		}
		String [] allParams = getAllUrlParams(params);
		return url + join(allParams, "&");
	}

	/**
	 * Returns a full url by prepending the base url.
	 * 
	 * @param suffixUrl
	 * @return
	 */
	public static String getUrlWithBase(String suffixUrl) {
		String baseUrl = getPropertyAsStr(URL_BASE);
		return baseUrl + suffixUrl;
	}

	/**
	 * Returns true if the url is of a certain type (name). We need this since
	 * we will add parameters to url such as the device name and os and user
	 * hash. We still need to know for example if this url is the gas stations
	 * url for example.
	 * 
	 * @param url
	 * @param urlName
	 * @return
	 */
	public static boolean isUrl(String url, String urlName) {
		return url.indexOf(getUrlByName(urlName)) == 0;
	}

	public static String join(String[] arr, String delim) {
		String result = "";
		for (int i = 0; i < arr.length; i++) {
			result += arr[i];
			if (i < (arr.length - 1)) {
				result += delim;
			}
		}
		return result;
	}

}
