package glo.gps;

import java.util.TimerTask;
import java.util.Vector;

import glo.sys.GLEvents;
import glo.sys.GLSettings;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.types.GasGeoData;
import glo.ui.GLApp;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.MathUtilities;

// MGI - Latitude, Longitude: 18.00297841340035, -76.75005912780762

/**
 * This class updates the list of gas stations based on your current GPS
 * location at intervals. It orders the list from closest to farthest gas
 * stations.
 * 
 * @author rahibbert
 */

public class UpdateGPSLocations extends TimerTask {

	/**
	 * 
	 */
	private static double EARTH_CIRC_METERS = 6371007; // 40030218 Radius =
														// 6371007 (GRS80)

	private static GasCompany[] GCArray;

	/**
	 * 
	 */
	public void run() {
		Vector gasStations = GasDataManager.getInstance().getGasStations();
		int gasStationLength = gasStations.size();
		if (gasStationLength > 1) {
			int currentSortMode = GLSettings.getSortMode();
			if ((currentSortMode == GLSettings.SORT_BY_CLOSEST)) {
				System.out.println("GL [EE] Sorting by distance");
				GCArray = new GasCompany[gasStationLength];
				gasStations.copyInto(GCArray);
				sortStationsByDistance(GCArray);
				gasStations.removeAllElements();
				arrayToVector(gasStations, GCArray);
				System.out.println("GL [II] Completed sorting by distance");
				GLEvents.triggerEvent(GLApp.EVENT_GAS_STATIONS_SORTED);
			} else if ((currentSortMode == GLSettings.SORT_BY_LOWEST)) {
				System.out.println("GL [II] Sorting by prices");
				GCArray = new GasCompany[gasStationLength];
				gasStations.copyInto(GCArray);
				sortStationsByPrice(GCArray);
				gasStations.removeAllElements();
				arrayToVector(gasStations, GCArray);
				System.out.println("GL [II] Completed sorting by prices");
				GLEvents.triggerEvent(GLApp.EVENT_GAS_STATIONS_SORTED);
			} else if (currentSortMode == GLSettings.SORT_BY_BOTH) {
				System.out.println("GL [II] Sorting by prices and distance");
				GCArray = new GasCompany[gasStationLength];
				gasStations.copyInto(GCArray);
				sortStationsByBoth(GCArray);
				gasStations.removeAllElements();
				arrayToVector(gasStations, GCArray);
				System.out.println("GL [II] Completed sorting by prices and distance");
				GLEvents.triggerEvent(GLApp.EVENT_GAS_STATIONS_SORTED);
			} 
			
		}
	}

	public static void sortStationsByBoth(GasCompany[] stations) {
		Arrays.sort(stations, new Comparator() {
			public int compare(Object o1, Object o2) {
				GasCompany g1 = (GasCompany) o1;
				GasCompany g2 = (GasCompany) o2;
				
				if(g1 == null){
					return -1;
				}
				
				if(g2 == null){
					return 1;
				}

				if (!GPSHandle.isGPSSupported()) {
					// System.out.println("GL [EE] No GPS Available, unable to sort");
					return 0;
				}

				// Get the users position
				double userLat = GPSHandle.getLatitude();
				double userLon = GPSHandle.getLongitude();

				// Get position of two gas stations ...
				GasGeoData g1Location = g1.getLocation();
				GasGeoData g2Location = g2.getLocation();
			

				double g1Distance = 0;
				double g2Distance = 0;

				if (g1.getDistanceFromUser() == -1) {
					g1Distance = distance(userLat, userLon,
							g1Location.getLatitude(), g1Location.getLongitude());
					g1.setDistanceFromUser(g1Distance);
				} else {
					g1Distance = g1.getDistanceFromUser();
				}

				if (g2.getDistanceFromUser() == -1) {
					g2Distance = distance(userLat, userLon,
							g2Location.getLatitude(), g2Location.getLongitude());
					g2.setDistanceFromUser(g2Distance);
				} else {
					g2Distance = g2.getDistanceFromUser();
				}

				double g1Dist = Math.abs(g1Distance);
				double g2Dist = Math.abs(g2Distance);

				double g1AvgPrice = 0;
				double g2AvgPrice = 0;

				if (g1.gasPricesAllAdded()) {
					g1AvgPrice = g1.getAveragePrices();
				} 

				if (g2.gasPricesAllAdded()) {
					g2AvgPrice = g2.getAveragePrices();
				} 

				double sum1 = g1AvgPrice + g1Dist / GLSettings.getDistanceThreshold();
				double sum2 = g2AvgPrice + g2Dist / GLSettings.getDistanceThreshold();

				if (sum1 > sum2) {
					// System.out.println("GL [EE] Gas Station "+g2.getCompanyName()+" has lower prices than "+g1.getCompanyName());
					return 1;
				} else if (sum1 == sum2) {
					// System.out.println("GL [EE] Gas Station "+g2.getCompanyName()+" has the same price as "+g1.getCompanyName());
					return 0;
				} else {
					// System.out.println("GL [EE] Gas Station "+g1.getCompanyName()+" has lower prices than "+g2.getCompanyName());
					return -1;
				}
			}
		});
	}

	public static void sortStationsByPrice(GasCompany[] stations) {
		Arrays.sort(stations, new Comparator() {
			public int compare(Object o1, Object o2) {
				GasCompany g1 = (GasCompany) o1;
				GasCompany g2 = (GasCompany) o2;

				// Gas Station Prices
				double g1AvgPrice = 0;
				if (g1.gasPricesAllAdded()) {
					g1AvgPrice = g1.getAveragePrices();
				} else {
					return 1;
				}

				double g2AvgPrice = 0;
				if (g2.gasPricesAllAdded()) {
					g2AvgPrice = g2.getAveragePrices();
				} else {
					return -1;
				}

				if (g1AvgPrice > g2AvgPrice) {
					return 1;
				} else if (g1AvgPrice == g2AvgPrice) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}

	public static void sortStationsByDistance(GasCompany[] stations) {
		Arrays.sort(stations, new Comparator() {
			public int compare(Object o1, Object o2) {
				GasCompany g1 = (GasCompany) o1;
				GasCompany g2 = (GasCompany) o2;

				if (!GPSHandle.isGPSSupported()) {
					return 0;
				}

				// Get the users position
				double userLat = GPSHandle.getLatitude();
				double userLon = GPSHandle.getLongitude();

				// Get position of two gas stations ...
				GasGeoData g1Location = g1.getLocation();
				if (g1Location.getLatitude() == 0.0
						&& g1Location.getLongitude() == 0.0) {
					return 1;
				}
				GasGeoData g2Location = g2.getLocation();
				if (g2Location.getLatitude() == 0.0
						&& g2Location.getLongitude() == 0.0) {
					return -1;
				}

				double g1Distance = 0;
				double g2Distance = 0;

				if (g1.getDistanceFromUser() == -1) {
					g1Distance = distance(userLat, userLon,
							g1Location.getLatitude(), g1Location.getLongitude());
					g1.setDistanceFromUser(g1Distance);
				} else {
					g1Distance = g1.getDistanceFromUser();
				}

				if (g2.getDistanceFromUser() == -1) {
					g2Distance = distance(userLat, userLon,
							g2Location.getLatitude(), g2Location.getLongitude());
					g2.setDistanceFromUser(g2Distance);
				} else {
					g2Distance = g2.getDistanceFromUser();
				}

				double g1Dist = roundUp(Math.abs(g1Distance) / 1000);
				double g2Dist = roundUp(Math.abs(g2Distance) / 1000);

				if (g1Dist > g2Dist) {
					return 1;
				} else if (g1Dist == g2Dist) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * Description -
	 * 
	 * @param lat1
	 *            - Latitude of origin point in decimal degrees
	 * @param lon1
	 *            - longitude of origin point in decimal degrees
	 * @param lat2
	 *            - latitude of destination point in decimal degrees
	 * @param lon2
	 *            - longitude of destination point in decimal degrees
	 * 
	 * @return metricDistance - great circle distance in meters
	 */

	public static double distance1(double lat1, double lon1, double lat2,
			double lon2) {

		double radLat1 = Math.toRadians(lat1);
		double radLon1 = Math.toRadians(lon1);
		double radLat2 = Math.toRadians(lat2);
		double radLon2 = Math.toRadians(lon2);

		double d = MathUtilities.acos((Math.cos(radLat1) * Math.cos(radLat2))
				+ (Math.sin(radLat1) * Math.sin(radLat2))
				* (Math.cos(radLon1 - radLon2)));
		return (d * EARTH_CIRC_METERS);
	}

	// CONSTANTS USED INTERNALLY
	static final double DEGREES_TO_RADIANS = (Math.PI / 180.0);

	// Mean radius in KM
	static final double EARTH_RADIUS = 6371.0;

	/**
	 * Method to compute Great Circle distance between two points. Please note
	 * that this algorithm assumes the Earth to be a perfect sphere, whereas in
	 * fact the equatorial radius is about 30Km greater than the Polar.
	 * 
	 * @param alt
	 *            other point to compute distance to
	 * @return The distance in Kilometres
	 */

	public static double distance2(double lat1, double lon1, double lat2,
			double lon2) {

		// There is no real reason to break this lot into
		// 4 statements but I just feel it's a little more
		// readable.
		double p1 = Math.cos(lat1) * Math.cos(lon1) * Math.cos(lat2)
				* Math.cos(lon2);
		double p2 = Math.cos(lat1) * Math.sin(lon1) * Math.cos(lat2)
				* Math.sin(lon2);
		double p3 = Math.sin(lat1) * Math.sin(lat2);

		return (MathUtilities.acos(p1 + p2 + p3) * EARTH_RADIUS);

	}

	public static double distance3(double lat1, double long1, double lat2,
			double long2) {
		return 6378.1 * MathUtilities.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - long1));
	}

	/**
	 * Description -
	 * 
	 * @param x
	 * @return
	 */
	public static int roundUp(double x) {
		if (x < 0) {
			return 0;
		}

		int m = (int) (x / 10) + 1;
		int rounded = m * 10;
		return rounded;
	}

	/**
	 * 
	 * @param vec
	 * @param arr
	 */
	public static void arrayToVector(Vector vec, GasCompany[] arr) {
		for (int a = 0; a < arr.length; a++) {
			vec.addElement(arr[a]);
		}
	}
	
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::                                                                         :*/
	/*::  This routine calculates the distance between two points (given the     :*/
	/*::  latitude/longitude of those points). It is being used to calculate     :*/
	/*::  the distance between two ZIP Codes or Postal Codes using our           :*/
	/*::  ZIPCodeWorld(TM) and PostalCodeWorld(TM) products.                     :*/
	/*::                                                                         :*/
	/*::  Definitions:                                                           :*/
	/*::    South latitudes are negative, east longitudes are positive           :*/
	/*::                                                                         :*/
	/*::  Passed to function:                                                    :*/
	/*::    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  :*/
	/*::    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  :*/
	/*::    unit = the unit you desire for results                               :*/
	/*::           where: 'M' is statute miles                                   :*/
	/*::                  'K' is kilometers (default)                            :*/
	/*::                  'N' is nautical miles                                  :*/
	/*::  United States ZIP Code/ Canadian Postal Code databases with latitude & :*/
	/*::  longitude are available at http://www.zipcodeworld.com                 :*/
	/*::                                                                         :*/
	/*::  For enquiries, please contact sales@zipcodeworld.com                   :*/
	/*::                                                                         :*/
	/*::  Official Web site: http://www.zipcodeworld.com                         :*/
	/*::                                                                         :*/
	/*::  Hexa Software Development Center © All Rights Reserved 2004            :*/
	/*::                                                                         :*/
	/*::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/

	public static double distance(double lat1, double lon1, double lat2, double lon2) {
	  double theta = lon1 - lon2;
	  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//	  dist = Math.acos(dist);
	  dist = MathUtilities.acos(dist);
	  dist = rad2deg(dist);
	  dist = dist * 60 * 1.1515;
	  dist = dist * 1.609344;
	  return (dist*1000);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
	  return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
	  return (rad * 180.0 / Math.PI);
	}

}
