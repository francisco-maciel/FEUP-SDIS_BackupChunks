package server;

public class BackedFile {

	String name;
	int size;
	public BackedFile(String name, int size) {
		this.name = name;
		this.size = size;
	}
	
	public String getName() {
		return name;
	}

}
