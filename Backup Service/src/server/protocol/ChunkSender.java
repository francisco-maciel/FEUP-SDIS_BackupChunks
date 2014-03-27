package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import server.BackupServer;
import server.DataChunk;
import server.messages.MessageChunk;

public class ChunkSender implements Runnable {

	DataChunk chunk;

	public ChunkSender(DataChunk dc) {
		chunk = dc;
	}

	@Override
	public void run() {
		(new RandomSleep(400)).go();
		// TODO dont send if received
		try {
			String message = new MessageChunk(chunk.getName(), chunk.getNo(),
					chunk.getData()).toMessage();

			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket();
			byte buf[] = message.getBytes("ISO-8859-1");

			DatagramPacket pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mdr_address),
					BackupServer.mdr_port);

			server.setTimeToLive(1);
			server.send(pack);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
