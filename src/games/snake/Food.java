package games.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Food {

	private int x;
	private int y;
	Random rand = new Random();

	public Food(Snake s) {

		// save all available coords
		List<int[]> coords = new ArrayList<int[]>();
		for (int i = 50; i <= 700; i += 25) { // run through x axis
			for (int k = 50; k <= 700; k += 25) { // run through y axis
				boolean add = true;

				for (int g = 0; g < s.getBodyCoords().size(); g++) { // run through snake coords
					if (s.getBodyCoords().get(g)[0] == i && s.getBodyCoords().get(g)[1] == k) {
						add = false;
						break;
					}
				}

				if (add) {
					coords.add(new int[] { i, k });
				}
			}
		}

		// new food location
		if (coords.size() > 0) {
			int r = rand.nextInt(coords.size());

			this.setX(coords.get(r)[0]);
			this.setY(coords.get(r)[1]);
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

}
