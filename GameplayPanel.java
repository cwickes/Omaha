import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;

public class GameplayPanel extends JPanel {
	// Grid layout to create a cell for each player (up to 10)
	private JPanel aboveTable = new JPanel(new GridLayout(1, 5));
	private JPanel belowTable = new JPanel(new GridLayout(1, 5));
	private ImageIcon table;
	private List<Player> playerList = new LinkedList<>();
	private JPanel community = new JPanel(new GridLayout(1, 5));
	private JLabel[] communityIcons = new JLabel[5];
	private JDialog controlsDialog;
	private JPanel controls = new JPanel(new GridLayout(2, 3));
	private JButton raise, call, fold, exit;
	private int buttonChoice;
	private JPanel textArea = new JPanel(new GridLayout(3, 1));
	public JLabel moveMade = new JLabel("No moves made");
	public JLabel pot = new JLabel("Pot at $0");
	public JLabel wager = new JLabel("Wager is $0");
	public PlayerPanel currentPlayer;

	// ASSETS
	private final String POKER_TABLE_PATH = "assets/poker_table.jpg";
	private final String CARD_PATH = "assets/poker_cards_chips_2d/PNGs/cards/Set_B/small/card_b_";

	protected GameplayPanel(List<Player> playerList) {
		// Components are displayed top to bottom
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.playerList = playerList;

		/*
		 * Adding graphical components
		 */
		add(aboveTable);

		// Add poker table asset
		java.net.URL imgURL = getClass().getResource(POKER_TABLE_PATH);
		table = new ImageIcon(imgURL);
		//add(new JLabel(table));
		addCommunity();

		add(belowTable);

		moveMade.setFont(moveMade.getFont().deriveFont(32.0f));
		wager.setFont(wager.getFont().deriveFont(32.0f));
		pot.setFont(pot.getFont().deriveFont(32.0f));
		textArea.add(moveMade);
		textArea.add(wager);
		textArea.add(pot);
		add(textArea);

		setupControls();
	}

	public void showWin(String winMessage) {
		JOptionPane.showMessageDialog(Omaha.gui, winMessage);
	}

	public void updateBalance(Player p) {
		for(Component i : aboveTable.getComponents()) {
			if(i instanceof PlayerPanel && ((PlayerPanel)i).player == p) {
				((PlayerPanel)i).playerBalance.setText("Balance: $" + p.balance);
				((PlayerPanel)i).playerWager.setText("Wager: $" + p.wager);
			}
		}
		for(Component i : belowTable.getComponents()) {
			if(i instanceof PlayerPanel && ((PlayerPanel)i).player == p) {
				((PlayerPanel)i).playerBalance.setText("Balance: $" + p.balance);
				((PlayerPanel)i).playerWager.setText("Wager: $" + p.wager);
			}
		}
	}

	public void updatePot() {
		wager.setText("Wager is $" + Omaha.bet);
		pot.setText("Pot at $" + Omaha.pot);
	}

	// Marks current players and updates balances
	public void markCurrentPlayer(Player p) {
		for(Component i : aboveTable.getComponents()) {
			if(i instanceof PlayerPanel && ((PlayerPanel)i).player == p) {
				((PlayerPanel)i).setBackground(Color.GREEN);
				currentPlayer = (PlayerPanel)i;
			}
			else
				((PlayerPanel)i).setBackground(null);
		}
		for(Component i : belowTable.getComponents()) {
			if(i instanceof PlayerPanel && ((PlayerPanel)i).player == p) {
				((PlayerPanel)i).setBackground(Color.GREEN);
				currentPlayer = (PlayerPanel)i;
			}
			else
				((PlayerPanel)i).setBackground(null);
		}
	}

	public void setupControls() {
		controlsDialog = new JDialog(Omaha.gui, true);

		ControlListener listener = new ControlListener();

		raise = new JButton("RAISE");
		raise.setActionCommand("r");
		raise.addActionListener(listener);

		call = new JButton("CALL");
		call.setActionCommand("c");
		call.addActionListener(listener);

		fold = new JButton("FOLD");
		fold.setActionCommand("f");
		fold.addActionListener(listener);

		exit = new JButton("Quit Game");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		controls.add(raise);
		controls.add(call);
		controls.add(fold);
		controls.add(new JLabel());
		controls.add(exit);
		controlsDialog.add(controls);

		controlsDialog.pack();
	}

	private class ControlListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("r"))
				buttonChoice = 0;
			if(e.getActionCommand().equals("c"))
				buttonChoice = 1;
			if(e.getActionCommand().equals("f"))
				buttonChoice = 2;

			controlsDialog.dispose();
		}
	}

	public int getChoice() {
		controlsDialog.setVisible(true);
		return buttonChoice;
	}

	public int getRaiseAmt() {
		String amt = JOptionPane.showInputDialog("Raise Amount");

		if(amt != null)
			return Integer.parseInt(amt);
		return -1;
	}

	public void outputMove(String text) {
		moveMade.setText(text);
		Omaha.gui.pack();
	}



	private void addCommunity() {
		for(int i = 0; i < 5; i++) {
			java.net.URL imgURL = getClass().getResource("assets/poker_cards_chips_2d/PNGs/decks/small/deck_3.png");
			ImageIcon card = new ImageIcon(imgURL);
			communityIcons[i] = new JLabel(card);
			community.add(communityIcons[i]);
		}
		add(community);
	}

	public void flop(Card[] cards) {
		for(int i = 0; i < 3; i++) {
			java.net.URL imgURL = getClass().getResource(CARD_PATH + cardToAsset(cards[i]));
			ImageIcon card = new ImageIcon(imgURL);
			communityIcons[i].setIcon(card);
		}
	}
	public void turn(Card[] cards) {
		java.net.URL imgURL = getClass().getResource(CARD_PATH + cardToAsset(cards[3]));
		ImageIcon card = new ImageIcon(imgURL);
		communityIcons[3].setIcon(card);
	}
	public void river(Card[] cards) {
		for(int i = 4; i < 5; i++) {
			java.net.URL imgURL = getClass().getResource(CARD_PATH + cardToAsset(cards[i]));
			ImageIcon card = new ImageIcon(imgURL);
			communityIcons[i].setIcon(card);
		}
	}

	public void setupAboveBelow() {

		// Add all players. loopCount used to keep track of players added (to deal with above/below properly)
		int loopCount = 0;
		for(Iterator<Player> i = playerList.iterator(); i.hasNext();) {
			Player p = i.next();
			// PlayerPanel created and placed in respective cell
			if(loopCount < 5)
				aboveTable.add(new PlayerPanel(p));
			else
				belowTable.add(new PlayerPanel(p));

			loopCount++;
		}

		revalidate();
		repaint();
	}

	public class PlayerPanel extends JPanel {
		private Player player;
		private JLabel playerName, playerBalance, playerWager;
		private JLabel[] cards = new JLabel[4];
		private JLabel chip;
		private JPanel hand = new JPanel();
		private JPanel hideHand = new JPanel();
		private JPanel handPanel = new JPanel();

		private final String CARD_PATH = "assets/poker_cards_chips_2d/PNGs/cards/Set_B/small/card_b_";

		public PlayerPanel() {
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		public PlayerPanel(Player player) {
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			hand.setLayout(new BoxLayout(hand, BoxLayout.X_AXIS));
			hideHand.setLayout(new BoxLayout(hideHand, BoxLayout.X_AXIS));

			java.net.URL imgURL = getClass().getResource("assets/poker_cards_chips_2d/PNGs/decks/small/deck_3.png");
			ImageIcon card = new ImageIcon(imgURL);
			for(int i = 0; i < 4; i++) {
				hideHand.add(new JLabel(card));
			}

			addPlayer(player);

			hideHand();
		}

		public void hideHand() {
			handPanel.removeAll();
			handPanel.add(hideHand);
		}

		public void showHand() {
			JOptionPane.showMessageDialog(Omaha.gui, "Click OK to view hand");
			handPanel.removeAll();
			handPanel.add(hand);
		}

		public void addPlayer(Player player) {
			this.player = player;

			playerName = new JLabel(player.name);
			playerBalance = new JLabel("Balance: $" + player.balance);
			playerWager = new JLabel("Wager: $" + player.wager);

			add(playerName);
			add(playerBalance);
			add(playerWager);
			add(handPanel);

			addCards();
		}

		private void addCards() {
			for(int i = 0; i < 4; i++) {
				java.net.URL imgURL = getClass().getResource(CARD_PATH + cardToAsset(player.hand[i]));
				ImageIcon card = new ImageIcon(imgURL);
				cards[i] = new JLabel(card);
				hand.add(cards[i]);
			}
		}
	}

	protected String cardToAsset(Card card) {
		StringBuilder path = new StringBuilder();

		if(card.suit == Suit.HEART)
			path.append("h");
		if(card.suit == Suit.DIAMOND)
			path.append("d");
		if(card.suit == Suit.SPADE)
			path.append("s");
		if(card.suit == Suit.CLUB)
			path.append("c");

		if(card.rank == Rank.ACE)
			path.append("a");
		if(card.rank == Rank.TWO)
			path.append("2");
		if(card.rank == Rank.THREE)
			path.append("3");
		if(card.rank == Rank.FOUR)
			path.append("4");
		if(card.rank == Rank.FIVE)
			path.append("5");
		if(card.rank == Rank.SIX)
			path.append("6");
		if(card.rank == Rank.SEVEN)
			path.append("7");
		if(card.rank == Rank.EIGHT)
			path.append("8");
		if(card.rank == Rank.NINE)
			path.append("9");
		if(card.rank == Rank.TEN)
			path.append("10");
		if(card.rank == Rank.JACK)
			path.append("j");
		if(card.rank == Rank.QUEEN)
			path.append("q");
		if(card.rank == Rank.KING)
			path.append("k");

		path.append(".png");

		return path.toString();

	}

}