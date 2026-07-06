package games.hpos_jeopardy;

import java.awt.Dimension;

/**
 * Utility class for scaling image dimensions while preserving aspect ratio.
 * Scales images down to fit within given maximum bounds but does not scale up.
 */
public class ImageScaler {

	/**
	 * Computes the scaled dimensions that fit within (maxW, maxH) while preserving
	 * the original aspect ratio of (w, h). Only scales down — if the image already
	 * fits within the bounds, the original dimensions are returned.
	 *
	 * @param w    the original width (must be positive)
	 * @param h    the original height (must be positive)
	 * @param maxW the maximum allowed width (must be positive)
	 * @param maxH the maximum allowed height (must be positive)
	 * @return the scaled dimensions preserving aspect ratio
	 * @throws IllegalArgumentException if any parameter is not positive
	 */
	public static Dimension scale(int w, int h, int maxW, int maxH) {
		if (w <= 0 || h <= 0 || maxW <= 0 || maxH <= 0) {
			throw new IllegalArgumentException(
				"All dimensions must be positive: w=" + w + ", h=" + h + ", maxW=" + maxW + ", maxH=" + maxH);
		}

		// If the image already fits, return original dimensions
		if (w <= maxW && h <= maxH) {
			return new Dimension(w, h);
		}

		// Calculate scale factors for both dimensions
		double scaleX = (double) maxW / w;
		double scaleY = (double) maxH / h;

		// Use the smaller scale factor to ensure both constraints are satisfied
		double scale = Math.min(scaleX, scaleY);

		int scaledWidth = (int) Math.round(w * scale);
		int scaledHeight = (int) Math.round(h * scale);

		// Clamp to max bounds to handle rounding edge cases
		scaledWidth = Math.min(scaledWidth, maxW);
		scaledHeight = Math.min(scaledHeight, maxH);

		return new Dimension(scaledWidth, scaledHeight);
	}
}
