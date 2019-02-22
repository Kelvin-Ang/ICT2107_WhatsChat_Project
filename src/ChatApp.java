
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

	JList groups, onlineUsers;
	static JTextArea messageTextArea;
	JButton createGroupBtn, editGroupNameBtn, sendMessageBtn, registerUserBtn;
	JTextField createGroup_txt, editGroupName_txt, sendMessage_txt;

	
	
	private JLabel imageLabel;
	private JButton btnLogOut;

	GroupController groupController = new GroupController(messageTextArea);
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					messageTextArea = new JTextArea();
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

		
		scrollPane_2.setViewportView(messageTextArea);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 162, 178, 206);
		contentPane.add(scrollPane);

		onlineUsers = new JList();
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
				groupController.sendMessage(sendMessage_txt.getText());
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
		lblUserName.setBounds(551, 26, 129, 18);
		contentPane.add(lblUserName);
		
		imageLabel = new JLabel("ImageLabel");
		imageLabel.setBounds(551, 58, 84, 68);
		contentPane.add(imageLabel);
		
		btnLogOut = new JButton("Log out");
		btnLogOut.setBounds(703, 26, 138, 23);
		contentPane.add(btnLogOut);

		groupController.connection(); 
	}
}