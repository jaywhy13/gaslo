package glo.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * This field shows the title of the application and the signal indicator
 * 
 * @author JMWright
 * 
 */
public class GLBanner extends Manager implements RadioStatusListener {

	/**
	 * The label used to manage the application title
	 */
	protected FontifiedLabel lbl;
	
	protected String caption = "";

	
	protected Bitmap _bgLeft = Bitmap
			.getBitmapResource("glo/icons/toolbar_logo.png");
	protected Bitmap _bgMid = Bitmap
			.getBitmapResource("glo/icons/toolbar_bg.png");
	protected Bitmap _bgRight = Bitmap
			.getBitmapResource("glo/icons/toolbar_right.png");

	/**
	 * The padding for
	 */
	protected int padding = 4;

	protected int bannerHeight = 80;

	protected int marginBottom = 16;

	Font bannerFont;

	public GLBanner() {
		super(Manager.USE_ALL_WIDTH);

		// Configure the background
		Background bg = BackgroundFactory.createSolidBackground(0x000000);
		setBackground(bg);

		Border border = BorderFactory.createSimpleBorder(
				new XYEdges(0, 0, 2, 0), new XYEdges(0, 0, 0xaabbff, 0),
				new XYEdges(0, 0, Border.STYLE_SOLID, 0));
		// setBorder(border);

		// derive the font for the lbl
		try {
			bannerFont = FontFamily.forName(GLApp.FONT_MYRIADPRO).getFont(
					Font.PLAIN, 40);
			System.out.println("GL [II] Myriad Pro font loaded successfully");
		} catch (ClassNotFoundException cnfe) {
			Font def = Font.getDefault();
			System.out
					.println("GL [EE] Myriad Pro could not be loaded, using default font");
			bannerFont = def.derive(Font.PLAIN, def.getHeight() * 3 / 2);
		}

		lbl = new FontifiedLabel(caption);
		lbl.setFont(bannerFont);
		lbl.setFontColor(0xFFFFFF);
		lbl.setFontSize(30);

		 add(lbl);

		UiApplication.getApplication().addRadioListener(this);
	}

	protected void sublayout(int width, int height) {
		if (lbl.getText() != null && lbl.getText() != "") {
			layoutChild(lbl, width, lbl.getHeight());
		}

		// setPositionChild(lbl,padding * 2 +
		// mgiBitmapField.getWidth(),padding);
		if (lbl.getText() != null && lbl.getText() != "") {
			int labelHeight = lbl.getHeight();
			setPositionChild(lbl, (_bgLeft.getWidth() + 10),
					(bannerHeight - lbl.getHeight()) / 2);
		}
		setExtent(width + padding * 2, bannerHeight);
	}

	protected void paintBackground(Graphics g) {
		// super.paintBackground(arg0);
		g.tileRop(Graphics.ROP_SRC_COPY, 0, 0, getWidth(), _bgMid.getHeight(),
				_bgMid, 0, 0);
		g.drawBitmap(0, 0, _bgLeft.getWidth(), _bgLeft.getHeight(), _bgLeft, 0,
				0);
		g.drawBitmap(getWidth() - _bgRight.getWidth(), 0, _bgRight.getWidth(),
				_bgRight.getHeight(), _bgRight, 0, 0);
	}

	public int getPreferredHeight() {
		// return lbl.getHeight();
		return bannerHeight;
	}

	public int getPreferredWidth() {
		return super.getPreferredWidth() + padding * 2;
	}

	public void baseStationChange() {
		// TODO Auto-generated method stub

	}

	public void networkScanComplete(boolean success) {
		// TODO Auto-generated method stub

	}

	public void networkServiceChange(int networkId, int service) {
		// TODO Auto-generated method stub

	}

	public void networkStarted(int networkId, int service) {
		// TODO Auto-generated method stub

	}

	public void networkStateChange(int state) {
		// TODO Auto-generated method stub

	}

	public void pdpStateChange(int apn, int state, int cause) {
		// TODO Auto-generated method stub

	}

	public void radioTurnedOff() {
		// TODO Auto-generated method stub

	}

	public void signalLevel(int level) {
		int min = -121;
		int max = -40;

		int division = (max - min) / 3;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		this.lbl.setText(caption);
	}

	

}
