package server.messages;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import server.Version;

public abstract class Message {
	
	protected MessageType type;
	protected String version;
	protected String fileId;
	protected int chunkNo;
	
	
	public Message( String fileId, int chunkNo) {
		this(Version.get(), fileId, chunkNo);       
    }
	
	public Message( String version , String fileId, int chunkNo) {
        this.fileId = fileId;
        this.version = version;
        this.type = null;
        this.chunkNo = chunkNo;
    }
	
	
	
	public MessageType getType() {
		return type;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getFileId() {
		return fileId;
	}
	public int getChunkNo() {
		return chunkNo;
	}
	
	public void setVersion() {
		 this.version = Version.get();
	}
	public void setVersion(String version) {
		 this.version = version;
	}
	
	public void setFileId(String fileId) {
		 this.fileId = fileId;
	}
	
	public void setChunkNo(int chunkNo) {
		  this.chunkNo = chunkNo;
	}
	
	public abstract String toMessage();
	
	public static Message parse(String message) {
		Message parsed = null;
		
		
		if (parseHead(message, parsed) == null) return null;
		
		return parsed;
		
	}

	private static Message parseHead(String message, Message parsed) {
		int lineIndex = 0;
		
		//parse header
		InputStream is = new ByteArrayInputStream(message.getBytes());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
	 
		String line;
		try {
			while ((line = br.readLine()) != null) {
			if (line.length() == 0) break; // empty line ens header
			
			// Traditional recognized line
			if (lineIndex == 0) {
				System.out.println(line);;
				String[] words = line.split(" ");
				if (words.length < 2) return null;
				
				// TODO parsed
				
			}
			// else ignored
			
			lineIndex++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (lineIndex < 1) return null;
		else return parsed;
	}
}

