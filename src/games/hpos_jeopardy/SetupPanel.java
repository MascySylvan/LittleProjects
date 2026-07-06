package games.hpos_jeopardy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * Setup screen with red and white theme for creating Jeopardy game data.
 */
public class SetupPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int NUM_CATEGORIES = 5;
	private static final int NUM_CLUES_PER_CATEGORY = 5;
	private static final int[] POINT_VALUES = {100, 200, 300, 500, 1000};

	private static final int MAX_CATEGORY_NAME_CHARS = 50;
	private static final int MAX_CLUE_TEXT_CHARS = 300;
	private static final int MAX_RESPONSE_TEXT_CHARS = 150;

	// Theme colors
	private static final Color DARK_RED = new Color(139, 0, 0);
	private static final Color BRIGHT_RED = new Color(200, 30, 30);
	private static final Color CREAM = new Color(255, 245, 238);
	private static final Color CARD_BG = new Color(255, 252, 250);
	private static final Color SECTION_HEADER_BG = new Color(180, 20, 20);
	private static final Color FIELD_BORDER = new Color(200, 180, 180);
	private static final Color GOLD = new Color(218, 165, 32);

	private HposJeopardy parentFrame;

	private JTextField[] categoryNameFields;
	private JTextField[][] clueTextFields;
	private JTextField[][] responseTextFields;
	private JTextField[][] imagePathFields;
	private boolean isDirty;
	private JButton saveButton;
	private JButton backButton;

	public SetupPanel(HposJeopardy parentFrame) {
		this.parentFrame = parentFrame;
		this.isDirty = false;

		categoryNameFields = new JTextField[NUM_CATEGORIES];
		clueTextFields = new JTextField[NUM_CATEGORIES][NUM_CLUES_PER_CATEGORY];
		responseTextFields = new JTextField[NUM_CATEGORIES][NUM_CLUES_PER_CATEGORY];
		imagePathFields = new JTextField[NUM_CATEGORIES][NUM_CLUES_PER_CATEGORY];

		setLayout(new BorderLayout());
		setBackground(CREAM);

		// Header
		JPanel headerPanel = createHeaderPanel();
		add(headerPanel, BorderLayout.NORTH);

		// Form content in scroll pane
		JPanel formPanel = buildFormPanel();
		JScrollPane scrollPane = new JScrollPane(formPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(CREAM);
		add(scrollPane, BorderLayout.CENTER);

		// Button panel
		JPanel buttonPanel = buildButtonPanel();
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private JPanel createHeaderPanel() {
		JPanel header = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				GradientPaint gp = new GradientPaint(0, 0, DARK_RED, getWidth(), 0, BRIGHT_RED);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		header.setPreferredSize(new Dimension(800, 50));
		header.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("  Game Setup");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
		titleLabel.setForeground(Color.WHITE);
		header.add(titleLabel, BorderLayout.WEST);
		return header;
	}

	private JPanel buildFormPanel() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(CREAM);
		formPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(3, 8, 3, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;

		for (int cat = 0; cat < NUM_CATEGORIES; cat++) {
			// Category card header
			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.gridwidth = 2;
			gbc.weightx = 1.0;
			JPanel catHeader = createCategoryHeader(cat + 1);
			formPanel.add(catHeader, gbc);
			row++;

			// Category name
			gbc.gridwidth = 1;
			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.weightx = 0;
			JLabel nameLabel = new JLabel("Category Name:");
			nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			nameLabel.setForeground(DARK_RED);
			formPanel.add(nameLabel, gbc);

			gbc.gridx = 1;
			gbc.weightx = 1.0;
			categoryNameFields[cat] = createStyledTextField(MAX_CATEGORY_NAME_CHARS, 30);
			formPanel.add(categoryNameFields[cat], gbc);
			row++;

			for (int clueIdx = 0; clueIdx < NUM_CLUES_PER_CATEGORY; clueIdx++) {
				int pointValue = POINT_VALUES[clueIdx];

				// Point value label
				gbc.gridx = 0;
				gbc.gridy = row;
				gbc.gridwidth = 2;
				gbc.weightx = 0;
				JLabel pointLabel = new JLabel("    \u25B6 " + pointValue + " Points");
				pointLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
				pointLabel.setForeground(BRIGHT_RED);
				pointLabel.setBorder(new EmptyBorder(8, 0, 2, 0));
				formPanel.add(pointLabel, gbc);
				row++;

				gbc.gridwidth = 1;

				// Clue
				gbc.gridx = 0;
				gbc.gridy = row;
				gbc.weightx = 0;
				formPanel.add(createFieldLabel("Clue:"), gbc);
				gbc.gridx = 1;
				gbc.weightx = 1.0;
				clueTextFields[cat][clueIdx] = createStyledTextField(MAX_CLUE_TEXT_CHARS, 40);
				formPanel.add(clueTextFields[cat][clueIdx], gbc);
				row++;

				// Response
				gbc.gridx = 0;
				gbc.gridy = row;
				gbc.weightx = 0;
				formPanel.add(createFieldLabel("Response:"), gbc);
				gbc.gridx = 1;
				gbc.weightx = 1.0;
				responseTextFields[cat][clueIdx] = createStyledTextField(MAX_RESPONSE_TEXT_CHARS, 40);
				formPanel.add(responseTextFields[cat][clueIdx], gbc);
				row++;

				// Image path
				gbc.gridx = 0;
				gbc.gridy = row;
				gbc.weightx = 0;
				formPanel.add(createFieldLabel("Image:"), gbc);
				gbc.gridx = 1;
				gbc.weightx = 1.0;
				imagePathFields[cat][clueIdx] = createStyledTextField(-1, 40);
				formPanel.add(imagePathFields[cat][clueIdx], gbc);
				row++;
			}

			// Spacer between categories
			gbc.gridx = 0;
			gbc.gridy = row;
			gbc.gridwidth = 2;
			gbc.weightx = 1.0;
			JPanel spacer = new JPanel();
			spacer.setOpaque(false);
			spacer.setPreferredSize(new Dimension(1, 15));
			formPanel.add(spacer, gbc);
			row++;
		}

		// Bottom filler
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 2;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		formPanel.add(new JLabel(), gbc);

		return formPanel;
	}

	private JPanel createCategoryHeader(int num) {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(SECTION_HEADER_BG);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2d.dispose();
			}
		};
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(600, 32));
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("  Category " + num);
		label.setFont(new Font("SansSerif", Font.BOLD, 14));
		label.setForeground(Color.WHITE);
		panel.add(label, BorderLayout.WEST);
		panel.setBorder(new EmptyBorder(4, 0, 4, 0));
		return panel;
	}

	private JLabel createFieldLabel(String text) {
		JLabel label = new JLabel("    " + text);
		label.setFont(new Font("SansSerif", Font.PLAIN, 12));
		label.setForeground(new Color(80, 40, 40));
		return label;
	}

	private JTextField createStyledTextField(int maxChars, int columns) {
		JTextField field = new JTextField(columns);
		field.setFont(new Font("SansSerif", Font.PLAIN, 12));
		field.setBackground(CARD_BG);
		field.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(FIELD_BORDER, 1),
				BorderFactory.createEmptyBorder(4, 6, 4, 6)));

		PlainDocument doc = (PlainDocument) field.getDocument();
		if (maxChars > 0) {
			doc.setDocumentFilter(new MaxLengthDocumentFilter(maxChars));
		}
		doc.addDocumentListener(new DirtyDocumentListener());
		return field;
	}

	private JPanel buildButtonPanel() {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(new Color(245, 235, 235));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				// Top border line
				g2d.setColor(FIELD_BORDER);
				g2d.drawLine(0, 0, getWidth(), 0);
				g2d.dispose();
			}
		};
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		panel.setPreferredSize(new Dimension(800, 50));

		backButton = createActionButton("Back", false);
		saveButton = createActionButton("Save", true);

		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { handleBack(); }
		});
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { handleSave(); }
		});

		panel.add(backButton);
		panel.add(saveButton);
		return panel;
	}

	private JButton createActionButton(String text, boolean primary) {
		JButton button = new JButton(text) {
			private static final long serialVersionUID = 1L;
			private boolean hovered = false;
			{
				setContentAreaFilled(false);
				setFocusPainted(false);
				setBorderPainted(false);
				setFont(new Font("SansSerif", Font.BOLD, 14));
				setPreferredSize(new Dimension(100, 35));
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				setForeground(primary ? Color.WHITE : DARK_RED);
				addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
					public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
				});
			}
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
				if (primary) {
					g2d.setColor(hovered ? BRIGHT_RED : DARK_RED);
				} else {
					g2d.setColor(hovered ? new Color(255, 230, 230) : CREAM);
				}
				g2d.fill(rect);
				g2d.setColor(primary ? new Color(100, 0, 0) : FIELD_BORDER);
				g2d.draw(rect);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		return button;
	}

	// -------------------------------------------------------------------------
	// Action Handlers
	// -------------------------------------------------------------------------

	private void handleSave() {
		GameData data = collectFormData();
		SetupValidator validator = new SetupValidator();
		List<String> errors = validator.validate(data);

		if (!errors.isEmpty()) {
			StringBuilder message = new StringBuilder("Validation errors:\n");
			for (String error : errors) {
				message.append("\u2022 ").append(error).append("\n");
			}
			JOptionPane.showMessageDialog(parentFrame, message.toString(),
					"Validation Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showSaveDialog(parentFrame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			QuestionFileWriter writer = new QuestionFileWriter();
			try {
				writer.write(data, selectedFile);
				clearDirty();
				JOptionPane.showMessageDialog(parentFrame,
						"Game data saved successfully.",
						"Save Successful", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(parentFrame,
						"Could not save file: " + ex.getMessage(),
						"Save Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void handleBack() {
		if (isDirty) {
			int choice = JOptionPane.showConfirmDialog(parentFrame,
					"You have unsaved changes. Are you sure you want to leave?",
					"Unsaved Changes", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (choice != JOptionPane.YES_OPTION) {
				return;
			}
		}
		parentFrame.showMainMenu();
	}

	private GameData collectFormData() {
		List<Category> categories = new ArrayList<Category>();
		for (int cat = 0; cat < NUM_CATEGORIES; cat++) {
			String categoryName = categoryNameFields[cat].getText();
			List<Clue> clues = new ArrayList<Clue>();
			for (int clueIdx = 0; clueIdx < NUM_CLUES_PER_CATEGORY; clueIdx++) {
				int pointValue = POINT_VALUES[clueIdx];
				String clueText = clueTextFields[cat][clueIdx].getText();
				String responseText = responseTextFields[cat][clueIdx].getText();
				String imagePath = imagePathFields[cat][clueIdx].getText();
				clues.add(new Clue(pointValue, clueText, responseText, imagePath));
			}
			categories.add(new Category(categoryName, clues));
		}
		return new GameData(categories);
	}

	// -------------------------------------------------------------------------
	// Public accessors
	// -------------------------------------------------------------------------

	public boolean isDirty() { return isDirty; }
	public void clearDirty() { isDirty = false; }
	public JTextField[] getCategoryNameFields() { return categoryNameFields; }
	public JTextField[][] getClueTextFields() { return clueTextFields; }
	public JTextField[][] getResponseTextFields() { return responseTextFields; }
	public JTextField[][] getImagePathFields() { return imagePathFields; }
	public JButton getSaveButton() { return saveButton; }
	public JButton getBackButton() { return backButton; }
	public HposJeopardy getParentFrame() { return parentFrame; }

	// -------------------------------------------------------------------------
	// Inner Classes
	// -------------------------------------------------------------------------

	private static class MaxLengthDocumentFilter extends DocumentFilter {
		private final int maxLength;
		public MaxLengthDocumentFilter(int maxLength) { this.maxLength = maxLength; }

		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {
			if (string == null) return;
			int allowable = maxLength - fb.getDocument().getLength();
			if (allowable <= 0) return;
			if (string.length() > allowable) string = string.substring(0, allowable);
			super.insertString(fb, offset, string, attr);
		}

		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			if (text == null) { super.replace(fb, offset, length, text, attrs); return; }
			int allowable = maxLength - (fb.getDocument().getLength() - length);
			if (allowable <= 0) return;
			if (text.length() > allowable) text = text.substring(0, allowable);
			super.replace(fb, offset, length, text, attrs);
		}
	}

	private class DirtyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) { isDirty = true; }
		public void removeUpdate(DocumentEvent e) { isDirty = true; }
		public void changedUpdate(DocumentEvent e) { isDirty = true; }
	}
}
