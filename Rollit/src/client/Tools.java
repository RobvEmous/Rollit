package client;

import java.awt.Dimension;
import java.awt.Point;

public class Tools {

	public static boolean containsLetterOrNumber(String s) {
		for (char c : s.toCharArray()) {
			if (isLetterOrNumber(c)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLetterOrNumber(char c) {
		return Character.isLetter(c) || Character.isDigit(c);
	}
	
	public static Point getCenterLocation(Dimension screenSize, Dimension windowSize) {
		double x = screenSize.getWidth() / 2 - windowSize.width / 2;
		double y = screenSize.getHeight() / 2 - windowSize.height / 2;
		return new Point((int)x, (int)y);
	}

}
