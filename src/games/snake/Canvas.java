package games.snake;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Canvas extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image dbImage;
	private Graphics dbg;

	private Snake snake = new Snake();
	final Timer motion = new Timer(75, null);

	public Canvas() {
		super("Snake");
		setBounds(0, 0, 750, 750);
		setVisible(true);

		motion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				snake.updateCoords();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				switch (evt.getKeyCode()) {
				case KeyEvent.VK_UP:
					if (!snake.getDirection().equalsIgnoreCase("d")) {
						snake.setDirection("u");
					}
					break;
				case KeyEvent.VK_DOWN:
					if (!snake.getDirection().equalsIgnoreCase("u")) {
						snake.setDirection("d");
					}
					break;
				case KeyEvent.VK_LEFT:
					if (!snake.getDirection().equalsIgnoreCase("r")) {
						snake.setDirection("l");
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!snake.getDirection().equalsIgnoreCase("l")) {
						snake.setDirection("r");
					}
					break;
				}
			}
		});

		motion.start();
	}

	public void paint(Graphics g) {
		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbImage, 0, 0, this);
	}

	public void paintComponent(Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D) g1;

		g.setStroke(new BasicStroke(5));
		g.drawLine(40, 40, 40, 710); // right
		g.drawLine(40, 710, 710, 710); // bottom
		g.drawLine(710, 710, 710, 40); // left
		g.drawLine(710, 40, 40, 40); // upper

		// draw snake
		for (int i = 0; i < snake.getBodyCoords().size() - 1; i++) {
			int x1 = snake.getBodyCoords().get(i)[0];
			int y1 = snake.getBodyCoords().get(i)[1];
			int x2 = snake.getBodyCoords().get(i + 1)[0];
			int y2 = snake.getBodyCoords().get(i + 1)[1];

			g.drawLine(x1, y1, x2, y2);
		}

		repaint();
	}

	public static void main(String[] args) {
		Canvas draw = new Canvas();
		draw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
