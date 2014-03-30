package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

import server.BackupServer;
import server.Chunk;
import server.ChunksRecord;
import server.messages.MessageRemoved;

public class RemovedSender extends Thread {
	String name;
	int no;
	AtomicBoolean result;

	public RemovedSender(String name, int no, AtomicBoolean result) {
		this.name = name;
		this.no = no;
		this.result = result;
	}

	@Override
	public void run() {

		(new RandomSleep(400)).go();
		try {
			String message = new MessageRemoved(name, no).toMessage();

			MulticastSocket server = new MulticastSocket();
			byte buf[] = message.getBytes("ISO-8859-1");

			DatagramPacket pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mc_address),
					BackupServer.mc_port);

			server.setTimeToLive(1);
			server.send(pack);
			try {
				// sleep while protocol may be happening
				Thread.sleep(500 + 1000 + 2000 + 400);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			int index = ChunksRecord.get().getChunkIndex(name, no);

			Chunk c = ChunksRecord.get().getChunks().get(index);

			if (c.getActualDegree() < c.desiredDegree) {
				result.set(false);
				ChunksRecord.get().removeFromRemoveList(c.getName(), c.getNo());

				(new Thread(new StoredSender(c.getName(), c.getNo(), false,
						c.desiredDegree))).start();

			} else {
				result.set(true);
				ChunksRecord.get().removeFromRemoveList(c.getName(), c.getNo());
				ChunksRecord.get().getChunks().remove(index);
				ChunksRecord.get().deleteChunk(c);

				ChunksRecord.get().updateTotalSize();
				ChunksRecord.get().updateRecordFile();

			}
			server.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
