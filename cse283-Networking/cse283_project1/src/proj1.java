import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class proj1 {
	private static AtomicBoolean isRunning;
	
	public static void main(String[] args) {
		int port = -1;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
			System.out.println("No port provided. Defaulting to 6789.");
			port = 6789;
		}
		
		isRunning = new AtomicBoolean(true);
		Socket s;
		
		//try to create a socket to listen on
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fatal error. Unable to create server socket.");
			System.exit(1);
		}
		
		//System.out.println("INFO: Started listening on port " + port);
		
		while(isRunning.get()) {
			// Listen for HTTP service requests
			try {
				s = serverSocket.accept(); //accept request
				HttpRequest req = new HttpRequest(s); //create new request object
				Thread t = new Thread(req); //run in new thread
				t.start(); // start thread
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
			e.printStackTrace();
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception {
		String clientSocket = socket.getRemoteSocketAddress().toString().substring(1);
		
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String requestLine = br.readLine();
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = "." + tokens.nextToken();
		System.out.println("INFO: " + clientSocket + " requesting " + fileName);
		
		while(br.readLine().length() != 0) {
			//Discard headers
		}
		
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
			os.writeBytes("HTTP/1.0 " + HttpResponseCodes.c200 + CRLF);
			String contentType = getContentType(fileName);
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
			os.writeBytes("Content-type: text/html" + CRLF);
			os.writeBytes(CRLF);
			String html = "<!doctype html>"
					+ "<html>"
					+ "<head><title>Not Found</title></head>"
					+ "<body>404: File not found</body>"
					+ "</html>";
			os.writeBytes(html);
		}

		os.close();
		br.close();
		socket.close();
	}
	
	private String getContentType(String fileName) {
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);
		switch(ext) {
		case "gif":
			return "image/gif";
		case "jpeg":
		case "jpg":
			return "image/jpeg";
		case "png":
			return "image/png";
		case "pdf":
			return "application/pdf";
		case "zip":
			return "application/zip";
		default:
			return "application/octet-stream";
		}
	}
}
final class HttpResponseCodes {
	public static String c200 = "200 OK";
	public static String c403 = "403 Forbidden";
	public static String c404 = "404 Not Found";
}