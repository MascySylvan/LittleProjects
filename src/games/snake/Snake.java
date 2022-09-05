package games.snake;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Snake {

	private int x;
	private int y;
	private int speed;
	private String direction;
	List<int[]> bodyCoords = new LinkedList<int[]>();

	public Snake() {
		this.setX(150);
		this.setY(150);
		this.setSpeed(speed);
		this.setDirection("r");

		this.bodyCoords.add(new int[] { 150, 150 });
		this.bodyCoords.add(new int[] { 125, 150 });
		this.bodyCoords.add(new int[] { 100, 150 });
	}

	public void updateCoords() {
		int newX = this.getX();
		int newY = this.getY();

		if (this.direction.equalsIgnoreCase("r")) {
			newX += 25;
		} else if (this.direction.equalsIgnoreCase("l")) {
			newX -= 25;
		} else if (this.direction.equalsIgnoreCase("u")) {
			newY -= 25;
		} else if (this.direction.equalsIgnoreCase("d")) {
			newY += 25;
		}

		this.setX(newX);
		this.setY(newY);
		this.bodyCoords.add(0, new int[] { newX, newY });
		this.bodyCoords.remove(this.bodyCoords.size() - 1);
	}
	
	public void checkStatus() {
		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public List<int[]> getBodyCoords() {
		return bodyCoords;
	}

	public void setBodyCoords(List<int[]> bodyCoords) {
		this.bodyCoords = bodyCoords;
	}

}
