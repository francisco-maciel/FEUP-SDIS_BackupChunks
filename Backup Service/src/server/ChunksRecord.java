package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Vector;

import utils.Debug;

public class ChunksRecord {
	// TODO removing chunks from record, file, and erasing file
	
	public static final String RECORD_NAME = ".chunkRecord";
	private static ChunksRecord chuncksRecord;

	private Vector<Chunk> chunks;
	
	@SuppressWarnings("unchecked")
	private ChunksRecord() {
		
		createDataFolder();
		
		File f = new File("data" + File.separator + RECORD_NAME);
		if(f.exists() && !f.isDirectory()) 
		{ 
			try {
		      InputStream file = new FileInputStream(f);
		      InputStream buffer = new BufferedInputStream(file);
		      ObjectInput input = new ObjectInputStream (buffer);
		  
		      chunks =  (Vector<Chunk>) input.readObject();
		      input.close();
		      
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
		else {
		    chunks = new Vector<Chunk>();		     
		    Debug.debug("Created original chunk Records File");
			updateRecordFile();
		}
		
	}

	private void createDataFolder() {
		
			File theDir = new File("data");  
		   if (!theDir.exists()) theDir.mkdir();  
		   File theDir2 = new File("data" + File.separator + "chunks");  
		   if (!theDir2.exists()) theDir2.mkdir();  
		
	}

	public static ChunksRecord getChunksRecord() {
		if (chuncksRecord == null) {
			chuncksRecord = new ChunksRecord();
		}
		return chuncksRecord;
	}
	
	public int getNumberChunks() {
		return chunks.size();
	}
	
	public void printChunksHeld() {
		int n = getNumberChunks();
		System.out.println("Chunks held: " + n);
			
			for (int i = 0; i < n; i++) {
				System.out.println(chunks.get(i));
			}	
	}
	public boolean addChunk(DataChunk dc ) {	
		return addChunk(dc.fileId,dc.chunkNo, dc.getData(), dc.getSize());
	}
	public boolean addChunk(String fileId, int chunkNo, byte[] data, int size) {
		Chunk newC = new Chunk(fileId,chunkNo);

		File f = new File("data"+File.separator+"chunks"+File.separator + newC.getChunkFileName());
		try {
			if (!f.createNewFile()) return false;
			
			OutputStream file = new FileOutputStream(f);
			BufferedOutputStream buffer = new BufferedOutputStream(file);
			
			buffer.write(data,0, size);
			buffer.close();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		chunks.add(newC);
		
		updateRecordFile();
		return true;
	}
	
	
	public void deleteData() {
		deleteDirectory(new File("data" + File.separator + "chunks"));
		createDataFolder();
		chunks.removeAllElements();
		updateRecordFile();
	}
	
	
	public static boolean deleteDirectory(File directory) {
		// Codigo retirado de http://stackoverflow.com/questions/3775694/deleting-folder-from-java
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	private void updateRecordFile() {
		try {
			File f = new File("data" + File.separator + RECORD_NAME);
			f.createNewFile();
			 OutputStream file = new FileOutputStream(f);
			 OutputStream buffer = new BufferedOutputStream(file);
		     ObjectOutput output = new ObjectOutputStream(buffer);
		     output.writeObject(chunks);
		     output.close();
		     
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Vector<Chunk> getChunks() {
		return chunks;
	}
}