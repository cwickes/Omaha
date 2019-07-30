// Cody Ickes
// Project

import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.util.Iterator;
import javax.swing.JTextField;
import javax.swing.JFrame;

public class Omaha {

	static Deck deck = new Deck();
	static Dealer dealer = new Dealer(deck);
	static int dealerLocation;
	static Card[] communityCards = new Card[5];
	static List<Player> players = new LinkedList<>();
	static List<Player> activePlayers = new LinkedList<>();
	static Player bigBlind, smallBlind;
	static int pot = 0;
	static int bet;
	static boolean setupComplete = false;

	static GameFrame gui;

	public static void main(String[] args) {

		//DEBUG
		gui = new GameFrame();

		startGame();

		gui.dispose();

		
	}

	public static void addPlayers(int numberOfPlayers, JTextField[] names, JTextField[] balances) {
		// Add players to game
		for(int i = 0; i < numberOfPlayers; i++) {
			// Retrieve name
			String name = names[i].getText();
			// Determine balance
			int balance = Integer.parseInt(balances[i].getText());
			players.add(new Player(name, balance));
		}
	}

	public static List<Player> getPlayers() {
		return players;
	}

	public static void startGame() {
		/**************
		 * GAME START *
		 **************/

		// Select random dealer to start
		Random random = new Random();
		// Saving index of dealer to mark blinds
		dealerLocation = random.nextInt(players.size());
		dealer.player = players.get(dealerLocation);

		// Mark blinds
		smallBlind = players.get((dealerLocation + 1) % players.size());
		bigBlind = players.get((dealerLocation + 2) % players.size());

		// Every loop is one hand
		GAME:
		while(players.size() > 1) {

			/*
			 * Shift positions of dealer and blinds.
			 * Not necessary on first hand, but done to ensure
			 * shift is performed every hand.
			 */
			if(shiftPositions()) {
				break GAME;
			}

			/*
			 * Setting up players active in the next hand. Used for tracking who has not folded
			 */
			// Clearing list of active players
			activePlayers.clear();
			// Adding all players still present to active list
			for(int i = 0, pos = players.indexOf(bigBlind) + 1; i < players.size(); i++, pos++) {
				activePlayers.add(players.get(pos % players.size()));
			}

			// Before cards are dealt, blinds bet
			pot += smallBlind.bet(5);
			pot += bigBlind.bet(10);
			bet = 10;

			dealer.dealCards(players);
			// Add cards to GUI
			gui.gamePanel = new GameplayPanel(Omaha.getPlayers());
			gui.getContentPane().removeAll();
			gui.add(gui.gamePanel);
			gui.gamePanel.setupAboveBelow();
			gui.pack();
			
			// Go through betting round
			if(bettingRound(0)) {
				continue;
			}

			// Deal flop
			dealer.dealFlop(communityCards);
			// Show flop on GUI
			gui.gamePanel.flop(communityCards);

			// Next betting round
			if(bettingRound(3)) {
				returnCommunityCards(3);
				continue;
			}

			// Deal turn
			dealer.dealTurn(communityCards);
			gui.gamePanel.turn(communityCards);

			// Next betting round
			if(bettingRound(4)) {
				returnCommunityCards(4);
				continue;
			}

			// Deal river
			dealer.dealRiver(communityCards);
			gui.gamePanel.river(communityCards);

			// Final betting round
			if(bettingRound(5)) {
				returnCommunityCards(5);
				continue;
			}

			// Showdown
			// Implement Hand check to determine winner
			showdown(activePlayers);

			// Clear pot
			pot = 0;
			// Return cards to deck
			giveCards();
			returnCommunityCards(5);

		}
	}

	// Go through betting round
	// Returns true if hand ends
	private static boolean bettingRound(int availableCommunityCards) {
		int startBet;

		// Keep cycling until no one raises
		do {
			// Get initial bet amount
			startBet = bet;

			for(Iterator<Player> i = activePlayers.iterator(); i.hasNext();) {
				Player p = i.next();

				if(gui.gamePanel.currentPlayer != null)
					gui.gamePanel.currentPlayer.hideHand();
				gui.gamePanel.markCurrentPlayer(p);
				gui.gamePanel.updatePot();
				gui.gamePanel.currentPlayer.showHand();
				gui.revalidate();
				gui.repaint();

				if(activePlayers.size() == 1) {
					p.balance += pot;
					pot = 0;
					giveCards();

					gui.gamePanel.showWin(p.name + " wins the hand");

					return true;
				}

				// Check if player is still eligible to play (enough money)
				// Case 1: not eligible, must fold
				if(bet > p.wager && p.balance < bet - p.wager) {
					activePlayers.remove(p);
					p.fold(dealer);
				}
				// Case 2: eligible
				else {
					int turnResult = p.takeTurn(dealer, bet, availableCommunityCards);
					// Player checks cards and checks, raises, or folds
					switch(turnResult) {
						case Player.FOLD:
							// Player is no longer active in hand if fold
							i.remove();
							break;
						case Player.RAISE:
							// Player raised bet. Update current bet
							bet = p.wager;
					}
				}
			}
		} while(startBet != bet);

		return false;
	}

	public static void addToPot(int amount) {
		pot += amount;
		System.out.println("Pot: " + pot);
	}

	private static void giveCards() {
		for(Iterator<Player> i = activePlayers.iterator(); i.hasNext();) {
			Player p = i.next();

			p.returnCards(dealer);
		}
	}

	private static void returnCommunityCards(int activeCards) {
		for(int i = 0; i < activeCards; i++) {
			dealer.returnCard(communityCards[i]);
			communityCards[i] = null;
		}
	}

	private static boolean shiftPositions() {
		// New dealer and blinds
		dealerLocation++;
		// Small blind becomes dealer
		dealer.player = players.get(dealerLocation % players.size());
		// Big blind becomes small blind
		smallBlind = players.get((dealerLocation + 1) % players.size());
		// If not enough money is available, player is kicked and new blind selected
		while(smallBlind.balance < 5) {
			smallBlind.returnCards(dealer);
			players.remove(smallBlind);
			if(players.size() > 1) {
				smallBlind = players.get((dealerLocation + 1) % players.size());
			}
			else {
				gui.gamePanel.showWin(players.get(0).name + " WINS THE GAME!");
				players.get(0).viewInfo();
				return true;
			}
		}
		// Player after big blind becomes new big blind
		bigBlind = players.get((dealerLocation + 2) % players.size());
		// If not enough money is available, player is kicked and new blind selected
		while(bigBlind.balance < 10) {
			bigBlind.returnCards(dealer);
			players.remove(bigBlind);
			if(players.size() > 1) {
			bigBlind = players.get((dealerLocation + 2) % players.size());
			}
			else {
				gui.gamePanel.showWin(players.get(0).name + " WINS THE GAME!");
				players.get(0).viewInfo();
				return true;
			}
		}

		return false;
	}

	static private void showdown(List<Player> activePlayers) {
		final int ROYAL_FLUSH = 9;
		final int STRAIGHT_FLUSH = 8;
		final int FOUR_OF_A_KIND = 7;
		final int FULL_HOUSE = 6;
		final int FLUSH = 5;
		final int STRAIGHT = 4;
		final int THREE_OF_A_KIND = 3;
		final int TWO_PAIR = 2;
		final int ONE_PAIR = 1;
		final int HIGH_CARD = 0;

		Card[][] hand = new Card[60][5];

		Player winner = activePlayers.get(0);

		for(Iterator<Player> itr = activePlayers.iterator(); itr.hasNext();) {
			Player p = itr.next();

			// 60 different combinations
			buildCombinations(p, hand);
			for(int i = 0; i < 60; i++) {
				int rank = rankPokerHand(hand[i]);
				if(p.handRank < rank) {
					p.handRank = rank;
				}
			}

			if(winner.handRank < p.handRank) {
				winner = p;
			}
		}

		winner.balance += pot;
		Omaha.gui.gamePanel.showWin(winner.name + " wins the hand");
	}

	// Code taken from https://rosettacode.org/wiki/Poker_hand_analyser#Java
	static private int rankPokerHand(Card[] hand) {
		int[] faceCount = new int[13];
		long straight = 0, flush = 0;

		for(Card card : hand) {
			int face = card.rankPriority();
			straight |= (1 << face);
			faceCount[face]++;
			flush |= (1 << card.suitPriority());
		}

		while(straight % 2 == 0) {
			straight >>= 1;
		}

		boolean hasStraight = (straight == 0b11111 || straight == 0b1111000000001);
		boolean hasFlush = ((flush & (flush - 1)) == 0);

		if(hasStraight && hasFlush) {
			return 8;
		}

		int total = 0;
		for(int count: faceCount) {
			if(count == 4) {
				return 7;
			}
			if(count == 3) {
				total += 3;
			}
			else if(count == 2) {
				total += 2;
			}
		}

		if(total == 5) {
			return 6;
		}
		if(hasFlush) {
			return 5;
		}
		if(hasStraight) {
			return 4;
		}
		if(total == 3) {
			return 3;
		}
		if(total == 4) {
			return 2;
		}
		if(total == 2) {
			return 1;
		}

		return 0;
	}

	// Brute forced combo builder. Found algorithms to perform combinations, too much to implement
	// Same complexity, just more handwritten code
	private static void buildCombinations(Player p, Card[][] allHands) {
		for(int i = 0; i < 10; i++) {
			allHands[i][0] = p.hand[0];
			allHands[i][1] = p.hand[1];

			allHands[i + 10][0] = p.hand[0];
			allHands[i + 10][1] = p.hand[2];

			allHands[i + 20][0] = p.hand[0];
			allHands[i + 20][1] = p.hand[3];

			allHands[i + 30][0] = p.hand[1];
			allHands[i + 30][1] = p.hand[2];

			allHands[i + 40][0] = p.hand[1];
			allHands[i + 40][1] = p.hand[3];

			allHands[i + 50][0] = p.hand[2];
			allHands[i + 50][1] = p.hand[3];
		}

		for(int i = 0; i < 60; i += 10) {
			allHands[i][2] = communityCards[0];
			allHands[i][3] = communityCards[1];
			allHands[i][4] = communityCards[2];

			allHands[i + 1][2] = communityCards[0];
			allHands[i + 1][3] = communityCards[1];
			allHands[i + 1][4] = communityCards[3];

			allHands[i + 2][2] = communityCards[0];
			allHands[i + 2][3] = communityCards[1];
			allHands[i + 2][4] = communityCards[4];

			allHands[i + 3][2] = communityCards[0];
			allHands[i + 3][3] = communityCards[2];
			allHands[i + 3][4] = communityCards[3];

			allHands[i + 4][2] = communityCards[0];
			allHands[i + 4][3] = communityCards[2];
			allHands[i + 4][4] = communityCards[4];

			allHands[i + 5][2] = communityCards[0];
			allHands[i + 5][3] = communityCards[3];
			allHands[i + 5][4] = communityCards[4];

			allHands[i + 6][2] = communityCards[1];
			allHands[i + 6][3] = communityCards[2];
			allHands[i + 6][4] = communityCards[3];

			allHands[i + 7][2] = communityCards[1];
			allHands[i + 7][3] = communityCards[2];
			allHands[i + 7][4] = communityCards[4];

			allHands[i + 8][2] = communityCards[1];
			allHands[i + 8][3] = communityCards[3];
			allHands[i + 8][4] = communityCards[4];

			allHands[i + 9][2] = communityCards[2];
			allHands[i + 9][3] = communityCards[3];
			allHands[i + 9][4] = communityCards[4];
		}
	}

}