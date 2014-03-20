package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

import server.BackedFile;
import server.BackupServer;
import server.Chunk;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

public class VisualInterface implements BackupListener {

	private JFrame frmBackupService;
	private BackupServer server;
	private DefaultMutableTreeNode filesHeld;
	DefaultMutableTreeNode chunksHeld;
	JTree tree;
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				  try {
					UIManager.setLookAndFeel(
					            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					e1.printStackTrace();
				}
				  
				  
				try {
					VisualInterface window = new VisualInterface();
					window.server =  BasicInterface.initServerWithArguments(args);
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
		frmBackupService = new JFrame();
		frmBackupService.setIconImage(((ImageIcon)UIManager.getIcon("FileView.hardDriveIcon")).getImage());
		frmBackupService.setResizable(false);
		frmBackupService.setTitle("Backup Service");
		frmBackupService.setBounds(100, 100, 763, 432);
		frmBackupService.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBackupService.getContentPane().setLayout(null);
	        
	         tree = new JTree();
	
	        tree.setCellRenderer(new MyTreeCellRenderer());
	        tree.setModel(new DefaultTreeModel(
	        	new DefaultMutableTreeNode("Backup") {
					private static final long serialVersionUID = -702610992789751703L;

					{
	        			
	        			chunksHeld = new DefaultMutableTreeNode("Chunks");
	        			add(chunksHeld);
	        			filesHeld = new DefaultMutableTreeNode("Files",true);
	        			
	        			add(filesHeld);
	        		}
	        	}
	        ));
	        tree.setRootVisible(false);
	        tree.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), null));
	        tree.setShowsRootHandles(true);
	        tree.setBounds(10, 77, 705, 243);
	        
	        JScrollPane qPane = new JScrollPane(tree,
	        	      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	        	      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        qPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), null));
	        qPane.setBounds(10, 77, 705, 243);
	        frmBackupService.getContentPane().add(qPane);
	        
	        JLabel lblBackupService = new JLabel("Backup Service");
	        lblBackupService.setFont(new Font("Tahoma", Font.BOLD, 17));
	        lblBackupService.setBounds(10, 11, 155, 36);
	        frmBackupService.getContentPane().add(lblBackupService);
	        
	        JButton btnNewButton = new JButton("Add file");
	        btnNewButton.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent arg0) {
	        		final JFileChooser fc = new JFileChooser();
	        		int returnVal = fc.showOpenDialog(frmBackupService);
	        		if (returnVal == JFileChooser.APPROVE_OPTION) {
	                    File file = fc.getSelectedFile();
	                    
	                   server.backupFile(file);
	                    
	        	}
	        	}
	        });
	        btnNewButton.setBounds(10, 331, 89, 23);
	        frmBackupService.getContentPane().add(btnNewButton);
	        
	        JButton btnRestore = new JButton("Restore");
	        btnRestore.setEnabled(false);
	        btnRestore.setBounds(102, 331, 89, 23);
	        frmBackupService.getContentPane().add(btnRestore);
	        
	        JButton btnDeleteAllChunks = new JButton("Delete All Chunks");
	        btnDeleteAllChunks.addActionListener(new ActionListener() {
	        	public void actionPerformed(ActionEvent arg0) {
	        		server.deleteData();
	        	}
	        });
	        btnDeleteAllChunks.setBounds(591, 331, 124, 23);
	        frmBackupService.getContentPane().add(btnDeleteAllChunks);
	        frmBackupService.setLocationRelativeTo(null);
	}
	
	
	private static class MyTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (node.getUserObject() instanceof String) {
                    // your root node, since you just put a String as a user obj                    
                   if (node.getUserObject().equals("Chunks")) setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
                  
                   if (node.getUserObject().equals("Files")) setIcon(UIManager.getIcon("FileView.directoryIcon"));
                   
                  if (node.getParent() != null) {
                	  if (((DefaultMutableTreeNode) node.getParent()).getUserObject().equals("Chunks")) setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
                	  if (((DefaultMutableTreeNode) node.getParent()).getUserObject().equals("Files")) setIcon(UIManager.getIcon("FileView.fileIcon"));
                      
                  }

                } 
                
            }

            return this;
        }

    }


	@Override
	public void updateChunks(Vector<Chunk> chunks) {
		chunksHeld.removeAllChildren();

			for (int i = 0; i < chunks.size(); i++) {
				chunksHeld.add(new DefaultMutableTreeNode(chunks.get(i).getName()));
			}
			
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
			model.reload(root);
	}

	@Override
	public void updateFiles(Vector<BackedFile> chunks) {
		// TODO Auto-generated method stub
		
	}
}
