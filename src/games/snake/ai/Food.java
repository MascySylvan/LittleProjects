package games.snake.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Food {

	private int x;
	private int y;
	private final Random rand = new Random();

	public Food(Snake s) {
		List<int[]> available = new ArrayList<int[]>();
		for (int col = 0; col < s.getGridSize(); col++) {
			for (int row = 0; row < s.getGridSize(); row++) {
				int px = Snake.GRID_MIN + col * s.getCellSize();
				int py = Snake.GRID_MIN + row * s.getCellSize();
				boolean occupied = false;
				for (int[] seg : s.getBodyCoords()) {
					if (seg[0] == px && seg[1] == py) {
						occupied = true;
						break;
					}
				}
				if (!occupied) {
					available.add(new int[] { px, py });
				}
			}
		}

		if (available.size() > 0) {
			int[] chosen = available.get(rand.nextInt(available.size()));
			this.x = chosen[0];
			this.y = chosen[1];
		}
	}

	public int getX() { return x; }
	public int getY() { return y; }
}
