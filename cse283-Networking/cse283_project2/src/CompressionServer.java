import java.io.*;
import java.util.zip.*;
import java.net.*; // for Socket, ServerSocket, and InetAddress

public class CompressionServer {
	private static final int BUFFER = 8096;

	public static void main(String[] args) throws IOException {
		int port = -1;
		try {
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e) {
//			System.out.println("No port provided. Defaulting to 6789.");
			port = 5000;
		}
		int servPort = Integer.parseInt(args[0]);

		// Create a server socket to accept client connection requests
		ServerSocket servSock = new ServerSocket(servPort);

		int recvMsgSize; // Size of received message
		byte[] byteBuffer = new byte[BUFFER]; // Receive buffer

		for (;;) { // Run forever, accepting and servicing connections
			Socket clntSock = servSock.accept(); // Get client connection

			InputStream in = clntSock.getInputStream();
			OutputStream out = clntSock.getOutputStream();
			
			BufferedInputStream bin = new BufferedInputStream(in, BUFFER);
			BufferedOutputStream bout = new BufferedOutputStream(out, BUFFER);

			compress(in, out, "proj2.bin");

			clntSock.close(); // Close the socket. We are done with this client!
		}
		/* NOT REACHED */
	}

	private static void compress(InputStream origin, OutputStream dest, String fileName) {
		try {
			ZipOutputStream out = new ZipOutputStream(dest);
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			ZipEntry entry = new ZipEntry(fileName);
			out.putNextEntry(entry);
			int count;
			while ((count = origin.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
				out.flush();
				dest.flush();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}