package glo.misc;

import glo.ui.GLSplashScreen;

import java.util.TimerTask;

public class GLPrerequisite {
	
	public static final int INITIALIZED = 0;
	public static final int STARTED = 1;
	public static final int ERROR = 2;
	public static final int TIMEDOUT = 3;
	public static final int COMPLETE = 4;
	
	public static final int POLICY_RETRY = 0;
	public static final int POLICY_FAIL = 1;
	
	private GLPrerequisites prereqs;
	
	private TimerTask timer = null;
	
	private String startupMessage = null;
	
	private String completeMessage = null;
	
	private boolean proceedOnTimeout = false;
	
	public String getStartupMessage() {
		return startupMessage;
	}

	public void setStartupMessage(String startupMessage) {
		this.startupMessage = startupMessage;
	}

	public String getCompleteMessage() {
		return completeMessage;
	}

	public void setCompleteMessage(String completeMessage) {
		this.completeMessage = completeMessage;
	}

	private int errorPolicy = 0;
	
	private int maximumRetries = 3;
	
	public int getErrorPolicy() {
		return errorPolicy;
	}

	public void setErrorPolicy(int errorPolicy) {
		this.errorPolicy = errorPolicy;
	}

	public int getMaximumRetries() {
		return maximumRetries;
	}

	public void setMaximumRetries(int maximumRetries) {
		this.maximumRetries = maximumRetries;
	}

	public GLPrerequisite(String name, int timeout) {
		super();
		this.name = name;
		this.timeout = timeout;
	}

	public GLPrerequisites getPrereqs() {
		return prereqs;
	}

	public void setPrereqs(GLPrerequisites prereqs) {
		this.prereqs = prereqs;
	}

	private String name;
	
	private int timeout = -1;
	
	private int status = 0;
	
	private boolean proceedOnFailure = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		if(this.status == STARTED){
			if(startupMessage != null){
				GLSplashScreen.setMessage(startupMessage);
			}
			
			timer = new TimerTask() {
				
				public void run() {
					if(getStatus() != COMPLETE){
						setStatus(TIMEDOUT);
						GLPrerequisites.prerequisteTimedOut(getName());
					}
				}
			};
		} 
		else if(this.status == COMPLETE){
			if(completeMessage != null){
				GLSplashScreen.setMessage(completeMessage);
			}
		}
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isProceedOnTimeout() {
		return proceedOnTimeout;
	}

	public void setProceedOnTimeout(boolean proceedOnTimeout) {
		this.proceedOnTimeout = proceedOnTimeout;
	}
	
	/**
	 * Called when the preerequisite times out 
	 */
	public void timedOut(){
		
	}

	public boolean isProceedOnFailure() {
		return this.proceedOnFailure;
	}

	public void setProceedOnFailure(boolean proceedOnFailure) {
		this.proceedOnFailure = proceedOnFailure;
	}

	public void failed() {
		
		
	}
	
	
	
	
	
	
	
}
