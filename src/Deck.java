


import java.util.Random;

public class Deck {
	private Card[] deck = new Card[108];
	private static int pos = 0;


	public Deck() {
			}

	
	public void add(String color, String type) {
		this.deck[pos] = new Card(color, type);
		Deck.pos++;
	}
	
	private static int RandomCard() {
		Random r = new Random();
		return r.nextInt(pos);
	}
	
	public Card draw() {
		pos--;
		return deck[RandomCard()];		
	}

	
	
}
