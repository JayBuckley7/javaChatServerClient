
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * connects to the server, gets and sends messages from other clients
 * 
 * @author Jay Buckley
 */
public class UnoClient implements Runnable {

	private static final Scanner in = new Scanner(System.in);
	private static String h;
	private static int p;
	public static String name;

	public UnoClient(String h2, int p2) {
		h = h2;
		p = p2;
	}

	/**
	 * Provides simple input from the console
	 */

	/**
	 * main method where the things happen defines server ip, port, and client name
	 * sends and receives messages
	 * 
	 * @param args
	 *            java is old -
	 */
	public static void main(String[] args) {
		/**
		 * some information about the client
		 */
		System.out.println("ChatClient started at " + new Date().toString());
		final String serverHost = "localhost";// consoleReader.getString("Enter
												// server's host name or IP: ");
		h = serverHost;
		final int serverPort = 1400;// consoleReader.getPortNumber("Enter
									// server's port number: ");
		p = serverPort;
		System.out.print("please enter a nick name: ");
		name = in.nextLine();
		Player p1 = new Player(name);
		/**
		 * connect to specified server
		 */
		UnoClient c = new UnoClient(h, p);
		Thread t = new Thread(c);
		t.start();
		try {
			InetAddress server = InetAddress.getByName(serverHost);
			System.out.println("finding a match from: " + server.getCanonicalHostName() + " on port " + serverPort);

			Socket chatClientSocket = new Socket(serverHost, serverPort);
			System.out.println("match found: ");
			System.out.println("=====-----=====-----=====------====");
			System.out.println("welcome to uno!");
			System.out.println("Commands: !start !msg !Uno !noCall !cmd");
			System.out.println("type command !start to start the game:");
			System.out.println("=====-----=====-----=====------====");

			/**
			 * defines some data streams to look at
			 */
			PrintWriter pw = new PrintWriter(chatClientSocket.getOutputStream());
			BufferedReader brServer = new BufferedReader(new InputStreamReader(chatClientSocket.getInputStream()));
			BufferedReader brClient = new BufferedReader(new InputStreamReader(System.in));
			/**
			 * reads in messages and sends them back out
			 */
			String inText;
			while ((inText = in.nextLine()) != null) {
				pw.println(name + "::" + inText);
				pw.flush();
			}

			System.out.println("Closing connection with ChatServer");

			pw.close();
			brClient.close();
			brServer.close();
			chatClientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	@Override
	public void run() {
		boolean loop = true;
		try {
			InetAddress server = InetAddress.getByName(h);
			Socket chatClientSocket = new Socket(h, p);
			/**
			 * defines some data streams to look at
			 */
			PrintWriter pw = new PrintWriter(chatClientSocket.getOutputStream());
			BufferedReader brServer = new BufferedReader(new InputStreamReader(chatClientSocket.getInputStream()));
			BufferedReader brClient = new BufferedReader(new InputStreamReader(System.in));
			/**
			 * reads in messages and sends them back out
			 */
			boolean looping = true;
			while (looping) {
				pw.println();
				pw.flush();

				String outText = brServer.readLine();
				String newText = outText.replace("/000", "\n");
				if (!newText.equals("")) {
					System.out.println(newText);
				}
				Thread.sleep(50);
			}

			System.out.println("Closing connection with ChatServer");

			pw.close();
			brClient.close();
			brServer.close();
			chatClientSocket.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();

		}
	}

}