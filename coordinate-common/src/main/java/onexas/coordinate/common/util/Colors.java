package onexas.coordinate.common.util;

import java.awt.Color;

/**
 * 
 * @author Dennis Chen
 *
 */
public class Colors {

	public static String getSmartHoverColor(String color, int offset) {
		try {
			return getSmartHoverColor0(color, offset);
		} catch (Exception x) {
			return color;
		}
	}

	private static String getSmartHoverColor0(String color, int offset) throws Exception {
		if (color == null) {
			return null;
		}
		if (color.startsWith("#")) {
			color = color.substring(1);
		}
		int r, g, b;
		if (color.length() == 3) {
			r = Integer.parseInt(color.substring(0, 1), 16) * 16;
			g = Integer.parseInt(color.substring(1, 2), 16) * 16;
			b = Integer.parseInt(color.substring(2, 3), 16) * 16;
		} else if (color.length() == 6) {
			r = Integer.parseInt(color.substring(0, 2), 16);
			g = Integer.parseInt(color.substring(2, 4), 16);
			b = Integer.parseInt(color.substring(4, 6), 16);
		} else {
			return color;
		}

		boolean light = isLightColor(r, g, b);

		if (light) {// darker
			r = Math.max(0, r - offset);
			g = Math.max(0, g - offset);
			b = Math.max(0, b - offset);
		} else {// lighter
			r = Math.min(255, r + offset);
			g = Math.min(255, g + offset);
			b = Math.min(255, b + offset);
		}

		return getRBG(r, g, b);
	}

	public static String getRBG(int[] color) {
		return getRBG(color.length > 0 ? color[0] : 0, color.length > 1 ? color[1] : 0,
				color.length > 2 ? color[2] : 0);
	}

	public static String getRBG(int r, int g, int b) {
		StringBuilder s = new StringBuilder("#");
		int[] color = new int[] { r, g, b };
		for (int i = 0; i < color.length; i++) {
			int c = color[i];
			if (c > 255) {
				c = 255;
			} else if (c < 0) {
				c = 0;
			}
			if (c < 16) {
				s.append("0");
			}
			s.append(Integer.toHexString(c));
		}
		return s.toString();
	}

	public static boolean isLightColor(int r, int g, int b) {
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		float brightness = hsb[2];
		if (brightness > 0.5) {
			return true;
		} else {
			return false;
		}
	}

	public static int[] getRGB(String color) {
		try {
			return getRGB0(color);
		} catch (Exception x) {
			return new int[] { 0, 0, 0 };
		}
	}

	private static int[] getRGB0(String color) throws Exception {
		if (color.startsWith("#")) {
			color = color.substring(1);
		}
		int r, g, b;
		r = g = b = 0;
		if (color.length() == 3) {
			r = Integer.parseInt(color.substring(0, 1), 16) * 16;
			g = Integer.parseInt(color.substring(1, 2), 16) * 16;
			b = Integer.parseInt(color.substring(2, 3), 16) * 16;
		} else if (color.length() == 6) {
			r = Integer.parseInt(color.substring(0, 2), 16);
			g = Integer.parseInt(color.substring(2, 4), 16);
			b = Integer.parseInt(color.substring(4, 6), 16);
		}
		return new int[] { r, g, b };
	}
}