package glo.gps;

import glo.sys.GLEvents;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.types.GasGeoData;
import glo.ui.GLApp;

import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.location.Location;

public class GLLocationUpdater {

	public static double origLat = -1;
	public static double origLong = -1;
	public static double distanceRange = 200;

	public static void updateCoordinates(Location l) {
		if (l != null) {
			double lat = l.getQualifiedCoordinates().getLatitude();
			double lon = l.getQualifiedCoordinates().getLongitude();

			if (GPSHandle.isValid(l)) {
				if (origLat == -1 || origLong == -1) {
					origLat = lat;
					origLong = lon;
					updateDistancesFromUser(l);
				} else {
					double newDist = Math.abs(UpdateGPSLocations.distance(
							origLat, origLong, lat, lon));
					if (newDist >= distanceRange) {
						updateDistancesFromUser(l);
					}

				}

			}
		}
	}

	public static void updateDistancesFromUser(Location l) {
		System.out.println("GL [II] Checking users location range");
		Vector gasStations = GasDataManager.getInstance().getGasStations();
		int gasStationLength = gasStations.size();
		if (gasStationLength > 1) {
			if (origLat != -1 || origLong != -1) {
				double currentLat = l.getQualifiedCoordinates().getLatitude();
				double currentLon = l.getQualifiedCoordinates().getLongitude();

				for (int a = 0; a < gasStationLength; a++) {
					GasCompany currentCompany = (GasCompany) gasStations
							.elementAt(a);
					if (currentCompany != null) {
						GasGeoData gsLocation = currentCompany.getLocation();
						double companyLat = gsLocation.getLatitude();
						double companyLon = gsLocation.getLongitude();
						double distanceFromUserToStation = UpdateGPSLocations
								.distance(currentLat, currentLon, companyLat,
										companyLon);
						currentCompany
								.setDistanceFromUser(distanceFromUserToStation);
//						System.out.println("GL [II] Set distance "
//								+ distanceFromUserToStation
//								+ " to gas station: "
//								+ currentCompany.getCompanyName()
//								+ " essentially: " + companyLat + ", "
//								+ companyLon + " to user: " + currentLat + ", "
//								+ currentLon);
					}
				}

				origLat = currentLat;
				origLong = currentLon;

				// update the list of stations that the user will see
				UpdateGPSLocations updateStations = new UpdateGPSLocations();
				updateStations.run();
				GLEvents.triggerEvent(GLApp.EVENT_GAS_STATIONS_POSITIONS_UPDATED);

			} else {
				System.out
						.println("GL [EE] Invalid lat and long values, cannot refresh.");
			}
		} else {
			System.out.println("GL [II] No gas stations to refresh.");
		}
		
		
	}
}
