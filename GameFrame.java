import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.awt.CardLayout;

public class GameFrame extends JFrame {

	private JPanel mainPanel = new JPanel(new CardLayout());
	private GridLayout layout = new GridLayout(12, 2);
	private JPanel infoInputPanel;
	private JButton resetButton, startButton;
	private JTextField[] nameInputArray = new JTextField[10];
	private JTextField[] balanceInputArray = new JTextField[10];
	private int numberOfPlayers = 2;

	public GameFrame() {
		super("Omaha Poker");

		layout.setVgap(10);
		// Create panel with 12x2 grid layout. Spaced 10 between each row
		infoInputPanel = new JPanel(layout);
		// Fill panel with input forms
		setGameData();
		mainPanel.add(infoInputPanel, "input panel");
		add(mainPanel);

		// Finish setting up frame, display
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 1000);
		setVisible(true);
	}

	// Displays GUI to collect data for game (# of players, balance, etc.)
	public void setGameData() {
		// Create dropdown to set # of players
		Integer[] countSelection = {2, 3, 4, 5, 6, 7, 8, 9, 10};
		JComboBox<Integer> playerCount = new JComboBox<>(countSelection);
		// Action listener to retrieve selected number of players
		playerCount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numberOfPlayers = (Integer)playerCount.getSelectedItem();
			}
		});
		infoInputPanel.add(new JLabel("How many players?"));
		infoInputPanel.add(playerCount);

		for(int i = 0; i < 10; i++) {
			infoInputPanel.add(new JLabel("Player " + (i + 1)));
			infoInputPanel.add(new PlayerDataPanel(i));
		}

		// Instantiate buttons and add listeners
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearForm();
			}
		});
		startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(checkForm()) {
					startGame();
				}
			}
		});

		infoInputPanel.add(resetButton);
		infoInputPanel.add(startButton);
	}

	// JPanel used inside of cell of grid layout
	private class PlayerDataPanel extends JPanel {
		JTextField nameInput = new JTextField();
		JTextField balanceInput = new JTextField();

		// Takes in loop iteration for saving input fields to array
		PlayerDataPanel(int loopIteration) {
			super(new GridLayout(2, 2));
			add(new JLabel("Name"));
			nameInputArray[loopIteration] = nameInput;
			add(nameInput);
			add(new JLabel("Starting Balance"));
			add(balanceInput);
			balanceInputArray[loopIteration] = balanceInput;
		}

	}

	// Clears data entered into form
	private void clearForm() {
		for(int i = 0; i < 10; i++) {
			nameInputArray[i].setText("");
			balanceInputArray[i].setText("");
		}
	}

	// Ensures form has valid data (enough players, names and balances)
	private boolean checkForm() {
		boolean[] invalidName = new boolean[numberOfPlayers];
		boolean[] invalidBalance = new boolean[numberOfPlayers];
		boolean invalidForm = false;

		for(int i = 0; i < numberOfPlayers; i++) {
			if(nameInputArray[i].getText().isEmpty()) {
				invalidName[i] = true;
				invalidForm = true;
			}
			if(balanceInputArray[i].getText().isEmpty()) {
				invalidBalance[i] = true;
				invalidForm = true;
			}
		}

		if(invalidForm) {
			// print field errors
			StringBuilder missingData = new StringBuilder();
			for(int i = 0; i < numberOfPlayers; i++) {
				if(invalidName[i]) {
					missingData.append("Player " + (i + 1) + " is missing a name\n");
				}
				if(invalidBalance[i]) {
					missingData.append("Player " + (i + 1) + " is missing a balance\n");
				}
			}
			JOptionPane.showMessageDialog(this, missingData.toString());

			return false;
		}

		return true;
	}

	// Replace infoInputPanel with gamePanel
	private void startGame() {
		Omaha.addPlayers(numberOfPlayers, nameInputArray, balanceInputArray);
		CardLayout cl = (CardLayout)mainPanel.getLayout();
		mainPanel.add(new GameplayPanel(), "game panel");
		cl.show(mainPanel, "game panel");
	}

}