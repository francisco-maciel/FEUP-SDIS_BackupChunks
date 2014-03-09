package ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.TextArea;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.ScrollPane;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class VisualInterface {

	private JFrame frmBackupService;
	private JTextArea mc_textField;
	private JTextArea mdb_textField;
	private JTextArea mdr_textField;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
		frmBackupService.setTitle("Backup Service");
		frmBackupService.setBounds(100, 100, 829, 647);
		frmBackupService.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frmBackupService.getContentPane().setLayout(null);
		
		
		  mc_textField = new JTextArea();
		  mc_textField.setEditable(false);
		  mc_textField.setLineWrap(true);


        JScrollPane scroll = new JScrollPane(mc_textField);
        scroll.setEnabled(false);
        scroll.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scroll.setBounds(219, 58, 584, 132);                
	    frmBackupService.getContentPane().add(scroll);
	        
	    mdb_textField = new JTextArea();
	    mdb_textField.setEditable(false);
	    mdb_textField.setLineWrap(true);


        JScrollPane scroll_2 = new JScrollPane(mdb_textField);
        scroll_2.setEnabled(false);
        scroll_2.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

        scroll_2.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scroll_2.setBounds(219, 220, 584, 123);                     // <-- THIS

        frmBackupService.getContentPane().add(scroll_2);
		        
        mdr_textField = new JTextArea();
        mdr_textField.setEditable(false);
        mdr_textField.setLineWrap(true);


        JScrollPane scroll_3 = new JScrollPane(mdr_textField);
        scroll_3.setEnabled(false);
        scroll_3.setHorizontalScrollBarPolicy ( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

        scroll_3.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scroll_3.setBounds(219, 370, 584, 148);                     // <-- THIS

	        frmBackupService.getContentPane().add(scroll_3);
	        
	        JLabel lblNewLabel = new JLabel("Multicast Control Channel");
	        lblNewLabel.setBounds(221, 33, 150, 14);
	        frmBackupService.getContentPane().add(lblNewLabel);
	        
	        JLabel lblFsd = new JLabel("Multicast Backup Channel");
	        lblFsd.setBounds(221, 201, 150, 14);
	        frmBackupService.getContentPane().add(lblFsd);
	        
	        JLabel lblMulticastRestoreChannel = new JLabel("Multicast Restore Channel");
	        lblMulticastRestoreChannel.setBounds(219, 354, 188, 14);
	        frmBackupService.getContentPane().add(lblMulticastRestoreChannel);
	        
	        JTree tree = new JTree();
	
	        tree.setModel(new DefaultTreeModel(
	        	new DefaultMutableTreeNode("Backup") {
	        		{
	        			DefaultMutableTreeNode node_1;
	        			node_1 = new DefaultMutableTreeNode("Chunks");
	        				node_1.add(new DefaultMutableTreeNode("chunk1"));
	        				node_1.add(new DefaultMutableTreeNode("chunk2"));
	        				node_1.add(new DefaultMutableTreeNode("chunk3"));
	        				node_1.add(new DefaultMutableTreeNode("chunk4"));
	        			add(node_1);
	        			node_1 = new DefaultMutableTreeNode("Files");
	        				node_1.add(new DefaultMutableTreeNode("a.txt"));
	        				node_1.add(new DefaultMutableTreeNode("b.doc"));
	        				node_1.add(new DefaultMutableTreeNode("c.jpg"));
	        			add(node_1);
	        			
	        		}
	        	}
	        ));
	        tree.setRootVisible(false);
	        tree.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), null));
	        tree.setShowsRootHandles(true);
	        tree.setBounds(10, 63, 186, 280);
	        frmBackupService.getContentPane().add(tree);
	        
	        JLabel lblBackupService = new JLabel("Backup Service");
	        lblBackupService.setFont(new Font("Tahoma", Font.BOLD, 17));
	        lblBackupService.setBounds(10, 11, 155, 36);
	        frmBackupService.getContentPane().add(lblBackupService);
	        
	        JButton btnNewButton = new JButton("Add file");
	        btnNewButton.setBounds(10, 350, 89, 23);
	        frmBackupService.getContentPane().add(btnNewButton);
	        
	        JButton btnRestore = new JButton("Restore");
	        btnRestore.setEnabled(false);
	        btnRestore.setBounds(107, 350, 89, 23);
	        frmBackupService.getContentPane().add(btnRestore);
	        frmBackupService.setLocationRelativeTo(null);
	}
}
