package glo.ui.screens;

import java.util.Hashtable;

import javax.microedition.location.Location;

import com.rimextra.device.api.ui.container.JustifiedHorizontalFieldManager;

import net.rim.device.api.database.Cursor;
import net.rim.device.api.database.DatabaseException;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EventInjector.TouchEvent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontManager;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import glo.db.DbManager;
import glo.gps.GPSHandle;
import glo.schema.GLSchemaManager;
import glo.types.GasCompany;
import glo.types.GasDataManager;
import glo.types.GasPriceInfo;
import glo.ui.FontifiedLabel;
import glo.ui.GLApp;
import glo.ui.GLGSDetails;
import glo.ui.GLScreenShell;

public class GLGasStationDetails extends GLScreenShell {

	private GasCompany gasStation = null;
	private static String title = "View Details";
	GLGSDetails details;
	protected Bitmap companyIcon;
	protected boolean favourite;
	BitmapField gasStationIcon;
	protected TouchBitmapField startBitmapField;

	protected int favouriteFontHeight = 24;

	public GLGasStationDetails(GasCompany gc) {
		super(title, false);
		gasStation = gc;
		setupContentArea();
	}

	ObjectChoiceField isFavourite = new ObjectChoiceField("", new String[] {
			"Yes", "No" }, 0, Field.FIELD_RIGHT | Field.FIELD_VCENTER) {
		protected void paintBackground(net.rim.device.api.ui.Graphics g) {
			g.setColor(0x007eca);
			g.setBackgroundColor(0x007eca);
			g.fillRoundRect(0, 0, getWidth(), favouriteFontHeight, 4, 4);

			// if focused...
			if (isFocus()) {
				g.setColor(0x000000);
			} else {
				g.setColor(0x007eca);
			}
			g.drawRoundRect(0, 0, getWidth(), favouriteFontHeight, 4, 4);
		};

		protected void paint(net.rim.device.api.ui.Graphics g) {
			String selectedTxt = this.getChoice(this.getSelectedIndex())
					.toString();
			g.setColor(0x000000);
			g.setFont(favouriteFont);
			// g.drawText(selectedTxt,
			// (getWidth() - favouriteFont.getAdvance(selectedTxt)) / 2,
			// (favouriteFontHeight - favouriteFont.getHeight()) / 2 - 2);
			g.setColor(0xFFFFFF);
			g.drawText(selectedTxt,
					(getWidth() - favouriteFont.getAdvance(selectedTxt)) / 2,
					(favouriteFontHeight - favouriteFont.getHeight()) / 2);

		};

		public int getPreferredHeight() {
			return favouriteFontHeight;
		}

		public boolean isFocusable() {
			return true;
		};

	};

	protected Font favouriteFont;

	protected Font descriptionFont;

	protected int textWidth = 0;
	private Font parishFont;
	private Font gasPriceFont;

	protected void setupFonts() {
		descriptionFont = Font.getDefault().derive(Font.BOLD, 20);
		try {
			descriptionFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.BOLD, 20);
		} catch (ClassNotFoundException cnfe) {

		}

		favouriteFont = Font.getDefault().derive(Font.BOLD, 14);
		try {
			favouriteFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.BOLD, 14);
			textWidth = favouriteFont.getAdvance("Yes");
		} catch (ClassNotFoundException cnfe) {

		}

		parishFont = Font.getDefault().derive(Font.BOLD);
		try {
			parishFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.BOLD, 18);
		} catch (ClassNotFoundException cnfe) {

		}
		
		gasPriceFont = Font.getDefault().derive(Font.BOLD,16);
		try {
			gasPriceFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(Font.BOLD,12);
		} catch(ClassNotFoundException cnfe){
		}
	}

	protected void setupContentArea() {

		// define main vfm
		VerticalFieldManager vfm = new VerticalFieldManager(
				Manager.USE_ALL_WIDTH | Manager.USE_ALL_HEIGHT
						| Manager.VERTICAL_SCROLL);

		// define fonts
		setupFonts();

		// define top bar with favourites
		HorizontalFieldManager hfm = new HorizontalFieldManager(
				Manager.USE_ALL_WIDTH | Manager.FIELD_RIGHT) {
			public int getPreferredHeight() {
				return favouriteFontHeight + 4;
			}
		};
		hfm.setBackground(BackgroundFactory.createSolidBackground(0xFFFFFF));
		hfm.setBorder(BorderFactory.createSimpleBorder(new XYEdges(0, 0, 1, 0),
				new XYEdges(0, 0, 0x00a7e2, 0), Border.STYLE_SOLID));
		hfm.setPadding(2, 2, 2, 2);
		LabelField favouriteLbl = new LabelField("Favourite",Field.FIELD_RIGHT);
		hfm.add(favouriteLbl);
		hfm.add(isFavourite);
		vfm.add(hfm);

		// update the favourites
		if (GasDataManager.getInstance().inFavourites(gasStation)) {
			isFavourite.setSelectedIndex(0);
			favourite = true;
		} else {
			isFavourite.setSelectedIndex(1);
			favourite = false;
		}

		// the actual details now
		HorizontalFieldManager contentHfm = new HorizontalFieldManager(
				Manager.USE_ALL_WIDTH);
		contentHfm.setPadding(0, 5, 0, 20);
		companyIcon = Bitmap.getBitmapResource("glo/icons/gas.png");
		contentHfm.add(new BitmapField(companyIcon));
		vfm.add(contentHfm);

		// gas station information
		VerticalFieldManager gsVfm = new VerticalFieldManager(
				Manager.USE_ALL_HEIGHT);
		contentHfm.add(gsVfm);

		FontifiedLabel name = new FontifiedLabel(gasStation.getCompanyName(),
				14, 0x007eca, Font.DROP_SHADOW_RIGHT_EFFECT | Font.BOLD);
		name.setFont(descriptionFont);
		gsVfm.add(name);

		FontifiedLabel parishLbl = new FontifiedLabel(gasStation.getParish(),
				12, 0x494d4a, Font.PLAIN);
		parishLbl.setFont(parishFont);
		gsVfm.add(parishLbl);

		for (int i = 0; i < gasStation.getPrices().size(); i++) {
			GasPriceInfo gpi = (GasPriceInfo) gasStation.getPrices().elementAt(
					i);

			String str[] = new String[] { gpi.getName() + ": ",
					gpi.getCost() + "" };
			int off[] = new int[] { 0, str[0].length(),
					str[0].length() + str[1].length() };
			byte attr[] = new byte[] { 0, 1 };
			FontFamily fontfam[] = FontFamily.getFontFamilies();
			Font fon[] = new Font[2];
			fon[0] = gasPriceFont;
			fon[1] = gasPriceFont.derive(Font.PLAIN);
			gsVfm.add(new RichTextField(str[0] + str[1], off, attr, fon,
					RichTextField.TEXT_ALIGN_LEFT));
		}

		Location loc = GPSHandle.getLocation();
		if (loc != null) {
			double lat = loc.getQualifiedCoordinates().getLatitude();
			double lon = loc.getQualifiedCoordinates().getLongitude();

			String userLocation = "User: " + lat + ", " + lon;
//			vfm.add(new LabelField(userLocation));

			String gasStationLoaction = "";
			lat = gasStation.getLocation().getLatitude();
			lon = gasStation.getLocation().getLongitude();

			gasStationLoaction = "Station: " + lat + ", " + lon;
//			vfm.add(new LabelField(gasStationLoaction));

//			vfm.add(new LabelField("Distance: " + gasStation.getDistanceFromUser() + "m"));
			String distanceFromUser = GPSHandle.getDistanceAsString(gasStation.getDistanceFromUser());
			
			
			String str[] = new String[] { "Distance from user: ",
					distanceFromUser };
			int off[] = new int[] { 0, str[0].length(),
					str[0].length() + str[1].length() };
			byte attr[] = new byte[] { 0, 1 };
			FontFamily fontfam[] = FontFamily.getFontFamilies();
			Font fon[] = new Font[2];
			fon[0] = gasPriceFont;
			fon[1] = gasPriceFont.derive(Font.PLAIN);
			gsVfm.add(new RichTextField(str[0] + str[1], off, attr, fon,
					RichTextField.TEXT_ALIGN_LEFT));
			
		}
		bodyMgr.add(vfm);
	}

	public boolean navigationClick(int status, int time) {
		if (favourite) {
			favourite = false;
			try {
				deleteFavourite(gasStation);
				isFavourite.setSelectedIndex(1);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			favourite = true;
			try {
				updateFavourite(gasStation);
				isFavourite.setSelectedIndex(0);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	private void updateFavourite(GasCompany station) throws DatabaseException {
		Cursor results = DbManager.select(
				GLSchemaManager.SCHEMA_GAS_FAVOURITES, station.getCompanyId());
		if (!results.first()) {
			Hashtable data = new Hashtable();
			data.put("id", new Integer(station.getCompanyId()));
			DbManager.insert(GLSchemaManager.SCHEMA_GAS_FAVOURITES, data);
			GasDataManager.getInstance().addFavourite(station);
		} else {
			System.out
					.println("GL [II] Gas Station " + station.getCompanyName()
							+ " alread added as a favourite");
		}
	}

	private void deleteFavourite(GasCompany station) throws DatabaseException {
		Cursor results = DbManager.select(
				GLSchemaManager.SCHEMA_GAS_FAVOURITES, station.getCompanyId());
		if (results.first()) {
			Hashtable data = new Hashtable();
			data.put("id", new Integer(station.getCompanyId()));
			DbManager.remove(GLSchemaManager.SCHEMA_GAS_FAVOURITES, data);
			GasDataManager.getInstance().removeFavourite(station);
		} else {
			System.out
					.println("GL [II] Gas Station " + station.getCompanyName()
							+ " not in the favourites table");
		}
	}
}

class TouchBitmapField extends BitmapField {
	public TouchBitmapField(Bitmap startBitmap) {
		super(startBitmap);
	}

	protected boolean touchEvent(net.rim.device.api.ui.TouchEvent message) {
		if (TouchEvent.CLICK == message.getEvent()) {
			FieldChangeListener listener = getChangeListener();
			if (null != listener)
				listener.fieldChanged(this, 1);
		}
		return super.touchEvent(message);
	}
}