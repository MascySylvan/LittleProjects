package simple_machine.cube_timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.Timer;

public class CubeTimer extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean isRunning = false;
	final Timer timerObj = new Timer(1, null);
	public Date startTime;
	public String liveTime = "";
	
	public CubeTimer() {
		super("Snake");
		setBounds(0, 0, 750, 750);
		setVisible(true);
		
		timerObj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Date currDate = new Date();
				
				long millDiff = currDate.getTime() - startTime.getTime();
				
				liveTime = String.format("%02d:%02d:%02d.%02d", 
						TimeUnit.MILLISECONDS.toHours(millDiff),
						TimeUnit.MILLISECONDS.toMinutes(millDiff) -  
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millDiff)), // The change is in this line
						TimeUnit.MILLISECONDS.toSeconds(millDiff) - 
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millDiff)),
						millDiff - TimeUnit.MILLISECONDS.toSeconds(millDiff));   
				
				System.out.println(liveTime);
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				switch (evt.getKeyCode()) {
					case KeyEvent.VK_SPACE: {
						if (isRunning) {
							Stopwatch(0); //stop running
						} else {
							Stopwatch(1); //start running
						}
						
						isRunning = !isRunning;
						break;
					}
				}
			}
		});
	}
	
	private void Stopwatch(int i) {
		if (i == 1) { //start
			startTime = new Date();
			
			timerObj.start();
		} else { //stop
			timerObj.stop();
			
			Date currDate = new Date();
			long millDiff = currDate.getTime() - startTime.getTime();
			
			liveTime = String.format("%02d:%02d:%02d.%02d", 
					TimeUnit.MILLISECONDS.toHours(millDiff),
					TimeUnit.MILLISECONDS.toMinutes(millDiff) -  
					TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millDiff)), // The change is in this line
					TimeUnit.MILLISECONDS.toSeconds(millDiff) - 
					TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millDiff)),
					millDiff - TimeUnit.MILLISECONDS.toSeconds(millDiff));   
			
			System.out.println(liveTime); 
		}
	}

	public static void main(String[] args) {
		CubeTimer draw = new CubeTimer();
		draw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
