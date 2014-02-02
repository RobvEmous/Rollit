package clientAndServer;

public class Password {
	public static final String INITIAL = "password123";
	private static final String ADMIN_PASS = "verysafe";

	private String password = "";
	
	public Password() {
		password = INITIAL;
	}
	
	public static boolean isValidPassword(String testPass) {
		boolean isAcceptable = false;
		if (testPass.length() > 5 && !(testPass.contains(" "))) {
			isAcceptable = true;
		}
		return isAcceptable;
	}
	
	public boolean acceptable(String testPass) {
		boolean isAcceptable = false;
		if (testPass.length() > 5 && !(testPass.contains(" "))) {
			isAcceptable = true;
		}
		return isAcceptable;
	}
	
	public boolean setPassword(String oldPass, String newPass) {
		boolean changed = false;
		if (testPassword(oldPass) && acceptable(newPass)) {
			password = newPass;
			changed = true;
		}
		return changed;
	}
	
	public boolean testPassword(String test) {
		boolean match = false;
		if (test.equals(password)) {
			match = true;
		}		
		return match;
	}
	
	public String getPass(String adminPass) {
		if (adminPass.equals(ADMIN_PASS)) {
			return password;
		}
		return null;
	}
	
	public boolean equals(Password p) {
		return (p.getPass(ADMIN_PASS).equals(password));
	}
	
    public static void main(String[] args) {
        @SuppressWarnings("unused")
		Password password = new Password();
    }
	
}
