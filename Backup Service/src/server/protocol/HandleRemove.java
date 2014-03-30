package server.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import server.BackupServer;
import server.Chunk;
import server.ChunksRecord;
import server.DataChunk;
import server.messages.Message;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;

public class HandleRemove extends Thread {
	String name;
	int no;
	String origin;
	BackupServer bs;

	public HandleRemove(String name, int no, String origin, BackupServer bs) {
		this.name = name;
		this.no = no;
		this.origin = origin;
		this.bs = bs;
	}

	@Override
	public void run() {

		if (ChunksRecord.get().getChunkIndex(name, no) != -1) {
			if (ChunksRecord.get().decrementChunkValue(name, origin, no)) {

				if (someoneStartedBackup())
					return;
				AtomicBoolean result = new AtomicBoolean(false);
				int index = ChunksRecord.get().getChunkIndex(name, no);
				Chunk c = bs.getRecord().getChunks().get(index);
				DataChunk dc = c.getDataChunk();

				Thread t = new BackupChunk(dc, c.desiredDegree, result);
				t.start();
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (result.get() == false) {
					JOptionPane.showMessageDialog(null,
							"Warning! Some data may have been compromised!",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}

				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						bs.updateVisuals();
					}
				});
			}
		}
	}

	private boolean someoneStartedBackup() {
		MulticastSocket s = null;
		try {
			s = new MulticastSocket(BackupServer.mdb_port);

			s.joinGroup(InetAddress.getByName(BackupServer.mdb_address));
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		byte buf[] = new byte[1024 * 64];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);

		Random rand = new Random();

		int timeout = rand.nextInt(400 + 1) + 1;
		long oldTime = (new java.util.Date()).getTime();

		while (true) {

			try {
				int diff = (int) ((new java.util.Date()).getTime() - oldTime);
				String data = null;
				try {
					s.setSoTimeout(Math.abs(timeout - diff) + 1);

					s.receive(pack);

					data = new String(Arrays.copyOf(pack.getData(),
							pack.getLength()), "ISO-8859-1");

				} catch (SocketException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Message received = null;
				try {
					received = Message.parse(data);
				} catch (UnrecognizedMessageException e1) {
				}
				if (received != null) {
					if (received.getType().equals(MessageType.PUTCHUNK)
							&& received.getFileId().equals(name)
							&& received.getChunkNo() == no) {
						return true;

					}
				}

			} catch (java.net.SocketTimeoutException e) {
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
