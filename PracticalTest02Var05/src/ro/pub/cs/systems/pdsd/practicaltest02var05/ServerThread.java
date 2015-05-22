package ro.pub.cs.systems.pdsd.practicaltest02var05;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;





import android.util.Log;

@SuppressWarnings("deprecation")
public class ServerThread extends Thread {
	
	private int          port         = 0;
	private ServerSocket serverSocket = null;
	
	// Pereche cheie, valoare
	private HashMap<String, String> data = null;
	// Pereche cheie, timestamp
	private HashMap<String, String> time = null;
	
	public ServerThread(int port) {
		this.port = port;
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException ioException) {
			Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		}
		this.data = new HashMap<String, String>();
		this.time = new HashMap<String, String>();
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setServerSocker(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public synchronized void setData(String key, String new_value, String timestamp) {
		
		String value = data.get(key);
		
		// Pereche cheie,valoare noua
		if (value == null) {
			
		}
		
		// Verificare timmestamp
		String[] timeStamp = time.get(key).split("[-T:+]");
		int hour = Integer.parseInt(timeStamp[3]);
		int minutes = Integer.parseInt(timeStamp[4]);
		int seconds = Integer.parseInt(timeStamp[5]);
		
		String[] curr_timeStamp = (new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())).split(":");
		int curr_hour = Integer.parseInt(curr_timeStamp[3]);
		int curr_minutes = Integer.parseInt(curr_timeStamp[4]);
		int curr_seconds = Integer.parseInt(curr_timeStamp[5]);
		
		
		this.data.put(key, value);
		this.time.put(key, timestamp);
	}
	
	public synchronized String getData(String key) {
		// Verificare timmestamp
		String[] timestamp = time.get(key).split("[-T:+]");
		int hour = Integer.parseInt(timestamp[3]);
		int minutes = Integer.parseInt(timestamp[4]);
		int seconds = Integer.parseInt(timestamp[5]);
		
		String[] curr_timestamp = (new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())).split(":");
		int curr_hour = Integer.parseInt(curr_timestamp[3]);
		int curr_minutes = Integer.parseInt(curr_timestamp[4]);
		int curr_seconds = Integer.parseInt(curr_timestamp[5]);
		
		// Trebuie sa fie in aceeasi ora
		if (curr_hour == hour) {
			// Daca sunt in acelasi minut sau la un minut distanta
			if (curr_minutes == minutes || (curr_minutes - minutes == 1)) {
				return data.get(key);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public void run() {
		try {		
			while (!Thread.currentThread().isInterrupted()) {
				Log.i(Constants.TAG, "[SERVER] Waiting for a connection...");
				Socket socket = serverSocket.accept();
				Log.i(Constants.TAG, "[SERVER] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
				CommunicationThread communicationThread = new CommunicationThread(this, socket);
				communicationThread.start();
			}			
		} catch (ClientProtocolException clientProtocolException) {
			Log.e(Constants.TAG, "An exception has occurred: " + clientProtocolException.getMessage());
			if (Constants.DEBUG) {
				clientProtocolException.printStackTrace();
			}			
		} catch (IOException ioException) {
			Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
			if (Constants.DEBUG) {
				ioException.printStackTrace();
			}
		}
	}
	
	public void stopThread() {
		if (serverSocket != null) {
			interrupt();
			try {
				serverSocket.close();
			} catch (IOException ioException) {
				Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
				if (Constants.DEBUG) {
					ioException.printStackTrace();
				}				
			}
		}
	}

}
