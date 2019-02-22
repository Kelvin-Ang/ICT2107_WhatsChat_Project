
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
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
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
import javax.swing.ImageIcon;

public class ChatApp extends JFrame {

	private JPanel contentPane;
	InetAddress multicastGroup = null;
	MulticastSocket multicastSocket = null;
	private String userID = "Unknown";

	JList groups, onlineUsers;
	JTextArea messageTextArea;
	JButton createGroupBtn, editGroupNameBtn, sendMessageBtn, registerUserBtn;
	JTextField createGroup_txt, editGroupName_txt, sendMessage_txt;

	List<Group> groupList = new ArrayList<>();
	Group adminRoom = new Group("230.1.1.1", "AdminRoom");

	List<User> userList = new ArrayList<>();
	String onlineUser[] = { "" };

	String TestImageBase64 = "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";
	
	
	private JLabel imageLabel;
	private JButton btnLogOut;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatApp frame = new ChatApp();
					
					frame.setVisible(true);
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

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 880, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(229, 162, 178, 206);
		contentPane.add(scrollPane_1);

		groups = new JList();
		scrollPane_1.setViewportView(groups);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(450, 162, 404, 206);
		contentPane.add(scrollPane_2);

		messageTextArea = new JTextArea();
		scrollPane_2.setViewportView(messageTextArea);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 162, 178, 206);
		contentPane.add(scrollPane);

		onlineUsers = new JList(onlineUser);
		scrollPane.setViewportView(onlineUsers);

		JLabel lblOnlineUser = new JLabel("Online Users");
		lblOnlineUser.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOnlineUser.setBounds(22, 137, 95, 14);
		contentPane.add(lblOnlineUser);

		JLabel lblGroups = new JLabel("Groups");
		lblGroups.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblGroups.setBounds(229, 137, 95, 14);
		contentPane.add(lblGroups);

		JLabel lblConversations = new JLabel("Conversation");
		lblConversations.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblConversations.setBounds(450, 137, 95, 14);
		contentPane.add(lblConversations);
		
		

		
		
		
		

		registerUserBtn = new JButton("Register");
		registerUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			


//				if (!(checkUserAvailable(userList, registerUser_txt.getText()) == -1)) {
//					try {
//						String msg = "User Existed Choose New Name";
//						byte[] buf = msg.getBytes();
//						DatagramPacket dgp = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
//						multicastSocket.send(dgp);
//					} catch (IOException ex) {
//						ex.printStackTrace();
//					}
//					return;
//				}
//				User newUser = new User(registerUser_txt.getText());
//				userList.add(newUser);
//
//				try {
//					String msg = userID + " : is change to " + registerUser_txt.getText();
//					byte[] buf = msg.getBytes();
//					DatagramPacket dgp = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
//					multicastSocket.send(dgp);
//				} catch (IOException ex) {
//					ex.printStackTrace();
//				}
//
//				userID = registerUser_txt.getText();
//				onlineUser = addUser(onlineUser, userID);
//				onlineUsers = new JList(onlineUser);
//				scrollPane.setViewportView(onlineUsers);

			}
		});
		registerUserBtn.setBounds(23, 26, 138, 23);
		contentPane.add(registerUserBtn);

		createGroupBtn = new JButton("Create Group");
		createGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		createGroupBtn.setBounds(22, 60, 139, 23);
		contentPane.add(createGroupBtn);

		createGroup_txt = new JTextField();
		createGroup_txt.setColumns(10);
		createGroup_txt.setBounds(171, 63, 170, 20);
		contentPane.add(createGroup_txt);

		editGroupNameBtn = new JButton("Edit Group Name");
		editGroupNameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		editGroupNameBtn.setBounds(22, 94, 139, 23);
		contentPane.add(editGroupNameBtn);

		editGroupName_txt = new JTextField();
		editGroupName_txt.setColumns(10);
		editGroupName_txt.setBounds(171, 95, 170, 20);
		contentPane.add(editGroupName_txt);

		sendMessageBtn = new JButton("Send Message");
		sendMessageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msg = sendMessage_txt.getText();
					msg = userID + " : " + msg;
					byte[] buf = msg.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
					multicastSocket.send(dgpSend);
					System.out.println(multicastGroup);

				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		sendMessageBtn.setBounds(726, 379, 128, 23);
		contentPane.add(sendMessageBtn);

		sendMessage_txt = new JTextField();
		sendMessage_txt.setColumns(10);
		sendMessage_txt.setBounds(10, 380, 706, 20);
		contentPane.add(sendMessage_txt);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(186, 26, 138, 23);
		contentPane.add(btnLogin);
		
		JLabel lblUserName = new JLabel("User Name");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblUserName.setBounds(467, 26, 129, 18);
		contentPane.add(lblUserName);
		
		imageLabel = new JLabel("ImageLabel");
		imageLabel.setBounds(477, 58, 84, 68);
		contentPane.add(imageLabel);
		
		btnLogOut = new JButton("Log out");
		btnLogOut.setBounds(703, 26, 138, 23);
		contentPane.add(btnLogOut);

		//connection();
	}
}