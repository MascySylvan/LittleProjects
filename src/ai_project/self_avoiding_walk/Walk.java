package ai_project.self_avoiding_walk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates a random space-filling walk on a grid using randomized DFS
 * with backtracking (Hamiltonian path). The full path is pre-computed,
 * then revealed step by step for animation.
 */
public class Walk {

	private final int cellSize;
	private final int gridStartX;
	private final int gridStartY;
	private final int cols;
	private final int rows;

	private final List<int[]> fullPath;
	private int revealedCount;

	private final Random rand = new Random();

	/**
	 * @param cellSize    pixel distance between grid points
	 * @param gridStartX  left boundary x (pixels)
	 * @param gridStartY  top boundary y (pixels)
	 * @param gridWidth   width of the walkable area (pixels)
	 * @param gridHeight  height of the walkable area (pixels)
	 */
	public Walk(int cellSize, int gridStartX, int gridStartY, int gridWidth, int gridHeight) {
		this.cellSize = cellSize;
		this.gridStartX = gridStartX;
		this.gridStartY = gridStartY;
		this.cols = gridWidth / cellSize;
		this.rows = gridHeight / cellSize;

		this.fullPath = generateFullPath();
		this.revealedCount = 1; // start with just the first point visible
	}

	/**
	 * Reveals one more step of the walk.
	 * @return true if a new step was revealed, false if the walk is complete
	 */
	public boolean revealNext() {
		if (revealedCount < fullPath.size()) {
			revealedCount++;
			return true;
		}
		return false;
	}

	/**
	 * Returns the portion of the path that has been revealed so far.
	 */
	public List<int[]> getRevealedPath() {
		return fullPath.subList(0, revealedCount);
	}

	/**
	 * Returns true if the entire path has been revealed.
	 */
	public boolean isComplete() {
		return revealedCount >= fullPath.size();
	}

	/**
	 * Resets and generates a new random path from scratch.
	 */
	public void reset() {
		fullPath.clear();
		fullPath.addAll(generateFullPath());
		revealedCount = 1;
	}

	public int getTotalCells() {
		return cols * rows;
	}

	public int getRevealedCount() {
		return revealedCount;
	}

	/**
	 * Generates a Hamiltonian path using randomized DFS with backtracking.
	 * Starts from a random cell and attempts to visit every cell exactly once.
	 */
	private List<int[]> generateFullPath() {
		int totalCells = cols * rows;
		boolean[][] visited = new boolean[cols][rows];

		List<int[]> path = new ArrayList<int[]>(totalCells);

		// Start from a random cell
		int startCol = rand.nextInt(cols);
		int startRow = rand.nextInt(rows);

		path.add(new int[] { startCol, startRow });
		visited[startCol][startRow] = true;

		// Use Warnsdorff's heuristic with random tiebreaking for better fill rates
		if (buildPathWarnsdorff(path, visited, totalCells)) {
			// Convert grid coordinates to pixel coordinates
			return toPixelCoords(path);
		}

		// Fallback: try pure randomized DFS from multiple starting points
		for (int attempt = 0; attempt < 10; attempt++) {
			path.clear();
			visited = new boolean[cols][rows];

			startCol = rand.nextInt(cols);
			startRow = rand.nextInt(rows);
			path.add(new int[] { startCol, startRow });
			visited[startCol][startRow] = true;

			if (buildPathDFS(path, visited, totalCells)) {
				return toPixelCoords(path);
			}
		}

		// If we couldn't fill completely, return whatever we got (best effort)
		return toPixelCoords(path);
	}

	/**
	 * Warnsdorff's rule: always move to the neighbor with the fewest onward moves.
	 * Breaks ties randomly. This heuristic produces near-Hamiltonian paths on grids
	 * very efficiently without deep backtracking.
	 */
	private boolean buildPathWarnsdorff(List<int[]> path, boolean[][] visited, int totalCells) {
		int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };

		while (path.size() < totalCells) {
			int[] current = path.get(path.size() - 1);
			int cx = current[0];
			int cy = current[1];

			// Find all unvisited neighbors and their degrees
			List<int[]> candidates = new ArrayList<int[]>(4);
			for (int[] dir : directions) {
				int nx = cx + dir[0];
				int ny = cy + dir[1];
				if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && !visited[nx][ny]) {
					int degree = countUnvisitedNeighbors(nx, ny, visited);
					candidates.add(new int[] { nx, ny, degree });
				}
			}

			if (candidates.isEmpty()) {
				return path.size() == totalCells;
			}

			// Find minimum degree
			int minDegree = Integer.MAX_VALUE;
			for (int[] c : candidates) {
				if (c[2] < minDegree) {
					minDegree = c[2];
				}
			}

			// Collect all candidates with minimum degree (tie-breaking randomly)
			List<int[]> best = new ArrayList<int[]>(4);
			for (int[] c : candidates) {
				if (c[2] == minDegree) {
					best.add(c);
				}
			}

			// Pick randomly among best
			int[] chosen = best.get(rand.nextInt(best.size()));
			path.add(new int[] { chosen[0], chosen[1] });
			visited[chosen[0]][chosen[1]] = true;
		}

		return true;
	}

	private int countUnvisitedNeighbors(int x, int y, boolean[][] visited) {
		int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
		int count = 0;
		for (int[] dir : directions) {
			int nx = x + dir[0];
			int ny = y + dir[1];
			if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && !visited[nx][ny]) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Randomized DFS with backtracking as a fallback.
	 */
	private boolean buildPathDFS(List<int[]> path, boolean[][] visited, int totalCells) {
		int[][] directions = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };

		while (path.size() < totalCells) {
			int[] current = path.get(path.size() - 1);
			int cx = current[0];
			int cy = current[1];

			// Shuffle directions for randomness
			List<int[]> shuffled = new ArrayList<int[]>(4);
			for (int[] d : directions) {
				shuffled.add(d);
			}
			Collections.shuffle(shuffled, rand);

			boolean moved = false;
			for (int[] dir : shuffled) {
				int nx = cx + dir[0];
				int ny = cy + dir[1];
				if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && !visited[nx][ny]) {
					path.add(new int[] { nx, ny });
					visited[nx][ny] = true;
					moved = true;
					break;
				}
			}

			if (!moved) {
				// Dead end — backtrack
				int[] dead = path.remove(path.size() - 1);
				visited[dead[0]][dead[1]] = false;
				if (path.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Converts grid coordinates to pixel coordinates for rendering.
	 */
	private List<int[]> toPixelCoords(List<int[]> gridPath) {
		List<int[]> pixelPath = new ArrayList<int[]>(gridPath.size());
		for (int[] cell : gridPath) {
			int px = gridStartX + cell[0] * cellSize + cellSize / 2;
			int py = gridStartY + cell[1] * cellSize + cellSize / 2;
			pixelPath.add(new int[] { px, py });
		}
		return pixelPath;
	}
}
