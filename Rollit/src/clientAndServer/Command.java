package clientAndServer;

public class Command {
	
	private String id;
	private String[] args;

	public Command(String id, String[] args) {
		this.id = id;
		this.args = args;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String[] getArgs() {
		return args;
	}	
	
	@Override
	public String toString() {
		return "Id: " + id + ", args: " + Tools.ArrayToString(getArgs());
	}

}
