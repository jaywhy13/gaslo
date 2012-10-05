package glo.ui;

import glo.net.GLNetworkNavigate;
import glo.sys.GLSettings;

import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.w3c.dom.Document;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.browser.field2.BrowserFieldListener;

public class GLAdManager extends HorizontalFieldManager  implements FieldChangeListener {
	
	private static final GLAdManager ads = new GLAdManager();
	
	protected BrowserField browser;
	
	private Border focusBg = BorderFactory.createSimpleBorder(new XYEdges(1,0,1,0), new XYEdges(0x000000,0x000000,0x000000,0x000000), Border.STYLE_SOLID);
	private Border plainBg = BorderFactory.createSimpleBorder(new XYEdges(0,0,0,0), Border.STYLE_SOLID);
	
	
	protected Vector adIds = new Vector();
	
	private GLAdManager(){
		super(Manager.USE_ALL_WIDTH);
		BrowserFieldConfig config = new BrowserFieldConfig();
		config.setProperty(BrowserFieldConfig.NAVIGATION_MODE, BrowserFieldConfig.NAVIGATION_MODE_NONE);
		browser = new BrowserField(config);
		browser.setChangeListener(this);
		add(browser);
		
		browser.addListener(new BrowserFieldListener() {
			public void documentError(BrowserField browserField,
					Document document) throws Exception {
				System.out.println("GL [EE] Document error loading ad content: " + document.getDocumentURI());
				browserField.displayContent("", "http://localhost");
			}
		});
		browser.setFocusListener(new FocusChangeListener() {
			
			public void focusChanged(Field field, int eventType) {
				if(eventType == FocusChangeListener.FOCUS_GAINED){
					browser.setBorder(focusBg);
				} else {
					browser.setBorder(plainBg);
				}
				
			}
		});
		browser.setEditable(false);
		browser.setPadding(0,0,0,0);
		browser.setMargin(0,0,0,0);
		setMargin(0,0,0,0);
		setPadding(0,0,0,0);
		startAdRotator();
	}
	
	public static final GLAdManager getInstance(){
		return ads;
	}
	
	
	
	public int getPreferredHeight() {
		return 85;
	}
	
	/**
	 * Adds an ad id to the list of ad ids we have
	 * @param adId
	 */
	public void addAdId(int adId){
		if(!adIds.contains(new Integer(adId))){
			System.out.println("GL [II] Adding advertisment with id: " + adId);
			adIds.addElement(new Integer(adId));
		}
	}
	
	/**
	 * Starts the rotation of ads
	 */
	public void startAdRotator(){
		System.out.println("GL [II] Starting ad rotator");
		TimerTask timerTask = new TimerTask() {
			public void run() {
				changeAd();
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, GLSettings.getRotateAdsEvery());
	}
	
	public void changeAd(){
		if(adIds.size() > 1){
			System.out.println("GL [II] Changing the advertisment");
			Random generator = new Random();
			int adId = generator.nextInt(adIds.size());
			showAd(adId);
		} else if(adIds.size() == 1){
			System.out.println("GL [II] Showing default ad");
			showAd( ((Integer) adIds.elementAt(0)).intValue() );
		} else {
			System.out.println("GL [II] No ads to change");
		}
	}
	
	public void showAd(final int adId){
		System.out.println("GL [II] Will show ad with id = " + adId);
		Runnable fetchAd = new Runnable(){
			public void run(){
				Hashtable params = new Hashtable();
				params.put("height", new Integer(Display.getHeight()));
				params.put("width", new Integer(Display.getWidth()));
				String url = GLSettings.getUrlByNameWithSuffix(GLSettings.URL_ADS, String.valueOf(adId), params);
//				loadUrl(url);
			}
		};
		UiApplication.getUiApplication().invokeLater(fetchAd);
	}
	
	private void loadUrl(String url){
		System.out.println("GL [II] Loading ad: " + url);
		try {
		browser.requestContent(url);
		} catch(NullPointerException npe){
			System.out.println("GL [EE] Null pointer exception occurred while trying to load ad: " + url);
		}
	}

	public void fieldChanged(Field field, int context) {
		System.out.println("GL [II] User clicked an ad");
	}
	
	
	
	
	
}
