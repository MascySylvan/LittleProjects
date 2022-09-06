package games.snake;

import java.util.LinkedList;
import java.util.List;

public class Snake {

	private int x;
	private int y;
	private int speed;
	private String direction;
	private boolean isAlive;
	private boolean isLonger;
	List<int[]> bodyCoords = new LinkedList<int[]>();
	List<int[]> tempDeadlyCoords = new LinkedList<int[]>();

	public Snake() {
		this.setX(150);
		this.setY(150);
		this.setSpeed(25);
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
			newX += this.getSpeed();
		} else if (this.direction.equalsIgnoreCase("l")) {
			newX -= this.getSpeed();
		} else if (this.direction.equalsIgnoreCase("u")) {
			newY -= this.getSpeed();
		} else if (this.direction.equalsIgnoreCase("d")) {
			newY += this.getSpeed();
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

	private boolean coordIsDeadly(int x, int y) {
		// border check
		if (x >= 710 || x <= 40 || y >= 710 || y <= 40) {
			return true;
		}

		// body check
		for (int i = 1; i < this.getBodyCoords().size(); i++) {
			if (x == this.getBodyCoords().get(i)[0] && y == this.getBodyCoords().get(i)[1]) {
				return true;
			}
		}

		return false;
	}

	public void aiMovement(Food f) {
		String[] movementPrio;

		if (f.getX() <= this.getX()) {
			if (f.getY() <= this.getY()) {
				movementPrio = new String[] { "l", "u", "r", "d" };
			} else {
				movementPrio = new String[] { "l", "d", "r", "u" };
			}
		} else {
			if (f.getY() <= this.getY()) {
				movementPrio = new String[] { "r", "u", "l", "d" };
			} else {
				movementPrio = new String[] { "r", "d", "l", "u" };
			}
		}

		String selectedMove = "";
		for (int i = 0; i < 4; i++) {
			String move = movementPrio[i];

			if (move.equalsIgnoreCase("l")) {
				if (!this.getDirection().equalsIgnoreCase("r")) {
					int newX = this.getX() - this.getSpeed();
					int newY = this.getY();

					if (!coordIsDeadly(newX, newY)) {
						selectedMove = "l";
						break;
					}
				}
			} else if (move.equalsIgnoreCase("u")) {
				if (!this.getDirection().equalsIgnoreCase("d")) {
					int newX = this.getX();
					int newY = this.getY() - this.getSpeed();

					if (!coordIsDeadly(newX, newY)) {
						selectedMove = "u";
						break;
					}
				}
			} else if (move.equalsIgnoreCase("r")) {
				if (!this.getDirection().equalsIgnoreCase("l")) {
					int newX = this.getX() + this.getSpeed();
					int newY = this.getY();

					if (!coordIsDeadly(newX, newY)) {
						selectedMove = "r";
						break;
					}
				}
			} else {
				if (!this.getDirection().equalsIgnoreCase("u")) {
					int newX = this.getX();
					int newY = this.getY() + this.getSpeed();

					if (!coordIsDeadly(newX, newY)) {
						selectedMove = "d";
						break;
					}
				}
			}
		}
		
		this.setDirection(selectedMove);
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

	public List<int[]> getTempDeadlyCoords() {
		return tempDeadlyCoords;
	}

	public void setTempDeadlyCoords(List<int[]> tempDeadlyCoords) {
		this.tempDeadlyCoords = tempDeadlyCoords;
	}

	@Override
	public String toString() {
		return "Snake [x=" + x + ", y=" + y + ", direction=" + direction + ", isAlive=" + isAlive + ", isLonger="
				+ isLonger + ", bodyCoords=" + bodyCoords + "]";
	}

}
