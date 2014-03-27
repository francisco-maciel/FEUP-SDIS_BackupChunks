package server.protocol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import server.BackupServer;
import server.DataChunk;
import server.FileInfo;
import server.messages.Message;
import server.messages.MessageChunk;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;
import ui.BackupListener;

public class ChunkRestoreProtocolInitiator implements Runnable {
	FileInfo file;
	HashSet<Integer> chunkNos;
	Vector<DataChunk> dc;
	BackupListener l;

	public ChunkRestoreProtocolInitiator(FileInfo file, BackupListener l) {
		this.file = file;
		chunkNos = new HashSet<Integer>();
		dc = new Vector<DataChunk>();
		this.l = l;
	}

	@Override
	public void run() {
		l.setEnabledButtons(false);
		MulticastSocket socket;
		try {
			socket = new MulticastSocket(BackupServer.mdr_port);

			socket.joinGroup(InetAddress.getByName(BackupServer.mdr_address));

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			l.updateProgressBar(0);
			// send all getChunks

			for (int i = 0; i < file.getNumChunks(); i++) {
				(new GetChunkSender(file.getCryptedName(), i)).run();
				int once = 0;
				while (true) {
					socket.setSoTimeout(1200);
					String data = null;
					try {
						data = listenForMessage(socket, pack);
					} catch (IOException e) {
						if (e instanceof java.net.SocketTimeoutException) {
							if (once == 2) {
								JOptionPane
										.showMessageDialog(
												null,
												"The file "
														+ file.getName()
														+ " could not be restored! Missing chunk: "
														+ i,
												"File Restore Failed!",
												JOptionPane.WARNING_MESSAGE);
								l.setEnabledButtons(true);

								return;
							} else {
								once++;
								(new GetChunkSender(file.getCryptedName(), i))
										.run();

							}
						}
					}

					Message received = null;
					try {
						received = Message.parse(data);
					} catch (UnrecognizedMessageException e) {
						System.out.println("Ignored Message");
					}
					if (received != null) {
						System.out.println("MDR: GOT " + received.getType()
								+ " " + received.getChunkNo());
						if (received.getType().equals(MessageType.CHUNK)
								&& received.getFileId().equals(
										file.getCryptedName())
								&& received.getChunkNo() == i) {
							processChunk((MessageChunk) received);
							break;
						}

					}

				}
				l.updateProgressBar(100 * (i + 1) / file.getNumChunks());

			}

			finishSavingFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		l.setEnabledButtons(true);

	}

	private void finishSavingFile() {
		// backup finished
		sortChunks(dc);

		JOptionPane.showMessageDialog(null, "The file " + file.getName()
				+ " was recovered successfully!", "File Restored!",
				JOptionPane.INFORMATION_MESSAGE);

		File directory = new File("data");
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showDialog(null, "Save File here");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			directory = fc.getSelectedFile();

		} else {
			JOptionPane.showMessageDialog(
					null,
					"The file " + file.getName()
							+ " was stored in default directory: "
							+ directory.getAbsolutePath(), "File Restored",
					JOptionPane.INFORMATION_MESSAGE);
		}

		saveFile(file.getName(), dc, directory);

	}

	private void saveFile(String name, Vector<DataChunk> chunks, File directory) {
		File saved = new File(directory.getAbsoluteFile() + File.separator
				+ name);
		try {
			saved.delete();
			saved.createNewFile();

			OutputStream file = new FileOutputStream(saved);
			BufferedOutputStream buffer = new BufferedOutputStream(file);

			for (int i = 0; i < chunks.size(); i++)
				buffer.write(chunks.get(i).getData(), 0, chunks.get(i)
						.getSize());

			buffer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean processChunk(MessageChunk received) {
		dc.add(new DataChunk(received.getFileId(), received.getChunkNo(),
				received.getBody(), received.getBody().length));
		return true;

	}

	private String listenForMessage(MulticastSocket s, DatagramPacket pack) throws IOException {
		s.receive(pack);
		String data = new String(
				Arrays.copyOf(pack.getData(), pack.getLength()), "ISO-8859-1");
		return data;
	}

	public void sortChunks(Vector<DataChunk> dataChunk) {
		Collections.sort(dataChunk, new Comparator<DataChunk>() {
			@Override
			public int compare(DataChunk o1, DataChunk o2) {
				return (new Integer(o1.getNo())).compareTo(new Integer(o2
						.getNo()));
			}

		});
	}
}
