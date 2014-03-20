package server;

import java.io.Serializable;

public class FileInfo implements Serializable {

	private static final long serialVersionUID = -4106048689272352962L;
	String name;
	String path;
	int size;
	int replicationDegree;
	int desiredDegree;

	public FileInfo(String name, String path, int size, int replicationDegree,
			int desiredDegree) {
		this.name = name;
		this.size = size;
		this.path = path;
		this.desiredDegree = desiredDegree;
		this.replicationDegree = replicationDegree;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public void setReplicationDegree(int deg) {
		this.replicationDegree = deg;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public void setDesiredDegree(int deg) {
		this.desiredDegree = deg;
	}

	public int getDesiredDegree() {
		return desiredDegree;
	}

}
