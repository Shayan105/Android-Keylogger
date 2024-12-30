import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.PrintWriter;
import java.net.URL;

public class Main {
	public static void main(String[] args) {
		String ip = "http://key.spotiphi.org"; // The URL
		int port = 80; // The port number
		String message = "Hello, secure world!"; // The message to send

		try {
			// Parse the hostname from the URL
			URL url = new URL(ip);
			String hostname = url.getHost(); // Extract "key.spotiphi.org"

			// Create an SSLSocketFactory
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

			// Create an SSLSocket connection
			SSLSocket sslSocket = (SSLSocket) factory.createSocket(hostname, port);
			System.out.println("Connected securely to " + hostname + " on port " + port);

			// Write the message to the socket
			PrintWriter pw = new PrintWriter(sslSocket.getOutputStream(), true);
			pw.write(message.concat("\n"));
			pw.flush();

			// Close the socket
			sslSocket.close();
			System.out.println("Message sent and connection closed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
