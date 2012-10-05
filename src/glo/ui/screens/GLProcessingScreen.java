package glo.ui.screens;


import glo.ui.AnimatedGIFField;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class GLProcessingScreen extends PopupScreen {
	
	private AnimatedGIFField ourAnimations;
	private LabelField ourLabelField;
	
	public GLProcessingScreen(String text) {
	    super(new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR));
	    GIFEncodedImage ourAnimation = (GIFEncodedImage) GIFEncodedImage.getEncodedImageResource("cycle.agif");
	    ourAnimations = new AnimatedGIFField(ourAnimation, Field.FIELD_HCENTER);
	    this.add(ourAnimations);
	    ourLabelField = new LabelField(text, Field.FIELD_HCENTER);
	    this.add(ourLabelField);
	}
	
	public static void showScreenAndWait(final Runnable runThis, String text) {
        final GLProcessingScreen thisScreen = new GLProcessingScreen(text);
        Thread threadToRun = new Thread() {
            public void run() {
                // First, display this screen
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        UiApplication.getUiApplication().pushScreen(thisScreen);
                    }
                });
                // Now run the code that must be executed in the Background
                try {
                    runThis.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                    throw new RuntimeException("Exception detected while waiting: " + t.toString());
                }
                // Now dismiss this screen
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        UiApplication.getUiApplication().popScreen(thisScreen);
                    }
                });
            }
        };
        threadToRun.start();
    }
}
