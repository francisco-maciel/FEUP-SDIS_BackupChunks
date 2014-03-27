package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import server.BackupServer;
import server.messages.Message;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class ChunkListener extends Thread {

	String name;
	int chunkNo;
	AtomicBoolean result;

	public ChunkListener(String name, int no, AtomicBoolean result) {
		this.name = name;
		this.chunkNo = no;
		this.result = result;
	}

	@Override
	public void run() {
		MulticastSocket socket;
		try {
			socket = new MulticastSocket(BackupServer.mdr_port);

			socket.joinGroup(InetAddress.getByName(BackupServer.mdr_address));

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);

			Random rand = new Random();
			int timeout = rand.nextInt(400 + 2);

			while (true) {
				String data = null;
				try {
					socket.setSoTimeout(Math.abs(timeout + 1));
					if (socket.getSoTimeout() == 0)
						return;
					data = listenForMessage(socket, pack);
				} catch (IOException e) {
					if (e instanceof java.net.SocketTimeoutException) {
						return;
					}
				}
				Message received = null;
				try {
					received = Message.parse(data);
				} catch (UnrecognizedMessageException e) {
				}
				if (received != null) {
					if (received.getType().equals(MessageType.CHUNK)) {
						if (received.getFileId().equals(name)
								&& received.getChunkNo() == chunkNo) {

							result.set(true);
							return;
						}
					}
				}

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// s.leaveGroup(InetAddress.getByName(BackupServer.mdb_address));
		// s.close();
	}

	private String listenForMessage(MulticastSocket s, DatagramPacket pack) throws IOException {
		s.receive(pack);
		String data = new String(
				Arrays.copyOf(pack.getData(), pack.getLength()), "ISO-8859-1");
		return data;
	}

}
