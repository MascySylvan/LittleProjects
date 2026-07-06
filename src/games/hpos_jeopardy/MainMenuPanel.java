package games.hpos_jeopardy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Main Menu panel with a custom-painted "HPOS Jeopardy" title banner
 * and styled Play/Setup buttons using a red and white theme.
 */
public class MainMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Theme colors
	private static final Color DARK_RED = new Color(139, 0, 0);
	private static final Color BRIGHT_RED = new Color(200, 30, 30);
	private static final Color LIGHT_RED = new Color(220, 60, 60);
	private static final Color CREAM = new Color(255, 245, 238);
	private static final Color GOLD = new Color(218, 165, 32);

	private final HposJeopardy parentFrame;

	public MainMenuPanel(HposJeopardy parentFrame) {
		this.parentFrame = parentFrame;
		setLayout(new BorderLayout());
		setBackground(DARK_RED);

		// Title banner (custom painted)
		TitleBanner banner = new TitleBanner();
		add(banner, BorderLayout.CENTER);

		// Button panel at bottom
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createVerticalStrut(20));

		JButton playButton = createStyledButton("Play");
		JButton setupButton = createStyledButton("Setup");

		playButton.setAlignmentX(CENTER_ALIGNMENT);
		setupButton.setAlignmentX(CENTER_ALIGNMENT);

		buttonPanel.add(playButton);
		buttonPanel.add(Box.createVerticalStrut(15));
		buttonPanel.add(setupButton);
		buttonPanel.add(Box.createVerticalStrut(40));

		add(buttonPanel, BorderLayout.SOUTH);

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPlayClicked();
			}
		});

		setupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.showSetup();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Gradient background
		GradientPaint gradient = new GradientPaint(
				0, 0, DARK_RED,
				0, getHeight(), new Color(80, 0, 0));
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		// Decorative diagonal stripes
		g2d.setColor(new Color(255, 255, 255, 15));
		for (int i = -getHeight(); i < getWidth() + getHeight(); i += 40) {
			g2d.drawLine(i, 0, i + getHeight(), getHeight());
		}

		g2d.dispose();
	}

	/**
	 * Creates a styled button with rounded edges and hover effects.
	 */
	private JButton createStyledButton(String text) {
		JButton button = new JButton(text) {
			private static final long serialVersionUID = 1L;
			private boolean hovered = false;

			{
				setContentAreaFilled(false);
				setFocusPainted(false);
				setBorderPainted(false);
				setForeground(DARK_RED);
				setFont(new Font("SansSerif", Font.BOLD, 20));
				setPreferredSize(new Dimension(200, 50));
				setMaximumSize(new Dimension(200, 50));
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						hovered = true;
						repaint();
					}
					public void mouseExited(MouseEvent e) {
						hovered = false;
						repaint();
					}
				});
			}

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

				if (hovered) {
					g2d.setColor(GOLD);
				} else {
					g2d.setColor(CREAM);
				}
				g2d.fill(rect);

				// Border
				g2d.setColor(hovered ? new Color(180, 140, 20) : BRIGHT_RED);
				g2d.draw(rect);

				g2d.dispose();
				super.paintComponent(g);
			}
		};
		return button;
	}

	private void onPlayClicked() {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(parentFrame);

		if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
			return;
		}

		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null) {
			return;
		}

		QuestionFileParser parser = new QuestionFileParser();
		try {
			GameData data = parser.parse(selectedFile);
			parentFrame.showGameBoard(data);
		} catch (ParseException ex) {
			JOptionPane.showMessageDialog(parentFrame, ex.getMessage(),
					"Invalid File", JOptionPane.ERROR_MESSAGE);
		}
	}

	// -------------------------------------------------------------------------
	// Inner class: Custom-painted title banner
	// -------------------------------------------------------------------------

	/**
	 * Custom painted panel that renders the "HPOS Jeopardy" title with
	 * decorative elements using Java Graphics2D.
	 */
	private static class TitleBanner extends JPanel {

		private static final long serialVersionUID = 1L;

		public TitleBanner() {
			setOpaque(false);
			setPreferredSize(new Dimension(800, 350));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

			int w = getWidth();
			int h = getHeight();
			int centerX = w / 2;
			int centerY = h / 2;

			// Decorative diamond shape behind the title
			int diamondSize = 180;
			int[] xPoints = {centerX, centerX + diamondSize, centerX, centerX - diamondSize};
			int[] yPoints = {centerY - diamondSize, centerY, centerY + diamondSize, centerY};
			g2d.setColor(new Color(255, 255, 255, 20));
			g2d.fillPolygon(xPoints, yPoints, 4);
			g2d.setColor(new Color(255, 255, 255, 40));
			g2d.drawPolygon(xPoints, yPoints, 4);

			// Outer glow circle
			g2d.setColor(new Color(255, 200, 200, 25));
			g2d.fillOval(centerX - 200, centerY - 120, 400, 240);

			// Title text shadow
			Font titleFont = new Font("Serif", Font.BOLD, 56);
			g2d.setFont(titleFont);
			String title = "HPOS Jeopardy";
			int titleWidth = g2d.getFontMetrics().stringWidth(title);
			int titleX = centerX - titleWidth / 2;
			int titleY = centerY - 10;

			// Drop shadow
			g2d.setColor(new Color(0, 0, 0, 100));
			g2d.drawString(title, titleX + 3, titleY + 3);

			// Main title in gold
			g2d.setColor(GOLD);
			g2d.drawString(title, titleX, titleY);

			// Subtitle
			Font subtitleFont = new Font("SansSerif", Font.PLAIN, 16);
			g2d.setFont(subtitleFont);
			String subtitle = "Test Your Knowledge";
			int subtitleWidth = g2d.getFontMetrics().stringWidth(subtitle);
			g2d.setColor(CREAM);
			g2d.drawString(subtitle, centerX - subtitleWidth / 2, titleY + 40);

			// Decorative lines flanking the subtitle
			int lineY = titleY + 38;
			int lineGap = subtitleWidth / 2 + 20;
			g2d.setColor(new Color(218, 165, 32, 150));
			g2d.drawLine(centerX - lineGap - 80, lineY, centerX - lineGap, lineY);
			g2d.drawLine(centerX + lineGap, lineY, centerX + lineGap + 80, lineY);

			// Small decorative dots
			g2d.setColor(GOLD);
			for (int i = 0; i < 5; i++) {
				int dotX = centerX - 40 + i * 20;
				g2d.fillOval(dotX, titleY + 60, 5, 5);
			}

			g2d.dispose();
		}
	}
}
