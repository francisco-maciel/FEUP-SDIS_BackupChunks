package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import server.BackupServer;
import server.DataChunk;
import server.PutChunkEnhancement;
import server.messages.Message;
import server.messages.MessagePutChunk;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class BackupChunk extends Thread {
	DataChunk chunk;
	int desiredDegree;
	int timeout;
	int timoutCounter;
	HashSet<String> repplies;
	MulticastSocket sendServer;
	DatagramPacket packet;
	AtomicBoolean result;

	public BackupChunk(DataChunk chunk, int degree, AtomicBoolean result) {

		repplies = new HashSet<String>();
		this.chunk = chunk;
		desiredDegree = degree;
		timeout = 500;
		timoutCounter = 0;

		this.result = result;
		this.result.set(false);
	}

	@Override
	public void run() {
		PutChunkEnhancement.saveToDisk(chunk, desiredDegree);
		sendChunk();
		PutChunkEnhancement.destroy(chunk);

	}

	private void sendChunk() {
		try {
			String message = new MessagePutChunk(chunk.getName(),
					chunk.getNo(), chunk.getData(), desiredDegree).toMessage();

			MulticastSocket server = new MulticastSocket();
			byte buf[] = message.getBytes("ISO-8859-1");

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

			long oldTime = (new java.util.Date()).getTime();

			sendServer.send(packet);
			while (true) {

				try {
					int diff = (int) ((new java.util.Date()).getTime() - oldTime);

					s.setSoTimeout(Math.abs(timeout - diff) + 1);
					s.receive(pack);

					String data = new String(Arrays.copyOf(pack.getData(),
							pack.getLength()), "ISO-8859-1");

					Message received = null;
					try {
						received = Message.parse(data);
					} catch (UnrecognizedMessageException e) {
					}
					if (received != null) {
						if (received.getType().equals(MessageType.STORED)
								&& received.getFileId().equals(name)
								&& received.getChunkNo() == no) {

							repplies.add(pack.getAddress().toString());

							if (repplies.size() >= desiredDegree) {
								result.set(true);
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
