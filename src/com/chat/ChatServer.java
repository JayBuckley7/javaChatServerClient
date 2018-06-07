//===================================
// Columbus State Community College
// CSCI 2469 - Spring Semester 2017
// Assignment: Lab8
// Programmer: Craig Wright
//===================================

package com.chat;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** - */
public class ChatServer {
	/** - */
	 static Scanner in = new Scanner(System.in);

	private static final Map<String, List<String>> clientList = new HashMap<>();

	/**
	 * -
	 * 
	 * @param args
	 *            -
	 */
	public static void main(String[] args) {
		System.out.println("ChatServer started at " + new Date().toString());
		System.out.println("port number:  ");
		final int serverPort = in.nextInt();

		try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
			System.out.println("Listening for connections on port " + serverSocket.getLocalPort());

			while (true) {
				Socket dataSocket = serverSocket.accept();
				ChatServerThread thread = new ChatServerThread(dataSocket);
				System.out.println("Connection from " + dataSocket.getLocalAddress().getHostName() + ", Thread="
						+ thread.getName());
				clientList.put(thread.getName(), new ArrayList<String>());
				thread.start();
			}
		} catch (IOException e) {
			System.out.println("Exception - " + e.getMessage());
			System.exit(1);
		}
	}

	/** - */
	private static class ChatServerThread extends Thread {
		/** - */
		private static int count = 1;

		/** - */
		private Socket socket;

		/**
		 * -
		 * 
		 * @param socket
		 *            -
		 */
		public ChatServerThread(Socket socket) {
			super("ChatServerThread-" + count);
			++count;
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String inputLine;
				while ((inputLine = br.readLine()) != null) 
				{
					System.out.println("Client=" + this.getName() + " Received Message='" + inputLine + "'");
					
					List<String> myList = null;
					synchronized (clientList) {
						for (String threadName : clientList.keySet()) {
							if (threadName.equals(this.getName())) {
								myList = clientList.get(threadName);
								continue;
							} else {
								List<String> theList = clientList.get(threadName);
								theList.add(inputLine);
							}
						}
						StringBuilder sb = new StringBuilder();
						for (String msg : myList) {
							if (sb.length() != 0) {
								sb.append("\n");
							}
							sb.append(msg);
						}
						pw.println(sb.toString());
						myList.clear();
						pw.flush();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
