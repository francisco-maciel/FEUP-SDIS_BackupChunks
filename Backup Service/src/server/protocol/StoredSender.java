package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import server.BackupServer;
import server.messages.MessageStored;

public class StoredSender extends Thread {

	String name;
	int no;

	public StoredSender(String name, int no) {
		this.name = name;
		this.no = no;
	}

	@Override
	public void run() {
		(new RandomSleep(400)).go();
		try {
			String message = new MessageStored(name, no).toMessage();

			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket();
			byte buf[] = message.getBytes("ISO-8859-1");

			DatagramPacket pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mc_address),
					BackupServer.mc_port);

			server.setTimeToLive(1);
			server.send(pack);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
