package glo.ui;

import com.rimextra.device.api.ui.container.JustifiedVerticalFieldManager;

import glo.types.GasCompany;
import glo.types.GasPriceInfo;
import glo.ui.screens.GLGasStationListingScreen;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * This class holds the template screen that all the subscreens "View Prices",
 * "Settings" etc will use. It is expected that these particular screens will
 * subclass this screen and implement their own functionality.
 * 
 * @author JMWright
 * 
 */
public class GLScreenShell extends MainScreen {

	/**
	 * The banner section of the page with the app name and the signal icon
	 */
	protected GLBanner banner = new GLBanner();

	/**
	 * The page is separated into two regions. The header and the body. The
	 * title of the page is added in the header region, the body (content)
	 * region is left blank for content to be added. Override setupContent to
	 * fill this region.
	 */
	protected VerticalFieldManager bodyMgr = new VerticalFieldManager(
			Manager.USE_ALL_WIDTH | Manager.USE_ALL_HEIGHT | Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);

	/**
	 * The page is separated into two regions. The header and content area. Use
	 * setupHeader to override the default modifications that are performed on
	 * the headerMgr.
	 */
	protected VerticalFieldManager headerMgr;

	/**
	 * The gradient blue bg.
	 */

	protected Bitmap bgIcon = Bitmap.getBitmapResource("glo/icons/striped_bg_20.png");
	Background stripedBg = BackgroundFactory.createBitmapBackground(bgIcon, 0, 0, Background.REPEAT_BOTH);

	/**
	 * The plain blue background
	 */
	protected Background plainBg = BackgroundFactory
			.createSolidBackground(0x336699);
	
	protected VerticalFieldManager adsVfm = new VerticalFieldManager(Manager.USE_ALL_WIDTH);
	
	protected JustifiedVerticalFieldManager jvfm;
	
	protected JustifiedVerticalFieldManager contentVfm;

	private Bitmap _shadedBg = Bitmap.getBitmapResource("glo/icons/bg_long.png");
	

	public GLScreenShell(String headerText, boolean setupContent) {
		super(Manager.USE_ALL_WIDTH | Manager.USE_ALL_HEIGHT | Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL);
		
		// Setup the content vfm 
		contentVfm = new JustifiedVerticalFieldManager(null,bodyMgr,null);
		contentVfm.setBackground(stripedBg);
		
		jvfm = new JustifiedVerticalFieldManager(banner,contentVfm,adsVfm);
		add(jvfm);
		
		if(setupContent) { setupContentArea(); }
	}


	/**
	 * Called in the constructor to modify the content region. Currently, this
	 * only sets up the background for the content area.
	 */
	protected void setupContentArea() {

	}
	
	protected boolean onSavePrompt() {
		return true;
	}

	public GLBanner getBanner() {
		return banner;
	}

	public void setBanner(GLBanner banner) {
		this.banner = banner;
	}
	
	
	
}
