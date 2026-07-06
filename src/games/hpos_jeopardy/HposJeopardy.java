package games.hpos_jeopardy;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * HPOS Jeopardy application entry point.
 * Single-JFrame application that swaps content panels for navigation.
 * Runs in fullscreen by default with font sizes scaled to screen resolution.
 */
public class HposJeopardy extends JFrame {

	private static final long serialVersionUID = 1L;

	/** Base resolution the UI was designed for. */
	private static final double BASE_WIDTH = 800.0;
	private static final double BASE_HEIGHT = 600.0;

	/** Scale factor based on actual screen size vs base resolution. */
	private final float scaleFactor;

	public HposJeopardy() {
		super("HPOS Jeopardy");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Fullscreen maximized
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screen.width, screen.height);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);

		// Calculate scale factor based on screen vs base resolution
		double scaleX = screen.getWidth() / BASE_WIDTH;
		double scaleY = screen.getHeight() / BASE_HEIGHT;
		scaleFactor = (float) Math.min(scaleX, scaleY);

		showMainMenu();
		setVisible(true);

		// Start background music
		AudioManager.getInstance().startBGM();
	}

	/**
	 * Returns the font scale factor relative to the base 800x600 design.
	 * All panels should multiply their font sizes by this value.
	 */
	public float getScaleFactor() {
		return scaleFactor;
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
	 * Navigates to the Settings screen.
	 */
	public void showSettings() {
		setContentPane(new SettingsPanel(this));
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
