package games.hpos_jeopardy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Writes GameData to a Question File in UTF-8 tab-delimited format.
 * 
 * Output format: 5 category blocks, each block consists of 1 category name line
 * followed by 5 clue lines ordered by ascending point value (100, 200, 300, 500, 1000).
 * Each clue line: pointValue\tclueText\tresponseText\timagePath
 * Total output: exactly 30 lines.
 */
public class QuestionFileWriter {

	/**
	 * Writes the given GameData to the specified file in UTF-8 tab-delimited format.
	 *
	 * @param data the game data containing exactly 5 categories with 5 clues each
	 * @param file the file to write to
	 * @throws IOException if an I/O error occurs while writing
	 */
	public void write(GameData data, File file) throws IOException {
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		try {
			List<Category> categories = data.getCategories();
			for (int i = 0; i < categories.size(); i++) {
				Category category = categories.get(i);
				writer.write(category.getName());
				writer.newLine();

				List<Clue> clues = category.getClues();
				for (int j = 0; j < clues.size(); j++) {
					Clue clue = clues.get(j);
					writer.write(clue.getPointValue() + "\t"
							+ clue.getClueText() + "\t"
							+ clue.getResponseText() + "\t"
							+ clue.getImagePath());
					writer.newLine();
				}
			}
		} finally {
			writer.close();
		}
	}

}
