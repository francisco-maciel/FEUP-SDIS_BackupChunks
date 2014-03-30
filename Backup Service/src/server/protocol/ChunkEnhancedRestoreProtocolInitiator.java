package server.protocol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import server.DataChunk;
import server.FileInfo;
import server.messages.Message;
import server.messages.MessageChunk;
import server.messages.MessageType;
import server.messages.UnrecognizedMessageException;
import ui.BackupListener;

public class ChunkEnhancedRestoreProtocolInitiator extends Thread {
	public FileInfo file;
	HashSet<Integer> chunkNos;
	public Vector<DataChunk> dc;
	AtomicBoolean result;
	BackupListener l;

	public ChunkEnhancedRestoreProtocolInitiator(FileInfo file,
			AtomicBoolean result, BackupListener listener) {
		this.file = file;
		chunkNos = new HashSet<Integer>();
		dc = new Vector<DataChunk>();
		this.result = result;
		this.l = listener;
	}

	@Override
	public void run() {
		DatagramSocket socket;
		try {
			socket = new DatagramSocket(8700);

			byte buf[] = new byte[1024 * 64];
			DatagramPacket pack = new DatagramPacket(buf, buf.length);
			l.updateProgressBar(0);
			// send all getChunks

			for (int i = 0; i < file.getNumChunks(); i++) {
				(new GetChunkSender(file.getCryptedName(), i)).start();
				int once = 0;
				while (true) {
					socket.setSoTimeout(500);
					String data = null;
					try {
						data = listenForMessage(socket, pack);
					} catch (IOException e) {
						if (e instanceof java.net.SocketTimeoutException) {
							if (once == 2) {
								result.set(false);

								return;
							} else {
								once++;
								(new GetChunkSender(file.getCryptedName(), i))
										.start();

							}
						}
					}

					Message received = null;
					try {

						received = Message.parse(data);
					} catch (UnrecognizedMessageException e) {
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
			result.set(true);
			finishSavingFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private String listenForMessage(DatagramSocket s, DatagramPacket pack) throws IOException {
		s.receive(pack);
		String data = new String(
				Arrays.copyOf(pack.getData(), pack.getLength()), "ISO-8859-1");
		return data;
	}

	private boolean processChunk(MessageChunk received) {
		dc.add(new DataChunk(received.getFileId(), received.getChunkNo(),
				received.getBody(), received.getBody().length));
		return true;

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
