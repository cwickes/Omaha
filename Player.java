// Cody Ickes
// Project

import java.util.Scanner;

public class Player {
	// TODO: Make Human and Computer subclass

	protected String name;
	protected int balance;
	protected Card[] hand = new Card[4];
	public int wager;
	public int handRank = -1;

	public final static int RAISE = 0;
	public final static int CALL = 1;
	public final static int FOLD = 2;

	public Player() {
		name = null;
		balance = 0;
	}

	public Player(String playerName, int startingBalance) {
		name = playerName;
		balance = startingBalance;
	}

	// Returns true if card can be collected. False otherwise
	public boolean getCard(Card card) {
		for(int i = 0; i < 4; i++) {
			// Get card if spot is available in hand
			if(hand[i] == null) {
				hand[i] = card;
				return true;
			}
		}

		return false;
	}

	public int bet(int amount) {
		balance -= amount;

		wager = amount;

		return amount;
	}

	public int takeTurn(Dealer dealer, int minBet, int activeCommunityCards) {
		int choice;

		choice = Omaha.gui.gamePanel.getChoice();

		switch(choice) {
			case RAISE:
				raise(minBet);
				break;
			case CALL:
				call(minBet);
				break;
			case FOLD:
				fold(dealer);
		}

		return choice;
	}

	private void raise(int bet) {
		int raiseAmt;

		// Get amount to raise by
		raiseAmt = Omaha.gui.gamePanel.getRaiseAmt();

		// Check if balance can support raise AND raise amount will exceed bet
		if(raiseAmt > 0 && balance >= raiseAmt && wager + raiseAmt > bet) {
			balance -= raiseAmt;
			Omaha.addToPot(raiseAmt);
			wager += raiseAmt;
			Omaha.gui.gamePanel.updateBalance(this);

			Omaha.gui.gamePanel.outputMove(name + " raises to " + wager);
		}
		else {
			call(bet);
		}
	}

	// Handles call and check
	private void call(int bet) {
		balance -= bet - wager;
		Omaha.addToPot(bet - wager);
		wager = bet;
		Omaha.gui.gamePanel.updateBalance(this);

		Omaha.gui.gamePanel.outputMove(name + " calls " + wager);
	}

	protected void fold(Dealer dealer) {
		returnCards(dealer);

		Omaha.gui.gamePanel.outputMove(name + " folds");
	}

	public void returnCards(Dealer dealer) {
		for(int i = 0; i < 4; i++) {
			dealer.returnCard(hand[i]);
			hand[i] = null;
		}
	}


	// GUI
	public void viewInfo() {
		System.out.println("Name: " + name);
		System.out.println("Balance: " + balance);
		System.out.println("Your hand:");
		try {
			for(int i = 0; i < 4; i++) {
				hand[i].printCard();
			}
		} catch(Exception e) {
			System.out.println("No card");
		}
	}
}