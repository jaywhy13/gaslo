package glo.ui;

import glo.misc.GLPrequisiteListener;
import glo.misc.GLPrerequisite;
import glo.misc.GLPrerequisites;

import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Spacer;
import javax.microedition.media.control.GUIControl;

import com.rimextra.device.api.ui.component.ProgressAnimationField;
import com.rimextra.device.api.ui.container.JustifiedHorizontalFieldManager;
import com.rimextra.device.api.ui.container.JustifiedVerticalFieldManager;

import net.rim.device.api.crypto.Key;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EventInjector.KeyCodeEvent;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

public class GLSplashScreen extends MainScreen implements GLPrequisiteListener,
		KeyListener {

	private Bitmap _bgBitmap;

	protected static final boolean showing = false;

	protected boolean dismissed = false;

	private static GLSplashScreen splash;

	private GaugeField gauge;

	private Bitmap _mgiBitmapBig = Bitmap
			.getBitmapResource("glo/icons/mgi_logo.png");

	private Bitmap _logoBitmap = Bitmap
			.getBitmapResource("glo/icons/gaslo_logo.png");

	private UiApplication application;

	private Timer timer = new Timer();

	private MainScreen next;

	private VerticalFieldManager container;

	private FontifiedLabel statusLbl;

	public static GLSplashScreen makeSplash(UiApplication ui, MainScreen next) {
		if (splash == null) {
			splash = new GLSplashScreen(ui, next);
		}
		return splash;
	}

	private GLSplashScreen(UiApplication ui, MainScreen next) {
		super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR);
		this.application = ui;
		this.next = next;

		container = new VerticalFieldManager(Manager.USE_ALL_WIDTH
				| Manager.NO_HORIZONTAL_SCROLL | Manager.NO_VERTICAL_SCROLL
				| Manager.NO_VERTICAL_SCROLLBAR | Manager.USE_ALL_HEIGHT) {
			public boolean isFocusable() {
				return false;
			}
		};

		Background bg = BackgroundFactory.createSolidBackground(0x336699);
		// container.setBackground(bg);

		// setup the _bgbitmap
		boolean lowRes = Display.getWidth() <= 320;
		if (lowRes) {
			// pearl, flip curve, 8800
			_bgBitmap = Bitmap
					.getBitmapResource("glo/icons/start_screen_bg_h360.png");
			_logoBitmap = Bitmap
					.getBitmapResource("glo/icons/start_screen_title_w_200.png");
			_mgiBitmapBig = Bitmap
					.getBitmapResource("glo/icons/mgi_logo_h_40.png");
		} else {
			_bgBitmap = Bitmap
					.getBitmapResource("glo/icons/start_screen_bg_w480.png");
			_logoBitmap = Bitmap
					.getBitmapResource("glo/icons/start_screen_title_w_280.png");
		}

		int bgLeft = (Display.getWidth() - _bgBitmap.getWidth()) / 2;
		int bgTop = (Display.getHeight() - _bgBitmap.getHeight()) / 2;

		Background imageBg = BackgroundFactory.createBitmapBackground(
				_bgBitmap, bgLeft, bgTop, Background.REPEAT_NONE);
		container.setBackground(imageBg);

		BitmapField logoField = new BitmapField(_logoBitmap,
				Field.FIELD_HCENTER | Field.FIELD_VCENTER);
		logoField.setPadding(new XYEdges(25, 0, 10, 0));

		HorizontalFieldManager hfm = new HorizontalFieldManager(
				Field.FIELD_HCENTER);
		hfm.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1, 1, 1, 1)));
		// hfm.add(logoField);

		// Add the status section
		statusLbl = new FontifiedLabel("Loading ...", 12, 0xFFFFFF,
				Font.ANTIALIAS_STANDARD | Font.PLAIN, Field.FIELD_HCENTER
						| Field.FIELD_BOTTOM){
			public void setText(Object txt) {
				super.setText(txt);
				setDirty(true);
			};
		};
		// hfm.add(statusLbl);

		VerticalFieldManager logoVfm = new VerticalFieldManager(
				Field.FIELD_HCENTER);
		logoVfm.add(logoField);
		logoVfm.add(new BitmapField(_mgiBitmapBig, Field.FIELD_HCENTER));
		logoVfm.setPadding(20, 0, 15, 0);

		JustifiedVerticalFieldManager jvfm = new JustifiedVerticalFieldManager(
				null, logoVfm, statusLbl, Manager.FIELD_HCENTER
						| Manager.NO_VERTICAL_SCROLL | Manager.USE_ALL_WIDTH);

		container.add(jvfm);

		// setup progressbar
		final int containerHeight = Display.getHeight();
		gauge = new GaugeField(null, 0, 10, 0, GaugeField.READONLY
				| GaugeField.NO_TEXT | Field.USE_ALL_WIDTH | Field.FIELD_BOTTOM) {
	
//			public int getPreferredHeight() {
//				int desiredHeight = Math.min(6,(int) (containerHeight * 0.05));
//				return desiredHeight;
//			}
			
			protected void layout(int width, int height) {
				int desiredHeight = Math.min(6,(int) (containerHeight * 0.05));
				setExtent(width,desiredHeight);
			}

			protected void paint(net.rim.device.api.ui.Graphics g) {
				g.setColor(0x0000000);
				g.fillRect(0,0,getWidth(),getHeight());
				
				
				g.setColor(0x0099dc);
				double maxValue = this.getValueMax();
				double value = this.getValue();
				int width = (int) ((value / maxValue) * getWidth());
				int desiredHeight = Math.min(6,(int) (containerHeight * 0.05));
				g.fillRect(0,0, width, desiredHeight);
			};
			

		};

		gauge.setBorder(BorderFactory.createSimpleBorder(
				new XYEdges(0, 0, 0, 0), new XYEdges(0x000000,0x000000,0x000000,0x000000), Border.STYLE_TRANSPARENT));
		gauge.setBackground(BackgroundFactory.createSolidTransparentBackground(0x000000,0));
		this.add(container);
		this.setStatus(gauge);
		

		// Push me unto the screen
		application.pushScreen(this);

		// Schedule a dismissal
		// scheduleDismissal(3000);
	}

	public static void scheduleDismissal(final int closeAfter) {
		// timer.cancel();
		if (splash == null)
			return; // dont bother trying if no splash initialized

		if (splash.isDismissed())
			return;

		splash.timer.schedule(new TimerTask() {
			public void run() {
				Thread dismissThread = new Thread() {
					public void run() {
						splash.dismiss();
					}
				};
				splash.application.invokeLater(dismissThread);
			}
		}, closeAfter);

	}

	public static void setMessage(final String msg, final int duration) {
		if (splash == null) {
			System.out.println("GL [II] Splash null, ignoring message: \""
					+ msg + "\"");
			return;
		}

		if (splash.isDismissed())
			return;

		Thread t = new Thread(new Runnable() {
			public void run() {
				splash.statusLbl.setText(msg);
				try {
					Thread.sleep(duration);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// splash.gauge.setLabel(msg);
			}
		});

		splash.application.invokeLater(t);

	}

	public static void setMessage(final String msg) {

		if (splash == null) {
			System.out.println("GL [II] Splash null, ignoring message: \""
					+ msg + "\"");
			return;
		}

		if (splash.isDismissed())
			return;

		Thread t = new Thread(new Runnable() {
			public void run() {
				splash.statusLbl.setText(msg);
				// splash.gauge.setLabel(msg);
			}
		});

		splash.application.invokeLater(t);
	}

	public void setNext(MainScreen next) {
		this.next = next;
	}

	private void dismiss() {
		timer.cancel();
		application.popScreen(this);
		application.pushScreen(next);
		dismissed = true;
	}

	public boolean isDismissed() {
		return this.dismissed;
	}

	public void prerequisitesComplete() {
		setMessage("Ready");
		scheduleDismissal(1500);
	}

	public boolean keyUp(int keycode, int time) {
		return true;
	}

	public boolean keyChar(char c, int status, int time) {
		return true;
	}

	public boolean keyDown(int keycode, int time) {
		
		
		System.out.println("GL [II] User pressed: " + keycode);
		
		if(Keypad.key(keycode) == (Keypad.KEY_SPACE)){
			String msg = "Waiting for... ";
			Enumeration e = GLPrerequisites.getPrerequisiteNames();
			while (e.hasMoreElements()) {
				String prereqName = e.nextElement().toString();
				GLPrerequisite prereq = GLPrerequisites.getPrerequisite(prereqName);
				msg += "[" + prereqName + " - " + prereq.getStatus() + "] ";
			}
			GLSplashScreen.setMessage(msg);
			if (GLPrerequisites.numPrequisitesComplete() == GLPrerequisites
					.numPrerequisites()) {
				GLSplashScreen.setMessage("Loading ...");
			}

			if (keycode == Keypad.KEY_END) {
				scheduleDismissal(1500);
			}

			return true;
		}
		
		return false;
		
		
	}

	public boolean keyRepeat(int keycode, int time) {
		return true;
	}

	public boolean keyStatus(int keycode, int time) {
		return true;
	}
	
	protected void updateGauge(){
		double complete = (double) GLPrerequisites.numPrequisitesComplete();
		double total = (double) GLPrerequisites.numPrerequisites();
		int newValue = (int) ((complete / total) * 10);
		System.out.println("GL [II] Upadting gauge: " + complete + " of "
				+ total + " prerequisites complete ... [" + newValue + "/10]");
		this.gauge.setValue(newValue);
	}

	public void prerequisiteComplete(GLPrerequisite prereq) {
		updateGauge();
	}
	
	public void prerequisiteRemoved(GLPrerequisite prereq) {
		updateGauge();
		
	}
	
	protected boolean onSavePrompt() {
		return true;
	}

}
