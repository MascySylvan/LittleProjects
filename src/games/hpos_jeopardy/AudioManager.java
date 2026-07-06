package games.hpos_jeopardy;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Manages background music and sound effects using javax.sound.sampled (WAV files).
 */
public class AudioManager {

	private static AudioManager instance;

	private Clip bgmClip;
	private boolean bgmEnabled = true;
	private boolean sfxEnabled = true;
	private float bgmVolume = 0.7f; // 0.0 to 1.0

	private final String bgmPath;
	private final String sfxPopPath;

	private AudioManager() {
		String basePath = getResourceBasePath();
		bgmPath = basePath + "Jeopardy Theme.wav";
		sfxPopPath = basePath + "pop.wav";
	}

	public static synchronized AudioManager getInstance() {
		if (instance == null) {
			instance = new AudioManager();
		}
		return instance;
	}

	private String getResourceBasePath() {
		// Try relative to working directory
		File resourceDir = new File("src/games/hpos_jeopardy/resources/");
		if (resourceDir.exists()) {
			return resourceDir.getAbsolutePath().replace("\\", "/") + "/";
		}
		// Try relative to class location (running from bin/)
		try {
			String classPath = AudioManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			File binDir = new File(classPath);
			File projectRoot = binDir.getParentFile();
			File altDir = new File(projectRoot, "src/games/hpos_jeopardy/resources/");
			if (altDir.exists()) {
				return altDir.getAbsolutePath().replace("\\", "/") + "/";
			}
		} catch (Exception e) {
			// ignore
		}
		return "src/games/hpos_jeopardy/resources/";
	}

	/**
	 * Starts playing background music on loop.
	 */
	public void startBGM() {
		if (!bgmEnabled) return;
		try {
			if (bgmClip != null && bgmClip.isOpen()) {
				bgmClip.stop();
				bgmClip.close();
			}
			File bgmFile = new File(bgmPath);
			if (!bgmFile.exists()) return;
			AudioInputStream ais = AudioSystem.getAudioInputStream(bgmFile);
			bgmClip = AudioSystem.getClip();
			bgmClip.open(ais);
			applyVolume(bgmClip, bgmVolume);
			bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			// Silently ignore audio errors
		}
	}

	/**
	 * Stops background music.
	 */
	public void stopBGM() {
		if (bgmClip != null && bgmClip.isRunning()) {
			bgmClip.stop();
		}
	}

	/**
	 * Plays the pop sound effect once.
	 */
	public void playSFX() {
		if (!sfxEnabled) return;
		try {
			File sfxFile = new File(sfxPopPath);
			if (!sfxFile.exists()) return;
			AudioInputStream ais = AudioSystem.getAudioInputStream(sfxFile);
			final Clip clip = AudioSystem.getClip();
			clip.open(ais);
			applyVolume(clip, bgmVolume);
			clip.addLineListener(new LineListener() {
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP) {
						clip.close();
					}
				}
			});
			clip.start();
		} catch (Exception e) {
			// Silently ignore audio errors
		}
	}

	/**
	 * Sets BGM volume (0.0 to 1.0).
	 */
	public void setBGMVolume(float volume) {
		this.bgmVolume = Math.max(0.0f, Math.min(1.0f, volume));
		if (bgmClip != null && bgmClip.isOpen()) {
			applyVolume(bgmClip, this.bgmVolume);
		}
	}

	public float getBGMVolume() {
		return bgmVolume;
	}

	public void setBGMEnabled(boolean enabled) {
		this.bgmEnabled = enabled;
		if (enabled) {
			startBGM();
		} else {
			stopBGM();
		}
	}

	public boolean isBGMEnabled() {
		return bgmEnabled;
	}

	public void setSFXEnabled(boolean enabled) {
		this.sfxEnabled = enabled;
	}

	public boolean isSFXEnabled() {
		return sfxEnabled;
	}

	/**
	 * Applies a volume level (0.0–1.0) to a Clip via its MASTER_GAIN control.
	 */
	private void applyVolume(Clip clip, float volume) {
		try {
			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				// Convert linear volume (0.0–1.0) to decibels
				float dB;
				if (volume <= 0.0f) {
					dB = gainControl.getMinimum();
				} else {
					dB = 20.0f * (float) Math.log10(volume);
					dB = Math.max(dB, gainControl.getMinimum());
					dB = Math.min(dB, gainControl.getMaximum());
				}
				gainControl.setValue(dB);
			}
		} catch (Exception e) {
			// ignore
		}
	}
}
