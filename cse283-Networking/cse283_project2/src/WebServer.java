import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WebServer {
	private static AtomicBoolean isRunning;
	public final static String compressionServerIP = "127.0.0.1";
	public final static int compressionServerPort = 5000;
	
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
		
		while(isRunning.get()) {
			// Listen for HTTP service requests
			try {
				s = serverSocket.accept(); //accept request
				HttpRequest req = new HttpRequest(s); //create new request object
				Thread t = new Thread(req); //run in new thread
				t.start(); // start thread
			} catch (Exception se) {
				se.printStackTrace();
				System.out.println("Failed to handle incoming connection.");
			}
		}
		
		try {
			serverSocket.close();
		} catch (IOException se) {
			se.printStackTrace();
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
		String method = tokens.nextToken();
		String fileName = "." + tokens.nextToken();
		System.out.println("INFO: " + clientSocket + " requesting " + fileName);
		
		String line = null;
		String boundary = null;
		while((line = br.readLine()).length() != 0) {
			System.out.println(line);
			if(line.startsWith("Content-Type:")) {
				boundary = "--" + line.substring(line.indexOf("boundary=")+9);
			}
			//System.out.println(line);
		}
		
		if(method.equals("POST") && fileName.equals("./upload")) { //handle upload
			String filename = "";
			line = br.readLine();
			while(br.ready()) {
				if(line.equals(boundary) && br.ready()) {
					String[] contDisp = br.readLine().split(" ");
					for(String param : contDisp) {
						System.out.println(param);
						if(param.equals("name=\"file\";")) {
							br.readLine();
							br.readLine();
							filename = "temp";
							FileOutputStream uncomp = new FileOutputStream("./" + filename);

							int recvMsgSize;
							byte byteBuffer[] = new byte[2048];
							while ((recvMsgSize = is.read(byteBuffer)) != -1) {
						        String rcv = new String(byteBuffer, 0, recvMsgSize);
						        int index = rcv.indexOf(boundary);
						        if (index != -1) {
						        	uncomp.write(byteBuffer, 0, index);
						        	break;
						        }
						        uncomp.write(byteBuffer, 0, recvMsgSize);
						        System.out.println(new String(byteBuffer, 0, recvMsgSize));
						    }
							
							uncomp.close();
						}
						else if(param.equals("name=\"destination\"")) {
							br.readLine();
							filename = br.readLine();
							(new File("./temp")).renameTo(new File("./" + filename));
							
						}
					}
				}
				else if(line.equals(boundary + "--")) {
					break;
				}
				else {
					line = br.readLine();
				}
			}
			//while done
			Socket compServ = new Socket(WebServer.compressionServerIP, WebServer.compressionServerPort);
			
			InputStream cis = compServ.getInputStream();
			OutputStream cos = compServ.getOutputStream();
			FileInputStream fis = new FileInputStream(filename);
			FileOutputStream comp = new FileOutputStream("./" + filename + ".zip");
			
			byte[] bufferOut = new byte[2048];
			while(fis.read(bufferOut) > 0) {
				cos.write(bufferOut);
			}
			
			
			byte[] data = new byte[2048];
			int count;
			while((count = cis.read(data, 0, 2048)) != -1) {
				comp.write(data, 0, count);
			}
			
			cos.close();
			cis.close();
			fis.close();
			comp.close();
			compServ.close();
		}
		else { //server file or 404
			//attempt to open the file
			FileInputStream fis = null;
			boolean fileExists = true;
			try {
				fis = new FileInputStream(fileName);
			}
			catch (FileNotFoundException e) {
				fileExists = false;
			}
			if(fileExists) {
				serveFile(fileName, os, fis);
			} else {
				notFound(os);
			}
		}

		os.close();
		br.close();
		socket.close();
	}
	
	private static String getContentType(String fileName) {
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
		case "html":
			return "text/html";
		default:
			return "application/octet-stream";
		}
	}
	
	private static void serveFile(String fileName, DataOutputStream os, FileInputStream fis) throws IOException {
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
	
	private static void notFound(DataOutputStream os) throws IOException {
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
}

final class HttpResponseCodes {
	public final static String c200 = "200 OK";
	public final static String c403 = "403 Forbidden";
	public final static String c404 = "404 Not Found";
}