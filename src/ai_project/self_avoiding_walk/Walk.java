package ai_project.self_avoiding_walk;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Walk {

	private int pointDistance;
	private int badCoordsLimit;
	List<int[]> bodyCoords = new LinkedList<int[]>();
	List<int[]> tempBadCoords = new LinkedList<int[]>();
	Random rand = new Random();

	public Walk(int pointDistance, int badCoordsLimit) {
		this.setPointDistance(pointDistance);
		this.setBadCoordsLimit(badCoordsLimit);
		this.bodyCoords.add(new int[] { 50, 50 });
	}

	public void createNewCoord() {
		List<String> tempDir = new LinkedList<String>();
		tempDir.add("u");
		tempDir.add("d");
		tempDir.add("l");
		tempDir.add("r");

		boolean isSafe = false;
		for (int i = 0; i < 4; i++) {
			int x = this.getBodyCoords().get(this.bodyCoords.size() - 1)[0];
			int y = this.getBodyCoords().get(this.bodyCoords.size() - 1)[1];
			int n = rand.nextInt(tempDir.size());

			if (tempDir.get(n).equalsIgnoreCase("u")) { // up
				x -= this.getPointDistance();
			} else if (tempDir.get(n).equalsIgnoreCase("d")) { // down
				x += this.getPointDistance();
			} else if (tempDir.get(n).equalsIgnoreCase("l")) { // left
				y -= this.getPointDistance();
			} else { // right
				y += this.getPointDistance();
			}

			if (!coordIsDeadly(x, y)) {
				this.bodyCoords.add(new int[] { x, y });
				isSafe = true;
				break;
			} else {
				tempDir.remove(n);
			}
		}

		if (!isSafe) {
			this.tempBadCoords.add(new int[] { this.getBodyCoords().get(this.bodyCoords.size() - 1)[0],
					this.getBodyCoords().get(this.bodyCoords.size() - 1)[1] });
			this.bodyCoords.remove(this.bodyCoords.size() - 1);
		} else {
			if (this.tempBadCoords.size() > this.getBadCoordsLimit()) {
				this.tempBadCoords.clear();
			}
		}
	}

	private boolean coordIsDeadly(int x, int y) {
		// border check
		if (x >= 760 || x <= 40 || y >= 760 || y <= 40) {
			return true;
		}

		// body check
		for (int i = 1; i < this.getBodyCoords().size(); i++) {
			if (x == this.getBodyCoords().get(i)[0] && y == this.getBodyCoords().get(i)[1]) {
				return true;
			}
		}

		// temp check
		if (this.tempBadCoords.size() > 0) {
			for (int i = 0; i < this.getTempBadCoords().size(); i++) {
				if (x == this.getTempBadCoords().get(i)[0] && y == this.getTempBadCoords().get(i)[1]) {
					return true;
				}
			}
		}

		return false;
	}

	public int getPointDistance() {
		return pointDistance;
	}

	public void setPointDistance(int pointDistance) {
		this.pointDistance = pointDistance;
	}

	public int getBadCoordsLimit() {
		return badCoordsLimit;
	}

	public void setBadCoordsLimit(int badCoordsLimit) {
		this.badCoordsLimit = badCoordsLimit;
	}

	public List<int[]> getBodyCoords() {
		return bodyCoords;
	}

	public void setBodyCoords(List<int[]> bodyCoords) {
		this.bodyCoords = bodyCoords;
	}

	public List<int[]> getTempBadCoords() {
		return tempBadCoords;
	}

	public void setTempBadCoords(List<int[]> tempBadCoords) {
		this.tempBadCoords = tempBadCoords;
	}

}
