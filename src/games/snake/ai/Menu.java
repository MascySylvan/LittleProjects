package games.snake.ai;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Menu extends JFrame {

	private static final long serialVersionUID = 1L;
	private int selectedGridSize = 20;

	public Menu() {
		super("AI Snake");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 350);
		setLocationRelativeTo(null);
		setResizable(false);

		showMainMenu();
		setVisible(true);
	}

	private void showMainMenu() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(30, 30, 30));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 15, 15, 15);
		gbc.gridx = 0;

		// Title
		gbc.gridy = 0;
		JLabel title = new JLabel("AI SNAKE");
		title.setFont(new Font("SansSerif", Font.BOLD, 36));
		title.setForeground(new Color(34, 139, 34));
		panel.add(title, gbc);

		// Start Game button
		gbc.gridy = 1;
		JButton startBtn = createButton("Start Game");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		panel.add(startBtn, gbc);

		// Settings button
		gbc.gridy = 2;
		JButton settingsBtn = createButton("Settings");
		settingsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSettings();
			}
		});
		panel.add(settingsBtn, gbc);

		setContentPane(panel);
		revalidate();
		repaint();
	}

	private void showSettings() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(new Color(30, 30, 30));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(12, 15, 12, 15);
		gbc.gridx = 0;

		// Title
		gbc.gridy = 0;
		JLabel title = new JLabel("Canvas Size");
		title.setFont(new Font("SansSerif", Font.BOLD, 24));
		title.setForeground(Color.WHITE);
		panel.add(title, gbc);

		// Radio buttons for grid size
		ButtonGroup group = new ButtonGroup();
		JRadioButton opt20 = createRadio("20 x 20", 20);
		JRadioButton opt50 = createRadio("50 x 50", 50);
		JRadioButton opt80 = createRadio("80 x 80", 80);

		// Select current
		if (selectedGridSize == 20) opt20.setSelected(true);
		else if (selectedGridSize == 50) opt50.setSelected(true);
		else opt80.setSelected(true);

		group.add(opt20);
		group.add(opt50);
		group.add(opt80);

		gbc.gridy = 1;
		panel.add(opt20, gbc);
		gbc.gridy = 2;
		panel.add(opt50, gbc);
		gbc.gridy = 3;
		panel.add(opt80, gbc);

		// Back button
		gbc.gridy = 4;
		JButton backBtn = createButton("Back");
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save selection
				if (opt20.isSelected()) selectedGridSize = 20;
				else if (opt50.isSelected()) selectedGridSize = 50;
				else selectedGridSize = 80;
				showMainMenu();
			}
		});
		panel.add(backBtn, gbc);

		setContentPane(panel);
		revalidate();
		repaint();
	}

	private void startGame() {
		dispose();
		new Canvas(selectedGridSize);
	}

	private JButton createButton(String text) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("SansSerif", Font.BOLD, 18));
		btn.setPreferredSize(new Dimension(200, 45));
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setBackground(new Color(50, 50, 50));
		btn.setForeground(Color.WHITE);
		btn.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
		return btn;
	}

	private JRadioButton createRadio(String text, int size) {
		JRadioButton rb = new JRadioButton(text);
		rb.setFont(new Font("SansSerif", Font.PLAIN, 16));
		rb.setForeground(Color.WHITE);
		rb.setBackground(new Color(30, 30, 30));
		rb.setFocusPainted(false);
		rb.setActionCommand(String.valueOf(size));
		return rb;
	}

	public static void main(String[] args) {
		new Menu();
	}
}
