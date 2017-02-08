import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket; // for Socket, ServerSocket, and InetAddress
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

public class CompressionServer {
	private static final int BUFFER = 8096;
	private static final String magicString = "--------MagicStringCSE283Miami";

	public static void main(String[] args) throws Exception {
		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			port = 5000;
		}

		// Create a server socket to accept client connection requests
		String ksName = "keystore.jks";
		char ksPass[] = "password".toCharArray();
		char ctPass[] = "password".toCharArray();
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(ksName), ksPass);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, ctPass);
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(kmf.getKeyManagers(), null, null);
		SSLServerSocketFactory ssf = sc.getServerSocketFactory();
		
		// Establish the listen socket.
		ServerSocket socket = ssf.createServerSocket(port);

		for (;;) { // Run forever, accepting and servicing connections
			Socket clntSock = socket.accept(); // Get client connection
			System.out.println("Handling new connection");
			InputStream in = clntSock.getInputStream();
	
			compress(in);

			clntSock.close(); // Close the socket. We are done with this client!
		}
		/* NOT REACHED */
	}

	/**
	 * Gets the filename for the file given an input stream
	 * @param in InputStream
	 * @return filename
	 * @throws IOException
	 */
	private static String getFileName(InputStream in) throws IOException {
		int count;
		byte data[] = new byte[BUFFER];
		String fileName = "";
		while ((count = in.read(data, 0, BUFFER)) != -1) {
			String str = new String(data, 0, count, "US-ASCII");
	    	  
			int index = str.indexOf(magicString);
			if (index != -1) {
				fileName = fileName + new String(data, 0, index);
				break;
			}
			fileName = fileName + new String(data, 0, count);
		}
		
		return fileName;
	}
	
	private static void compress(InputStream origin) {
		FileOutputStream uncompressed = null;
		FileOutputStream compressed = null;
		ZipOutputStream zip = null;
		String fileName = null;
		
		try {			
			int count;
			byte data[] = new byte[BUFFER];
			//String str_last = "";
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				String str = new String(data, 0, count, "US-ASCII");
		    	
				int magic = str.indexOf(magicString);
				if(fileName == null && magic > 0) {
					fileName = new String(data, 0, magic);
					uncompressed = new FileOutputStream(fileName);
					compressed = new FileOutputStream(fileName + ".zip");
					zip = new ZipOutputStream(compressed);
					ZipEntry entry = new ZipEntry(fileName);
					zip.putNextEntry(entry);
					System.out.println("Filename: " + fileName);
				} 
				else if (uncompressed != null){
					//magic = (str_last + str).lastIndexOf(magicString);
					//boolean endOfFile = false;
					//if(str_last.length()+count-magic-magicString.length() == 0)
					//	endOfFile = true;
					//if (endOfFile) {
					//	uncompressed.write(data, 0, magic);
					//	zip.write(data, 0, magic);
					//	break;
					//}
					uncompressed.write(data, 0, count);
			    	zip.write(data, 0, count);
				}
				
				//str_last = str;
			}
			zip.close();
			compressed.close();
			uncompressed.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}