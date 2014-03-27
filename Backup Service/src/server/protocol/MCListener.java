package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

import server.BackupServer;
import server.Chunk;
import server.ChunksRecord;
import server.DataChunk;
import server.messages.Message;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class MCListener implements Runnable {
	BackupServer bs;

	public MCListener(BackupServer bs) {
		this.bs = bs;
	}

	@Override
	public void run() {
		MulticastSocket socket;
		try {
			socket = new MulticastSocket(BackupServer.mc_port);

			socket.joinGroup(InetAddress.getByName(BackupServer.mc_address));

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);

			while (true) {

				String data = listenForMessage(socket, pack);

				Message received = null;
				try {
					received = Message.parse(data);
				} catch (UnrecognizedMessageException e) {
					System.out.println("Ignored Message");
				}
				if (received != null) {
					System.out.println("MC : GOT " + received.getType());
					if (received.getType().equals(MessageType.STORED))
						handleStored(received, pack.getAddress().toString());
					else if (received.getType().equals(MessageType.GETCHUNK))
						handleGetChunk(received);
					else if (received.getType().equals(MessageType.DELETE))
						handleDelete(received);
				}

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// s.leaveGroup(InetAddress.getByName(BackupServer.mc_address));
		// s.close();
	}

	private void handleGetChunk(Message received) {
		int index = bs.getRecord().getChunkIndex(received.getFileId(),
				received.getChunkNo());
		if (index != -1) {
			Chunk c = bs.getRecord().getChunks().get(index);
			DataChunk dc = c.getDataChunk();
			(new ChunkSender(dc)).run();

		}
	}

	private String listenForMessage(MulticastSocket s, DatagramPacket pack) throws IOException {
		s.receive(pack);
		String data = new String(
				Arrays.copyOf(pack.getData(), pack.getLength()), "ISO-8859-1");
		return data;
	}

	private void handleStored(Message received, String origin) {
		if (ChunksRecord.getChunksRecord().incrementChunkValue(
				received.getFileId(), origin, received.getChunkNo())) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					bs.updateVisuals();
				}
			});
		}

	}

	private void handleDelete(Message received) {
		if (ChunksRecord.getChunksRecord().deleteChunksOfFile(
				received.getFileId())) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					bs.updateVisuals();
				}
			});
		}
	}
}
