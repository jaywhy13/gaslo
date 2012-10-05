package glo.misc;

import glo.gps.GPSHandle;
import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;
import glo.net.GLNetworkAction;
import glo.net.GLNetworkNavigate;
import glo.sys.GLSettings;
import glo.ui.GLAdManager;
import glo.ui.GLApp;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.location.Location;

import net.rim.device.api.crypto.MD5Digest;
import net.rim.device.api.system.DeviceInfo;

public class GLUser {

	public static final String DEVICE_OS_KEY = "device_os";
	public static final String DEVICE_NAME_KEY = "device_name";

	private static String userHash = null;

	/**
	 * Returns an array of parameters to send to the server on each call
	 * 
	 * @return
	 */
	public static String[] getDefaultURLParameters() {
		Hashtable params = getDefaultURLParametersMap();
		String paramArr [] = new String [params.size()];
		int i = 0;
		Enumeration keys = params.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement().toString();
			String value = params.get(key).toString();
			paramArr[i] = key + "=" + value;
			i++;
		}
		
		return paramArr;
	}
	
	public static Hashtable getDefaultURLParametersMap(){
		Hashtable params = new Hashtable();
		if (GPSHandle.isValid()) {
			double lat = GPSHandle.getLatitude();
			double lon = GPSHandle.getLongitude();
			params.put("lat",new Double(lat));
			params.put("lon",new Double(lon));
		}
		
		params.put("dd", new Integer(GLSettings.getDistanceThreshold()));
		params.put("hash", getUserHash());
		params.put(DEVICE_NAME_KEY,getDeviceOS());
		params.put(DEVICE_NAME_KEY, getDeviceName());
		
		return params;
	}

	/**
	 * Retrieve the user hash
	 * 
	 * @return
	 */
	public static String getUserHash() {
		int pin = DeviceInfo.getDeviceId();
		if (userHash == null) {
			userHash = hash(String.valueOf(pin));
		}
		return userHash.substring(0, 15);
	}

	public static String getDeviceName() {
		return DeviceInfo.getDeviceName();
	}

	public static String getDeviceOS() {
		return DeviceInfo.getPlatformVersion();
	}

	/**
	 * Parses JSON Data to create settings properties
	 * 
	 * @param arr
	 *            - JSONArray with settings information
	 */
	public static void fromJSON(JSONArray arr) {
		try {
			JSONArray outer = arr;
			if (outer != null) {
				int outerLength = outer.length();
				for (int a = 0; a < outerLength; a++) {
					// Get the inner objects and Parse the data
					JSONObject inner = (JSONObject) outer.get(a);
					if (inner != null) {
						
						JSONArray ads = inner.getJSONArray("ads");
						if(ads != null){
							for(int ac = 0; ac < ads.length(); ac++){
								int adId = ads.getInt(ac);
								GLAdManager.getInstance().addAdId(adId);
							}
						}
						
						
						JSONObject urls = inner.getJSONObject("urls");
						String search_url = urls.getString("search");
						
						String ads_url = urls.getString("ads");
						String geo_data_url = urls.getString("geo_data");
						String base_url = urls.getString("base_url");
						String gas_stations_url = urls
								.getString("gas_stations");
						String prices_url = urls.getString("prices");
						String gas_types_url = urls.getString("gas_types");
						String serverUrl = GLSettings
								.getUrlByName(GLSettings.URL_HELLO);
						String stations_and_prices_url = urls.getString("stations_prices");

						if (urls.getString("hello") != null) {
							serverUrl = urls.getString("hello");
						}
						
						if(stations_and_prices_url != null){
							GLSettings.addProperty(GLSettings.URL_STATIONS_PRICES, stations_and_prices_url);
						}
						
						GLSettings.addProperty(GLSettings.URL_SEARCH,
								search_url);
						GLSettings.addProperty(GLSettings.URL_ADS, ads_url);
						GLSettings.addProperty(GLSettings.URL_GEODATA,
								geo_data_url);
						GLSettings.addProperty(GLSettings.URL_BASE, base_url);
						GLSettings.addProperty(GLSettings.URL_STATIONS,
								gas_stations_url);
						GLSettings.addProperty(GLSettings.URL_PRICES,
								prices_url);
						GLSettings.addProperty(GLSettings.URL_GASTYPES,
								gas_types_url);
						GLSettings.addProperty(GLSettings.URL_HELLO, serverUrl);
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Produce the md5
	 * 
	 * @param data
	 * @return
	 */
	public static String hash(String data) {
		byte[] bytes;
		try {
			bytes = data.getBytes("UTF-8");
			MD5Digest digest = new MD5Digest();
			digest.update(bytes, 0, bytes.length);
			int length = digest.getDigestLength();
			byte[] md5 = new byte[length];
			digest.getDigest(md5, 0, true);
			return convertToHex(md5);
			// ... convert md5 to string.
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Will be used to send data to the server for polling purposes
	 * 
	 * @param url
	 * @param params
	 */
	public static void postData(final String url, Hashtable params, final GLNetworkAction action) {
		if (params == null) {
			params = new Hashtable();
		}

		final GLNetworkNavigate poller = new GLNetworkNavigate();

		if (poller.canConnect()) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					GLNetworkAction _action = action;
					if(_action == null){
						_action = new GLNetworkAction() {
							public void success(Object data, int status) {
								System.out.println("GL [II] Got the response: "
										+ data + " with code: " + status);
							}
							
							public void failure(Object data, int status) {
								// TODO Auto-generated method stub
								super.failure(data, status);
							}
						};
					}
					poller.get(url,_action);
				}
			});
			t.start();
		} else {
			System.out.println("GL [EE] Cannot connect to the net");
		}
	}
	
	public static void postData(final String url){
		postData(url,new Hashtable(),null);
	}
	
	public static void postData(final String url,GLNetworkAction action){
		postData(url,new Hashtable(),action);
	}

	
}
