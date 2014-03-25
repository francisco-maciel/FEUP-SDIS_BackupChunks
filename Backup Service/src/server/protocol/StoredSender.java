package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import server.BackupServer;
import server.messages.MessageStored;

public class StoredSender implements Runnable {

	String name;
	int no;

	public StoredSender(String name, int no) {
		this.name = name;
		this.no = no;
	}

	@Override
	public void run() {
		(new Sleep(400)).go();
		// TODO random 0 - 400 ms interval
		try {
			String message = new MessageStored(name, no).toMessage();

			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket(BackupServer.mc_port);
			byte buf[] = message.getBytes();

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
