package clientAndServer;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

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
	
	public static String replaceSpace(String s) {
		String newS = "";
		for (char c : s.toCharArray()) {
			if (c == ' ') {
				newS += '\24';
			} else {
				newS += c;
			}
		}
		return newS;	
	}
	
	public String returnSpace(String s) {
		String newS = "";
		for (char c : s.toCharArray()) {
			if (c == '\24') {
				newS += ' ';
			} else {
				newS += c;
			}
		}
		return newS;		
	}
	
	/**
	 * Returns a new list containing the first <code>p</code> items of
	 * <code>values</code>. If <code>values.size() <= p</code> the new 
	 * list is equal to the old list.
	 * @param values the old list.
	 * @param p the number of items.
	 * @return
	 */
	public static <V> ArrayList<V> getFirstP(ArrayList<V> values, int p) {
		ArrayList<V> newValues = new ArrayList<V>();
		int size = values.size();
		for (int i = 0; i <= p && i < size; i++) {
			newValues.add(values.get(i));
		}
		return newValues;	
	}
	
	public static Point getCenterLocation(Dimension screenSize, Dimension windowSize) {
		double x = screenSize.getWidth() / 2 - windowSize.width / 2;
		double y = screenSize.getHeight() / 2 - windowSize.height / 2;
		return new Point((int)x, (int)y);
	}
	
	public static Point getRightBottomLocation(Dimension screenSize, Dimension windowSize) {
		double x = screenSize.getWidth() - windowSize.width;
		double y = screenSize.getHeight() - windowSize.height;
		return new Point((int)x, (int)y);
	}

	public static String ArrayToString(String[] args) {
		String string = "";
		for (int i = 0; i < args.length - 1; i++) {
			string += args[i] + " ";
		}
		return string + args[args.length - 1];
	}
	
	public static String[] removeOne(String[] all, String theOne) {
		ArrayList<String> rest = new ArrayList<String>();
		for (String one : all) {
			if (!one.equals(theOne)) {
				rest.add(one);
			}
		}
		return (String[])rest.toArray();
	}
	
	public static <V> ArrayList<V> addOneToStart(V[] allButOne, V theOne) {
		ArrayList<V> all = new ArrayList<V>();
		all.add(theOne);
		for (V one : allButOne) {
			all.add(one);
		}
		return all;
	}

}
