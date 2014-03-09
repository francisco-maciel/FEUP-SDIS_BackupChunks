package server;

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
		
		
		ChunkedFile fr = new ChunkedFile("robots.jpg","data\\randomfile.rf");
		if (fr.loadFile()) System.out.println(fr);
		
		
		for (int i = 0; i < fr.data.size(); i++) {
			record.addChunk(fr.data.get(i));
			
		}
		

		
	}

}
