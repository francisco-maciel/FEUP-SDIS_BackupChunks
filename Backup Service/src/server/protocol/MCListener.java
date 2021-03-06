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
import server.messages.MessageGetChunk;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class MCListener extends Thread {
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
				}
				if (received != null) {
					System.out.println("MC : GOT " + received.getType());
					if (received.getType().equals(MessageType.STORED)) {

						handleStored(received, pack.getAddress().toString());
					} else if (received.getType().equals(MessageType.GETCHUNK))
						handleGetChunk(received, pack.getAddress()
								.getHostAddress());
					else if (received.getType().equals(MessageType.DELETE))
						handleDelete(received);
					else if (received.getType().equals(MessageType.ISLOST))
						handleIsLost(received);
					else if (received.getType().equals(MessageType.REMOVED))
						handleRemoved(received, pack.getAddress().toString());
				}

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// s.leaveGroup(InetAddress.getByName(BackupServer.mc_address));
		// s.close();
	}

	private void handleRemoved(Message received, String origin) {
		String name = received.getFileId();
		int no = received.getChunkNo();
		((new HandleRemove(name, no, origin, bs))).start();

	}

	private void handleIsLost(Message received) {
		String name = received.getFileId();
		if (ChunksRecord.get().wasDeleted(name))
			(new DeleteSender(name)).start();
	}

	private void handleGetChunk(Message received, String ip) {
		int index = bs.getRecord().getChunkIndex(received.getFileId(),
				received.getChunkNo());
		if (index != -1) {
			Chunk c = bs.getRecord().getChunks().get(index);
			DataChunk dc = c.getDataChunk();
			if (((MessageGetChunk) received).getPort() == 0)
				(new ChunkSender(dc)).start();
			else
				(new ChunkSenderEnhanced(dc, ip,
						((MessageGetChunk) received).getPort())).start();

		}
	}

	private String listenForMessage(MulticastSocket s, DatagramPacket pack) throws IOException {
		s.receive(pack);
		String data = new String(
				Arrays.copyOf(pack.getData(), pack.getLength()), "ISO-8859-1");
		return data;
	}

	private void handleStored(Message received, String origin) {
		if (ChunksRecord.get().incrementChunkValue(received.getFileId(),
				origin, received.getChunkNo())) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					bs.updateVisuals();
				}
			});
		}

	}

	private void handleDelete(Message received) {
		if (ChunksRecord.get().deleteChunksOfFile(received.getFileId())) {
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					bs.updateVisuals();
				}
			});
		}
	}
}
