package glo.types;

import java.util.Vector;

import glo.json.JSONArray;
import glo.json.JSONException;
import glo.json.JSONObject;

/**
 * This class hold the geographic data of each gas station. The objects created are 
 * attached to each gas station according to id matches and are used by the gas sation
 * to provide necessary geographic data.
 * @author rahibbert
 *
 */
public class GasGeoData {

	/**
	 * Gas Station Geographic Data id
	 */
	private int GSgeoID;
	
	/**
	 * Gas Station city
	 */
	private String city = null;
	
	/**
	 * Gas Station parish
	 */
	private String parish = null; // Region
	
	/**
	 * Gas Station Address
	 */
	private String address = null;

	/**
	 * Gas Station longitude and latitude
	 */
	private double longitude, latitude; 
	
	public static Vector lastAddedGeoData = new Vector();
	/**
	 * Constructor function to create gas station geographic data objects with
	 * the id, latitude and longitude of the gas station
	 * @param geoID - ID of the gas station geographic data
	 * @param longitude - Latitude of the gas station
	 * @param latitude - Longitude of the gas station
	 */
	public GasGeoData(int geoID, double longitude, double latitude){
		this.GSgeoID = geoID;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * Returns the gas station geographic data ID
	 * @return int
	 */
	public int getGeoID() {
		return GSgeoID;
	}

	/**
	 * Sets the gas station geographic data ID
	 * @param geoID - The gas station geographic data ID
	 */
	public void setGeoID(int geoID) {
		this.GSgeoID = geoID;
	}

	/**
	 * The city of the gas station
	 * @return String
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Returns the city of the gas station
	 * @param city - The city of the gas station
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Returns the parish of the gas station
	 * @return String 
	 */
	public String getParish() {
		return parish;
	}

	/**
	 * Sets the parish of the gas station
	 * @param parish - The parish of the gas station
	 */
	public void setParish(String parish) {
		this.parish = parish;
	}

	/**
	 * Returns the longitude of the gas station
	 * @return double
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude of the gas station
	 * @param longitude - The longitude of the gas station
	 */
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

	/**
	 * Returns the latitude of the gas station
	 * @return double
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude of the gas station
	 * @param latitude - The latitude of the gas station
	 */
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Parses JSON Data to create GasGeoData Objects and add the necessary
	 * additional information to the objects. All objects are placed in a
	 * static array.
	 * @param arr - JSONArray with gas station geographic data
	 */
	public static void fromJSON(JSONArray arr) {
		System.out.println("GL [II] Parsing JSON gas station geo data");
		try{
			JSONArray outer = arr;	
			if(outer != null) {
				int outerLength = outer.length();
				lastAddedGeoData.removeAllElements();
				for(int a=0; a < outerLength; a++){
					// Get the inner objects and Parse the data
					JSONObject inner = (JSONObject) outer.get(a);
					if(inner != null){
						int geoID = inner.getInt("pk");
						JSONObject geoDatails = inner.getJSONObject("fields");
						String city = geoDatails.getString("city");
						String parish = geoDatails.getString("region");
						double longitude = geoDatails.getDouble("long");
						double latitude = geoDatails.getDouble("lat");
						
						GasGeoData currentGGD = new GasGeoData(geoID, longitude, latitude);
						
						if(city != null | city != ""){
							currentGGD.setCity(city);
						}
						
						if(parish != null | parish != ""){
							currentGGD.setParish(parish);
						}
						
						GasDataManager.getInstance().addGasGeoData(currentGGD);
//						GasDataManager.getInstance().addGasGeoDataWithIndex(currentGGD, geoID);
						lastAddedGeoData.addElement(currentGGD);
					}
				}
				// Used to minimize storage of vector
				GasDataManager.getInstance().getGasGeoData().trimToSize();
			}
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "Latitude: " + latitude + " Longitude: " + longitude + " Parish: " + parish;
	}
		
}
