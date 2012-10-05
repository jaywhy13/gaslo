package glo.sys;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import net.rim.device.api.gps.*;

public class GPSMonitor {
	private static Location location;
	
	public static void updatePos(){
		if(GPSInfo.isGPSModeAvailable(GPSInfo.GPS_DEVICE_INTERNAL) || GPSInfo.isGPSModeAvailable(GPSInfo.GPS_DEVICE_BLUETOOTH)){
			
		}
		
		// Setup the criteria
		Criteria myCriteria = new Criteria();
		
		// JSR 179 - Get a provider
		try {
			LocationProvider provider = LocationProvider.getInstance(myCriteria);
			
		} catch (LocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
