package games.snake.ai;

import java.util.LinkedList;
import java.util.List;

public class Snake {

	public static final int GRID_MIN = 50;

	private final int gridSize;
	private final int cellSize;
	private final int gridMax;
	private final int totalCells;

	private int x;
	private int y;
	private String direction;
	private boolean isAlive;
	private boolean isLonger;
	List<int[]> bodyCoords = new LinkedList<int[]>();

	private int[] cycleOrder;
	private int[] cycleIndex;

	public Snake(int gridSize) {
		this.gridSize = gridSize;
		this.cellSize = 700 / gridSize; // scale cell size to fit ~750px
		this.gridMax = GRID_MIN + (gridSize - 1) * cellSize;
		this.totalCells = gridSize * gridSize;

		buildHamiltonianCycle();

		// Start on column 0 heading down (cycle positions 2, 1, 0)
		this.setX(GRID_MIN);
		this.setY(GRID_MIN + 2 * cellSize);
		this.setDirection("d");
		this.setAlive(true);

		this.bodyCoords.add(new int[] { GRID_MIN, GRID_MIN + 2 * cellSize });
		this.bodyCoords.add(new int[] { GRID_MIN, GRID_MIN + 1 * cellSize });
		this.bodyCoords.add(new int[] { GRID_MIN, GRID_MIN });
	}

	/**
	 * Builds a Hamiltonian cycle on an even-sized grid.
	 * Construction:
	 *   1) Down column 0: rows 0..N-1
	 *   2) Right along bottom row: cols 1..N-1
	 *   3) Zigzag columns N-1 to 1 (N-1 columns, odd count for even N):
	 *      alternating UP/DOWN, ending at (row 0, col 1) adjacent to start.
	 */
	private void buildHamiltonianCycle() {
		cycleOrder = new int[totalCells];
		cycleIndex = new int[totalCells];

		int pos = 0;

		// Step 1: Down column 0
		for (int row = 0; row < gridSize; row++) {
			int idx = row * gridSize;
			cycleOrder[idx] = pos;
			cycleIndex[pos] = idx;
			pos++;
		}

		// Step 2: Right along bottom row (cols 1..N-1)
		for (int col = 1; col < gridSize; col++) {
			int idx = (gridSize - 1) * gridSize + col;
			cycleOrder[idx] = pos;
			cycleIndex[pos] = idx;
			pos++;
		}

		// Step 3: Zigzag columns N-1 to 1
		for (int c = gridSize - 1; c >= 1; c--) {
			int distFromRight = (gridSize - 1) - c;
			if (distFromRight % 2 == 0) {
				for (int row = gridSize - 2; row >= 0; row--) {
					int idx = row * gridSize + c;
					cycleOrder[idx] = pos;
					cycleIndex[pos] = idx;
					pos++;
				}
			} else {
				for (int row = 0; row <= gridSize - 2; row++) {
					int idx = row * gridSize + c;
					cycleOrder[idx] = pos;
					cycleIndex[pos] = idx;
					pos++;
				}
			}
		}
	}

	private int toGridIndex(int px, int py) {
		int col = (px - GRID_MIN) / cellSize;
		int row = (py - GRID_MIN) / cellSize;
		return row * gridSize + col;
	}

	private int getPathPos(int px, int py) {
		return cycleOrder[toGridIndex(px, py)];
	}

	private int cycleDistance(int posA, int posB) {
		return (posB - posA + totalCells) % totalCells;
	}

	public void aiMovement(Food f) {
		int headPos = getPathPos(this.x, this.y);
		int[] tail = bodyCoords.get(bodyCoords.size() - 1);
		int tailPos = getPathPos(tail[0], tail[1]);
		int foodPos = getPathPos(f.getX(), f.getY());

		String bestMove = getNextCycleDir(headPos);
		int snakeLength = bodyCoords.size();

		if (snakeLength < totalCells / 2) {
			int bestDist = cycleDistance(getNextPos(headPos), foodPos);
			String opposite = getOpposite(this.direction);
			int[][] neighbors = {
				{ this.x, this.y - cellSize },
				{ this.x, this.y + cellSize },
				{ this.x - cellSize, this.y },
				{ this.x + cellSize, this.y }
			};
			String[] dirs = { "u", "d", "l", "r" };

			for (int i = 0; i < 4; i++) {
				if (dirs[i].equals(opposite)) continue;
				int nx = neighbors[i][0], ny = neighbors[i][1];
				if (nx < GRID_MIN || nx > gridMax || ny < GRID_MIN || ny > gridMax) continue;
				if (isBodyAt(nx, ny)) continue;

				int nPos = getPathPos(nx, ny);
				int distToN = cycleDistance(headPos, nPos);
				int distToTail = cycleDistance(headPos, tailPos);

				if (distToN >= distToTail) continue;

				int distToFood = cycleDistance(nPos, foodPos);
				if (distToFood < bestDist) {
					bestDist = distToFood;
					bestMove = dirs[i];
				}
			}
		}

		this.setDirection(bestMove);
	}

	private int getNextPos(int pos) {
		return (pos + 1) % totalCells;
	}

	private String getNextCycleDir(int headPos) {
		int nextPos = getNextPos(headPos);
		int nextIdx = cycleIndex[nextPos];
		int nextCol = nextIdx % gridSize;
		int nextRow = nextIdx / gridSize;
		int nextPx = GRID_MIN + nextCol * cellSize;
		int nextPy = GRID_MIN + nextRow * cellSize;

		if (nextPx > this.x) return "r";
		if (nextPx < this.x) return "l";
		if (nextPy > this.y) return "d";
		return "u";
	}

	private String getOpposite(String dir) {
		switch (dir) {
			case "r": return "l";
			case "l": return "r";
			case "u": return "d";
			case "d": return "u";
		}
		return "";
	}

	private boolean isBodyAt(int px, int py) {
		for (int[] seg : bodyCoords) {
			if (seg[0] == px && seg[1] == py) return true;
		}
		return false;
	}

	public void updateCoords(Food f) {
		int newX = this.x, newY = this.y;
		this.setLonger(false);

		switch (this.direction) {
			case "r": newX += cellSize; break;
			case "l": newX -= cellSize; break;
			case "u": newY -= cellSize; break;
			case "d": newY += cellSize; break;
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
		if (this.x > gridMax || this.x < GRID_MIN || this.y > gridMax || this.y < GRID_MIN) {
			this.setAlive(false);
			return;
		}
		for (int i = 1; i < bodyCoords.size(); i++) {
			if (this.x == bodyCoords.get(i)[0] && this.y == bodyCoords.get(i)[1]) {
				this.setAlive(false);
				return;
			}
		}
	}

	// --- Getters / Setters ---

	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }
	public String getDirection() { return direction; }
	public void setDirection(String d) { this.direction = d; }
	public boolean isAlive() { return isAlive; }
	public void setAlive(boolean a) { this.isAlive = a; }
	public boolean isLonger() { return isLonger; }
	public void setLonger(boolean l) { this.isLonger = l; }
	public List<int[]> getBodyCoords() { return bodyCoords; }
	public int getGridSize() { return gridSize; }
	public int getCellSize() { return cellSize; }
	public int getGridMax() { return gridMax; }
	public int getTotalCells() { return totalCells; }
}
