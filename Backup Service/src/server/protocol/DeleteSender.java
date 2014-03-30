package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import server.BackupServer;
import server.messages.MessageDelete;

public class DeleteSender extends Thread {

	String name;

	public DeleteSender(String name) {
		this.name = name;

	}

	@Override
	public void run() {
		try {
			String message = new MessageDelete(name).toMessage();

			MulticastSocket server = new MulticastSocket();
			byte buf[] = message.getBytes("ISO-8859-1");

			DatagramPacket pack = new DatagramPacket(buf, message.length(),
					InetAddress.getByName(BackupServer.mc_address),
					BackupServer.mc_port);

			server.setTimeToLive(1);

			try {
				server.send(pack);
				Thread.sleep(1000);
				server.send(pack);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			server.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
