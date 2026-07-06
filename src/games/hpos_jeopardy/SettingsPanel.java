package games.hpos_jeopardy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Settings panel for controlling audio options (BGM and SFX).
 */
public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color DARK_RED = new Color(139, 0, 0);
	private static final Color BRIGHT_RED = new Color(200, 30, 30);
	private static final Color CREAM = new Color(255, 245, 238);

	private final HposJeopardy parentFrame;
	private final float scale;

	public SettingsPanel(HposJeopardy parentFrame) {
		this.parentFrame = parentFrame;
		this.scale = parentFrame.getScaleFactor();

		setLayout(new BorderLayout());
		setBackground(DARK_RED);

		// Header
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
		header.setPreferredSize(new Dimension(800, (int)(60 * scale)));
		header.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("  Settings");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(26 * scale)));
		titleLabel.setForeground(Color.WHITE);
		header.add(titleLabel, BorderLayout.WEST);
		add(header, BorderLayout.NORTH);

		// Content
		JPanel content = new JPanel(new GridBagLayout()) {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				GradientPaint gp = new GradientPaint(0, 0, new Color(60, 0, 0), 0, getHeight(), new Color(30, 0, 0));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		content.setBorder(new EmptyBorder((int)(30 * scale), (int)(50 * scale), (int)(30 * scale), (int)(50 * scale)));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets((int)(12 * scale), (int)(10 * scale), (int)(12 * scale), (int)(10 * scale));
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		AudioManager audio = AudioManager.getInstance();

		// Audio Section Label
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel audioLabel = new JLabel("Audio");
		audioLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(18 * scale)));
		audioLabel.setForeground(new Color(218, 165, 32));
		content.add(audioLabel, gbc);

		// BGM Enable checkbox
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		final JCheckBox bgmCheckbox = new JCheckBox("Background Music (Jeopardy Theme)");
		bgmCheckbox.setSelected(audio.isBGMEnabled());
		bgmCheckbox.setFont(new Font("SansSerif", Font.PLAIN, (int)(14 * scale)));
		bgmCheckbox.setForeground(CREAM);
		bgmCheckbox.setOpaque(false);
		bgmCheckbox.setFocusPainted(false);
		bgmCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AudioManager.getInstance().setBGMEnabled(bgmCheckbox.isSelected());
			}
		});
		content.add(bgmCheckbox, gbc);

		// Volume Slider
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		JLabel volLabel = new JLabel("Volume:");
		volLabel.setFont(new Font("SansSerif", Font.PLAIN, (int)(14 * scale)));
		volLabel.setForeground(CREAM);
		content.add(volLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		final JSlider volumeSlider = new JSlider(0, 100, (int)(audio.getBGMVolume() * 100));
		volumeSlider.setOpaque(false);
		volumeSlider.setPreferredSize(new Dimension((int)(300 * scale), (int)(30 * scale)));
		volumeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float vol = volumeSlider.getValue() / 100.0f;
				AudioManager.getInstance().setBGMVolume(vol);
			}
		});
		content.add(volumeSlider, gbc);

		// SFX Enable checkbox
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.weightx = 0;
		final JCheckBox sfxCheckbox = new JCheckBox("Sound Effects (Pop)");
		sfxCheckbox.setSelected(audio.isSFXEnabled());
		sfxCheckbox.setFont(new Font("SansSerif", Font.PLAIN, (int)(14 * scale)));
		sfxCheckbox.setForeground(CREAM);
		sfxCheckbox.setOpaque(false);
		sfxCheckbox.setFocusPainted(false);
		sfxCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AudioManager.getInstance().setSFXEnabled(sfxCheckbox.isSelected());
			}
		});
		content.add(sfxCheckbox, gbc);

		// Filler
		gbc.gridy = 4;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		content.add(new JLabel(), gbc);

		add(content, BorderLayout.CENTER);

		// Back button panel
		JPanel bottomPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(40, 0, 0));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		bottomPanel.setPreferredSize(new Dimension(800, (int)(60 * scale)));
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setBorder(new EmptyBorder(10, (int)(50 * scale), 10, 0));

		JButton backButton = createBackButton();
		bottomPanel.add(backButton, BorderLayout.WEST);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private JButton createBackButton() {
		final int btnW = (int)(120 * scale);
		final int btnH = (int)(38 * scale);
		JButton button = new JButton("Back") {
			private static final long serialVersionUID = 1L;
			private boolean hovered = false;
			{
				setContentAreaFilled(false);
				setFocusPainted(false);
				setBorderPainted(false);
				setFont(new Font("SansSerif", Font.BOLD, (int)(16 * scale)));
				setForeground(DARK_RED);
				setPreferredSize(new Dimension(btnW, btnH));
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
				g2d.setColor(hovered ? new Color(218, 165, 32) : CREAM);
				g2d.fill(rect);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentFrame.showMainMenu();
			}
		});
		return button;
	}
}
