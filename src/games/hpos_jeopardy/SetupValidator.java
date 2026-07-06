package games.hpos_jeopardy;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates GameData before saving to a Question File.
 * Checks that all required fields are non-empty/non-whitespace and do not
 * contain disallowed characters (tab or newline).
 */
public class SetupValidator {

	/**
	 * Validates the given GameData.
	 *
	 * @param data the game data to validate
	 * @return an empty list if valid; otherwise a list of error messages
	 *         identifying each invalid field
	 */
	public List<String> validate(GameData data) {
		List<String> errors = new ArrayList<>();
		List<Category> categories = data.getCategories();

		for (int i = 0; i < categories.size(); i++) {
			int categoryPosition = i + 1;
			Category category = categories.get(i);

			// Validate category name
			String name = category.getName();
			if (isBlank(name)) {
				errors.add("Category " + categoryPosition + " name is empty");
			} else if (containsDisallowedChars(name)) {
				errors.add("Category " + categoryPosition + " name contains a tab or newline character");
			}

			// Validate each clue in the category
			List<Clue> clues = category.getClues();
			for (int j = 0; j < clues.size(); j++) {
				Clue clue = clues.get(j);
				int pointValue = clue.getPointValue();

				// Validate clue text
				String clueText = clue.getClueText();
				if (isBlank(clueText)) {
					errors.add("Category " + categoryPosition + ", " + pointValue + " point clue text is empty");
				} else if (containsDisallowedChars(clueText)) {
					errors.add("Category " + categoryPosition + ", " + pointValue + " point clue text contains a tab or newline character");
				}

				// Validate response text
				String responseText = clue.getResponseText();
				if (isBlank(responseText)) {
					errors.add("Category " + categoryPosition + ", " + pointValue + " point response text is empty");
				} else if (containsDisallowedChars(responseText)) {
					errors.add("Category " + categoryPosition + ", " + pointValue + " point response text contains a tab or newline character");
				}

				// Image path is allowed to be empty — no validation needed
			}
		}

		return errors;
	}

	/**
	 * Returns true if the string is null, empty, or contains only whitespace.
	 */
	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	/**
	 * Returns true if the string contains a tab or newline character.
	 */
	private boolean containsDisallowedChars(String s) {
		return s.indexOf('\t') >= 0 || s.indexOf('\n') >= 0;
	}

}
