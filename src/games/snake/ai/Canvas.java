package games.snake.ai;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Canvas extends JFrame {

	private static final long serialVersionUID = 1L;

	private final int gridSize;
	private Snake snake;
	private Food food;
	private final Timer motion;
	private boolean won = false;
	private boolean aiMode = true;

	public Canvas(int gridSize) {
		super("AI Snake - " + gridSize + "x" + gridSize);
		this.gridSize = gridSize;

		snake = new Snake(gridSize);
		food = new Food(snake);

		// Size window to fit the grid
		int windowSize = snake.getGridMax() + 80;
		setSize(windowSize, windowSize + 30);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GamePanel panel = new GamePanel();
		setContentPane(panel);
		setVisible(true);

		motion = new Timer(30, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!snake.isAlive() || won) return;

				if (aiMode) {
					snake.aiMovement(food);
				}
				snake.updateCoords(food);
				snake.checkStatus();

				if (!snake.isAlive()) {
					System.out.println("Dead! Length: " + snake.getBodyCoords().size());
				} else if (snake.isLonger()) {
					if (snake.getBodyCoords().size() >= snake.getTotalCells()) {
						won = true;
						System.out.println("WIN! Snake filled the grid!");
					} else {
						food = new Food(snake);
					}
				}
				panel.repaint();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				switch (evt.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					aiMode = !aiMode;
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					if (!aiMode && !snake.getDirection().equals("d")) snake.setDirection("u");
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					if (!aiMode && !snake.getDirection().equals("u")) snake.setDirection("d");
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					if (!aiMode && !snake.getDirection().equals("r")) snake.setDirection("l");
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					if (!aiMode && !snake.getDirection().equals("l")) snake.setDirection("r");
					break;
				case KeyEvent.VK_R:
					reset();
					break;
				case KeyEvent.VK_P:
					if (motion.isRunning()) motion.stop();
					else motion.start();
					break;
				case KeyEvent.VK_EQUALS:
				case KeyEvent.VK_PLUS:
					if (motion.getDelay() > 5) motion.setDelay(motion.getDelay() - 5);
					break;
				case KeyEvent.VK_MINUS:
					motion.setDelay(motion.getDelay() + 5);
					break;
				case KeyEvent.VK_ESCAPE:
					backToMenu();
					break;
				}
			}
		});

		motion.start();
	}

	private void reset() {
		snake = new Snake(gridSize);
		food = new Food(snake);
		won = false;
		motion.start();
	}

	private void backToMenu() {
		motion.stop();
		dispose();
		new Menu();
	}

	private class GamePanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public GamePanel() {
			setDoubleBuffered(true);
			setBackground(Color.WHITE);
		}

		@Override
		protected void paintComponent(Graphics g1) {
			super.paintComponent(g1);
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			int cellSz = snake.getCellSize();
			int strokeWidth = Math.max(2, cellSz - 4);

			// Border
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(4));
			int bMin = Snake.GRID_MIN - 12;
			int bMax = snake.getGridMax() + 12;
			g.drawRect(bMin, bMin, bMax - bMin, bMax - bMin);

			// Snake body
			g.setColor(new Color(34, 139, 34));
			g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			for (int i = 0; i < snake.getBodyCoords().size() - 1; i++) {
				int x1 = snake.getBodyCoords().get(i)[0];
				int y1 = snake.getBodyCoords().get(i)[1];
				int x2 = snake.getBodyCoords().get(i + 1)[0];
				int y2 = snake.getBodyCoords().get(i + 1)[1];
				g.drawLine(x1, y1, x2, y2);
			}

			// Snake head
			if (!snake.getBodyCoords().isEmpty()) {
				int hx = snake.getBodyCoords().get(0)[0];
				int hy = snake.getBodyCoords().get(0)[1];
				g.setColor(new Color(0, 100, 0));
				int headR = Math.max(3, cellSz / 3);
				g.fillOval(hx - headR, hy - headR, headR * 2, headR * 2);
			}

			// Food
			if (!won) {
				g.setColor(Color.RED);
				int foodR = Math.max(3, cellSz / 3);
				g.fillOval(food.getX() - foodR, food.getY() - foodR, foodR * 2, foodR * 2);
			}

			// HUD
			g.setColor(Color.BLACK);
			g.setFont(new Font("Monospaced", Font.BOLD, 12));
			g.drawString("Length: " + snake.getBodyCoords().size() + " / "
					+ snake.getTotalCells() + "  Mode: " + (aiMode ? "AI" : "MANUAL"),
					Snake.GRID_MIN, Snake.GRID_MIN - 18);
			g.drawString("[SPACE] toggle  [+/-] speed  [P] pause  [R] reset  [ESC] menu",
					Snake.GRID_MIN, Snake.GRID_MIN - 5);

			// Win / death overlay
			int cx = (Snake.GRID_MIN + snake.getGridMax()) / 2;
			int cy = (Snake.GRID_MIN + snake.getGridMax()) / 2;
			if (won) {
				g.setColor(new Color(0, 128, 0));
				g.setFont(new Font("SansSerif", Font.BOLD, 28));
				g.drawString("COMPLETE!", cx - 80, cy);
			} else if (!snake.isAlive()) {
				g.setColor(Color.RED);
				g.setFont(new Font("SansSerif", Font.BOLD, 28));
				g.drawString("DEAD - Press R", cx - 110, cy);
			}
		}
	}

	public static void main(String[] args) {
		new Menu();
	}
}
