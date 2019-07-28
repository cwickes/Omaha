import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;

public class GameplayPanel extends JPanel {
	protected GameplayPanel() {
		super();

		// Start game when window is shown.
		// Need to add all methods due to abstract class, only use Shown.
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				Omaha.startGame();
			}
			@Override
			public void componentResized(ComponentEvent e) {
				
			}
			@Override
			public void componentMoved(ComponentEvent e) {
				
			}
			@Override
			public void componentHidden(ComponentEvent e) {
				
			}
		});
	}

}