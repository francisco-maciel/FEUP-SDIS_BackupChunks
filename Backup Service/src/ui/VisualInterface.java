package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import server.BackupServer;
import server.Chunk;
import server.ChunksRecord;
import server.FileInfo;
import server.FilesRecord;

public class VisualInterface implements BackupListener, TreeSelectionListener {

	private JFrame frmBackupService;
	private BackupServer server;
	private DefaultMutableTreeNode filesHeld;
	DefaultMutableTreeNode chunksHeld;
	JTree tree;
	String selectedNode;
	String selectedChunk;
	private JTextField nameField;
	private JTextField pathField;
	private JTextField sizeField;
	private JTextField desiredField;
	JButton restoreButton;
	JButton backupButton;
	JButton deleteButton;
	private JTextField degreeField;
	private JTextField fileIdField;
	private JTextField chunkNoField;
	private JTextField chunkSizeField;
	private JTextField replicationDegreeField;
	private JTextField DesiredDegreeField;
	private JProgressBar progressBar;
	private boolean enableButtons;
	private JTextField maxSizeField;
	private JTextField actualSizeField;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (System.getProperty("os.name").contains("Windows")) {
						UIManager
								.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					}
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}

				try {
					VisualInterface window = new VisualInterface();
					window.server = BasicInterface
							.initServerWithArguments(args);
					window.server.setListener(window);
					window.server.start();

					window.frmBackupService.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VisualInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		enableButtons = true;
		selectedNode = null;
		selectedChunk = null;
		frmBackupService = new JFrame();
		if (System.getProperty("os.name").contains("Windows")) {
			frmBackupService.setIconImage(((ImageIcon) UIManager
					.getIcon("FileView.hardDriveIcon")).getImage());
		}
		frmBackupService.setResizable(false);
		frmBackupService.setTitle("Backup Service");
		frmBackupService.setBounds(100, 100, 912, 500);
		frmBackupService.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBackupService.getContentPane().setLayout(null);

		tree = new JTree();

		tree.setCellRenderer(new MyTreeCellRenderer());
		tree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Backup") {
					private static final long serialVersionUID = -702610992789751703L;

					{

						chunksHeld = new DefaultMutableTreeNode("Chunks");
						filesHeld = new DefaultMutableTreeNode("Files", true);

						add(filesHeld);
						add(chunksHeld);

					}
				}));
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tree.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)),
				null));
		tree.setShowsRootHandles(true);
		tree.setBounds(10, 77, 650, 243);

		JScrollPane qPane = new JScrollPane(tree,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		qPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)),
				null));
		qPane.setBounds(10, 77, 650, 243);
		frmBackupService.getContentPane().add(qPane);

		JLabel lblBackupService = new JLabel("Backup Service");
		lblBackupService.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblBackupService.setBounds(10, 11, 155, 36);
		frmBackupService.getContentPane().add(lblBackupService);

		backupButton = new JButton("Add file");
		backupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				(new Thread(new Runnable() {

					@Override
					public void run() {
						final JFileChooser fc = new JFileChooser();
						int returnVal = fc.showOpenDialog(frmBackupService);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							server.backupFile(file, new Integer(
									VisualInterface.this.degreeField.getText()));
							VisualInterface.this.clearDetailedText();
							restoreButton.setEnabled(enableButtons);
							deleteButton.setEnabled(enableButtons);
						}

					}

				})).start();
			}
		});
		backupButton.setBounds(10, 331, 89, 23);
		frmBackupService.getContentPane().add(backupButton);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Detailed File Info",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(680, 43, 226, 210);
		frmBackupService.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		Panel panel = new Panel();
		panel.setBounds(6, 16, 214, 188);
		panel_1.add(panel);
		panel.setLayout(null);

		JLabel lblName = new JLabel("Name: ");
		lblName.setBounds(10, 14, 46, 14);
		panel.add(lblName);

		JLabel lblPath = new JLabel("Path: ");
		lblPath.setBounds(10, 52, 29, 14);
		panel.add(lblPath);

		JLabel lblSize = new JLabel("Size:");
		lblSize.setBounds(10, 106, 46, 14);
		panel.add(lblSize);

		JLabel lblBytes = new JLabel("Bytes");
		lblBytes.setBounds(158, 106, 46, 14);
		panel.add(lblBytes);

		JLabel lblDesiredDegree = new JLabel("Desired Degree");
		lblDesiredDegree.setBounds(58, 134, 89, 14);
		panel.add(lblDesiredDegree);

		nameField = new JTextField();
		nameField.setEditable(false);
		nameField.setBounds(48, 11, 156, 20);
		panel.add(nameField);
		nameField.setColumns(10);

		pathField = new JTextField();
		pathField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				pathField.moveCaretPosition(0);
				pathField.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				pathField.setCaretPosition(0);

			}
		});
		pathField.setEditable(false);
		pathField.setColumns(10);
		pathField.setBounds(38, 49, 166, 20);
		panel.add(pathField);

		sizeField = new JTextField();
		sizeField.setEditable(false);
		sizeField.setColumns(10);
		sizeField.setBounds(48, 103, 100, 20);
		panel.add(sizeField);

		desiredField = new JTextField();
		desiredField.setEditable(false);
		desiredField.setColumns(10);
		desiredField.setBounds(140, 131, 36, 20);
		panel.add(desiredField);

		restoreButton = new JButton("Restore");
		restoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				server.restoreFile(selectedNode);
			}
		});
		restoreButton.setBounds(115, 159, 89, 23);
		panel.add(restoreButton);
		restoreButton.setEnabled(false);

		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Are you sure you with to delete the file  "
								+ selectedNode + "?", "Delete file",
						JOptionPane.YES_NO_OPTION);

				if (dialogResult == JOptionPane.YES_OPTION) {
					server.deleteFile(selectedNode);

				}
			}
		});
		deleteButton.setBounds(16, 159, 89, 23);
		panel.add(deleteButton);
		deleteButton.setEnabled(false);

		JLabel lblDefaultReplicationDegree = new JLabel("Replication Degree:");
		lblDefaultReplicationDegree.setBounds(10, 365, 108, 14);
		frmBackupService.getContentPane().add(lblDefaultReplicationDegree);

		degreeField = new JTextField();

		final JSlider slider = new JSlider();
		slider.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				frmBackupService.requestFocusInWindow();
			}
		});
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				degreeField.setText(slider.getValue() + "");
				frmBackupService.requestFocusInWindow();
			}
		});
		slider.setLabelTable(slider.createStandardLabels(10));
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(1);
		slider.setValue(BackupServer.DEFAULT_REP_VALUE);
		slider.setMinorTickSpacing(1);
		slider.setMinimum(1);
		slider.setMaximum(10);
		slider.setBounds(10, 385, 141, 75);
		frmBackupService.getContentPane().add(slider);

		degreeField.setEditable(false);
		degreeField.setBounds(109, 362, 42, 20);
		frmBackupService.getContentPane().add(degreeField);
		degreeField.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Chunk Info",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(690, 264, 226, 170);
		frmBackupService.getContentPane().add(panel_3);
		panel_3.setLayout(null);

		Panel panel_2 = new Panel();
		panel_2.setBounds(10, 25, 206, 135);
		panel_3.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblFileid = new JLabel("FileId:");
		lblFileid.setBounds(10, 14, 46, 14);
		panel_2.add(lblFileid);

		JLabel lblChunkno = new JLabel("ChunkNo:");
		lblChunkno.setBounds(10, 39, 62, 14);
		panel_2.add(lblChunkno);

		JLabel label_2 = new JLabel("Size:");
		label_2.setBounds(10, 64, 46, 14);
		panel_2.add(label_2);

		JLabel label_3 = new JLabel("Bytes");
		label_3.setBounds(158, 64, 46, 14);
		panel_2.add(label_3);

		JLabel label_4 = new JLabel("Replication Degree");
		label_4.setBounds(48, 89, 111, 14);
		panel_2.add(label_4);

		JLabel label_5 = new JLabel("Desired Degree");
		label_5.setBounds(58, 114, 89, 14);
		panel_2.add(label_5);

		fileIdField = new JTextField();
		fileIdField.setEditable(false);
		fileIdField.setColumns(10);
		fileIdField.setBounds(48, 11, 156, 20);
		panel_2.add(fileIdField);

		chunkNoField = new JTextField();
		chunkNoField.setEditable(false);
		chunkNoField.setColumns(10);
		chunkNoField.setBounds(68, 36, 46, 20);
		panel_2.add(chunkNoField);

		chunkSizeField = new JTextField();
		chunkSizeField.setEditable(false);
		chunkSizeField.setColumns(10);
		chunkSizeField.setBounds(48, 64, 100, 20);
		panel_2.add(chunkSizeField);

		replicationDegreeField = new JTextField();
		replicationDegreeField.setEditable(false);
		replicationDegreeField.setColumns(10);
		replicationDegreeField.setBounds(139, 89, 36, 20);
		panel_2.add(replicationDegreeField);

		DesiredDegreeField = new JTextField();
		DesiredDegreeField.setEditable(false);
		DesiredDegreeField.setColumns(10);
		DesiredDegreeField.setBounds(139, 114, 36, 20);
		panel_2.add(DesiredDegreeField);

		progressBar = new JProgressBar();
		progressBar.setBounds(109, 331, 416, 23);
		progressBar.setForeground(new Color(51, 153, 255));
		frmBackupService.getContentPane().add(progressBar);

		JLabel lblNewLabel = new JLabel("Space occupied: ");
		lblNewLabel.setBounds(268, 52, 81, 14);
		frmBackupService.getContentPane().add(lblNewLabel);

		JLabel label = new JLabel("/");
		label.setBounds(458, 52, 14, 14);
		frmBackupService.getContentPane().add(label);

		actualSizeField = new JTextField();
		actualSizeField.setHorizontalAlignment(SwingConstants.RIGHT);
		actualSizeField.setEditable(false);
		actualSizeField.setBounds(359, 52, 89, 14);
		frmBackupService.getContentPane().add(actualSizeField);
		actualSizeField.setColumns(10);

		maxSizeField = new JTextField();
		maxSizeField.setHorizontalAlignment(SwingConstants.LEFT);
		maxSizeField.setEditable(false);
		maxSizeField.setBounds(466, 52, 108, 14);
		frmBackupService.getContentPane().add(maxSizeField);
		maxSizeField.setColumns(10);

		JButton btnNewButton = new JButton("Force clean");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double ans = 0;
				try {
					ans = Double.parseDouble((String) JOptionPane
							.showInputDialog(null, "Set max size (in 64k)",
									"Change max size",
									JOptionPane.INFORMATION_MESSAGE, null,
									null, "1 = 64000"));
				} catch (NumberFormatException e) {

					return;
				}
				ChunksRecord.get().setMaxSize((int) (ans * 64 * 1000),
						VisualInterface.this);
				updateChunks(ChunksRecord.get().getChunks());
			}
		});
		btnNewButton.setBounds(584, 48, 91, 23);
		frmBackupService.getContentPane().add(btnNewButton);
		frmBackupService.setLocationRelativeTo(null);
	}

	private static class MyTreeCellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = -8070832356267898503L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			// decide what icons you want by examining the node
			if (value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (node.getUserObject() instanceof String) {
					// your root node, since you just put a String as a user obj
					if (node.getUserObject().equals("Chunks"))
						setIcon(UIManager.getIcon("FileView.hardDriveIcon"));

					if (node.getUserObject().equals("Files"))
						setIcon(UIManager.getIcon("FileView.directoryIcon"));

					if (node.getParent() != null) {
						if (((DefaultMutableTreeNode) node.getParent())
								.getUserObject().equals("Chunks"))
							setIcon(UIManager
									.getIcon("FileView.floppyDriveIcon"));
						if (((DefaultMutableTreeNode) node.getParent())
								.getUserObject().equals("Files"))
							setIcon(UIManager.getIcon("FileView.fileIcon"));

					}

				}

			}

			return this;
		}

	}

	@Override
	public void updateChunks(Vector<Chunk> chunks) {
		maxSizeField.setText("" + ChunksRecord.get().getMaxSize());
		if (ChunksRecord.get().getTotalSize() >= ChunksRecord.get()
				.getMaxSize())
			actualSizeField.setForeground(Color.RED);
		else
			actualSizeField.setForeground(Color.BLACK);
		actualSizeField.setText("" + ChunksRecord.get().getTotalSize());

		try {
			chunksHeld.removeAllChildren();

			for (int i = 0; i < chunks.size(); i++) {
				chunksHeld.add(new DefaultMutableTreeNode(chunks.get(i)
						.getChunkFileName()));
			}

			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model
					.getRoot();
			model.reload(root);
			clearDetailedText();
			clearDetailedChunkText();
		} catch (Exception e) {

		}
	}

	@Override
	public synchronized void updateFiles(Vector<FileInfo> files) {

		filesHeld.removeAllChildren();

		for (int i = 0; i < files.size(); i++) {
			filesHeld.add(new DefaultMutableTreeNode(files.get(i).getName()));
		}

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		model.reload(root);
		clearDetailedText();
		clearDetailedChunkText();

	}

	@Override
	public synchronized void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (node == null) {
			selectedNode = null;
			selectedChunk = null;
			return;
		}

		String nodeInfo = (String) node.getUserObject();
		if (node.isLeaf()) {
			if (node.getParent() != null) {
				if (((DefaultMutableTreeNode) node.getParent()).getUserObject()
						.equals("Files")) {
					selectedNode = nodeInfo;
					selectedChunk = null;
				} else
					selectedNode = null;

				if (((DefaultMutableTreeNode) node.getParent()).getUserObject()
						.equals("Chunks")) {
					selectedChunk = nodeInfo;
					selectedNode = null;
				} else
					selectedChunk = null;
			} else {
				selectedNode = null;
				selectedChunk = null;
			}
		} else {
			selectedNode = null;
			selectedChunk = null;
		}

		if (selectedNode != null) {
			setDetailedFileText();
			clearDetailedChunkText();
			restoreButton.setEnabled(enableButtons);
			deleteButton.setEnabled(enableButtons);
		} else {
			clearDetailedText();
			restoreButton.setEnabled(false);
			deleteButton.setEnabled(false);

		}

		if (selectedChunk != null) {
			setDetailedChunkText();
			clearDetailedText();
		} else {
			clearDetailedChunkText();
		}
	}

	private void setDetailedFileText() {
		if (selectedNode != null) {
			FileInfo file = FilesRecord.get().getFileInfo(selectedNode);
			setDetailedText(file.getName(), file.getPath(), file.getSize(),
					file.getDesiredDegree());
		}
	}

	private void setDetailedChunkText() {
		if (selectedChunk != null) {
			for (int i = 0; i < ChunksRecord.get().getChunks().size(); i++) {
				if (ChunksRecord.get().getChunks().get(i).getChunkFileName()
						.equals(selectedChunk)) {
					Chunk chunk = ChunksRecord.get().getChunks().get(i);
					setDetailedChunk(chunk.getName(), chunk.getNo(),
							chunk.getSize(), chunk.getActualDegree(),
							chunk.desiredDegree);

				}
			}
		}
	}

	private void setDetailedChunk(String name, int no, int size, int actualDegree, int desiredDegree) {
		fileIdField.setText(name);
		fileIdField.setCaretPosition(0);
		chunkSizeField.setText(size + "");
		chunkSizeField.setCaretPosition(0);
		chunkNoField.setText(no + "");
		chunkNoField.setCaretPosition(0);

		replicationDegreeField.setText(actualDegree + "");
		DesiredDegreeField.setText(desiredDegree + "");
		if (actualDegree < desiredDegree)
			replicationDegreeField.setForeground(Color.RED);
		else
			replicationDegreeField.setForeground(Color.BLACK);
	}

	private void clearDetailedText() {
		nameField.setText("");
		pathField.setText("");
		sizeField.setText("");
		desiredField.setText("");
	}

	private void clearDetailedChunkText() {
		fileIdField.setText("");
		chunkSizeField.setText("");
		chunkNoField.setText("");
		replicationDegreeField.setText("");
		DesiredDegreeField.setText("");

	}

	private void setDetailedText(String name, String path, int size, int desiredDegree) {
		nameField.setText(name);
		nameField.setCaretPosition(0);
		pathField.setText(path);
		pathField.setCaretPosition(0);
		sizeField.setText(size + "");
		sizeField.setCaretPosition(0);
		desiredField.setText(desiredDegree + "");

	}

	@Override
	public void updateProgressBar(int value) {
		progressBar.setValue(value);
		progressBar.repaint();
	}

	@Override
	public void setEnabledButtons(boolean value) {
		deleteButton.setEnabled(false);
		backupButton.setEnabled(value);
		enableButtons = value;

	}
}
