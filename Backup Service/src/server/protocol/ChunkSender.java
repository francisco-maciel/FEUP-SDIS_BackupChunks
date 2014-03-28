package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import server.BackupServer;
import server.DataChunk;
import server.messages.MessageChunk;

public class ChunkSender extends Thread {

	DataChunk chunk;

	public ChunkSender(DataChunk dc) {
		chunk = dc;
	}

	@Override
	public void run() {
		if (checkChunkAlreadySent())
			return;

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

	private boolean checkChunkAlreadySent() {
		AtomicBoolean result = new AtomicBoolean(false);
		Thread t = (new ChunkListener(chunk.getName(), chunk.getNo(), result));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return result.get();

	}
}
