package glo.net;

public interface GLNetwork {
	
	/**
	 * Returns true if we are able to connect to the network via any of the 
	 * means provided by the phone for Internet connectivity 
	 * @return
	 */
	public boolean canConnect();
	
	
	/**
	 * Gets data from a url and then calls success or failure on the action 
	 * parameter passed. 
	 * @param url - url that needs to be accessed. 
	 * @param action - an action that will act on the response from the server 
	 */
	public void get(String url, GLNetworkAction action);	
	
	
	/**
	 * Posts data to a url then calls success or failure on the action.
	 * @param url - url that data should be posted to.
	 * @param data - data that should be posted to the url 
	 * @param action - action that will act on the response from the server
	 */
	public void post(String url, Object data, GLNetworkAction action);
	
}
