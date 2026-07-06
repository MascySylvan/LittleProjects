package games.hpos_jeopardy;

import java.util.List;

/**
 * Represents a category on the Jeopardy board.
 * Each category has a name and exactly 5 clues ordered by ascending point value.
 */
public class Category {

	private String name;
	private List<Clue> clues;

	/**
	 * Constructs a Category with the given name and clues.
	 *
	 * @param name  the category name (max 50 characters)
	 * @param clues the list of 5 clues for this category, ordered by point value
	 */
	public Category(String name, List<Clue> clues) {
		this.name = name;
		this.clues = clues;
	}

	public String getName() {
		return name;
	}

	public List<Clue> getClues() {
		return clues;
	}

}
