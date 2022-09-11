package games.snake.ai;

import java.awt.BasicStroke;
import java.awt.Color;
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
	private Food food = new Food(snake);
	final Timer motion = new Timer(10, null);

	public Canvas() {
		super("AI: Snake");
		setBounds(0, 0, 750, 750);
		setVisible(true);

		motion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				snake.aiMovement(food);
				snake.updateCoords(food);
				snake.checkStatus();
				
				if (!snake.isAlive() ) {
					System.out.println("Dead");
					motion.stop();
				} else {
					if (snake.isLonger() ) {
						food = new Food(snake);
					}
				}
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
				case KeyEvent.VK_R:
					reset();
					break;
				case KeyEvent.VK_P:
					pause();
					break;
				}
			}
		});

		motion.start();
	}
	
	private void reset() {
		snake = new Snake();
		food = new Food(snake);
		motion.start();
	}
	
	private void pause() {
		if (motion.isRunning()) {
			motion.stop();
		} else {
			motion.start();
		}
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

		g.setStroke(new BasicStroke(10));
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
		
		//draw food
		g.setColor(Color.DARK_GRAY);
		g.drawLine(food.getX(), food.getY(), food.getX(), food.getY());

		repaint();
	}

	public static void main(String[] args) {
		Canvas draw = new Canvas();
		draw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
