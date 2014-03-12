package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ChunkedFile {

	String path;
	String fileName;
	String preCryptName;
	String cryptName;
	Vector<DataChunk> data;
	int size;
	public static final int CHUNK_SIZE = 64 * 1024;
	
	public ChunkedFile(String filename, String path) {
		this.path = path;
		this.data = new Vector<DataChunk>();
		this.fileName = filename;
		size = 0;
		preCryptName = "";
	}
	
	public boolean loadFile() {
		File f = new File(path);

		if (!f.exists()) return false;
		
		processChunkName(f);
		
		 InputStream file;
		try {
			//f.createNewFile();
			file = new FileInputStream(f);
			BufferedInputStream buffer = new BufferedInputStream(file);
			
			int i = 0;
			
			int next = 0;
			int oldnext;
			while(true) {
				oldnext = next;
				
				byte[] buf = new byte[CHUNK_SIZE];
				if ((next = buffer.read(buf, 0, CHUNK_SIZE)) == -1)  break;

				size += next;
				data.add(new DataChunk(cryptName,i,buf));
				i++;
				
			}
			if (oldnext == CHUNK_SIZE) {
				data.add(new DataChunk(cryptName,i,new byte[0]));
			}
			
			buffer.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public DataChunk getChunk(int index) {
		return data.get(index);
	}

	private void processChunkName(File f) {
		
		String computername = "";
		try {
			computername = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:ms");
		String time = sdf.format(f.lastModified());
		
		preCryptName = fileName + " " +  time + " " +  computername;

		crypt();
	}
	
	
	
	private void crypt() {
		
		 MessageDigest digester;
		    try {
		    	digester = MessageDigest.getInstance("SHA-256");
		    	 cryptName =  byteArrayToHexString(digester.digest(preCryptName.getBytes()));
		    }
		    catch(NoSuchAlgorithmException e) {
		    	cryptName = preCryptName;
		        e.printStackTrace();
		    } 
		   
		
	}

	
	
	public static String byteArrayToHexString(byte[] b) {
		// CODIGO RETIRADO DE: http://stackoverflow.com/questions/4895523/java-string-to-sha1
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
		}
	
	
	public String toString() {
		return "File: " + fileName + "\nPath: " + path + "\nSize: " + size +  "\nChunks: " + data.size();
		
	}
}
