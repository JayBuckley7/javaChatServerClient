
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
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/** - */
public class UnoServer {
	/** - */
	static Scanner in = new Scanner(System.in);
	public static final String SEPARATORS = " \t\n\r:";
	private static final Map<String, List<String>> clientList = new HashMap<>();
	private static Player[] playerList;

	private static String[] colors = new String[4];
	private static String RED = "Red";
	private static String YELLOW = "Yellow";
	private static String GREEN = "Green";
	private static String BLUE = "Blue";
	private static boolean started = false;

	private static String ZERO = "Zero";
	private static String ONE = "One";
	private static String TWO = "Three";
	private static String THREE = "Three";
	private static String FOUR = "Four";
	private static String FIVE = "Five";
	private static String SIX = "Six";
	private static String SEVEN = "Seven";
	private static String EIGHT = "Eight";
	private static String NINE = "Nine";
	private static String SKIP = "Skip";
	private static String REVERSE = "Reverse";
	private static String DRAW2 = "DrawTwo";
	private static String WILDDRAW4 = "WildDrawFour";
	private static String WILD = "Wild";

	private static void InitGame(Deck deck) {
		colors[0] = RED;
		colors[1] = BLUE;
		colors[2] = GREEN;
		colors[3] = YELLOW;

		refreshDeck(deck);
	}

	private static void refreshDeck(Deck d) {
		add4(d, ZERO);
		add4(d, WILD);
		add4(d, WILDDRAW4);

		add8(d, ONE);
		add8(d, TWO);
		add8(d, THREE);
		add8(d, FOUR);

		add8(d, FIVE);
		add8(d, SIX);
		add8(d, SEVEN);
		add8(d, EIGHT);

		add8(d, NINE);
		add8(d, REVERSE);
		add8(d, SKIP);
		add8(d, DRAW2);

	}

	private static void add4(Deck d, String type) {
		for (int i = 0; i < 4; i++) {
			d.add(colors[i], type);
		}
	}

	private static void add8(Deck d, String type) {
		add4(d, type);
		add4(d, type);
	}

	private static void newHand(Player p, Deck d) {
		for (int i = 0; i < 7; i++) {
			p.hand[i] = d.draw();
		}

	}

	private static String nextWordOrSeparator(String text, int position) {
		assert text != null : "Violation of: text is not null";
		assert 0 <= position : "Violation of: 0 <= position";
		assert position < text.length() : "Violation of: position < |text|";

		Set<Character> separatorSet = new HashSet<Character>();

		for (char c : SEPARATORS.toCharArray()) {
			separatorSet.add(c);
		}
		StringBuilder sb = new StringBuilder();
		int count = position;
		char slice = text.charAt(position);// take the first letter as a "slice"
		char nxtSlice = ' ';
		if (count != text.length() - 1) {
			nxtSlice = text.charAt(position + 1);
		}

		boolean isSep = separatorSet.contains(slice);
		boolean nxtSep = separatorSet.contains(nxtSlice);

		while (((isSep && nxtSep) || (!isSep && !nxtSep)) && (count < text.length() - 1)) { // over complicated

			sb.append(slice);
			count++;

			slice = text.charAt(count);// take the first letter as a "slice"
			if (count != text.length() - 1) {
				nxtSlice = text.charAt(count + 1);
			}
			isSep = separatorSet.contains(slice);
			nxtSep = separatorSet.contains(nxtSlice);

		}
		sb.append(slice);
		return sb.toString();

	}

	/**
	 * -
	 * 
	 * @param args
	 *            -
	 */
	public static void main(String[] args) {
		Deck deck = new Deck();
		InitGame(deck);

		System.out.println("ChatServer started at " + new Date().toString());
		System.out.println("port number:  ");
		final int serverPort = 1400; // in.nextInt();

		try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
			System.out.println("Listening for connections on port " + serverSocket.getLocalPort());

			while (!started) {
				Socket dataSocket = serverSocket.accept();
				UnoServerThread thread = new UnoServerThread(dataSocket);
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
	private static class UnoServerThread extends Thread {
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
		public UnoServerThread(Socket socket) {
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
				while ((inputLine = br.readLine()) != null) {
					if (!inputLine.equals("")) {

						String name = nextWordOrSeparator(inputLine, 0);
						String separ1 = nextWordOrSeparator(inputLine, name.length());
						String cmd = nextWordOrSeparator(inputLine, name.length() + separ1.length());
						int pos = name.length() + separ1.length();

						char test = cmd.charAt(0);
						if (test == ('!')) {
							handleCmd(inputLine.substring(pos + 1, inputLine.length()));
						}
						System.out.println("Client=" + this.getName() + " Received Message='" + inputLine + "'");
					}

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

	private static void checkUno() {
		// TODO Auto-generated method stub

	}

	private static void verifyUno() {
		// TODO Auto-generated method stub

	}

	private static void lockGame() {
		started=true;
		playerList = new Player[clientList.size()/2];
		System.out.print("numbers of players locked at "+playerList.length);
		

	}

	public static void parseCard(String s) {
		// TODO Auto-generated method stub

	}

	public static void handleCmd(String s) {
		String separ="";
		String card="";
		String cmd = nextWordOrSeparator(s, 0);
		if(cmd.length()<s.length()) {
		separ = nextWordOrSeparator(s, cmd.length());
		card = nextWordOrSeparator(s, cmd.length()+separ.length());
	}
		System.out.print(card);

		switch (cmd) {
		case "card":
			parseCard(s);
			break;
		case "start":
			lockGame();
			break;
		case "uno":
			verifyUno();
			break;
		case "noCall":
			checkUno();
			break;
		default:
			throw new IllegalArgumentException("Invalid day of the week: " + s);
		}

	}
}
