package server;

import java.io.File;

import server.messages.Message;
import server.messages.PutChunk;
import server.messages.Stored;

public class BackupServer {

	String mc_address, mdb_address, mdr_address;
	int mc_port, mdb_port, mdr_port;
	
	ChunksRecord record;
	
	public BackupServer(String mc_address, int mc_port, String mdb_address,
			int mdb_port, String mdr_address, int mdr_port) {
		
		this.mc_address = mc_address;
		this.mdb_address = mdb_address;
		this.mdr_address = mdr_address;
		this.mc_port = mc_port;
		this.mdb_port = mdb_port;
		this.mdr_port = mdr_port;
		this.record = ChunksRecord.getChunksRecord();

	}

	public void start() {
		
	//	record.printChunksHeld();
		
		
		ChunkedFile chunkedFile = new ChunkedFile("robots.jpg","data" +File.separator+"robots.jpg");
		if (chunkedFile.loadFile()) System.out.println(chunkedFile);
		else System.out.println("File not found");
		
		for (int i = 0; i < chunkedFile.data.size(); i++) {
			DataChunk dc = chunkedFile.getChunk(i);
			record.addChunk(dc);
			
			Message m = new Stored(dc.fileId, dc.chunkNo);
			
				
		}
		

		
	}

}
