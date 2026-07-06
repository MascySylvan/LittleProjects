package games.hpos_jeopardy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a Question File in UTF-8 tab-delimited format into a GameData object.
 * 
 * Expected format: exactly 30 lines organized as 5 category blocks.
 * Each block = 1 category name line + 5 clue lines (point values 100, 200, 300, 500, 1000).
 * Each clue line: pointValue\tclueText\tresponseText\timagePath
 */
public class QuestionFileParser {

	private static final int EXPECTED_LINE_COUNT = 30;
	private static final int CATEGORIES_COUNT = 5;
	private static final int CLUES_PER_CATEGORY = 5;
	private static final int FIELDS_PER_CLUE = 4;

	/**
	 * Parses the given Question File into a GameData object.
	 *
	 * @param file the file to parse
	 * @return the parsed GameData containing 5 categories with 5 clues each
	 * @throws ParseException if the file is empty, has wrong line count,
	 *                        contains malformed delimiters, or has missing fields
	 */
	public GameData parse(File file) throws ParseException {
		List<String> lines = readLines(file);

		if (lines.isEmpty()) {
			throw new ParseException("File is empty");
		}

		if (lines.size() != EXPECTED_LINE_COUNT) {
			throw new ParseException("Expected " + EXPECTED_LINE_COUNT
					+ " lines but found " + lines.size());
		}

		List<Category> categories = new ArrayList<Category>();
		int lineIndex = 0;

		for (int cat = 0; cat < CATEGORIES_COUNT; cat++) {
			String categoryName = lines.get(lineIndex);
			lineIndex++;

			List<Clue> clues = new ArrayList<Clue>();
			for (int clueIdx = 0; clueIdx < CLUES_PER_CATEGORY; clueIdx++) {
				String clueLine = lines.get(lineIndex);
				int lineNumber = lineIndex + 1;
				Clue clue = parseClue(clueLine, lineNumber, cat + 1, clueIdx);
				clues.add(clue);
				lineIndex++;
			}

			categories.add(new Category(categoryName, clues));
		}

		return new GameData(categories);
	}

	/**
	 * Reads all lines from the file using UTF-8 encoding.
	 */
	private List<String> readLines(File file) throws ParseException {
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			throw new ParseException("Failed to read file: " + e.getMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// ignore close errors
				}
			}
		}
		return lines;
	}

	/**
	 * Parses a single clue line into a Clue object.
	 *
	 * @param line           the tab-delimited clue line
	 * @param lineNumber     the 1-based line number (for error messages)
	 * @param categoryNumber the 1-based category number (for error messages)
	 * @param clueIndex      the 0-based clue index within the category
	 * @return the parsed Clue
	 * @throws ParseException if the line has wrong field count or malformed data
	 */
	private Clue parseClue(String line, int lineNumber, int categoryNumber, int clueIndex)
			throws ParseException {
		String[] fields = line.split("\t", -1);

		if (fields.length != FIELDS_PER_CLUE) {
			throw new ParseException("Line " + lineNumber
					+ " (category " + categoryNumber + ", clue " + (clueIndex + 1)
					+ "): expected " + FIELDS_PER_CLUE
					+ " tab-separated fields but found " + fields.length);
		}

		int pointValue;
		try {
			pointValue = Integer.parseInt(fields[0]);
		} catch (NumberFormatException e) {
			throw new ParseException("Line " + lineNumber
					+ " (category " + categoryNumber + ", clue " + (clueIndex + 1)
					+ "): invalid point value '" + fields[0] + "'");
		}

		String clueText = fields[1];
		String responseText = fields[2];
		String imagePath = fields[3];

		return new Clue(pointValue, clueText, responseText, imagePath);
	}

}
