package glo.gps;

import glo.misc.GLPrerequisites;
import glo.sys.GLEvents;
import glo.ui.GLApp;
import glo.ui.GLSplashScreen;
import glo.ui.GLUtils;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.ui.component.Dialog;

/**
 * This class manages the GPS point changes for the application. Call
 * setupGPSListener to begin listening in for changes in location.
 * 
 * 
 * Sample points: lat,lon 18.00281515641136, -76.74962997436523 - mgi
 * 18.01824227384845, -76.75658226013184 - jc
 * 
 * @author JMWright
 * 
 */
public class GPSHandle implements LocationListener {

	/**
	 * Private constructor, use "getHandle" to get the instance of this class.
	 */
	private GPSHandle() {
	}

	private boolean supported = false;

	/**
	 * Private gps handle .. one for application
	 */
	private static final GPSHandle gpsHandle = new GPSHandle();

	protected static final String ERROR_NO_GPS_AVAILABLE = "No GPS is available";

	/**
	 * Returns an instance to this class
	 * 
	 * @return
	 */
	public static GPSHandle getHandle() {
		return gpsHandle;
	}

	protected BlackBerryLocationProvider provider = null;

	protected BlackBerryCriteria criteria = null;

	/**
	 * The current location
	 */
	protected Location currentLocation = null;

	/**
	 * Gets the last Location received
	 * 
	 * @return
	 */
	public static Location getLocation() {
		return gpsHandle.currentLocation;
	}

	// /**
	// * Asks for an immediate update of user location
	// */
	// public static void updateLatLong() {
	// try {
	// Location l = gpsHandle.provider.getLocation(-1);
	// if (l != null) {
	// gpsHandle.currentLocation = l;
	// if (gpsHandle.setLocation(l)) {
	// double lat = gpsHandle.currentLocation
	// .getQualifiedCoordinates().getLatitude();
	// double lon = gpsHandle.currentLocation
	// .getQualifiedCoordinates().getLongitude();
	// System.out.println("GL [II] Updated GPS Coord: Lat=" + lat
	// + ", Lon=" + lon);
	// }
	// } else {
	// System.out
	// .println("GL [EE] Null location received from setLatLong, exiting...");
	// return;
	// }
	// } catch (LocationException e) {
	// System.out.println("GL [EE] Error setting lat long: "
	// + e.getMessage());
	// e.printStackTrace();
	// } catch (InterruptedException e) {
	// System.out.println("GL [EE] Error setting lat long: "
	// + e.getMessage());
	// e.printStackTrace();
	// }
	//
	// }

	public static boolean isValid(double lat, double lon) {
		return lat >= 17 && lat <= 19 && lon > -79 && lon < -75;
	}

	/**
	 * Updates the location variable stored internally
	 * 
	 * @param Location
	 *            l - the location
	 */
	protected boolean setLocation(Location l) {
		if (l == null) {
			return false;
		}

		// we might not be able to get qualified coords
		if (l.getQualifiedCoordinates() == null) {
			return false;
		}
		
		double lat = l.getQualifiedCoordinates().getLatitude();
		double lon = l.getQualifiedCoordinates().getLongitude();

		if (isValid(lat, lon)) {
			System.out.println("GL [II] New location received: " + lat + ","
					+ lon);

			this.currentLocation = l;
			if (!GLPrerequisites.isPrerequisiteComplete(GLApp.GPS_FIX)) {
				GLPrerequisites.prerequisiteComplete(GLApp.GPS_FIX);
			}
			// Updates the coordinates and recalculate the distances if
			// necessary
			GLLocationUpdater.updateCoordinates(l);
			GLEvents.triggerEvent(GLApp.EVENT_GPS_POSITION_UPDATED);
			return true;

		} else {
			System.out.println("GL [EE] Set location discarded invalid point: "
					+ lat + ", " + lon);
			return false;
		}
	}

	private static void setupCriteria(int gpsMode, BlackBerryCriteria criteria) {
		switch (gpsMode) {
		case GPSInfo.GPS_MODE_CELLSITE:
			criteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
			criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
			criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
			criteria.setCostAllowed(true);
			criteria.setFailoverMode(GPSInfo.GPS_MODE_ASSIST, 3, 5000);
			criteria.setSubsequentMode(GPSInfo.GPS_MODE_AUTONOMOUS);
			break;
		case GPSInfo.GPS_MODE_ASSIST:
			criteria.setHorizontalAccuracy(100);
			criteria.setVerticalAccuracy(100);
			criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM);
			criteria.setCostAllowed(true);
			criteria.setFailoverMode(GPSInfo.GPS_MODE_AUTONOMOUS, 5, 10000);
			criteria.setSubsequentMode(GPSInfo.GPS_MODE_AUTONOMOUS);
			break;

		case GPSInfo.GPS_MODE_AUTONOMOUS:
			gpsHandle.setSupported(true);
			criteria.setHorizontalAccuracy(0);
			criteria.setVerticalAccuracy(0);
			criteria.setPreferredPowerConsumption(0);
			criteria.setCostAllowed(false);
			criteria.setFailoverMode(GPSInfo.GPS_MODE_ASSIST, 5, 60000);
			break;
		}

	}

	/**
	 * Starts a thread that registers a listener for GPS position updates
	 */
	public static void setupGPSListener() {
		int gpsMode = -1;
		gpsHandle.criteria = new BlackBerryCriteria();
		if (GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_CELLSITE)) {
			System.out.println("GL [II] GPS: Using cell site mode");
			GLSplashScreen.setMessage("Using cell site", 2000);
			gpsMode = GPSInfo.GPS_MODE_CELLSITE;
		} else if (GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_ASSIST)) {
			System.out.println("GL [II] GPS: Using assisted mode");
			gpsMode = GPSInfo.GPS_MODE_ASSIST;
			GLSplashScreen.setMessage("Using assisted mode", 2000);

		} else if (GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_AUTONOMOUS)) {
			GLSplashScreen.setMessage("Using internal device", 2000);
			System.out.println("GL [II] GPS: Using internal device");
		}

		if (gpsMode != -1) {
			gpsHandle.setSupported(true);

			System.out.println("GL [II] Setting up GPS listener");
			GLPrerequisites.prerequisiteStarted(GLApp.GPS_SETUP);
			GLPrerequisites.prerequisiteStarted(GLApp.GPS_FIX);

			try {
				gpsHandle.criteria.setMode(gpsMode);
				setupCriteria(gpsMode, gpsHandle.criteria);
				gpsHandle.provider = (BlackBerryLocationProvider) LocationProvider
						.getInstance(gpsHandle.criteria);
				gpsHandle.provider.setLocationListener(gpsHandle, 10, -1, -1);
				System.out.println("GL [II] GPS Listener setup");
				GLPrerequisites.prerequisiteComplete(GLApp.GPS_SETUP);
			} catch (LocationException e) {
				GLPrerequisites.prerequisiteFailed(GLApp.GPS_SETUP);
				GLPrerequisites.prerequisiteFailed(GLApp.GPS_FIX);
				Dialog.alert(e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("GL [EE] No GPS support available");
			GLSplashScreen.setMessage("No GPS support available");
			gpsHandle.setSupported(false);
			// Dialog.alert(ERROR_NO_GPS_AVAILABLE);
			GLUtils.alert(ERROR_NO_GPS_AVAILABLE);
			GLPrerequisites.prerequisiteFailed(GLApp.GPS_SETUP);
			GLPrerequisites.prerequisiteFailed(GLApp.GPS_FIX);
			// System.exit(0);
		}
	}

	public static void setupGPSListener2() {
		if (GPSInfo.isGPSModeAvailable(GPSInfo.GPS_DEVICE_INTERNAL)
				|| GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_ASSIST)
				|| GPSInfo.isGPSModeAvailable(GPSInfo.GPS_MODE_AUTONOMOUS)) {
			System.out.println("GL [II] Setting up GPS listener");
			GLPrerequisites.prerequisiteStarted(GLApp.GPS_SETUP);
			GLPrerequisites.prerequisiteStarted(GLApp.GPS_FIX);
			gpsHandle.setSupported(true);
			gpsHandle.criteria = new BlackBerryCriteria();
			gpsHandle.criteria.setHorizontalAccuracy(0);
			gpsHandle.criteria.setVerticalAccuracy(0);
			gpsHandle.criteria.setPreferredPowerConsumption(0);

			try {
				gpsHandle.provider = (BlackBerryLocationProvider) LocationProvider
						.getInstance(gpsHandle.criteria);
				gpsHandle.provider.setLocationListener(gpsHandle, 10, -1, -1);
				// GPSHandle.updateLatLong();
				// GLLocationUpdater.origLat = GPSHandle.getLatitude();
				// GLLocationUpdater.origLong = GPSHandle.getLongitude();

				System.out.println("GL [II] GPS Listener setup");
				GLPrerequisites.prerequisiteComplete(GLApp.GPS_SETUP);
			} catch (LocationException e) {
				Dialog.alert(e.getMessage());
				e.printStackTrace();
				GLPrerequisites.prerequisiteFailed(GLApp.GPS_SETUP);

			}
		} else {
			System.out.println("GL [EE] No GPS support available");
			GLSplashScreen.setMessage("No GPS support available");
			gpsHandle.setSupported(false);
			Dialog.alert(ERROR_NO_GPS_AVAILABLE);
			System.exit(0);
		}
	}

	/**
	 * This method is called each time a new fix is received. Override to
	 * provide additional functionality. Retain the call to the super class as
	 * this updates the location variable stored internally.
	 */
	public void locationUpdated(LocationProvider provider, Location location) {
		if (location.isValid()) {
			setLocation(location);
		}
	}

	public void providerStateChanged(LocationProvider provider, int newState) {

	}

	public static boolean isGPSSupported() {
		return gpsHandle.isSupported();
	}

	protected boolean isSupported() {
		return supported;
	}

	/**
	 * Returns true if a current location is valid
	 * 
	 * @param l
	 * @return
	 */
	protected static boolean isValid(Location l) {
		if (l == null) {
			return false;
		}
		double lat = l.getQualifiedCoordinates().getLatitude();
		double lon = l.getQualifiedCoordinates().getLongitude();
		return isValid(lat, lon);
	}

	/**
	 * Returns true if the current point stored is valid
	 * 
	 * @return
	 */
	public static boolean isValid() {
		return isValid(GPSHandle.getLocation());
	}

	protected void setSupported(boolean supported) {
		this.supported = supported;
	}

	public static double getLatitude() {
		Location l = gpsHandle.currentLocation;
		if (l != null) {
			return l.getQualifiedCoordinates().getLatitude();
		}
		return -1;
	}

	public static double getLongitude() {
		Location l = gpsHandle.currentLocation;
		if (l != null) {
			return l.getQualifiedCoordinates().getLongitude();
		}
		return -1;
	}

	public static String getDistanceAsString(double distance) {
		distance = Math.ceil(distance);
		String distanceFromUser = "";
		String unit = "m";
		if (distance >= 1000) {
			unit = "km";
			distance /= 1000;

			if (distance > 500) {
				distanceFromUser = " >500 " + unit;
			} else {
				distanceFromUser = ((int) distance) + " " + unit;
			}
		}

		if (distance < 0 || distance > 500) {
			distanceFromUser = " ... ";
		}
		return distanceFromUser;
	}

}
