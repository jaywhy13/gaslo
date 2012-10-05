package glo.ui.screens;

import glo.gps.GPSHandle;
import glo.sys.GLEventListener;
import glo.sys.GLEvents;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.ui.AnimatedGIFField;
import glo.ui.GLApp;
import glo.ui.GLPriceInfo;
import glo.ui.GLPriceList;
import glo.ui.GLScreenShell;
import glo.ui.GLUtils;

import java.util.Vector;

import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class GLGasStationListingScreen extends GLScreenShell implements
		GLEventListener {

	public static String listType = "Gas Stations";
	protected Vector stationList = new Vector();
	protected static String message = "No Data Found";
	protected static int maxStationList = 200;
	protected LabelField notification = new LabelField("Waiting for GPS");
	protected GIFEncodedImage loadingAnim;
	protected AnimatedGIFField loadingGifField;
	protected int refreshThreshold = 10 * 1000; // number of seconds that must
												// be exceed for a refresh
	protected long lastRefresh = Integer.MAX_VALUE / 2;

	public int getRefreshThreshold() {
		return refreshThreshold;
	}

	public void setRefreshThreshold(int refreshThreshold) {
		this.refreshThreshold = refreshThreshold;
	}

	protected HorizontalFieldManager notificationHfm = new HorizontalFieldManager(
			Manager.USE_ALL_WIDTH) {
		public int getPreferredHeight() {
			return 20;
		};
	};

	protected GLPriceList priceUIList = new GLPriceList();

	public GLGasStationListingScreen() {
		super("Gas Stations", false);
		System.out.println("GL [II] Setting up gas station listing screen");
		GLGasStationListingScreen.listType = "Gas Stations";
		setupContentArea();
	}

	public GLGasStationListingScreen(String headerText) {
		super(headerText, false);
		GLGasStationListingScreen.listType = headerText;
		System.out.println("GL [II] Setting up gas station listing screen");
		setupContentArea();
	}

	/**
	 * Called in the constructor to modify the content region. Currently, this
	 * only sets up the background for the content area.
	 */
	protected void setupContentArea() {
		// setup animation
		loadingAnim = (GIFEncodedImage) GIFEncodedImage
				.getEncodedImageResource("loader.gif");
		loadingGifField = new AnimatedGIFField(loadingAnim, Field.FIELD_RIGHT);

		priceUIList = new GLPriceList();
		System.out
				.println("GL [II] Setting up content area for Gas station listing screen");
		bodyMgr.add(notificationHfm);
		bodyMgr.add((Field) priceUIList);
		bodyMgr.getVerticalScroll();
		GLEvents.addEventListener(GLApp.EVENT_GAS_STATIONS_POSITIONS_UPDATED,
				this);
		refresh();

	}

	public void updateScreen() {
		System.out.println("GL [II] Updating screen for gas station listing");
		Thread deleteAll = new Thread(new Runnable() {
			public void run() {
				refresh();
			}
		});

		GLUtils.runEventSafeThread(deleteAll);
	}

	protected boolean refreshNeeded() {
		long currentTime = System.currentTimeMillis();
		if (currentTime > lastRefresh + refreshThreshold) {
			return true;
		}
		return false;
	}

	protected void refresh() {
		updateNotification();
		if (GPSHandle.isValid()) {
			if (refreshNeeded()) {
				notificationHfm.deleteAll();
				priceUIList.deleteAll();
				addStations();
				lastRefresh = System.currentTimeMillis();
			}
		}
	}

	protected void updateNotification() {
		notificationHfm.deleteAll();
		if (!GPSHandle.isValid()) {
			notification.setText("Waiting on GPS");
			System.out.println("GL [II] Adding NO GPS warning");
			notificationHfm.add(notification);
			notificationHfm.add(loadingGifField);
		} else {
			if (stationList.size() == 0) {
				notification.setText("Loading ...");
				notificationHfm.add(notification);
				notificationHfm.add(loadingGifField);
			}
			System.out.println("GL [II] Removing NO GPS warning");

		}
	}

	protected void addStations() {

		if (GLGasStationListingScreen.listType == "Search Results") {
			stationList = GasDataManager.getInstance().getSearchStations();
		} else if (GLGasStationListingScreen.listType == "Favourites") {
			stationList = GasDataManager.getInstance().getFavouriteStations();
		} else {
			stationList = GasDataManager.getInstance().getGasStations();
		}

		boolean validPoint = GPSHandle.isValid();

		if (stationList.size() > 0 && validPoint) {
			int listNumber = 0;
			if (stationList.size() > GLGasStationListingScreen.maxStationList) {
				listNumber = GLGasStationListingScreen.maxStationList;
			} else if (stationList.size() == GLGasStationListingScreen.maxStationList) {
				listNumber = stationList.size();
			} else {
				listNumber = stationList.size();
			}
			System.out.println("GL [II] Will add " + listNumber + " stations");
			int stationsAdded = 0;
			for (int a = 0; a < stationList.size(); a++) {
				if (a < stationList.size()) {
					GasCompany currentStation = (GasCompany) stationList
							.elementAt(a);
					if (currentStation != null) {
						if (currentStation.getPrices().size() > 0) {
							GLPriceInfo glInfo = new GLPriceInfo(currentStation);
							priceUIList.add((Field) glInfo);
							stationsAdded++;
						} else {
							listNumber += 1;
						}
					}
				} else {
					a++;
				}

				if (stationsAdded > maxStationList)
					break;
			}
			System.out.println("GL [II] Done adding " + stationsAdded
					+ " stations");
		} else {
			System.out
					.println("GL [II] Cannot add any stations... point valid? "
							+ validPoint + " size: " + stationList.size());
			// GLCustomMessage glMess = new GLCustomMessage(
			// GLGasStationListingScreen.message);
			// priceUIList.add((Field) glMess);
		}
	}

	public void eventOccurred(String eventName, Object data) {
		System.out.println("GL [II] Events work!!!! Updating screen!!!!");
		if (eventName == GLApp.EVENT_GAS_STATIONS_POSITIONS_UPDATED || eventName == GLApp.EVENT_GAS_STATIONS_SORTED) {
			updateScreen();
		}
	}

}
