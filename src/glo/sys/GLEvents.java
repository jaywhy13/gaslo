package glo.sys;

import java.util.Hashtable;
import java.util.Vector;

public class GLEvents {
	private static Hashtable events = new Hashtable();
	
	public static void addEvent(String eventName){
		if(!events.containsKey(eventName)){
			events.put(eventName, new Vector());
		}
	}
	
	public static boolean eventExists(String eventName){
		return events.containsKey(eventName);
	}
	
	public static Vector getListeners(String eventName){
		if(eventExists(eventName)){
			return (Vector) events.get(eventName);
		}
		
		return new Vector();
	}
	
	public static void triggerEvent(String eventName){
		if(eventExists(eventName)){
			notifyListeners(eventName);
		}
	}
	
	public static void addEventListener(String eventName, GLEventListener listener){
		if(eventExists(eventName)){
			Vector listeners = getListeners(eventName);
			listeners.addElement(listener);
			events.put(eventName, listeners);
		}
	}
	
	private static void notifyListeners(String eventName){
		Vector listeners = getListeners(eventName);
		int numberOfListeners = listeners.size();
		System.out.println("GL [II] Notifying " + numberOfListeners + " listener(s) of: " + eventName);

		for(int i = 0; i < numberOfListeners; i++){
			GLEventListener listener = (GLEventListener) listeners.elementAt(i);
			listener.eventOccurred(eventName, null);
		}
	}
	
}
