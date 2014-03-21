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

public class MDBListener implements Runnable {
	BackupServer bs;

	public MDBListener(BackupServer bs) {
		this.bs = bs;
	}

	@Override
	public void run() {
		MulticastSocket s;
		try {
			s = new MulticastSocket(BackupServer.mdb_port);

			s.joinGroup(InetAddress.getByName(BackupServer.mdb_address));

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);

			while (true) {

				s.receive(pack);
				String data = new String(Arrays.copyOf(pack.getData(),
						pack.getLength()));

				Message received = null;
				try {
					received = Message.parse(data);
				} catch (UnrecognizedMessageException e) {
					System.out.println("Ignored Message");
				}
				if (received != null) {
					System.out.println("GOT " + received.getType());
					if (received.getType().equals(MessageType.PUTCHUNK)) {

						MessagePutChunk mpc = (MessagePutChunk) received;
						bs.getRecord().addChunk(
								new DataChunk(mpc.getFileId(),
										mpc.getChunkNo(), mpc.getBody(), mpc
												.getBody().length));
						bs.updateVisuals();
						(new Thread(new StoredSender(received.getFileId(),
								received.getChunkNo()))).start();
					}
				}

			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// s.leaveGroup(InetAddress.getByName(BackupServer.mdb_address));
		// s.close();
	}
}
