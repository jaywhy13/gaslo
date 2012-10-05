package glo.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class GLUtils {
	public static void alert(final String message){
		Thread t = getRunnableThread(new Runnable(){
			public void run(){
				Dialog.alert(message);
			}
		});
		runEventSafeThread(t);
	}
	
	public static Thread getRunnableThread(Runnable r){
		return new Thread(r);
	}
	
	public static void runEventSafeThread(final Thread t){
		if(UiApplication.getUiApplication().isEventThread()){
			t.start();
		} else {
			UiApplication.getUiApplication().invokeLater(t);
		}
	}

	public static void alertDebug(String message) {
		alert(message);
	}
}
