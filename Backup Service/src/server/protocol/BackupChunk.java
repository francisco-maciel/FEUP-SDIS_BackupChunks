package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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

	public BackupChunk(DataChunk chunk, int degree) {
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

			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket(BackupServer.mdb_port);
			byte buf[] = message.getBytes();

			DatagramPacket pack = new DatagramPacket(buf, buf.length,
					InetAddress.getByName(BackupServer.mdb_address),
					BackupServer.mdb_port);

			server.setTimeToLive(1);
			server.send(pack);
			listenForStored(chunk.getName(), chunk.getNo());

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void listenForStored(String name, int no) {

		final Timer timer = new Timer();
		try {

			final MulticastSocket s = new MulticastSocket(BackupServer.mc_port);
			s.joinGroup(InetAddress.getByName(BackupServer.mc_address));

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);

			class WaitTimeout extends TimerTask {
				public void run() {

					try {
						s.leaveGroup(InetAddress
								.getByName(BackupServer.mc_address));
					} catch (IOException e) {
						e.printStackTrace();
					}
					s.close();
					timer.cancel();
				}
			}

			while (true) {

				try {
					timer.schedule(new WaitTimeout(), timeout);
					s.receive(pack);
				} catch (java.net.SocketTimeoutException e) {
					System.out.println("timeout. remaining: "
							+ s.getSoTimeout());
				}

				String data = new String(Arrays.copyOf(pack.getData(),
						pack.getLength()));

				Message received = null;
				try {
					received = Message.parse(data);
				} catch (UnrecognizedMessageException e) {
					System.out.println("Ignored Message");
				}
				if (received != null) {
					if (received.getType().equals(MessageType.STORED)
							&& received.getFileId().equals(name)
							&& received.getChunkNo() == no) {

						// TODO a chunk foi recebida por alguem.
						System.out.println("GOT " + received.getType());
						timer.cancel();
						break;
					}
				}

			}
			s.leaveGroup(InetAddress.getByName(BackupServer.mc_address));
			s.close();

		} catch (IOException e) {
			if (e.getMessage().equals("socket closed")) {
				timoutCounter++;
				// System.out.println("timeout");
				if (timoutCounter == 5)
					return;
				timeout = timeout * 2;
				sendChunk();
			}
		}

	}
}
