// Cody Ickes
// Project

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Deck {
	List<Card> cards = new LinkedList<>();

	// Creates a standard, 52 card deck
	public Deck() {
		// Populating List with all enum combinations.
		for(Rank rank : Rank.values()) {
			// Avoiding Jokers
			if(rank != Rank.JOKER) {
				for(Suit suit : Suit.values()) {
					cards.add(new Card(rank, suit));
				}
			}
		}
	}

	public void printDeck() {
		for(int i = 0; i < cards.size(); i++) {
			cards.get(i).printCard();
		}
	}

	public Card drawCard() {
		Random randomCard = new Random();

		// Draw and remove a random card from the deck
		return cards.remove(randomCard.nextInt(cards.size()));
	}

	public void returnCard(Card card) {
		cards.add(card);
	}
}