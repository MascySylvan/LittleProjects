package games.hpos_jeopardy;

import java.util.List;

/**
 * Represents a complete Jeopardy game data set.
 * Contains exactly 5 categories, each with 5 clues.
 */
public class GameData {

	private List<Category> categories;

	/**
	 * Constructs a GameData with the given categories.
	 *
	 * @param categories the list of 5 categories for this game
	 */
	public GameData(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getCategories() {
		return categories;
	}

}
