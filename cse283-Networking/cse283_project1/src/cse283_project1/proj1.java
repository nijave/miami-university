package cse283_project1;

import java.io.*;
import java.net.*;
import java.util.*;

public final class proj1 {
	public static void main(String[] args) {
		boolean isRunning = true;
		int port = 6789;
		
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fatal error. Unable to create server socket.");
			System.exit(1);
		}
		
		System.out.println("INFO: Started listening on port " + port);
		
		while(isRunning) {
			// Listen for HTTP service requests
			try {
				Socket s = serverSocket.accept();
				HttpRequest req = new HttpRequest(s);
				Thread t = new Thread(req);
				t.start();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Failed to handle incoming connection.");
			}
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("INFO: Stopping server on port " + port);
		
		System.exit(0);
	}
}
final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;
	
	public HttpRequest(Socket s) throws Exception {
		this.socket = s;
	}
	
	public void run() {
		try {
			this.processRequest();
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception {
		String clientSocket = socket.getRemoteSocketAddress().toString().substring(1);
		
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		//System.out.println("INFO: Opening connection to: " + clientSocket);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String requestLine = br.readLine();
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = "." + tokens.nextToken();
		System.out.println("INFO: " + clientSocket + " requesting " + fileName);
		
		while(br.readLine().length() != 0) {
			//Discard headers
		}
		
		if (fileName.contains("../") 
				|| fileName.equals("proj1.java")
				|| fileName.equals("proj1.class")) {
			//then...
			os.writeBytes("HTTP/1.0 " + HttpResponseCodes.c403 + CRLF);
		}
		else {
			FileInputStream fis = null;
			boolean fileExists = true;
			
			//attempt to open the file
			try {
				fis = new FileInputStream(fileName);
			}
			catch (FileNotFoundException e) {
				fileExists = false;
			}
			
			if(fileExists) {
				os.writeBytes("HTTP/1.1 " + HttpResponseCodes.c200 + CRLF);
				//String contentType = (new URL(fileName)).openConnection().getContentType();
				String contentType = "text/plain";
				String contentHeader = "Content-type: " + contentType + CRLF;
				os.writeBytes(contentHeader + CRLF);
				
				//buffer
				byte[] buffer = new byte[1024];
				int bytes = 0;
				while((bytes = fis.read(buffer)) != -1) {
					//and send
					os.write(buffer, 0, bytes);
				}
			}
			else {
				os.writeBytes("HTTP/1.0 " + HttpResponseCodes.c404 + CRLF);
			}
		}
		
		//System.out.println("INFO: Closing connection to: " + clientSocket);

		os.close();
		br.close();
		socket.close();
	}
}
final class HttpResponseCodes {
	public static String c200 = "200 OK";
	public static String c403 = "403 Forbidden";
	public static String c404 = "404 Not Found";
}