package ro.pub.cs.systems.pdsd.practicaltest02var05;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;

import android.util.Log;


@SuppressWarnings("deprecation")
public class CommunicationThread extends Thread {
	
	private ServerThread serverThread;
	private Socket       socket;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public CommunicationThread(ServerThread serverThread, Socket socket) {
		this.serverThread = serverThread;
		this.socket       = socket;
	}
	
	@Override
	public void run() {
		if (socket != null) {
			try {
				BufferedReader bufferedReader = Utilities.getReader(socket);
				PrintWriter    printWriter    = Utilities.getWriter(socket);
				
				if (bufferedReader != null && printWriter != null) {
					Log.i(Constants.TAG, "[COMMUNICATION THREAD] Nothing to read!");
				}
				
				Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for for client to send me a (key,value)");
				// citesc o comanda si o parsez
				String command = bufferedReader.readLine();
				String[] parsedCommand = command.split(",");
						
				if (parsedCommand[0].equals("put")) {
					String key = parsedCommand[1];
					String value = parsedCommand[2];
				
					Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the time from the webservice...");
					
					String url = Constants.WEB_SERVICE_ADDRESS;
					 
					HttpClient client = new DefaultHttpClient();
					HttpGet request = new HttpGet(url);

					request.addHeader("User-Agent", USER_AGENT);
					HttpResponse response = client.execute(request);
			 
					BufferedReader rd = new BufferedReader(
			                       new InputStreamReader(response.getEntity().getContent()));
			 
					StringBuffer result = new StringBuffer();
					String line = "";
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
			 
					// Am primit timestamp, il trimit la server
					serverThread.setData(key, value, result.toString());
				}
				
				if (parsedCommand[0].equals("get")) {
					String key = parsedCommand[1];
					String value = serverThread.getData(key);
					
					if (value == null)
						printWriter.println("Nu exista");
					else
						printWriter.println(value);
				}
						

				socket.close();
				} catch (IOException ioException) {
					Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
					if (Constants.DEBUG) {
						ioException.printStackTrace();
				}
			}
		}
	}
}
