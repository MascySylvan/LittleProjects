# Implementation Plan: HPOS Jeopardy

## Overview

Build a standalone Java 8 Swing Jeopardy game as a self-contained sub-project under `src/games/hpos_jeopardy/`. The implementation follows a model-first approach: data classes and file I/O first, then UI panels, then wiring and integration. Each task builds incrementally so there is no orphaned code.

## Tasks

- [x] 1. Set up project structure and core model classes
  - [x] 1.1 Create package directory and model classes (Clue, Category, GameData)
    - Create `src/games/hpos_jeopardy/` directory
    - Create `Clue.java`: POJO with `int pointValue`, `String clueText`, `String responseText`, `String imagePath` fields, constructor, and getters
    - Create `Category.java`: POJO with `String name`, `List<Clue> clues` (size 5), constructor, and getters
    - Create `GameData.java`: POJO with `List<Category> categories` (size 5), constructor, and getters
    - _Requirements: 7.1, 7.2, 8.1_

  - [x] 1.2 Implement SetupValidator
    - Create `SetupValidator.java` with method `List<String> validate(GameData data)`
    - Return empty list if all category names, clue texts, and response texts are non-empty/non-whitespace
    - Return error messages identifying each empty/whitespace field by category position and point value
    - Reject any field containing tab (`\t`) or newline (`\n`) characters with a specific error message
    - Allow empty image path fields without error
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 7.4_

  - [x] 1.3 Implement QuestionFileWriter
    - Create `QuestionFileWriter.java` with method `void write(GameData data, File file) throws IOException`
    - Write UTF-8 encoded file: 5 category blocks, each block = 1 category name line + 5 tab-delimited clue lines (pointValue\tclueText\tresponseText\timagePath)
    - Clue lines ordered by ascending point value: 100, 200, 300, 500, 1000
    - Total output: exactly 30 lines
    - _Requirements: 7.1, 7.2, 4.2_

  - [x] 1.4 Implement QuestionFileParser
    - Create `QuestionFileParser.java` with method `GameData parse(File file) throws ParseException`
    - Read UTF-8 file, expect exactly 30 lines organized as 5 blocks of 6 lines each
    - Parse tab-delimited clue lines into Clue objects
    - Throw `ParseException` with descriptive message for: empty file, wrong line count, malformed delimiters, missing fields
    - Create custom `ParseException.java` class (extends Exception)
    - _Requirements: 6.2, 6.4, 6.5, 7.1, 7.2_

- [x] 2. Checkpoint - Verify model layer
  - Ensure all model classes compile cleanly: `javac -d bin src/games/hpos_jeopardy/*.java`
  - Ask the user if questions arise.

- [ ] 3. Property-based tests for model layer
  - [ ]* 3.1 Write property test for serialization round-trip (Property 1)
    - **Property 1: Serialization Round-Trip**
    - Create `PropertyTests.java` with `main` method as standalone test harness
    - Generate random valid GameData (random strings without tabs/newlines, random image paths)
    - Verify `parse(write(data)) == data` field-by-field for 100+ iterations
    - Print seed on failure for reproducibility
    - **Validates: Requirements 4.2, 6.2, 7.1, 7.2, 7.3**

  - [ ]* 3.2 Write property test for validation completeness (Property 2)
    - **Property 2: Validation Completeness**
    - Generate random GameData with random subset of fields set to whitespace-only
    - Verify error count equals number of whitespace-only fields (excluding image paths)
    - Run 100+ iterations
    - **Validates: Requirements 5.1, 5.2, 5.3, 5.4, 5.5**

  - [ ]* 3.3 Write property test for disallowed character rejection (Property 3)
    - **Property 3: Disallowed Character Rejection**
    - Generate random GameData with embedded `\t` or `\n` in random text fields
    - Verify validator rejects with error identifying the correct field
    - Run 100+ iterations
    - **Validates: Requirements 7.4**

  - [ ]* 3.4 Write property test for image scaling aspect ratio (Property 7)
    - **Property 7: Image Scaling Preserves Aspect Ratio**
    - Create `ImageScaler.java` utility with method `Dimension scale(int w, int h, int maxW, int maxH)`
    - Generate random (width, height, maxWidth, maxHeight) tuples
    - Verify scaledWidth ≤ maxWidth, scaledHeight ≤ maxHeight, and aspect ratio preserved within tolerance
    - Run 100+ iterations
    - **Validates: Requirements 12.3**

- [x] 4. Implement UI panels — Main Menu and Setup Screen
  - [x] 4.1 Create HposJeopardy entry point (JFrame)
    - Create `HposJeopardy.java` extending `JFrame` with `public static void main(String[] args)`
    - Set window size 800×600, centered, title "HPOS Jeopardy", EXIT_ON_CLOSE
    - Implement navigation methods: `showMainMenu()`, `showSetup()`, `showGameBoard(GameData data)`
    - Each method calls `setContentPane()` and `revalidate()`/`repaint()`
    - Call `showMainMenu()` on startup
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

  - [x] 4.2 Create MainMenuPanel
    - Create `MainMenuPanel.java` extending `JPanel`
    - Display "Play" and "Setup" buttons centered on screen
    - "Play" button → open JFileChooser, delegate to QuestionFileParser, call `showGameBoard()` on success
    - Handle parse errors with JOptionPane error dialog, return to Main Menu
    - Handle file chooser cancel by staying on Main Menu
    - "Setup" button → call `showSetup()`
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 6.1, 6.3, 6.4, 6.5_

  - [x] 4.3 Create SetupPanel with form layout
    - Create `SetupPanel.java` extending `JPanel`
    - Build scrollable form with 5 category sections, each containing:
      - Category name JTextField (max 50 chars via DocumentFilter)
      - 5 clue groups (one per point value: 100, 200, 300, 500, 1000), each with:
        - Clue text JTextField (max 300 chars)
        - Response text JTextField (max 150 chars)
        - Image path JTextField (no max)
    - Wrap form in JScrollPane
    - Add "Save" and "Back" buttons
    - Track dirty state via DocumentListeners on all text fields
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7_

  - [x] 4.4 Implement SetupPanel save and back logic
    - "Save" click: collect form data into GameData, run SetupValidator
    - If validation fails: show all errors in single JOptionPane dialog, abort save
    - If validation passes: open JFileChooser for save location, write via QuestionFileWriter
    - On successful save: show confirmation JOptionPane
    - On IOException: show error JOptionPane, preserve form data
    - On File_Chooser cancel: return to Setup_Screen with data preserved
    - "Back" click: if dirty, show JOptionPane.showConfirmDialog; navigate to Main Menu on Yes
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4, 5.5, 3.8, 3.9_

- [x] 5. Checkpoint - Verify application launches and navigates
  - Ensure application compiles and runs: Main Menu displays, Setup Screen shows form, navigation works
  - Ensure all tests pass, ask the user if questions arise.

- [x] 6. Implement Game Board and Question Popup
  - [x] 6.1 Create GameBoardPanel with tile grid
    - Create `GameBoardPanel.java` extending `JPanel`
    - Render category names across top row (truncate >20 chars with "...")
    - Render 5×5 grid of tile buttons below categories, showing point values
    - Each tile tracks state: AVAILABLE or USED
    - Use GridLayout or GridBagLayout for the tile grid
    - All 25 tiles start in AVAILABLE state
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

  - [x] 6.2 Create QuestionPopup (modal JDialog)
    - Create `QuestionPopup.java` extending `JDialog`
    - Modal, undecorated dialog sized to cover the game board area
    - Implement state machine: SHOWING_CLUE → SHOWING_RESPONSE → DISMISSED
    - On first display: show image (if any) above clue text
    - On first click: replace with response text
    - On second click: dispose dialog
    - Block keyboard and outside-click dismissal (override `processKeyEvent`, set modal)
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6_

  - [x] 6.3 Implement image loading and scaling in QuestionPopup
    - Create `ImageScaler.java` utility (if not already created in 3.4) with aspect-ratio-preserving scaling
    - Load image from file path using ImageIO.read()
    - Scale to fit popup bounds while preserving aspect ratio
    - On load failure (missing file, unreadable): display clue text only, no error dialog
    - _Requirements: 12.1, 12.2, 12.3_

  - [x] 6.4 Wire tile click to popup and implement tile state transitions
    - On available tile click: open QuestionPopup with the corresponding Clue
    - On popup dismiss: mark tile as USED (remove text, change background color)
    - On used tile click: do nothing (no popup, no state change)
    - Retain USED state for entire game session
    - _Requirements: 9.1, 10.1, 10.2, 10.3, 10.4_

  - [x] 6.5 Implement game completion
    - After 25th tile's popup is dismissed: display game-over overlay message
    - Show "Main Menu" button on game-over overlay
    - "Main Menu" button navigates back to Main_Menu
    - _Requirements: 11.1, 11.2, 11.3_

- [x] 7. Checkpoint - Full game playable
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 8. Property-based tests for game board logic
  - [ ]* 8.1 Write property test for category name truncation (Property 4)
    - **Property 4: Category Name Truncation**
    - Generate random strings of length 0–100
    - Verify: ≤20 chars → unchanged; >20 chars → first 20 chars + "..."
    - Run 100+ iterations
    - **Validates: Requirements 8.1**

  - [ ]* 8.2 Write property test for tile state persistence (Property 5)
    - **Property 5: Tile State Persistence**
    - Generate random permutations of tile selection order
    - Simulate selections, verify all previously selected tiles remain USED after each step
    - Run 100+ iterations
    - **Validates: Requirements 10.1, 10.4**

  - [ ]* 8.3 Write property test for used tile click no-op (Property 6)
    - **Property 6: Used Tile Click Is No-Op**
    - Generate random board states with some tiles USED
    - Click a random USED tile, verify no state change occurs
    - Run 100+ iterations
    - **Validates: Requirements 10.3**

- [x] 9. Final integration and wiring
  - [x] 9.1 Wire all panels into HposJeopardy frame navigation
    - Ensure `showMainMenu()` creates fresh MainMenuPanel
    - Ensure `showSetup()` creates fresh SetupPanel
    - Ensure `showGameBoard(GameData)` creates GameBoardPanel with the loaded data
    - Verify full navigation flow: Main Menu → Play → Game Board → Game Over → Main Menu
    - Verify full navigation flow: Main Menu → Setup → Save → Back → Main Menu
    - _Requirements: 1.1, 2.1, 2.2, 2.3, 2.4, 11.3_

- [x] 10. Final checkpoint - Ensure all code compiles and runs end-to-end
  - Compile entire package: `javac -d bin src/games/hpos_jeopardy/*.java`
  - Run application: `java -cp bin games.hpos_jeopardy.HposJeopardy`
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests use a standalone `PropertyTests.java` with a `main` method and `java.util.Random` — no external test framework needed
- The package is `games.hpos_jeopardy` following the project's underscore-separated naming convention
- All source files go in `src/games/hpos_jeopardy/`
- Entry point class is `HposJeopardy.java` with `public static void main`

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2", "1.3"] },
    { "id": 2, "tasks": ["1.4"] },
    { "id": 3, "tasks": ["3.1", "3.2", "3.3", "3.4", "4.1"] },
    { "id": 4, "tasks": ["4.2", "4.3"] },
    { "id": 5, "tasks": ["4.4"] },
    { "id": 6, "tasks": ["6.1", "6.2", "6.3"] },
    { "id": 7, "tasks": ["6.4"] },
    { "id": 8, "tasks": ["6.5", "8.1", "8.2", "8.3"] },
    { "id": 9, "tasks": ["9.1"] }
  ]
}
```
