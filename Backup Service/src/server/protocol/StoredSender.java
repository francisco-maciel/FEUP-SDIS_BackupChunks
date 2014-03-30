package server.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import server.BackupServer;
import server.ChunksRecord;
import server.messages.Message;
import server.messages.MessageStored;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;
import ui.BackupListener;

public class StoredSender extends Thread {

	String name;
	int no;
	boolean enhance;
	int desiredDegree;
	BackupListener listener;
	HashSet<String> repplies;

	public StoredSender(String name, int no, boolean enhance,
			int desiredDegree, BackupListener l) {
		this.name = name;
		this.no = no;
		this.enhance = enhance;
		this.desiredDegree = desiredDegree;
		this.listener = l;
		repplies = new HashSet<String>();

	}

	public StoredSender(String name, int no, boolean enhance, int desiredDegree) {
		this.name = name;
		this.no = no;
		this.enhance = enhance;
		this.desiredDegree = desiredDegree;
		this.listener = null;
	}

	@Override
	public void run() {
		if (enhance) {

			int stores = countStoresHeard();
			if (stores >= desiredDegree) {
				ChunksRecord.get().removeFromChunks(name, no);
				if (listener != null)
					listener.updateChunks(ChunksRecord.get().getChunks());
				return;
			}
		}

		try {
			String message = new MessageStored(name, no).toMessage();

			MulticastSocket server = new MulticastSocket();
			byte buf[] = message.getBytes("ISO-8859-1");

			DatagramPacket pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mc_address),
					BackupServer.mc_port);

			server.setTimeToLive(1);
			server.send(pack);
			server.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private int countStoresHeard() {
		MulticastSocket s = null;
		try {
			s = new MulticastSocket(BackupServer.mc_port);

			s.joinGroup(InetAddress.getByName(BackupServer.mc_address));
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		byte buf[] = new byte[1024 * 64];
		DatagramPacket pack = new DatagramPacket(buf, buf.length);

		Random rand = new Random();

		int timeout = rand.nextInt(400 + 2) + 1;
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

					if (received.getType().equals(MessageType.STORED)
							&& received.getFileId().equals(name)
							&& received.getChunkNo() == no) {
						if (!pack
								.getAddress()
								.getHostAddress()
								.equals(Inet4Address.getLocalHost()
										.getHostAddress()))
							repplies.add(pack.getAddress().toString());
					}
				}

			} catch (java.net.SocketTimeoutException e) {
				return repplies.size();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

	}
}
