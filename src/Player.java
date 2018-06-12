

/**
 * Player holds a name and hand of Cards[5].
 * 
 * @author Jay
 * 
 */
public final class Player {

	/*
	 * Private members
	 */

	private String name;
	Card[] hand = new Card[7];
	private int CardLeftInHand = 0;

	/** CONSTANTS. **/

	/**
	 * Constructor. a player is a name and 5 cards of<int,String> sets up a map full
	 * of card combos. for specific hand for easy calculations
	 * 
	 * @param name
	 *            string associated with card
	 * @param hand
	 *            Array of Card for hand.
	 */
	public Player(String name) {
		this.name = name;
	}
	/*
	 * Methods ----------------------------------------------------------------
	 */




	/**
	 * gives back the name of the player.
	 * 
	 * @return name of player
	 */
	public String name() {
		return this.name;
	}



	/**
	 * checks for uno in players hand
	 * 
	 * @return
	 */
	public boolean hasUno() {
		return this.CardLeftInHand == 1;
	}
}
