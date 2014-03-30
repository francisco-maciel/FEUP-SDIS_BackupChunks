package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import server.BackupServer;
import server.messages.MessageGetChunk;

public class GetChunkSender extends Thread {

	String name;
	int n;
	boolean enhanced;

	public GetChunkSender(String name, int n, boolean enhanced) {
		this.name = name;
		this.n = n;
		this.enhanced = enhanced;
	}

	@Override
	public void run() {
		try {

			MulticastSocket server = new MulticastSocket();
			server.setTimeToLive(1);
			DatagramPacket pack;
			String message = new MessageGetChunk(name, n, enhanced).toMessage();

			byte buf[] = message.getBytes("ISO-8859-1");
			pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mc_address),
					BackupServer.mc_port);

			server.send(pack);
			server.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
