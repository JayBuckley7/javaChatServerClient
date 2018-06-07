package com.chat;

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
public class ChatClient implements Runnable {
	static PrintWriter pw;
	static BufferedReader brServer;
	static String serverHost;
	static int serverPort;

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
		Scanner in = new Scanner(System.in);
		String name = "";
		System.out.println("ChatClient started at " + new Date().toString());
		serverHost = "localhost";// consoleReader.getString("Enter
									// server's host name or IP: ");
		System.out.print("Enter server's port number: ");
		serverPort = in.nextInt();

		System.out.print("name: ");
		name = in.nextLine();
		/**
		 * connect to specified server
		 */
		try {
			InetAddress server = InetAddress.getByName(serverHost);
			System.out.println("Connecting to " + server.getCanonicalHostName() + " on port " + serverPort);

			Socket chatClientSocket = new Socket(serverHost, serverPort);
			System.out.println("Connection successful");
			/**
			 * defines some data streams to look at
			 */
			pw = new PrintWriter(chatClientSocket.getOutputStream());
			BufferedReader brClient = new BufferedReader(new InputStreamReader(System.in));

			ChatClient c = new ChatClient();
			Thread t = new Thread(c);
			t.start();

			/**
			 * reads in messages and sends them back out
			 */
			String inText;
			System.out.println("Enter Message: ");
			while ((inText = in.nextLine()) != null) {
				pw.println("from " + name + "::" + inText);
				pw.flush();
				System.out.print("Enter message: ");
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
		Socket chatClientSocket = new Socket(serverHost, serverPort);
		BufferedReader brServer = new BufferedReader(new InputStreamReader(chatClientSocket.getInputStream()));

		while (loop) {

			String outText = brServer.readLine();
			
			if (outText == null) {
				System.out.println("ChatServer has died");
				loop = false;
				System.exit(0);
			}
			
			String newText = outText.replace("/000", "\n");
			System.out.println(newText);
		}
		
		chatClientSocket.close();
	}catch (IOException e) {
		e.printStackTrace();

	}
	}

}