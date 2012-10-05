package glo.ui;

import glo.db.DBOperations;
import glo.db.DbManager;
import glo.db.SchemaManager;
import glo.db.SchemaManagerListener;
import glo.gps.GLLocationUpdater;
import glo.gps.GLUpdateStationListing;
import glo.gps.GPSHandle;
import glo.json.JSONException;
import glo.misc.GLPrerequisites;
import glo.misc.GLUser;
import glo.net.ServerDataManager;
import glo.schema.GLSchemaManager;
import glo.sys.GLSettings;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.types.GasGeoData;
import glo.types.GasPriceInfo;
import glo.types.GasType;
import glo.ui.screens.GLFavouriteGasStationListingScreen;
import glo.ui.screens.GLGasStationListingScreen;
import glo.ui.screens.GLGasStationSearchScreen;
import glo.ui.screens.GLSettingScreen;

import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.BorderFactory;

import com.rimextra.device.api.ui.container.JustifiedHorizontalFieldManager;
import com.rimextra.device.api.ui.container.JustifiedVerticalFieldManager;

public class GLMainScreen extends MainScreen implements FieldChangeListener {

	/**
	 * Main screen container
	 */
	private JustifiedVerticalFieldManager bodyMgr;

	private GLIconTray iconMgr = new GLIconTray();

	private VerticalFieldManager footerMgr = new VerticalFieldManager(
			Manager.USE_ALL_WIDTH);

	private JustifiedVerticalFieldManager jvfm;

	/**
	 * Fields for the page
	 */
	private GLBanner banner = new GLBanner();

	private FontifiedLabel caption;

	private GLAdManager adsVfm;

	
	Font captionFont;
	
	/**
	 * Backgrounds
	 */
	protected Bitmap bgIcon = Bitmap
			.getBitmapResource("glo/icons/striped_bg_20.png");
	Background stripedBg = BackgroundFactory.createBitmapBackground(bgIcon, 0,
			0, Background.REPEAT_BOTH);

	protected void paintBackground(Graphics graphics) {

		super.paintBackground(graphics);
	}

	public GLMainScreen() {
		super(Manager.USE_ALL_WIDTH | Manager.USE_ALL_HEIGHT
				| Manager.NO_VERTICAL_SCROLL | Manager.NO_HORIZONTAL_SCROLL);
		// Initiate GPS Data
		initiateGPSData();

		// setup the bg
		// this.getMainManager().setBackground(shadedBg);
		// this.getMainManager().setBackground(stripedBg);

		// setup the caption
		caption = new FontifiedLabel("Gas Prices", 20, 0x7e8b8c,
				Field.FIELD_HCENTER, Field.FIELD_HCENTER);
		// derive the font for the lbl
		try {
			captionFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.ITALIC, 20);
			System.out.println("GL [II] Myriad Pro font loaded successfully");
		} catch (ClassNotFoundException cnfe) {
			Font def = Font.getDefault();
			System.out
					.println("GL [EE] Myriad Pro could not be loaded, using default font");
			captionFont = def.derive(Font.ITALIC, def.getHeight());
		}
		caption.setFont(captionFont);
		caption.setFontColor(0x7e8b8c);
		caption.setPadding(new XYEdges(5, 0, 5, 0));
		JustifiedHorizontalFieldManager captionHfm = new JustifiedHorizontalFieldManager(
				null, caption, null, Manager.USE_ALL_WIDTH);
		// captionHfm.setBorder(BorderFactory.createSimpleBorder(new
		// XYEdges(1,1,1,1)));

		// Setup icon manger
		setupTrayManager();
		iconMgr.setStatus(caption);

		// Setup the central body jvfm - icon manager and caption
		bodyMgr = new JustifiedVerticalFieldManager(null, iconMgr, captionHfm);
		bodyMgr.setBackground(stripedBg);

		// Setup the ads section
		adsVfm = GLAdManager.getInstance();

		// Setup the main jvfm
		jvfm = new JustifiedVerticalFieldManager(banner, bodyMgr, adsVfm);
		add(jvfm);

		// Dialog.alert("Hello world");
		System.out
				.println("Going to display the splash screen now. Current screen count is: "
						+ UiApplication.getUiApplication().getScreenCount());
		sayHello();
		
		startupChecks();
		
//		 gasStationLocationUpdate();
//		GLUser.sayHello();
	}
	
	protected void sayHello(){
		try {
			GLApp.dataManager.getSettings();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds the icons to the tray manager
	 */
	private void setupTrayManager() {
		GLHoverIcon gasIcon = new GLHoverIcon("glo/icons/nearest_icon_off.png",
				"glo/icons/nearest_icon_on.png", "Nearest",
				"Find the nearest stations to you") {
			public GLScreenShell getContentArea() {
				return new GLGasStationListingScreen();
			}
		};

		GLHoverIcon searchIcon = new GLHoverIcon(
				"glo/icons/search_icon_off.png",
				"glo/icons/search_icon_on.png", "Search", "Search for stations") {
			public GLScreenShell getContentArea() {
				return new GLGasStationSearchScreen();
			}
		};

		GLHoverIcon favIcon = new GLHoverIcon(
				"glo/icons/favorites_icon_off.png",
				"glo/icons/favorites_icon_on.png", "Favourites",
				"Store and recall your favourites") {
			public GLScreenShell getContentArea() {
				return new GLFavouriteGasStationListingScreen();
			}
		};

		GLHoverIcon settingsIcon = new GLHoverIcon(
				"glo/icons/settings_icon_off.png",
				"glo/icons/settings_icon_on.png", "Settings",
				"Adjust settings for GasLo") {
			public GLScreenShell getContentArea() {
				return new GLSettingScreen();
			}
		};

		iconMgr.addIcon(gasIcon);
		iconMgr.addIcon(searchIcon);
		iconMgr.addIcon(favIcon);
		iconMgr.addIcon(settingsIcon);
	}

	public void fieldChanged(Field field, int context) {
	}

	/**
	 * 
	 */
	private void startupChecks() {
		
		SchemaManager.registerManager("glo.schema.GLSchemaManager");
		SchemaManager.addSchemaManagerListener(new SchemaManagerListener() {
			public void schemaReady(SchemaManager sm) {
				Thread checks = new Thread(new Runnable() {
					public void run() {
						// contact the server and say hello
						System.out.println("GL [II] Checking user database... do we download or read from db?");

						if(DbManager.isEmpty(GLSchemaManager.SCHEMA_GAS_TYPE)) {
							try {
								GLApp.dataManager.getGasTypes(true);
							} catch (JSONException e) {
								System.out.println("GL [EE] Fatal Error, could not download types");
							} 
						} 
						else {
							// Get gas types from DB
							try {
								DBOperations.getGasStationTypesFromDB();
							} catch (DataTypeException e) {
								System.out.println("GL [EE] Fatal Error, could not get types from DB");
								e.printStackTrace();
							} catch (JSONException e) {
								System.out.println("GL [EE] Fatal Error, could not parse types from JSON");
							}
						}

						// Check if Database exists
						if (DbManager.isFirstLoad()
								|| DbManager
										.isEmpty(GLSchemaManager.SCHEMA_GAS_STATIONS)) {
							initiateGasData(GLApp.dataManager);
						} else {
							// Get data from tables
							try {
								getDBInfo();
								initiateGasData(GLApp.dataManager);
							} catch (DataTypeException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						GLUpdateStationListing.getInstance().startTimer();
					}
				});
				checks.start();
			}
			
			public void schemaRegistrationFailed(String className, Exception e) {
				Thread downloadOnly = new Thread(new Runnable(){
					public void run(){
						System.out.println("GL [EE] Schema failed, downloading stations any way");
						initiateGasData(GLApp.dataManager);
					}
				});
				downloadOnly.start();
				
			}
			
			public void schemaCreated(SchemaManager schemaManager) {
				// TODO Auto-generated method stub
			}

			public void schemaRegistered(SchemaManager sm) {
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * Setup the GPS listener
	 */
	private void initiateGPSData() {
		Thread gpsThread = new Thread(new Runnable() {
			public void run() {
				// Gets the users current Location
				GPSHandle.setupGPSListener();
			}
		});
		gpsThread.start();
	}

	/**
	 * 
	 * @throws DataTypeException
	 * @throws JSONException 
	 */
	private void getDBInfo() throws DataTypeException, JSONException {
		DBOperations.getGasStationsFromDB();
		DBOperations.getGasStationPropsFromDB();
		DBOperations.getGasStationPricesFromDB();
		DBOperations.getGasStationFavouritesFromDB();

		GasDataManager.addGasTypetoPrice();
		GasDataManager.addPricetoGasStation();
	}

	/**
	 * 
	 * @param dataManager
	 */
	private void initiateGasData(ServerDataManager dataManager) {
		if(!dataManager.fetchStations(GLSettings.getNumberOfStationsToFetch())) {
			System.out.println("GL [WW] Fetch stations did not fetch anything");
			// try fetching again or another way
		}
	}
}
