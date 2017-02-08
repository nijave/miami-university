import java.io.*;
import java.util.zip.*;
import java.net.*; // for Socket, ServerSocket, and InetAddress

public class CompressionServer {
	private static final int ECHOMAX = 65535; // Maximum size of a udp datagram
	private static DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);
	private static final String magicString = "--------MagicStringCSE283Miami";

	public static void main(String[] args) throws IOException {
		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			port = 5000;
		}

		// Create a server socket to accept client connection requests
		DatagramSocket socket = new DatagramSocket(port);
		socket.setReceiveBufferSize(1024*1024*10); //10MB receive buffer

		for (;;) { // Run forever, accepting and servicing connections
			socket.receive(packet); // Receive packet from client
			String filename = new String(packet.getData(), 0, packet.getLength());
			filename = filename.trim();
			
			compress(socket, filename);
		}
		/* NOT REACHED */
	}

	private static void compress(DatagramSocket server, String fileName) {
		try {
			FileOutputStream uncompressed = new FileOutputStream(fileName);
			FileOutputStream compressed = new FileOutputStream(fileName + ".zip");
			ZipOutputStream zip = new ZipOutputStream(compressed);
			ZipEntry entry = new ZipEntry(fileName);
			zip.putNextEntry(entry);
			
			while(true) {
		    	  server.receive(packet);
		    	  byte[] data = packet.getData();
		    	  String str = new String(data, 0, packet.getLength());
		    	  
		    	  int index = str.indexOf(magicString);
		    	  if(index != -1) {
		    		  uncompressed.write(data, 0, index);
		    		  zip.write(data, 0, index);
		    		  break;
		    	  }
		    	  uncompressed.write(data, 0, packet.getLength());
		    	  zip.write(data, 0, packet.getLength());
		    }
			
			uncompressed.close();
			zip.close();
			compressed.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}