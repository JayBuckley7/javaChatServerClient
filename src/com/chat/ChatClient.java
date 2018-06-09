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

	private static final Scanner in = new Scanner(System.in);
	private static String h;
	private static int p;

	public ChatClient(String h2, int p2) {
		h=h2;
		p=p2;
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
		h= serverHost;
		final int serverPort = 1500;// consoleReader.getPortNumber("Enter
									// server's port number: ");
		p=serverPort;
		System.out.print("name: ");
		final String name = in.nextLine();
		/**
		 * connect to specified server
		 */
		ChatClient c = new ChatClient(h,p);
		Thread t = new Thread(c);
		t.start();
		try {
			InetAddress server = InetAddress.getByName(serverHost);
			System.out.println("Connecting to " + server.getCanonicalHostName() + " on port " + serverPort);

			Socket chatClientSocket = new Socket(serverHost, serverPort);
			System.out.println("Connection successful");
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
			System.out.print("\nEnter Message:... ");
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
		System.out.println("hey jay: Thread here. ");
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
				if(!newText.equals("")) {
				System.out.println(newText);
				}
				Thread.sleep(500);
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