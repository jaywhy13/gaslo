package glo.ui;

import java.io.InputStream;
import java.util.Vector;

import glo.json.JSONArray;
import glo.json.JSONException;
import glo.misc.GLPrequisiteListener;
import glo.misc.GLPrerequisite;
import glo.misc.GLPrerequisites;
import glo.misc.GLUser;
import glo.net.ServerDataManager;
import glo.sys.GLEvents;
import glo.sys.GLSettings;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

public class GLApp extends UiApplication {

	public static final String GPS_SETUP = "gpsSetup";
	public static final String GPS_FIX = "gpsFix";
	public static final String DATA_DOWNLOAD = "dataDownload";
	public static final String READ_DOWNLOADED_DATA = "readDownloadedData";
	public static final String POPULATE_DB = "populateDb";
	public static final String SCHEMA_REGISTRATION = "schemaRegistration";
	public static final String CUSTOM_FONT_LOAD = "loadCustomFonts";

	public static final String SAY_HELLO = "sayHello";
	
	public static final String FONT_MYRIADPRO_SRC = "/glo/fonts/MYRIADAT.TTF";
	public static final String FONT_MYRIADPRO = "Myriad Apple";

	// system events
	public static final String EVENT_GPS_POSITION_UPDATED = "newPoint";
	public static final String EVENT_GAS_STATIONS_POSITIONS_UPDATED = "locationsUpdated";
	public static final String EVENT_GAS_STATIONS_FETCHED = "stationsFetched";
	public static final String EVENT_SETTING_ADDED = "settingAdded";
	public static final String EVENT_PROPERTY_UPDATED = "settingUpdated";
	public static final String EVENT_SORT_TYPE_CHANGED = "sortTypeChanged";
	public static final String EVENT_GAS_STATIONS_SORTED = "stationsSorted";
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! REMEMBER TO ADD YOUR EVENT TO addEvents method
	
	/**
	 * Static instance of the application, initialized in the main method
	 */
	public static GLApp app;

	/**
	 * Makes all connections to the server through this class
	 */
	public static final ServerDataManager dataManager = new ServerDataManager();
	
	public static void loadCustomFonts() {
		GLPrerequisites.prerequisiteStarted(GLApp.CUSTOM_FONT_LOAD);
		InputStream stream = app.getClass().getResourceAsStream(
				FONT_MYRIADPRO_SRC);
		int result = FontManager.getInstance().load(stream, FONT_MYRIADPRO,
				FontManager.APPLICATION_FONT);

		if (result == FontManager.SUCCESS) {
			GLPrerequisites.prerequisiteComplete(GLApp.CUSTOM_FONT_LOAD);
		} else {
			switch (result) {
			case FontManager.DUPLICATE_DATA:
			case FontManager.DUPLICATE_NAME:
				System.out.println("GL [II] Font already exists");
				GLPrerequisites.prerequisiteComplete(CUSTOM_FONT_LOAD);
				break;
			case FontManager.EXCEEDS_LIMIT:
				System.out.println("GL [EE] Font exceeds limit");
				break;
			case FontManager.FAILED_TO_LOAD_FILE:
				System.out.println("GL [EE] Failed to load font file");
				break;
			case FontManager.NO_FONT_DATA:
				System.out.println("GL [EE] Missing font data");
				break;
			case FontManager.NO_PERMISSION:
				System.out.println("GL [EE] No permissions");
				break;
			case FontManager.READ_FAIL:
				System.out.println("GL [EE] Failed to read the data");
				break;
			}

		}
	}

	/**
	 * Request permissions needed for this application to run
	 */
	public static void requestPermissions() {
		ApplicationPermissions requestedPermissions = new ApplicationPermissions();
		ApplicationPermissionsManager appPermMgr = ApplicationPermissionsManager
				.getInstance();

		// list of permissions needed
		Vector permissionsNeeded = new Vector();
		permissionsNeeded.addElement(new Integer(
				ApplicationPermissions.PERMISSION_LOCATION_DATA)); // location
		// data
		permissionsNeeded.addElement(new Integer(
				ApplicationPermissions.PERMISSION_INTERNET)); // Internet
		// transmissions
		permissionsNeeded.addElement(new Integer(
				ApplicationPermissions.PERMISSION_FILE_API)); // Internet
																// transmissions
		permissionsNeeded.addElement(new Integer(ApplicationPermissions.PERMISSION_SERVER_NETWORK));
		
		permissionsNeeded.addElement(new Integer(ApplicationPermissions.PERMISSION_WIFI));
		
		
		// check which permissions we do not have a formulate a request ...
		for (int i = 0; i < permissionsNeeded.size(); i++) {
			int permission = ((Integer) permissionsNeeded.elementAt(i))
					.intValue();
			if (appPermMgr.getPermission(permission) != ApplicationPermissions.VALUE_ALLOW) {
				requestedPermissions.addPermission(permission);
			}
		}

		if (requestedPermissions.getPermissionKeys().length > 0) { // we have
			// permissions
			// to
			// request
			boolean wasGranted = appPermMgr
					.invokePermissionsRequest(requestedPermissions);
			if (!wasGranted) {
				// Dialog.alert("GasLo needs to connect to the internet to retrieve gas information and to provide prices based on your location. Please provide us with the proper permissions.");
				System.exit(-1);
			}
		}
	}

	/**
	 * The entry point for the application, creates a new app, adds the main
	 * screen to it and then enters the event dispatcher
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// get permissions
		requestPermissions();
		GLSettings.addDefaultSettings();

		addEvents();
		app = new GLApp();
		 GLSplashScreen splash = GLSplashScreen.makeSplash(app, null);
		 GLPrerequisites.addPrerequisiteListener(splash);
		 addPrerequisites();
		 loadCustomFonts();
		 GLMainScreen glScrn = new GLMainScreen();
		 splash.setNext(glScrn);
		app.enterEventDispatcher();
	}

	private static void addEvents() {
		GLEvents.addEvent(EVENT_GPS_POSITION_UPDATED);
		GLEvents.addEvent(EVENT_GAS_STATIONS_POSITIONS_UPDATED);
		GLEvents.addEvent(EVENT_GAS_STATIONS_FETCHED);
		GLEvents.addEventListener(EVENT_GPS_POSITION_UPDATED, dataManager);
		GLEvents.addEvent(EVENT_SORT_TYPE_CHANGED);
		GLEvents.addEvent(EVENT_PROPERTY_UPDATED);
		GLEvents.addEvent(EVENT_GAS_STATIONS_SORTED);
	}

	private static void addPrerequisites() {
		// gps setup
		GLPrerequisite gpsSetup = new GLPrerequisite(GPS_SETUP, 10000);
		gpsSetup.setStartupMessage("Setting up GPS");
		gpsSetup.setCompleteMessage("GPS Ready");

		// gps fix
		GLPrerequisite gpsFix = new GLPrerequisite(GPS_FIX, 15000);
		gpsFix.setStartupMessage("Obtaining user location");
		gpsFix.setCompleteMessage("Obtained user location");
		gpsFix.setProceedOnFailure(true);
		gpsFix.setProceedOnTimeout(true);

		// data download
		GLPrerequisite dataDownload = new GLPrerequisite(DATA_DOWNLOAD, 120000) {
			public void failed() {
				GLSplashScreen.setMessage("Unable to connect to the net", 3000);
			}
		};
		dataDownload.setStartupMessage("Downloading data");
		dataDownload.setCompleteMessage("Data downloaded");
		dataDownload.setProceedOnTimeout(false);
		dataDownload.setProceedOnFailure(false);

		// read downloaded data
		GLPrerequisite readDownloadedData = new GLPrerequisite(
				READ_DOWNLOADED_DATA, 120000);
		readDownloadedData.setStartupMessage("Reading downloaded data");
		readDownloadedData.setCompleteMessage("Done reading downloaded data");

		// schema registration
		GLPrerequisite schemaRegistration = new GLPrerequisite(
				SCHEMA_REGISTRATION, 30000);
		schemaRegistration.setStartupMessage("Registering schema");
		schemaRegistration.setCompleteMessage("Finished registering schema");

		// font loading
		GLPrerequisite fontLoad = new GLPrerequisite(CUSTOM_FONT_LOAD, 400);
		fontLoad.setStartupMessage("Loading fonts");
		fontLoad.setCompleteMessage("Completed loading fonts");
		fontLoad.setProceedOnTimeout(true);
		
		// saying hello
		GLPrerequisite sayHello = new GLPrerequisite(SAY_HELLO, 3000){
			public void failed() {
				GLSplashScreen.setMessage("Unable to reach the GasLo server", 3000);
			}
		};
		sayHello.setStartupMessage("Contacting GasLo server");
		sayHello.setCompleteMessage("Server contacted");
		sayHello.setProceedOnFailure(false);
		sayHello.setProceedOnTimeout(false);
		

		// add all
		GLPrerequisites.addPrerequisite(gpsSetup);
//		GLPrerequisites.addPrerequisite(gpsFix);
//		GLPrerequisites.addPrerequisite(dataDownload);
//		GLPrerequisites.addPrerequisite(readDownloadedData);
//		GLPrerequisites.addPrerequisite(schemaRegistration);
		GLPrerequisites.addPrerequisite(fontLoad);
		GLPrerequisites.addPrerequisite(sayHello);

	}

	/**
	 * Adds a screen to the display
	 * 
	 * @param screen
	 */
	public static final void addScreen(final MainScreen screen) {
		if (screen == null) {
			Dialog.alert("Null screen man");
			return;
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				synchronized (Application.getEventLock()) {
					app.pushScreen(screen);
				}
			}
		});
		t.start();
	}
}
