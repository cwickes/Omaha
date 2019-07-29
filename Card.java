public class Card {

	protected Rank rank;
	protected Suit suit;

	// Default option. Creates a Joker card.
	public Card() {
		this(Rank.JOKER, null);
	}

	public Card(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public void setCard(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public int rankPriority() {
		if(rank == Rank.ACE)
			return 0;
		if(rank == Rank.KING)
			return 1;
		if(rank == Rank.QUEEN)
			return 2;
		if(rank == Rank.JACK)
			return 3;
		if(rank == Rank.TEN)
			return 4;
		if(rank == Rank.NINE)
			return 5;
		if(rank == Rank.EIGHT)
			return 6;
		if(rank == Rank.SEVEN)
			return 7;
		if(rank == Rank.SIX)
			return 8;
		if(rank == Rank.FIVE)
			return 9;
		if(rank == Rank.FOUR)
			return 10;
		if(rank == Rank.THREE)
			return 11;
		if(rank == Rank.TWO)
			return 12;

		return -1;
	}

	public char suitPriority() {
		if(Suit.HEART == suit)
			return 'H';
		if(Suit.DIAMOND == suit)
			return 'D';
		if(Suit.SPADE == suit)
			return 'S';
		if(Suit.CLUB == suit)
			return 'C';

		return 'E';
	}

	public void printCard() {
		System.out.println(rank + " of " + suit);
	}

}

/*
 Enums created to make Card object simpler to handle.
 Used instead of variables due to a card ALWAYS having a suit
 and/or rank. This should reduce errors as it only allows one
 of the enum values.
 */
enum Rank {
	ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, JOKER
}

enum Suit {
	HEART, SPADE, DIAMOND, CLUB
}