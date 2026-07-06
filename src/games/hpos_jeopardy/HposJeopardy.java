package games.hpos_jeopardy;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * HPOS Jeopardy application entry point.
 * Single-JFrame application that swaps content panels for navigation.
 */
public class HposJeopardy extends JFrame {

	private static final long serialVersionUID = 1L;

	public HposJeopardy() {
		super("HPOS Jeopardy");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);

		showMainMenu();
		setVisible(true);
	}

	/**
	 * Navigates to the Main Menu screen.
	 */
	public void showMainMenu() {
		setContentPane(new MainMenuPanel(this));
		revalidate();
		repaint();
	}

	/**
	 * Navigates to the Setup screen.
	 */
	public void showSetup() {
		setContentPane(new SetupPanel(this));
		revalidate();
		repaint();
	}

	/**
	 * Navigates to the Game Board screen with the given game data.
	 *
	 * @param data the parsed game data to display on the board
	 */
	public void showGameBoard(GameData data) {
		setContentPane(new GameBoardPanel(this, data));
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new HposJeopardy();
			}
		});
	}
}
