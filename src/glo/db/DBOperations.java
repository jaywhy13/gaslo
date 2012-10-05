package glo.db;

import java.util.Hashtable;
import java.util.Vector;

import glo.json.JSONException;
import glo.schema.GLSchemaManager;
import glo.sys.GLSettings;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.types.GasGeoData;
import glo.types.GasPriceInfo;
import glo.types.GasType;
import glo.ui.GLApp;
import glo.ui.GLMainScreen;
import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DataTypeException;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.database.Row;

public class DBOperations extends DbManager {
	
	public static boolean hasGSInDB = false;
	public static boolean hasGSPropsInDB = false;
	public static boolean hasGTInDB = false;
	public static boolean hasGPInDB = false;
	
	/**
	 * 
	 * @throws DataTypeException
	 * @throws JSONException 
	 */
	public static void getGasStationsFromDB() throws DataTypeException, JSONException { // get gas stations and their geo data
		Cursor gs = DbManager.getTableData(GLSchemaManager.SCHEMA_GAS_STATIONS);
		if (gs != null) {
			hasGSInDB = true;
			try {
				Row r = gs.getRow();
				GasCompany currentGS = new GasCompany(r.getString(1), r.getInteger(0));
				GasGeoData currentGGD = new GasGeoData(r.getInteger(0), r.getDouble(3), r.getDouble(4));
				if (r.getString(2) != null) {
					currentGGD.setParish(r.getString(2));
				}
				currentGS.setGeoID(r.getInteger(0));
				currentGS.setLocation(currentGGD);
				GasDataManager.getInstance().addGasStationWithIndex(currentGS, r.getInteger(0));
				GasDataManager.getInstance().addGasGeoDataWithIndex(currentGGD, r.getInteger(0));
				while (gs.next()) {
					r = gs.getRow();
					currentGS = new GasCompany(r.getString(1), r.getInteger(0));
					currentGGD = new GasGeoData(r.getInteger(0), r.getDouble(3), r.getDouble(4));
					if (r.getString(2) != null) {
						currentGGD.setParish(r.getString(2));
					}
					currentGS.setGeoID(r.getInteger(0));
					currentGS.setLocation(currentGGD);
					GasDataManager.getInstance().addGasStationWithIndex(currentGS, r.getInteger(0));
					GasDataManager.getInstance().addGasGeoDataWithIndex(currentGGD, r.getInteger(0));
				}
			} catch (DatabaseException e) {
				System.out
						.println("GL [EE] Database exception occurred while attempting select on table"
								+ GLSchemaManager.SCHEMA_GAS_STATIONS
								+ ".\nReason: " + e.getMessage());
			}
		} else {
			if(GLApp.dataManager.getGasStations(GLSettings.getNumberOfStationsToFetch(), true)) {
				hasGSInDB = true;
			}
		}
	}
	
	/**
	 * 
	 * @throws DataTypeException
	 * @throws JSONException 
	 */
	public static void getGasStationPropsFromDB() throws DataTypeException, JSONException { // Get Gas Station Props
		Cursor gsp = DbManager.getTableData(GLSchemaManager.SCHEMA_GAS_STATION_PROPS);
		if (gsp != null) {
			hasGSPropsInDB = true;
			try {
				Row r = gsp.getRow();
				GasCompany currentGS = (GasCompany) GasDataManager.getInstance().getGasStations().elementAt(r.getInteger(0));
				currentGS.addFeatures(toBoolean((String) r.getObject(2)),
						toBoolean((String) r.getObject(3)),
						toBoolean((String) r.getObject(1)),
						toBoolean((String) r.getObject(4)));
				while (gsp.next()) {
					r = gsp.getRow();
					currentGS = (GasCompany) GasDataManager.getInstance().getGasStations().elementAt(r.getInteger(0));
					currentGS.addFeatures(toBoolean((String) r.getObject(2)),
							toBoolean((String) r.getObject(3)),
							toBoolean((String) r.getObject(1)),
							toBoolean((String) r.getObject(4)));
				}
			} catch (DatabaseException e) {
					System.out
							.println("GL [EE] Database exception occurred while attempting select on table"
									+ GLSchemaManager.SCHEMA_GAS_STATION_PROPS
									+ ".\nReason: " + e.getMessage());
			}
		} else {
			if(GLApp.dataManager.getGasStations(GLSettings.getNumberOfStationsToFetch(), true)) {
				hasGSPropsInDB = true;
			}
		}
	}
	
	/**
	 * 
	 * @throws DataTypeException
	 * @throws JSONException 
	 */
	public static void getGasStationTypesFromDB() throws DataTypeException, JSONException { // Get Gas Types
		Cursor gt = DbManager.getTableData(GLSchemaManager.SCHEMA_GAS_TYPE);
		if (gt != null) {
			hasGTInDB = true;
			try {
				Row r = gt.getRow();
				GasType currentType = new GasType(r.getInteger(0), r.getString(1));
				if (r.getString(2) != null) {
					currentType.setAlias(r.getString(2));
				}
				GasDataManager.getInstance().addGasTypeWithIndex(currentType, r.getInteger(0));
	
				while (gt.next()) {
					r = gt.getRow();
					System.out
							.println("GL [II] Retrieved row: " + r.toString());
					currentType = new GasType(r.getInteger(0), r.getString(1));
					if (r.getString(2) != null) {
						currentType.setAlias(r.getString(2));
					}
					GasDataManager.getInstance().addGasTypeWithIndex(
							currentType, r.getInteger(0));
				}
			} catch (DatabaseException e) {
				System.out
						.println("GL [EE] Database exception occurred while attempting select on table"
								+ GLSchemaManager.SCHEMA_GAS_TYPE
								+ ".\nReason: " + e.getMessage());
			}
		} else {
			if(GLApp.dataManager.getGasTypes(true)) {
				hasGTInDB = true;
			}
		}
	}

	/**
	 * 
	 * @throws DataTypeException
	 * @throws JSONException 
	 */
	public static void getGasStationPricesFromDB() throws DataTypeException, JSONException { // Get Gas Station Prices
		Cursor gp = DbManager.getTableData(GLSchemaManager.SCHEMA_GAS_STATION_PRICE);
		if (gp != null) {
			hasGPInDB = true;
			try {
				Row r = gp.getRow();
				GasPriceInfo currentGasPrice = new GasPriceInfo(r.getInteger(0), 
						"Type Not Available", r.getDouble(2));
				currentGasPrice.setGasTypeID(r.getInteger(1));
				GasDataManager.getInstance().addGasPrices(currentGasPrice);
	
				while (gp.next()) {
					r = gp.getRow();
					currentGasPrice = new GasPriceInfo(r.getInteger(0),
							"Type Not Available", r.getDouble(2));
					currentGasPrice.setGasTypeID(r.getInteger(1));
					GasDataManager.getInstance().addGasPrices(currentGasPrice);
				}
			} catch (DatabaseException e) {
				System.out
						.println("GL [EE] Database exception occurred while attempting select on table"
								+ GLSchemaManager.SCHEMA_GAS_STATION_PRICE
								+ ".\nReason: " + e.getMessage());
			}
		} else {
			if(GLApp.dataManager.getGasPrices(GLSettings.getNumberOfPricesToFetch(), true)) {
				hasGPInDB = true;
			}
		}
	}

	/**
	 * 
	 * @throws DataTypeException
	 */
	public static void getGasStationFavouritesFromDB() throws DataTypeException { // Get Gas Station Favourites
	Cursor gf = DbManager.getTableData(GLSchemaManager.SCHEMA_GAS_FAVOURITES);
		if (gf != null) {
			try {
				Row r = gf.getRow();
				GasCompany currentGS = (GasCompany) GasDataManager
						.getInstance().getGasStations()
						.elementAt(r.getInteger(0));
				GasDataManager.getInstance().addFavourite(currentGS);
				while (gf.next()) {
					r = gf.getRow();
					currentGS = (GasCompany) GasDataManager.getInstance()
					.getGasStations()
					.elementAt(r.getInteger(0));
					GasDataManager.getInstance().addFavourite(currentGS);
				}
			} catch (DatabaseException e) {
				System.out
						.println("GL [EE] Database exception occurred while attempting select on table"
								+ GLSchemaManager.SCHEMA_GAS_FAVOURITES
								+ ".\nReason: " + e.getMessage());
			}
		}
		else {
			// Get favourites from the cloud
		}
	}
	
	/**
	 * 
	 * @param gasStationList
	 * @throws DatabaseException
	 */
	public static boolean populateStations(Vector gasStationList) throws DatabaseException {
		try {
			gasStationList = filterExistingInstances(gasStationList);
			int length = gasStationList.size();
			beginTransaction();
			for (int a = 0; a < length; a++) {
				GasCompany currentStation = (GasCompany) gasStationList
						.elementAt(a);
				if (currentStation != null) {
					Hashtable stationData = new Hashtable();
					Hashtable propsData = new Hashtable();

					stationData.put("id",
							new Integer(currentStation.getCompanyId()));

					String nam = sqlCleanString(currentStation.getCompanyName());
					stationData.put("name", nam);

					if (currentStation.getLocation().getParish() != null) {
						String par = sqlCleanString(currentStation.getLocation()
								.getParish());
						stationData.put("parish", par);
					}
					stationData.put("y", new Double(currentStation.getLocation()
							.getLatitude()));
					stationData.put("x", new Double(currentStation.getLocation()
							.getLongitude()));

					if (currentStation.getDateOfPrices() != null) {
						stationData.put("price_update", currentStation
								.getDateOfPrices());
					}

					if (insert(GLSchemaManager.SCHEMA_GAS_STATIONS, stationData)) {
						System.out
								.println("GL [II] Inserted Gas Station Data into "
										+ GLSchemaManager.SCHEMA_GAS_STATIONS);
					} else {
						System.out
								.println("GL [EE] Could not insert Gas Station Data into "
										+ GLSchemaManager.SCHEMA_GAS_STATIONS);
					}

					propsData.put("gs_id", new Integer(currentStation
							.getCompanyId()));
					propsData.put("has_atm", new Boolean(currentStation
							.getFeature().isAtm()));
					propsData.put("has_mini_mart", new Boolean(currentStation
							.getFeature().isMiniMart()));
					propsData.put("has_bathroom", new Boolean(currentStation
							.getFeature().isBathroom()));
					propsData.put("has_air_pump", new Boolean(currentStation
							.getFeature().isAirPump()));

					if (insert(GLSchemaManager.SCHEMA_GAS_STATION_PROPS, propsData)) {
						System.out
								.println("GL [II] Inserted Gas Station Props Data into "
										+ GLSchemaManager.SCHEMA_GAS_STATION_PROPS);
					} else {
						System.out
								.println("GL [EE] Could not insert Gas Station Props Data into "
										+ GLSchemaManager.SCHEMA_GAS_STATION_PROPS);
					}
				}
			}
			commitTransaction();
			return true;
		} catch(Exception e ){
			System.out.println("GL [EE] Error occurred while trying to populateStations. " + e.getMessage() + "[" + e.getClass().getName() + "]");
		}
		return false;
	}

	/**
	 * 
	 * @param gasPriceList
	 * @throws DatabaseException
	 */
	public static boolean populateGasPrices(Vector gasPriceList) throws DatabaseException {
		try {
		gasPriceList = filterExistingInstances(gasPriceList);
		int length = gasPriceList.size();
		beginTransaction();
		for (int a = 0; a < length; a++) {
			GasPriceInfo currentPrice = (GasPriceInfo) gasPriceList
					.elementAt(a);
			if (currentPrice != null) {
				Hashtable data = new Hashtable();
				data.put("gs_id", new Integer(currentPrice.getGasCompanyID()));
				data.put("gst_id", new Integer(currentPrice.getGasTypeID()));
				data.put("cost", new Double(currentPrice.getCost()));
				data.put("date_effective", currentPrice.getLastUpdated());

				if (insert(GLSchemaManager.SCHEMA_GAS_STATION_PRICE, data)) {
					System.out
							.println("GL [II] Inserted Gas Station Price Data into "
									+ GLSchemaManager.SCHEMA_GAS_STATION_PRICE);
				} else {
					System.out
							.println("GL [EE] Could not insert Gas Station Price Data into "
									+ GLSchemaManager.SCHEMA_GAS_STATION_PRICE);
				}
			}
		}
		commitTransaction();
		return true;
		} catch(Exception e){
			System.out.println("GL [EE] Error occurred while trying to populateStations. " + e.getMessage() + "[" + e.getClass().getName() + "]");
		}
		return false;
	}
	
	/**
	 * 
	 * @param gasTypeList
	 * @throws DatabaseException
	 */
	public static boolean populateGasTypes(Vector gasTypeList) throws DatabaseException {		
		try {
		gasTypeList = filterExistingInstances(gasTypeList);
		int length = gasTypeList.size();
		beginTransaction();
		for (int a = 0; a < length; a++) {
			GasType currentType = (GasType) gasTypeList.elementAt(a);
			if (currentType != null) {
				Hashtable data = new Hashtable();
				data.put("id", new Integer(currentType.getId()));

				String nam = sqlCleanString(currentType.getName());
				data.put("name", nam);

				if (currentType.getAlias() != null) {
					String al = sqlCleanString(currentType.getAlias());
					data.put("alias", al);
				}

				if (insert(GLSchemaManager.SCHEMA_GAS_TYPE, data)) {
					System.out.println("GL [II] Inserted Gas Types Data into "
							+ GLSchemaManager.SCHEMA_GAS_TYPE);
				} else {
					System.out
							.println("GL [EE] Could not insert Gas Type Data into "
									+ GLSchemaManager.SCHEMA_GAS_TYPE);
				}
			}
		}
		commitTransaction();
		return true;
		} catch(Exception e){
			System.out.println("GL [EE] Error occurred while trying to populateStations. " + e.getMessage() + "[" + e.getClass().getName() + "]");
		}
		return false;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static boolean toBoolean(String s) {
		return ((s != null) && s.equalsIgnoreCase("true"));
	}

	/**
	 * 
	 * @param s
	 * @return
	 */
	public static Boolean valueOf(String s) {
		return (toBoolean(s) ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * 
	 * @return
	 */
	protected boolean onSavePrompt() {
		return true;
	}
}
