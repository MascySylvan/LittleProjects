package ai_project.self_avoiding_walk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Displays a random space-filling walk that covers the entire grid.
 * 
 * Controls:
 *   SPACE - pause/resume
 *   R     - generate a new walk
 *   +/-   - speed up/slow down
 */
public class Canvas extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final int WINDOW_SIZE = 800;
	private static final int MARGIN = 40;
	private static final int GRID_SIZE = WINDOW_SIZE - 2 * MARGIN; // 720px
	private static final int CELL_SIZE = 10;

	private Image dbImage;
	private Graphics dbg;

	private Walk walk;
	private boolean paused = false;
	private int stepsPerFrame = 1;

	private final Timer motion;

	public Canvas() {
		super("Self Avoiding Walk - Space-Filling");
		setBounds(0, 0, WINDOW_SIZE, WINDOW_SIZE);
		setResizable(false);

		walk = new Walk(CELL_SIZE, MARGIN, MARGIN, GRID_SIZE, GRID_SIZE);

		setVisible(true);

		motion = new Timer(5, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!paused) {
					for (int i = 0; i < stepsPerFrame; i++) {
						if (!walk.revealNext()) {
							break;
						}
					}
				}
				repaint();
			}
		});

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_SPACE:
						paused = !paused;
						break;
					case KeyEvent.VK_R:
						walk = new Walk(CELL_SIZE, MARGIN, MARGIN, GRID_SIZE, GRID_SIZE);
						break;
					case KeyEvent.VK_EQUALS:
					case KeyEvent.VK_ADD:
						stepsPerFrame = Math.min(stepsPerFrame * 2, 128);
						break;
					case KeyEvent.VK_MINUS:
					case KeyEvent.VK_SUBTRACT:
						stepsPerFrame = Math.max(stepsPerFrame / 2, 1);
						break;
				}
			}
		});

		setFocusable(true);
		requestFocus();
		motion.start();
	}

	public void paint(Graphics g) {
		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbImage, 0, 0, this);
	}

	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (walk == null) {
			return;
		}

		// Border
		g.setColor(Color.DARK_GRAY);
		g.setStroke(new BasicStroke(2));
		g.drawRect(MARGIN, MARGIN, GRID_SIZE, GRID_SIZE);

		// Draw the walk path
		java.util.List<int[]> path = walk.getRevealedPath();
		if (path.size() > 1) {
			g.setStroke(new BasicStroke(2));
			for (int i = 0; i < path.size() - 1; i++) {
				// Gradient color based on position in path
				float ratio = (float) i / walk.getTotalCells();
				g.setColor(Color.getHSBColor(ratio, 0.9f, 0.9f));

				int x1 = path.get(i)[0];
				int y1 = path.get(i)[1];
				int x2 = path.get(i + 1)[0];
				int y2 = path.get(i + 1)[1];
				g.drawLine(x1, y1, x2, y2);
			}

			// Draw head position
			int[] head = path.get(path.size() - 1);
			g.setColor(Color.WHITE);
			g.fillOval(head[0] - 3, head[1] - 3, 6, 6);
		}

		// HUD
		g.setColor(Color.WHITE);
		g.setFont(new Font("Monospaced", Font.PLAIN, 12));
		int percent = (int) (100.0 * walk.getRevealedCount() / walk.getTotalCells());
		String status = String.format("Cells: %d/%d (%d%%)  Speed: %dx",
				walk.getRevealedCount(), walk.getTotalCells(), percent, stepsPerFrame);
		g.drawString(status, MARGIN, WINDOW_SIZE - 15);

		if (paused) {
			g.setColor(Color.YELLOW);
			g.drawString("PAUSED", WINDOW_SIZE - 120, WINDOW_SIZE - 15);
		}
		if (walk.isComplete()) {
			g.setColor(Color.GREEN);
			g.drawString("COMPLETE! Press R to restart", WINDOW_SIZE / 2 - 100, WINDOW_SIZE - 15);
		}
	}

	public static void main(String[] args) {
		Canvas draw = new Canvas();
		draw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
