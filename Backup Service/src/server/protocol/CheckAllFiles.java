package server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;
import java.util.Vector;

import server.BackupServer;
import server.Chunk;
import server.messages.MessageIsLost;

public class CheckAllFiles extends Thread {
	HashSet<String> files;

	public CheckAllFiles(Vector<Chunk> chunks) {
		files = new HashSet<String>();

		for (Chunk c : chunks) {
			files.add(c.getName());
		}
	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			MulticastSocket server = new MulticastSocket();
			server.setTimeToLive(1);

			for (String f : files) {
				(new RandomSleep(400)).go();
				String message = new MessageIsLost(f).toMessage();
				byte buf[] = message.getBytes("ISO-8859-1");

				DatagramPacket pack = new DatagramPacket(buf, message.length(),
						InetAddress.getByName(BackupServer.mc_address),
						BackupServer.mc_port);

				server.send(pack);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
