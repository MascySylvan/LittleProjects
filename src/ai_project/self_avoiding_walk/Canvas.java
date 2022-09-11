package ai_project.self_avoiding_walk;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Canvas extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Image dbImage;
	private Graphics dbg;
	
	private Walk walk = new Walk(10, 1000);
	final Timer motion = new Timer(10, null);

	public Canvas() {
		super("Self Avoiding Walk");
		setBounds(0, 0, 800, 800);
		setVisible(true);
		
		motion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				walk.createNewCoord();
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
		g.drawLine(40, 40, 40, 760); // right
		g.drawLine(40, 760, 760, 760); // bottom
		g.drawLine(760, 760, 760, 40); // left
		g.drawLine(760, 40, 40, 40); // upper
		
		// draw walk
		for (int i = 0; i < walk.getBodyCoords().size() - 1; i++) {
			int x1 = walk.getBodyCoords().get(i)[0];
			int y1 = walk.getBodyCoords().get(i)[1];
			int x2 = walk.getBodyCoords().get(i + 1)[0];
			int y2 = walk.getBodyCoords().get(i + 1)[1];

			g.drawLine(x1, y1, x2, y2);
		}

		repaint();
	}

	public static void main(String[] args) {
		Canvas draw = new Canvas();
		draw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
