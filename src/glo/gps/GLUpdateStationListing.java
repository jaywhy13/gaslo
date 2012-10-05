package glo.gps;

import java.util.Timer;



public class GLUpdateStationListing {
	
	private static GLUpdateStationListing stationUpdater = new GLUpdateStationListing();
	private static int timeOut = 600000;
	private static int distanceMove = 500;
	private static Timer stationsUpdater = new Timer();
	public static boolean periodChange = false;
	
	private GLUpdateStationListing() {
	}
	
	public static GLUpdateStationListing getInstance() {
		return stationUpdater;
	}
	
	public void stopTimer() {
		stationsUpdater.cancel();
	}
	
	public void startTimer() {
		stationsUpdater.scheduleAtFixedRate(new UpdateGPSLocations(), 0, timeOut);
	}
	
	public int getTimeOut() {
		return timeOut;
	}
	
	public void addTimer(Timer time) {
		stationsUpdater = time;
	}

	public void setTimeOut(int timeOut) {
		GLUpdateStationListing.timeOut = timeOut;
	}

	public static int getDistanceMove() {
		return distanceMove;
	}

	public static void setDistanceMove(int distanceMove) {
		GLUpdateStationListing.distanceMove = distanceMove;
	}

}
