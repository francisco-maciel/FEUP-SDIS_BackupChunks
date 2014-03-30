package server.protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import server.DataChunk;
import server.messages.MessageChunk;

public class ChunkSenderEnhanced extends Thread {
	DataChunk chunk;
	int port;
	String ip;

	public ChunkSenderEnhanced(DataChunk dc, String ip, int port) {
		this.chunk = dc;
		this.port = port;
		this.ip = ip;
	}

	@Override
	public void run() {

		String message = new MessageChunk(chunk.getName(), chunk.getNo(),
				chunk.getData()).toMessage();
		byte buf[];
		try {
			buf = message.getBytes("ISO-8859-1");

			try {
				DatagramSocket socket = new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName(ip);

				DatagramPacket packet = new DatagramPacket(buf,
						message.length(), IPAddress, port);
				socket.send(packet);

				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

	}
}
