package games.snake;

import java.util.LinkedList;
import java.util.List;

public class Snake {

	private int x;
	private int y;
	private String direction;
	private boolean isAlive;
	private boolean isLonger;
	List<int[]> bodyCoords = new LinkedList<int[]>();

	public Snake() {
		this.setX(150);
		this.setY(150);
		this.setDirection("r");
		this.setAlive(true);

		this.bodyCoords.add(new int[] { 150, 150 });
		this.bodyCoords.add(new int[] { 125, 150 });
		this.bodyCoords.add(new int[] { 100, 150 });
	}

	public void updateCoords(Food f) {
		int newX = this.getX();
		int newY = this.getY();
		this.setLonger(false);

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
		
		if (f.getX() == newX && f.getY() == newY) {
			this.setLonger(true);
		} else {
			this.bodyCoords.remove(this.bodyCoords.size() - 1);
		}
	}

	public void checkStatus() {

		// border check
		if (this.getX() >= 710 || this.getX() <= 40 || this.getY() >= 710 || this.getY() <= 40) {
			this.setAlive(false);
			return;
		}

		// body check
		for (int i = 1; i < this.getBodyCoords().size(); i++) {
			if (this.getX() == this.getBodyCoords().get(i)[0] && this.getY() == this.getBodyCoords().get(i)[1]) {
				this.setAlive(false);
				return;
			}
		}
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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public boolean isLonger() {
		return isLonger;
	}

	public void setLonger(boolean isLonger) {
		this.isLonger = isLonger;
	}

	public List<int[]> getBodyCoords() {
		return bodyCoords;
	}

	public void setBodyCoords(List<int[]> bodyCoords) {
		this.bodyCoords = bodyCoords;
	}
	

	@Override
	public String toString() {
		return "Snake [x=" + x + ", y=" + y + ", direction=" + direction + ", isAlive=" + isAlive + ", isLonger="
				+ isLonger + ", bodyCoords=" + bodyCoords + "]";
	}

}
