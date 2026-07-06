package games.hpos_jeopardy;

/**
 * Represents a single clue on the Jeopardy board.
 * Each clue has a point value, clue text (the "answer" shown to the player),
 * response text (the "question" the player must provide), and an optional image path.
 */
public class Clue {

	private int pointValue;
	private String clueText;
	private String responseText;
	private String imagePath;

	/**
	 * Constructs a Clue with the given fields.
	 *
	 * @param pointValue   the point value (100, 200, 300, 500, or 1000)
	 * @param clueText     the clue text displayed to the player
	 * @param responseText the correct response text
	 * @param imagePath    the file path to an optional image (empty string if none)
	 */
	public Clue(int pointValue, String clueText, String responseText, String imagePath) {
		this.pointValue = pointValue;
		this.clueText = clueText;
		this.responseText = responseText;
		this.imagePath = imagePath;
	}

	public int getPointValue() {
		return pointValue;
	}

	public String getClueText() {
		return clueText;
	}

	public String getResponseText() {
		return responseText;
	}

	public String getImagePath() {
		return imagePath;
	}

}
