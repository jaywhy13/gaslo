package glo.net;

public class GLNetworkManager {
	
	private static GLNetwork nwork;
	
	private static int maximumNetworkFailures = 2;
	
	private static int urlTimeout = 4000;
	
	private static int networkFailures = 0;
	
	private static boolean enabled = true;
	
	
	public static void setNetworkManager(GLNetwork nw){
		if(GLNetworkManager.nwork == null){
			GLNetworkManager.nwork = nw;
		} 
	}
	
	public static GLNetwork getNetworkManager(){
		return nwork;
	}

	public static void setMaximumNetworkFailures(int maximumNetworkFailures) {
		GLNetworkManager.maximumNetworkFailures = maximumNetworkFailures;
	}

	public static int getMaximumNetworkFailures() {
		return maximumNetworkFailures;
	}

	public static void setUrlTimeout(int urlTimeout) {
		GLNetworkManager.urlTimeout = urlTimeout;
	}

	public static int getUrlTimeout() {
		return urlTimeout;
	}
	
	public static void incrementNetworkFailure(){
		networkFailures++;
	}

	public static void setNetworkFailures(int networkFailures) {
		GLNetworkManager.networkFailures = networkFailures;
	}
	
	private static void updateNetworkFailures(){
		if(networkFailures > maximumNetworkFailures){
			setEnabled(false);
		}
	}
	
	public static int getNetworkFailures() {
		return networkFailures;
	}

	public static void setEnabled(boolean enabled) {
		GLNetworkManager.enabled = enabled;
	}

	public static boolean isEnabled() {
		return enabled;
	}
	
}
