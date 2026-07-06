# Requirements Document

## Introduction

HPOS Jeopardy is a standalone Java Swing desktop application that replicates the popular TV quiz show Jeopardy. The application allows users to create custom question sets through a setup interface, save them to local text files, and then play the game using any previously created question file. The game board displays 5 categories with 5 point-value tiers (100, 200, 300, 500, 1000). Players select tiles to reveal clues (presented as "answers" in Jeopardy style) and then reveal the correct response (phrased as a question).

## Glossary

- **Application**: The HPOS Jeopardy Java Swing desktop program running as a self-contained sub-project within LittleProjects.
- **Main_Menu**: The starting screen of the Application displaying navigation options.
- **Setup_Screen**: The screen where users create and edit question data for a game.
- **Game_Board**: The screen displaying the 5x5 grid of categories and point values during gameplay.
- **Question_Popup**: A modal dialog that appears when a tile is selected, showing the clue content.
- **Clue**: The "answer" text and optional image presented to the player on a tile selection (in Jeopardy, clues are phrased as statements).
- **Response**: The correct "question" that the player must provide in response to a Clue.
- **Category**: A themed column on the Game_Board containing 5 Clues of increasing point value.
- **Tile**: A clickable cell on the Game_Board identified by its Category and point value.
- **Question_File**: A local text file storing all Categories, Clues, Responses, and optional image paths for one complete game.
- **File_Chooser**: A standard Swing file selection dialog (JFileChooser) for opening or saving Question_Files.

## Requirements

### Requirement 1: Application Entry Point

**User Story:** As a user, I want to launch the Jeopardy application, so that I can access the main menu.

#### Acceptance Criteria

1. WHEN the user launches the Application, THE Application SHALL display the Main_Menu.
2. THE Application SHALL use a JFrame as the primary window with a default size of 800 by 600 pixels.
3. THE Application SHALL set the window title to "HPOS Jeopardy".
4. THE Application SHALL center the window on the screen.
5. WHEN the user closes the window, THE Application SHALL terminate the process.

### Requirement 2: Main Menu Navigation

**User Story:** As a user, I want to see Play and Setup buttons on the main menu, so that I can choose my activity.

#### Acceptance Criteria

1. THE Main_Menu SHALL display a "Play" button and a "Setup" button simultaneously.
2. WHEN the user clicks the "Play" button, THE Application SHALL display the File_Chooser to select a Question_File for opening.
3. WHEN the user clicks the "Setup" button, THE Application SHALL navigate to the Setup_Screen.
4. IF the user cancels the File_Chooser displayed after clicking "Play", THEN THE Application SHALL return to the Main_Menu with both buttons available.

### Requirement 3: Setup Screen — Category and Clue Entry

**User Story:** As a user, I want to enter categories, clues, responses, and optional images, so that I can create a custom Jeopardy game.

#### Acceptance Criteria

1. THE Setup_Screen SHALL provide 5 labeled text input fields for Category names, each accepting a maximum of 50 characters.
2. THE Setup_Screen SHALL provide labeled text input fields for each of the 25 Clues (5 per Category) corresponding to point values 100, 200, 300, 500, and 1000, each accepting a maximum of 300 characters.
3. THE Setup_Screen SHALL provide a labeled text input field for the Response associated with each Clue, each accepting a maximum of 150 characters.
4. THE Setup_Screen SHALL provide an optional file path input for an image associated with each Clue.
5. THE Setup_Screen SHALL organize input fields by Category, with each Category section displaying its name field followed by its 5 Clue/Response/Image groups labeled by point value.
6. THE Setup_Screen SHALL provide scrollable access to all input fields when the content exceeds the visible window area.
7. THE Setup_Screen SHALL provide a "Save" button to persist the entered data.
8. WHEN the user clicks the "Back" button with unsaved modifications, THE Application SHALL display a confirmation dialog warning that unsaved changes will be lost before returning to the Main_Menu.
9. WHEN the user clicks the "Back" button with no unsaved modifications, THE Application SHALL return to the Main_Menu.

### Requirement 4: Setup Screen — Save to File

**User Story:** As a user, I want to save my question set to a local text file, so that I can reuse it for gameplay.

#### Acceptance Criteria

1. WHEN the user clicks the "Save" button, THE Application SHALL display the File_Chooser for the user to choose a save location and file name.
2. WHEN the user confirms a file selection in the File_Chooser, THE Application SHALL write all Category names, Clue texts, Response texts, and image paths to the selected Question_File in the format defined by the Question_File serialization rules.
3. WHEN the save operation completes successfully, THE Application SHALL display a confirmation message to the user.
4. IF the file cannot be written due to an I/O error, THEN THE Application SHALL display an error message indicating the file could not be saved and return the user to the Setup_Screen with all entered data preserved.
5. WHEN the user cancels the File_Chooser, THE Application SHALL return to the Setup_Screen with all entered data preserved.

### Requirement 5: Setup Screen — Input Validation

**User Story:** As a user, I want to be informed if my question set is incomplete, so that I can correct it before saving.

#### Acceptance Criteria

1. WHEN the user clicks "Save" with any Category name field empty or containing only whitespace, THE Application SHALL prevent the save operation and display a validation error message identifying the empty Category by its position number (1 through 5).
2. WHEN the user clicks "Save" with any Clue text field empty or containing only whitespace, THE Application SHALL prevent the save operation and display a validation error message identifying the empty Clue by its Category position number and point value.
3. WHEN the user clicks "Save" with any Response text field empty or containing only whitespace, THE Application SHALL prevent the save operation and display a validation error message identifying the empty Response by its Category position number and point value.
4. THE Application SHALL allow image path fields to remain empty without triggering a validation error.
5. IF multiple fields fail validation, THEN THE Application SHALL report all validation errors in a single message rather than stopping at the first error found.

### Requirement 6: Play — File Selection

**User Story:** As a user, I want to select a previously saved question file, so that I can play a game with those questions.

#### Acceptance Criteria

1. WHEN the user clicks "Play" on the Main_Menu, THE Application SHALL display the File_Chooser for selecting a Question_File.
2. WHEN the user selects a Question_File that conforms to the format defined in Requirement 7 (containing exactly 5 Category names, 25 Clues with point values, Clue text, Response text, and image paths), THE Application SHALL parse the file and navigate to the Game_Board populated with the parsed data.
3. WHEN the user cancels the File_Chooser, THE Application SHALL return to the Main_Menu.
4. IF the selected file cannot be parsed because it does not conform to the expected format (e.g., missing categories, missing clue fields, or malformed delimiters), THEN THE Application SHALL display an error message indicating that the file format is invalid and return to the Main_Menu.
5. IF the selected file is empty (zero bytes), THEN THE Application SHALL display an error message indicating that the file format is invalid and return to the Main_Menu.

### Requirement 7: Question File Format — Serialization

**User Story:** As a developer, I want a well-defined text file format, so that question data can be reliably saved and loaded.

#### Acceptance Criteria

1. THE Application SHALL serialize game data using a line-based plain text format encoded in UTF-8, using a single tab character as the field delimiter within a line and a newline as the record separator.
2. THE Application SHALL write the file as 5 consecutive Category blocks, where each block consists of: one line containing the Category name, followed by 5 Clue lines (one per point value in ascending order: 100, 200, 300, 500, 1000), each Clue line containing the point value, Clue text, Response text, and image path (empty string if none) separated by the tab delimiter.
3. FOR ALL valid game data, saving a Question_File and then loading that Question_File SHALL produce a data set where every Category name, Clue text, Response text, point value, and image path matches the original field-by-field in the same order.
4. IF any Clue text, Response text, or Category name contains a tab character or newline character, THEN THE Application SHALL reject the input with a validation error before saving, indicating which field contains the disallowed character.

### Requirement 8: Game Board Display

**User Story:** As a user, I want to see the Jeopardy board with categories and point values, so that I can select clues to play.

#### Acceptance Criteria

1. THE Game_Board SHALL display 5 Category names across the top row, truncating any name that exceeds 20 characters with an ellipsis.
2. THE Game_Board SHALL display 5 rows of Tiles below the Category names with point values 100, 200, 300, 500, and 1000, ordered from lowest value at the top to highest value at the bottom.
3. THE Game_Board SHALL render each Tile as a clickable button showing its point value.
4. THE Game_Board SHALL arrange Tiles in a grid with Categories as columns and point values as rows, producing a 5-column by 5-row layout.
5. WHEN the Game_Board is first displayed, THE Game_Board SHALL show all 25 Tiles in the available state.

### Requirement 9: Tile Selection and Question Popup

**User Story:** As a user, I want to click a tile to see the clue, so that I can attempt to answer it.

#### Acceptance Criteria

1. WHEN the user clicks an available Tile, THE Application SHALL display the Question_Popup as a modal overlay covering the Game_Board, containing the Clue text.
2. WHEN the user clicks an available Tile, IF the Clue has an associated image, THEN THE Question_Popup SHALL display the image above the Clue text.
3. WHEN the user clicks an available Tile, IF the Clue has no associated image, THEN THE Question_Popup SHALL display only the Clue text.
4. WHILE the Question_Popup is displaying the Clue, WHEN the user clicks the Question_Popup, THE Question_Popup SHALL replace the Clue text and any displayed image with the Response text.
5. WHILE the Question_Popup is displaying the Response, WHEN the user clicks the Question_Popup, THE Application SHALL dismiss the Question_Popup and return to the Game_Board.
6. THE Question_Popup SHALL only be dismissable by completing the full click-through sequence (Clue → Response → dismiss) and SHALL not close in response to keyboard input or clicking outside the popup.

### Requirement 10: Tile State After Selection

**User Story:** As a user, I want tiles I have already selected to appear different, so that I know which clues have been revealed.

#### Acceptance Criteria

1. WHEN a Tile has been selected and its Question_Popup dismissed, THE Game_Board SHALL mark that Tile as used.
2. THE Game_Board SHALL render used Tiles with their point-value text removed and their background color changed from the available Tile color, so that used Tiles are distinguishable from available Tiles without relying on text content alone.
3. WHEN the user clicks a used Tile, THE Application SHALL not display a Question_Popup and SHALL not change any game state.
4. WHILE a game session is active, THE Game_Board SHALL retain the used state of all Tiles until the user navigates away from the Game_Board or closes the Application.

### Requirement 11: Game Completion

**User Story:** As a user, I want to know when all clues have been revealed, so that the game feels complete.

#### Acceptance Criteria

1. WHEN the 25th Tile's Question_Popup is dismissed, THE Application SHALL display a game-over message on the Game_Board indicating that all clues have been revealed.
2. THE game-over message SHALL include a "Main Menu" button to return to the Main_Menu.
3. WHEN the user clicks the "Main Menu" button on the game-over message, THE Application SHALL navigate to the Main_Menu.

### Requirement 12: Image Loading

**User Story:** As a user, I want images to display correctly in clue popups, so that visual clues work as intended.

#### Acceptance Criteria

1. WHEN a Clue has an image path specified, THE Application SHALL attempt to load the image from the local file system.
2. IF the image file cannot be loaded due to a missing or unreadable file, THEN THE Application SHALL display the Clue text without an image and continue normal operation.
3. THE Application SHALL scale loaded images to fit within the Question_Popup while preserving the original aspect ratio, without exceeding the popup dimensions.
