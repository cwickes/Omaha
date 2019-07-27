import java.util.List;
import java.util.Iterator;

public class Dealer {
	private static Deck deck;
	public Player player;


	public Dealer(Deck gameDeck) {
		deck = gameDeck;
	}

	public void dealCards(List<Player> playerList) {
		// Used for dealing cards. When card can't be dealt, used to return to deck
		Card card;

		for(Iterator<Player> i = playerList.iterator(); i.hasNext();) {
			Player p = i.next();
			// Deal cards to player until hand is full
			do {
				card = deck.drawCard();
			} while(p.getCard(card));
			
			// At this point, card has been drawn, but not dealt. Return to deck.
			deck.returnCard(card);
		}
	}

	public void dealFlop(Card[] cards) {
		cards[0] = deck.drawCard();
		cards[1] = deck.drawCard();
		cards[2] = deck.drawCard();
	}

	public void dealTurn(Card[] cards) {
		cards[3] = deck.drawCard();
	}

	public void dealRiver(Card[] cards) {
		cards[4] = deck.drawCard();
	}

	public void returnCard(Card card) {
		deck.returnCard(card);
	}
}