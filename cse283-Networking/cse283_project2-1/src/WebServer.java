import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

final class HttpRequest implements Runnable {
	final static int BUF_SIZE = 1024000;
	final static String CRLF = "\r\n";
	final static String compressionIP = "127.0.0.1";
	final static int compressionPort = 5000;

	byte[] buffer;
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
		buffer = new byte[BUF_SIZE];
	}

	// Implement the run() method of the Runnable interface.
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private String getContent() throws Exception {
		int rcv = 0;
		int uploadStart = -1;
		int uploadEnd = -1;
		String headers = "";
		String boundary = null;
		String filename = null;
		FileOutputStream fos = null; //uncompressed file output stream
		FileOutputStream zfos = null; //zipped file output stream
		Socket compression = null; //compression server socket
		InputStream cis = null; //compression input stream
		OutputStream cos = null; //compression output stream
		
		while (rcv != -1) {
			boolean containsEnd = false;
			byte _buffer[] = new byte[BUF_SIZE]; //compression receive buffer
			buffer = new byte[BUF_SIZE];
			
			rcv = socket.getInputStream().read(buffer, 0,
				BUF_SIZE);
			String msg = new String(buffer, 0, rcv, "US-ASCII");
			
			if(msg.indexOf("Upload"+CRLF) != -1) {
				containsEnd = true;
			}
			
			if(msg.startsWith("POST")) {
				int contentType = msg.indexOf("Content-Type");
				int contentTypeEnd = msg.indexOf("\r\n", contentType);
				boundary = "--" + msg.substring(contentType, contentTypeEnd).split("boundary=")[1];
				headers = new String(buffer, 0, msg.indexOf(boundary));
				
				uploadStart = msg.indexOf("Content-Disposition");
				uploadStart = msg.indexOf("\r\n\r\n", uploadStart) + 4;
				fos = new FileOutputStream(new File("./temp"));
				zfos = new FileOutputStream(new File("./temp.zip"));
				compression = new Socket(compressionIP, compressionPort);
				cis = compression.getInputStream();
				cos = compression.getOutputStream();
			}
			else if(msg.startsWith("GET")) {
				return new String(buffer, 0, rcv);
			}
			if (uploadStart >= 0 && uploadEnd == -1) {
				uploadEnd = msg.indexOf(boundary, uploadStart);
				if(uploadEnd != -1)
					uploadEnd -= 2;
			}
			
			//handle saving file
			if(uploadStart >= 0) {
				int _uploadEnd = uploadEnd == -1 ? rcv : uploadEnd;
				fos.write(buffer, uploadStart, _uploadEnd-uploadStart);
				cos.write(buffer, uploadStart, _uploadEnd-uploadStart);
				
				//receive compressed file
				if(cis.available() > 0) {
					rcv = cis.read(_buffer, 0, BUF_SIZE);
					zfos.write(_buffer, 0, rcv);
				}
				
				uploadStart = 0;
			}
			//close file if end is reached
			if(uploadEnd > 0) {
				
				compression.shutdownOutput();
				rcv = 0;
				//finish receiving compressed file
				while((rcv = cis.read(_buffer)) != -1) {
					zfos.write(_buffer, 0, rcv);
				}
				
				fos.close();
				zfos.close();
				compression.close();
				uploadEnd = -1;
				uploadStart = -1;
			}
			
			int filename_index = msg.indexOf("Content-Disposition: form-data; name=\"destination\"");
			if(filename_index != -1) {				
				int filename_start = msg.indexOf("\r\n\r\n", filename_index) + 4;
				int filename_end = msg.indexOf(boundary, filename_index) - 2;
				filename = new String(buffer, filename_start, filename_end-filename_start);
				(new File("./temp")).renameTo(new File(filename));
				(new File("./temp.zip")).renameTo(new File(filename + ".zip"));
			}
			
			// Only loop if it is not a GET message and have not reached
			// end of POST message, Upload+CRLF represents end of request
			if (msg.startsWith("GET") || containsEnd) {
				//System.out.println("EXITING");
				break;
			}
		}
		// returns the total bytes in the buffer
		return headers;
	}

	private void processRequest() throws Exception {

		String msg = getContent();

		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(msg);
		String method = tokens.nextToken(); // skip over the method, which
											// should be "GET"
		
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		boolean fileExists = false;
		FileInputStream fis = null;
		if(method.equals("GET")) {
			String fileName = tokens.nextToken();
	
			// Prepend a "." so that file request is within the current directory.
			fileName = "." + fileName;
	
			// Open the requested file.
			fileExists = true;
			try {
				fis = new FileInputStream(fileName);
			} catch (FileNotFoundException e) {
				fileExists = false;
			}
	
			// Construct the response message.
			if (fileExists) {
				statusLine = "HTTP/1.0 200 OK" + CRLF;
				contentTypeLine = "Content-Type: " + contentType(fileName) + CRLF;
			} else {
				statusLine = "HTTP/1.0 404 Not Found" + CRLF;
				contentTypeLine = "Content-Type: text/html" + CRLF;
				entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>"
						+ "<BODY>Not Found</BODY></HTML>";
			}
		}
		else if (method.equals("POST")) {
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Upload Successful</TITLE></HEAD>"
					+ "<BODY>File uploaded successfully</BODY></HTML>";
		}
		// Send the status line.
		os.writeBytes(statusLine);

		// Send the content type line.
		os.writeBytes(contentTypeLine);

		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes(entityBody);
		}

		// Close streams and socket.
		os.close();
		socket.close();
	}

	private static void sendBytes(FileInputStream fis, OutputStream os)
			throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".png")) {
			return "image/png";
		}
		if (fileName.endsWith(".pdf")) {
			return "application/pdf";
		}
		if (fileName.endsWith(".zip")) {
			return "application/zip";
		}
		if (fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		return "application/octet-stream";
	}
}

public final class WebServer {
	public static void main(String argv[]) throws Exception {
		// Get the port number from the command line.
		int port = Integer.parseInt(argv[0]);

		// Establish the listen socket.
		ServerSocket socket = new ServerSocket(port);

		// Process HTTP service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			Socket connection = socket.accept();

			// Construct an object to process the HTTP request message.
			HttpRequest request = new HttpRequest(connection);

			// Create a new thread to process the request.
			Thread thread = new Thread(request);

			// Start the thread.
			thread.start();
		}
	}
}