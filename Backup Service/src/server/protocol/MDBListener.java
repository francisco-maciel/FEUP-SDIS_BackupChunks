package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

import server.BackupServer;
import server.DataChunk;
import server.messages.Message;
import server.messages.MessagePutChunk;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class MDBListener extends Thread {
	BackupServer bs;

	public MDBListener(BackupServer bs) {
		this.bs = bs;
	}

	@Override
	public void run() {
		MulticastSocket socket;
		try {
			socket = new MulticastSocket(BackupServer.mdb_port);

			socket.joinGroup(InetAddress.getByName(BackupServer.mdb_address));

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
					System.out.println("MDB: GOT " + received.getType());
					if (received.getType().equals(MessageType.PUTCHUNK))
						handlePutChunk(received);
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

	private void handlePutChunk(final Message received) {
		final MessagePutChunk mpc = (MessagePutChunk) received;
		final DataChunk dc = new DataChunk(mpc.getFileId(), mpc.getChunkNo(),
				mpc.getBody(), mpc.getBody().length);
		dc.desiredDegree = mpc.getDegree();

		(new Thread(new Runnable() {

			@Override
			public void run() {

				boolean stored = bs.getRecord().addChunk(dc, bs.getListener());
				if (stored) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						public void run() {
							bs.updateVisuals();
						}
					});
				}
				if (!bs.getRecord().onRemovedList(mpc.getFileId(),
						mpc.getChunkNo())) {

					(new Thread(new StoredSender(received.getFileId(),
							received.getChunkNo(), true, mpc.getDegree(),
							bs.getListener()))).start();
				}

			}
		})).start();

	}
}
