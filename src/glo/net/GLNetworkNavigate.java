package glo.net;

/**
 * This Class is setup to navigate a network by POSTING and GETTING
 * Data via a URL.
 * 
 * @author rahibbert
 */
import glo.net.GLNetwork;
import glo.net.GLNetworkAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.HttpConnection;

import javax.microedition.io.Connection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.Arrays;

public class GLNetworkNavigate extends Thread implements GLNetwork {

	// List of all possible connections
	protected ConnectionFactory factory = new ConnectionFactory();

	private int timesAlerted = 0;
	// Create preference ordered list of transports
	private int[] transportOrder = { TransportInfo.TRANSPORT_TCP_WIFI,
			TransportInfo.TRANSPORT_BIS_B, TransportInfo.TRANSPORT_WAP2,
			TransportInfo.TRANSPORT_TCP_CELLULAR, TransportInfo.TRANSPORT_MDS };

	// Holds all available connection methods
	private int[] transportsAvailable = {};

	private boolean result = false;

	public GLNetworkNavigate() {
		// Remove any transports that are not (currently) available
		String transportList = "";
		for (int a = 0; a < transportOrder.length; a++) {
			int transport = transportOrder[a];
			if (DeviceInfo.isSimulator()
					|| (TransportInfo.isTransportTypeAvailable(transport) || TransportInfo
							.hasSufficientCoverage(transport))) {
				String msg = "";
				switch (transport) {
					case TransportInfo.TRANSPORT_TCP_WIFI:
						msg = "WIFI";
						transportList += " " + msg;
						break;
	
					case TransportInfo.TRANSPORT_BIS_B:
						msg = "BIS";
						transportList += " " + msg;
						break;
	
					case TransportInfo.TRANSPORT_WAP2:
						msg = "WAP2";
						transportList += " " + msg;
						break;
						
					case TransportInfo.TRANSPORT_TCP_CELLULAR:
						msg = "TCP";
						transportList += " " + msg;
						break;
	
					case TransportInfo.TRANSPORT_MDS:
						msg = "Found MDS";
						transportList += " " + msg;
						break;
				}
				System.out.println("GL [II] " + msg);

				Arrays.add(transportsAvailable, transport);
			} 
		}
	}

	public boolean canConnect() {
		// Set ConnectionFactory options
		if (transportsAvailable.length > 0) {
			factory.setPreferredTransportTypes(transportsAvailable);
			factory.setAttemptsLimit(GLNetworkManager.getMaximumNetworkFailures());
			result = true;
			if (timesAlerted < 4) {
				timesAlerted++;
			}
		} else {
			result = false;
			if (timesAlerted < 4) {
				// GLUtils.alert("Cannot connect, NO transports");
				timesAlerted++;
			}
		}

		return result;
	}

	public void get(String tmpUrl, final GLNetworkAction action) {
		if(tmpUrl == null){
			System.out.println("GL [EE] Asked to get NULL url, exiting");
			return;
		}
		ConnectionDescriptor cd = null;
		if ((TransportInfo
				.isTransportTypeAvailable(TransportInfo.TRANSPORT_TCP_WIFI) && TransportInfo
				.hasSufficientCoverage(TransportInfo.TRANSPORT_TCP_WIFI))) {
			tmpUrl += ";interface=wifi";
//			deviceside=true
		} else {
//			tmpUrl += ";deviceside=true";
		}

		final String url = tmpUrl;
		action.setUrl(url); // set the url
		action.setRequest(this);
		try {
			factory.setTimeLimit(GLNetworkManager.getUrlTimeout()); // set the
																	// time
																	// limit to
																	// 15
																	// seconds
			cd = factory.getConnection(url);
		} catch (Exception e) {
			// Data is unreachable
			action.urlUnreachable();
		}
		if (cd != null) {
			Connection con = cd.getConnection();
			OutputStream os = null;
			InputStream is = null;
			byte[] data;
			int responseCode;

			try { // Send HTTP GET to the server
				OutputConnection outputConn = (OutputConnection) con;
				os = outputConn.openOutputStream();

				String getCommand = "GET " + "/" + " HTTP/1.0\r\n\r\n";
				os.write(getCommand.getBytes());
				os.flush();

				// Get InputConnection and read the server's response
				InputConnection inputConn = (InputConnection) con;
				is = inputConn.openInputStream();
				data = net.rim.device.api.io.IOUtilities.streamToBytes(is);

				String resultStr = new String(data);

				responseCode = ((HttpConnection) con).getResponseCode();
				
				action.status(resultStr,responseCode);
				if (responseCode != HttpConnection.HTTP_OK) {
					action.failure(resultStr, responseCode);
				} else {
					action.success(resultStr, responseCode);
				}
			} catch (Exception e) {
				e.toString();
			} finally {
				if (os != null) { // Close OutputStream
					try {
						os.close();
					} catch (IOException e) {
					}
				}

				if (is != null) { // Close InputStream
					try {
						is.close();
					} catch (IOException e) {
					}
				}

				// Close Connection
				try {
					con.close();
				} catch (IOException ioe) {
				}
			}
		} else {
			// Data is unreachable
			action.urlUnreachable();
		}
	}

	/**
	 * @deprecated
	 */
	public void post(final String url, final Object data,
			final GLNetworkAction action) {
		ConnectionDescriptor cd = factory.getConnection(url);
		if (cd != null) {
			Connection con = cd.getConnection();
			OutputStream os = null;
			InputStream is = null;
			int responseCode;

			try {
				// Set the request method and headers
				((HttpConnection) con).setRequestMethod(HttpConnection.POST);
				// ((HttpConnection) con).setRequestProperty("Content-Language",
				// "en-US");
				OutputConnection outputConn = (OutputConnection) con;
				os = outputConn.openOutputStream();
				os.write(((String) data).getBytes());
				os.flush();

				responseCode = ((HttpConnection) con).getResponseCode();
//				action.status(resultStr,responseCode);
				if (responseCode != HttpConnection.HTTP_OK) {
					action.failure(data, responseCode);
				} else {
					action.success(data, responseCode);
				}

				// Not currently useful
				InputConnection inputConn = (InputConnection) con;
				is = inputConn.openInputStream();

				// Get the ContentType, may be useful
				// String type = ((ContentConnection) con).getType();
			} catch (Exception e) {
				e.toString();
			} finally {
				if (os != null) { // Close OutputStream
					try {
						os.close();
					} catch (IOException e) {
					}
				}

				if (is != null) { // Close InputStream
					try {
						is.close();
					} catch (IOException e) {
					}
				}

				// Close Connection
				try {
					con.close();
				} catch (IOException ioe) {
				}
			}
		}
	}

}
