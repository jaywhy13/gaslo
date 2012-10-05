package glo.net;

import javax.microedition.io.HttpConnection;

/**
 * This class represents an action that is performed in response 
 * to a network request. The methods success or failure are called 
 * depending on the status of the response 
 * 
 * @author JMWright
 *
 */
public class GLNetworkAction extends Thread {
	
	/**
	 * Hashmap of data to be used 
	 */
	private Object data;
	
	/**
	 * Server response code
	 */
	private int status;
	
	/**
	 * Server data confirmation variable
	 */
	private boolean resposnseSuccess = false;
	
	/**
	 * 
	 */
	private String url;
	
	
	private boolean autoRetryOnFailure = false;

	private GLNetworkNavigate request;

	
	private int numRetries = 0;
	
	private int maxRetries = 3;


	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public int getNumRetries() {
		return numRetries;
	}

	public void setNumRetries(int numRetries) {
		this.numRetries = numRetries;
	}

	public boolean isAutoRetryOnFailure() {
		return autoRetryOnFailure;
	}

	public void setAutoRetryOnFailure(boolean autoRetryOnFailure) {
		this.autoRetryOnFailure = autoRetryOnFailure;
	}

	/**
	 * This method is called if a request was executed succesfully. 
	 * @param data - json data from the server 
	 * @param status - status code 
	 */
	public void success(Object data, int status){	 
		setData(data);
		setStatus(status);
		setResposnseSuccess(true);
	}
	
	/**
	 * This method is called if a network request failed. 
	 * @param data - json data
	 * @param status - status code
	 */
	public void failure(Object data, int status) {
		setData(data);
		setStatus(status);
		setResposnseSuccess(false);
		GLNetworkManager.incrementNetworkFailure();
		System.out.println("GL [WW] Network request failed: " + getUrl() + " [code=" + status + "]\nData:\n" + data);
		retry();
		
	}
	
	/**
	 * If the server cannot be contacted through the given url 
	 * then the response is set to false.
	 */
	public void urlUnreachable() {
		setResposnseSuccess(false);
		GLNetworkManager.incrementNetworkFailure();
	}

	/**
	 * Returns the data
	 * @return
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Sets the data
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * Returns the status
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Sets the status
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * Returns response confirmation
	 * @return
	 */
	public boolean isResposnseSuccess() {
		return resposnseSuccess;
	}

	/**
	 * Sets response confirmation
	 * @param resposnseSuccess
	 */
	public void setResposnseSuccess(boolean resposnseSuccess) {
		this.resposnseSuccess = resposnseSuccess;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void status(String resultStr, int responseCode) {
		System.out.println("GL [II] Network status: " + getUrl() + " [code=" + status + "]\nData:\n" + resultStr);
		if(responseCode == HttpConnection.HTTP_MOVED_PERM || responseCode == HttpConnection.HTTP_CLIENT_TIMEOUT || responseCode == HttpConnection.HTTP_MOVED_TEMP){
			retry();
		}		
	}

	
	/**
	 * Retries if we are allowed to do that 
	 */
	protected void retry() {
		if(this.isAutoRetryOnFailure() && canRetry()){
			if(this.request != null){
				System.out.println("GL [II] Retrying " + numRetries + " of " + maxRetries + " for url: " + url);
				incrementRetries();
				this.request.get(this.getUrl(),this);
			}
		}		
	}

	public void setRequest(GLNetworkNavigate request) {
		this.request = request;
	}

	public GLNetworkNavigate getRequest() {
		return request;
	}
	
	/**
	 * Increment the number of retries 
	 */
	protected void incrementRetries(){
		this.numRetries++;
	}
	
	public boolean canRetry (){
		return this.numRetries < this.maxRetries;
	}
	
	
	
	
}
