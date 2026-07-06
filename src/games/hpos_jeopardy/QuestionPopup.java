package games.hpos_jeopardy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Modal popup dialog that displays a clue and its response in a click-through sequence.
 * <p>
 * State machine: SHOWING_CLUE -> SHOWING_RESPONSE -> DISMISSED
 * <ul>
 *   <li>First display: shows image placeholder (if any) above clue text</li>
 *   <li>First click: replaces content with response text</li>
 *   <li>Second click: disposes the dialog</li>
 * </ul>
 * Blocks keyboard dismissal and outside-click dismissal via modal behavior
 * and key event consumption.
 * <p>
 * Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6
 */
public class QuestionPopup extends JDialog {

	private static final long serialVersionUID = 1L;

	/** Popup states representing the click-through sequence. */
	enum State {
		SHOWING_CLUE, SHOWING_RESPONSE, DISMISSED
	}

	private static final Color BACKGROUND_COLOR = new Color(139, 0, 0);
	private static final Color TEXT_COLOR = Color.WHITE;
	private static final Color GOLD = new Color(218, 165, 32);

	private final Clue clue;
	private final Font clueFont;
	private final Font responseFont;
	private State currentState;
	private final JPanel contentPanel;
	private final MouseAdapter clickHandler;

	/**
	 * Constructs a QuestionPopup for the given clue, positioned over the parent frame.
	 *
	 * @param parent the parent frame (game board window)
	 * @param clue   the clue to display
	 */
	public QuestionPopup(JFrame parent, Clue clue) {
		super(parent, true); // modal
		this.clue = clue;
		this.currentState = State.SHOWING_CLUE;

		// Scale fonts based on parent frame
		float scale = 1.0f;
		if (parent instanceof HposJeopardy) {
			scale = ((HposJeopardy) parent).getScaleFactor();
		}
		this.clueFont = new Font("SansSerif", Font.BOLD, (int)(28 * scale));
		this.responseFont = new Font("SansSerif", Font.BOLD, (int)(24 * scale));

		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		// Size to cover the game board area (match parent content pane size)
		Dimension parentSize = parent.getContentPane().getSize();
		setSize(parentSize);
		setLocationRelativeTo(parent);

		// Create content panel
		contentPanel = new JPanel(new BorderLayout(10, 10)) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				GradientPaint gp = new GradientPaint(0, 0, BACKGROUND_COLOR,
						0, getHeight(), new Color(80, 0, 0));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				// Subtle radial glow
				g2d.setColor(new Color(255, 255, 255, 10));
				g2d.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);
				g2d.dispose();
			}
		};
		contentPanel.setBackground(BACKGROUND_COLOR);
		contentPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(GOLD, 3),
				BorderFactory.createEmptyBorder(40, 40, 40, 40)));
		setContentPane(contentPanel);

		// Mouse listener on content pane to advance state on click
		clickHandler = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				advanceState();
			}
		};
		contentPanel.addMouseListener(clickHandler);

		// Show initial clue content
		showClueContent();

		// Play pop SFX when card opens
		AudioManager.getInstance().playSFX();
	}

	/**
	 * Displays the clue text (and image if applicable).
	 */
	private void showClueContent() {
		contentPanel.removeAll();

		// If there's an image path, attempt to load and display it
		String imagePath = clue.getImagePath();
		if (imagePath != null && !imagePath.trim().isEmpty()) {
			try {
				File imageFile = new File(imagePath.trim());
				BufferedImage image = ImageIO.read(imageFile);
				if (image != null) {
					// Calculate max bounds for the image (leave room for clue text)
					int popupW = getWidth();
					int popupH = getHeight();
					// Fallback if popup hasn't been sized yet
					if (popupW <= 80 || popupH <= 0) {
						popupW = 800;
						popupH = 600;
					}
					int maxW = popupW - 80;
					int maxH = (int) (popupH * 0.5);

					Dimension scaled = ImageScaler.scale(image.getWidth(), image.getHeight(), maxW, maxH);

					// Scale the image using Graphics2D for quality
					BufferedImage scaledImage = new BufferedImage(scaled.width, scaled.height, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2d = scaledImage.createGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g2d.drawImage(image, 0, 0, scaled.width, scaled.height, null);
					g2d.dispose();

					JLabel imageLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
					imageLabel.addMouseListener(clickHandler);
					contentPanel.add(imageLabel, BorderLayout.NORTH);
				}
			} catch (Exception e) {
				// On load failure: display clue text only, no error dialog
			}
		}

		// Clue text centered with word wrapping via HTML
		JLabel clueLabel = createWrappingLabel(clue.getClueText(), clueFont);
		contentPanel.add(clueLabel, BorderLayout.CENTER);

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	/**
	 * Displays the response text, replacing all previous content.
	 */
	private void showResponseContent() {
		contentPanel.removeAll();

		JLabel responseLabel = createWrappingLabel(clue.getResponseText(), responseFont);
		contentPanel.add(responseLabel, BorderLayout.CENTER);

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	/**
	 * Advances the popup through its state machine.
	 */
	private void advanceState() {
		switch (currentState) {
			case SHOWING_CLUE:
				currentState = State.SHOWING_RESPONSE;
				AudioManager.getInstance().playSFX();
				showResponseContent();
				break;
			case SHOWING_RESPONSE:
				currentState = State.DISMISSED;
				dispose();
				break;
			case DISMISSED:
				// Already dismissed, do nothing
				break;
		}
	}

	/**
	 * Override to consume all key events, preventing keyboard-based dismissal.
	 */
	@Override
	protected void processKeyEvent(KeyEvent e) {
		// Consume all key events - popup only responds to mouse clicks
		e.consume();
	}

	/**
	 * Creates a centered, word-wrapping label using HTML for text wrapping.
	 *
	 * @param text the text to display
	 * @param font the font to use
	 * @return a configured JLabel with click handler attached
	 */
	private JLabel createWrappingLabel(String text, Font font) {
		String htmlText = "<html><div style='text-align: center;'>"
				+ escapeHtml(text) + "</div></html>";
		JLabel label = new JLabel(htmlText, SwingConstants.CENTER);
		label.setFont(font);
		label.setForeground(TEXT_COLOR);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		// Attach click handler so clicks on the label also advance state
		label.addMouseListener(clickHandler);
		return label;
	}

	/**
	 * Escapes basic HTML special characters in the given text.
	 *
	 * @param text the raw text
	 * @return HTML-safe text
	 */
	private String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;")
				   .replace("<", "&lt;")
				   .replace(">", "&gt;")
				   .replace("\"", "&quot;");
	}

	/**
	 * Returns the current state of the popup (for testing purposes).
	 *
	 * @return the current state
	 */
	State getCurrentState() {
		return currentState;
	}
}
