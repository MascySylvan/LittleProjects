package games.hpos_jeopardy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Game board with red and white theme displaying category names and a 5x5 tile grid.
 */
public class GameBoardPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public enum TileState { AVAILABLE, USED }

	private static final int ROWS = 5;
	private static final int COLS = 5;
	private static final int MAX_CATEGORY_DISPLAY_LENGTH = 20;
	private static final int[] POINT_VALUES = {100, 200, 300, 500, 1000};

	// Theme colors
	private static final Color DARK_RED = new Color(139, 0, 0);
	private static final Color BRIGHT_RED = new Color(200, 30, 30);
	private static final Color TILE_RED = new Color(160, 20, 20);
	private static final Color TILE_HOVER = new Color(180, 40, 40);
	private static final Color TILE_USED_BG = new Color(60, 50, 50);
	private static final Color CREAM = new Color(255, 245, 238);
	private static final Color GOLD = new Color(218, 165, 32);

	private final HposJeopardy frame;
	private final GameData gameData;
	private final JButton[][] tiles;
	private final TileState[][] tileStates;

	public GameBoardPanel(HposJeopardy frame, GameData gameData) {
		this.frame = frame;
		this.gameData = gameData;
		this.tiles = new JButton[ROWS][COLS];
		this.tileStates = new TileState[ROWS][COLS];

		setLayout(new BorderLayout(0, 2));
		setBackground(new Color(40, 0, 0));

		// ESC key binding to return to main menu
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitToMenu");
		getActionMap().put("exitToMenu", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				frame.showMainMenu();
			}
		});

		// Category header row
		JPanel categoryPanel = new JPanel(new GridLayout(1, COLS, 2, 0));
		categoryPanel.setBackground(new Color(40, 0, 0));
		categoryPanel.setBorder(new EmptyBorder(8, 4, 4, 4));

		for (int col = 0; col < COLS; col++) {
			Category category = gameData.getCategories().get(col);
			String displayName = truncateCategoryName(category.getName());
			JLabel label = new JLabel(displayName, SwingConstants.CENTER) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					GradientPaint gp = new GradientPaint(0, 0, DARK_RED, 0, getHeight(), new Color(100, 0, 0));
					g2d.setPaint(gp);
					g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
					g2d.dispose();
					super.paintComponent(g);
				}
			};
			label.setOpaque(false);
			label.setForeground(Color.WHITE);
			label.setFont(new Font("SansSerif", Font.BOLD, 13));
			label.setPreferredSize(new Dimension(100, 40));
			label.setBorder(new EmptyBorder(8, 4, 8, 4));
			categoryPanel.add(label);
		}
		add(categoryPanel, BorderLayout.NORTH);

		// Tile grid
		JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 3, 3));
		gridPanel.setBackground(new Color(40, 0, 0));
		gridPanel.setBorder(new EmptyBorder(2, 4, 8, 4));

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				tileStates[row][col] = TileState.AVAILABLE;

				final int tileRow = row;
				final int tileCol = col;

				JButton button = new JButton("$" + POINT_VALUES[row]) {
					private static final long serialVersionUID = 1L;
					private boolean hovered = false;
					{
						setContentAreaFilled(false);
						setFocusPainted(false);
						setBorderPainted(false);
						setFont(new Font("SansSerif", Font.BOLD, 20));
						setForeground(GOLD);
						setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						addMouseListener(new MouseAdapter() {
							public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
							public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
						});
					}
					@Override
					protected void paintComponent(Graphics g) {
						Graphics2D g2d = (Graphics2D) g.create();
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						RoundRectangle2D rect = new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
						if (tileStates[tileRow][tileCol] == TileState.USED) {
							g2d.setColor(TILE_USED_BG);
						} else {
							g2d.setColor(hovered ? TILE_HOVER : TILE_RED);
						}
						g2d.fill(rect);
						// Subtle inner highlight
						if (tileStates[tileRow][tileCol] == TileState.AVAILABLE) {
							g2d.setColor(new Color(255, 255, 255, 20));
							g2d.drawLine(4, 4, getWidth() - 4, 4);
						}
						g2d.dispose();
						super.paintComponent(g);
					}
				};

				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (tileStates[tileRow][tileCol] != TileState.AVAILABLE) {
							return;
						}
						Clue clue = gameData.getCategories().get(tileCol).getClues().get(tileRow);
						QuestionPopup popup = new QuestionPopup(frame, clue);
						popup.setVisible(true);
						markTileUsed(tileRow, tileCol);
						if (allTilesUsed()) {
							showGameOverOverlay();
						}
					}
				});

				tiles[row][col] = button;
				gridPanel.add(button);
			}
		}
		add(gridPanel, BorderLayout.CENTER);
	}

	public static String truncateCategoryName(String name) {
		if (name.length() > MAX_CATEGORY_DISPLAY_LENGTH) {
			return name.substring(0, MAX_CATEGORY_DISPLAY_LENGTH) + "...";
		}
		return name;
	}

	public TileState getTileState(int row, int col) { return tileStates[row][col]; }

	public void markTileUsed(int row, int col) {
		tileStates[row][col] = TileState.USED;
		tiles[row][col].setText("");
		tiles[row][col].setCursor(Cursor.getDefaultCursor());
		tiles[row][col].repaint();
	}

	public JButton getTileButton(int row, int col) { return tiles[row][col]; }
	public GameData getGameData() { return gameData; }
	public HposJeopardy getFrame() { return frame; }

	public boolean allTilesUsed() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (tileStates[row][col] != TileState.USED) return false;
			}
		}
		return true;
	}

	private void showGameOverOverlay() {
		removeAll();

		JPanel overlayPanel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint gp = new GradientPaint(0, 0, DARK_RED, 0, getHeight(), new Color(60, 0, 0));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				// Decorative elements
				g2d.setColor(new Color(255, 255, 255, 10));
				int cx = getWidth() / 2;
				int cy = getHeight() / 2;
				g2d.fillOval(cx - 200, cy - 200, 400, 400);
				g2d.dispose();
			}
		};

		JLabel messageLabel = new JLabel("All clues have been revealed!", SwingConstants.CENTER);
		messageLabel.setFont(new Font("Serif", Font.BOLD, 32));
		messageLabel.setForeground(GOLD);

		JLabel subLabel = new JLabel("Great game!", SwingConstants.CENTER);
		subLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
		subLabel.setForeground(CREAM);

		JPanel textPanel = new JPanel(new BorderLayout(0, 10));
		textPanel.setOpaque(false);
		textPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
		textPanel.add(messageLabel, BorderLayout.CENTER);
		textPanel.add(subLabel, BorderLayout.SOUTH);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(new EmptyBorder(0, 0, 40, 0));

		JButton mainMenuButton = new JButton("Main Menu") {
			private static final long serialVersionUID = 1L;
			private boolean hovered = false;
			{
				setContentAreaFilled(false);
				setFocusPainted(false);
				setBorderPainted(false);
				setFont(new Font("SansSerif", Font.BOLD, 18));
				setForeground(DARK_RED);
				setPreferredSize(new Dimension(180, 45));
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
					public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
				});
			}
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
				g2d.setColor(hovered ? GOLD : CREAM);
				g2d.fill(rect);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		mainMenuButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { frame.showMainMenu(); }
		});
		buttonPanel.add(mainMenuButton);

		overlayPanel.add(textPanel, BorderLayout.CENTER);
		overlayPanel.add(buttonPanel, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(overlayPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}
