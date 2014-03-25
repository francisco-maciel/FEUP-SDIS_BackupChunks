package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.HashSet;

import server.BackupServer;
import server.DataChunk;
import server.messages.Message;
import server.messages.MessagePutChunk;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class BackupChunk extends Thread {
	DataChunk chunk;
	int desiredDegree;
	int timeout;
	int timoutCounter;
	HashSet<InetAddress> repplies;
	MulticastSocket sendServer;
	DatagramPacket packet;

	public BackupChunk(DataChunk chunk, int degree) {
		repplies = new HashSet<InetAddress>();
		this.chunk = chunk;
		desiredDegree = degree;
		timeout = 500;
		timoutCounter = 0;
	}

	@Override
	public void run() {

		sendChunk();

	}

	private void sendChunk() {
		try {
			String message = new MessagePutChunk(chunk.getName(),
					chunk.getNo(), chunk.getData(), desiredDegree).toMessage();

			MulticastSocket server = new MulticastSocket(BackupServer.mdb_port);
			byte buf[] = message.getBytes();

			DatagramPacket pack = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(BackupServer.mdb_address),
					BackupServer.mdb_port);

			server.setTimeToLive(1);
			sendServer = server;
			packet = pack;
			listenForStored(chunk.getName(), chunk.getNo());

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void listenForStored(String name, int no) {
		try {

			final MulticastSocket s = new MulticastSocket(BackupServer.mc_port);
			s.joinGroup(InetAddress.getByName(BackupServer.mc_address));

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);

			(new Sleep(200)).go();

			long oldTime = (new java.util.Date()).getTime();

			sendServer.send(packet);
			while (true) {

				try {
					int diff = (int) ((new java.util.Date()).getTime() - oldTime);

					s.setSoTimeout(Math.abs(timeout - diff));
					s.receive(pack);

					String data = new String(Arrays.copyOf(pack.getData(),
							pack.getLength()));

					Message received = null;
					try {
						received = Message.parse(data);
					} catch (UnrecognizedMessageException e) {
					}
					if (received != null) {
						if (received.getType().equals(MessageType.STORED)
								&& received.getFileId().equals(name)
								&& received.getChunkNo() == no) {

							repplies.add(pack.getAddress());
							// System.out.println("BCK:GOT " +
							// received.getType());

							if (repplies.size() >= desiredDegree) {
								break;
							}
						}
					}

				} catch (java.net.SocketTimeoutException e) {

					timoutCounter++;
					// System.out.println("timeout");
					if (timoutCounter == 5) {
						s.close();
						return;
					}

					(new Sleep(200)).go();

					timeout = timeout * 2;
					oldTime = (new java.util.Date()).getTime();
					sendServer.send(packet);

				}

			}

			if (!s.isClosed())
				s.leaveGroup(InetAddress.getByName(BackupServer.mc_address));
			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
