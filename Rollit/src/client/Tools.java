package client;

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

}
