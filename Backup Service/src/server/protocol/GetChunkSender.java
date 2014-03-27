package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import server.BackupServer;
import server.messages.MessageGetChunk;

public class GetChunkSender implements Runnable {

	String name;
	int n;

	public GetChunkSender(String name, int n) {
		this.name = name;
		this.n = n;
	}

	@Override
	public void run() {
		try {

			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket();
			server.setTimeToLive(1);
			DatagramPacket pack;
			String message = new MessageGetChunk(name, n).toMessage();

			byte buf[] = message.getBytes("ISO-8859-1");
			pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mc_address),
					BackupServer.mc_port);

			server.send(pack);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
