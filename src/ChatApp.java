import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.Box; // import the HashMap class
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

public class ChatApp extends JFrame {

	// Declare UI variables
	private JPanel contentPane;
	JList<Group> onGoingGroups;
	JList<User> onlineUsers;
	static JTextArea messageTextArea;
	JButton createGroupBtn, sendMessageBtn, registerUserBtn;
	JTextField createGroup_txt, sendMessage_txt;
	private JLabel imageLabel, lblUserName;
	
	// Declare value variables
	GroupController groupController;
	static ChatApp frame;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
					messageTextArea = new JTextArea();
					frame = new ChatApp();
					frame.setVisible(true);
					frame.setTitle("WhatsChat");
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatApp() {
		
		// Implement controller into the client's application
		groupController = new GroupController(messageTextArea);
		
		/**
		 * Start of User Interface
		 */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 880, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 120, 180, 250);
		contentPane.add(scrollPane);
		onlineUsers = new JList<>();
		scrollPane.setViewportView(onlineUsers);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(220, 120, 180, 250);
		contentPane.add(scrollPane_1);
		onGoingGroups = new JList<Group>(groupController.getCurrentUserGroupList());
		onGoingGroups.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("static-access")
		    public void mouseClicked(MouseEvent evt) {
		        JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            // Double-click detected
		            int index = list.locationToIndex(evt.getPoint());
			    	GroupInformation groupInformation = new GroupInformation();
			    	groupInformation.getObj().setGroupController(groupController);
			    	groupInformation.getObj().setCurrentGroup(groupController.getCurrentUser().getGroupList().get(index));
			    	groupInformation.getObj().setVisible(true);
			    	groupInformation.getObj().setTitle(groupController.getCurrentUser().getGroupList().get(index).getGroupName() + " Information");
		        }
		    }
		});
		scrollPane_1.setViewportView(onGoingGroups);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(430, 120, 420, 250);
		contentPane.add(scrollPane_2);
		scrollPane_2.setViewportView(messageTextArea);
		DefaultListModel<User> model = new DefaultListModel<>();
		User u1 = new User("Darren", "230.1.1.1");
		User u2 = new User("Ziyi", "230.1.1.1");
		User u3 = new User("Kelvin", "230.1.1.1");
		model.addElement(u1);
		model.addElement(u2);
		model.addElement(u3);

		JLabel lblOnlineUser = new JLabel("Online Users");
		lblOnlineUser.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOnlineUser.setBounds(10, 90, 180, 25);
		contentPane.add(lblOnlineUser);

		JLabel lblGroups = new JLabel("Groups");
		lblGroups.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblGroups.setBounds(220, 90, 180, 25);
		contentPane.add(lblGroups);

		JLabel lblConversations = new JLabel("Conversation");
		lblConversations.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblConversations.setBounds(430, 90, 420, 25);
		contentPane.add(lblConversations);
		
		lblUserName = new JLabel("User Name");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblUserName.setBounds(582, 34, 129, 18);
		contentPane.add(lblUserName);
		
		imageLabel = new JLabel("ImageLabel");
		imageLabel.setBounds(431, 10, 84, 68);
		contentPane.add(imageLabel);

		registerUserBtn = new JButton("Register");
		registerUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Register register = new Register(lblUserName, imageLabel);
				register.setVisible(true);
			}
		});
		registerUserBtn.setBounds(10, 10, 150, 25);
		contentPane.add(registerUserBtn);

		createGroupBtn = new JButton("Create Group");
		createGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				groupController.createGroup(createGroup_txt.getText());
				onGoingGroups.setModel(groupController.getCurrentUserGroupList());
			}
		});
		createGroupBtn.setBounds(10, 50, 150, 25);
		contentPane.add(createGroupBtn);

		createGroup_txt = new JTextField();
		createGroup_txt.setColumns(10);
		createGroup_txt.setBounds(170, 50, 170, 25);
		contentPane.add(createGroup_txt);

		sendMessageBtn = new JButton("Send Message");
		sendMessageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				groupController.sendMessage(groupController.getCurrentUser().getUserName(), sendMessage_txt.getText());
			}
		});
		sendMessageBtn.setBounds(722, 380, 128, 25);
		contentPane.add(sendMessageBtn);

		sendMessage_txt = new JTextField();
		sendMessage_txt.setColumns(10);
		sendMessage_txt.setBounds(10, 380, 706, 25);
		contentPane.add(sendMessage_txt);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Login login = new Login(lblUserName, imageLabel);
				login.setVisible(true);
			}
		});
		btnLogin.setBounds(170, 10, 150, 25);
		contentPane.add(btnLogin);
		/**
		 * End of User Interface
		 */
	}
}
