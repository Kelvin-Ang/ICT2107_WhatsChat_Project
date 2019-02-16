

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.HashMap;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.Font;
import javax.swing.Box; // import the HashMap class



public class ChatApp extends JFrame {

	private JPanel contentPane;
	InetAddress multicastGroup = null;
	MulticastSocket multicastSocket = null; 
	private String userID = "Unknown";
	
	String GroupS = "228.1.1.1,AdminRoom";
	JList groups,onlineUsers;
	JTextArea messageTextArea; 
	JButton createGroupBtn,editGroupNameBtn,sendMessageBtn,registerUserBtn,LeaveGroupBtn,closeGroupBtn;
	JTextField registerUser_txt,createGroup_txt,editGroupName_txt,sendMessage_txt;
	
	
	

	

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
		
		registerUserBtn = new JButton("Register User");
		registerUserBtn.setBounds(23, 26, 138, 23);
		contentPane.add(registerUserBtn);
		
		registerUser_txt = new JTextField();
		registerUser_txt.setBounds(171, 27, 170, 20);
		contentPane.add(registerUser_txt);
		registerUser_txt.setColumns(10);
		
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
		
		closeGroupBtn = new JButton("Close Group");
		closeGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		closeGroupBtn.setBounds(711, 26, 143, 23);
		contentPane.add(closeGroupBtn);
		
		sendMessageBtn = new JButton("Send Message");
		sendMessageBtn.setBounds(726, 379, 128, 23);
		contentPane.add(sendMessageBtn);
		
		sendMessage_txt = new JTextField();
		sendMessage_txt.setColumns(10);
		sendMessage_txt.setBounds(10, 380, 706, 20);
		contentPane.add(sendMessage_txt);
		
		LeaveGroupBtn = new JButton("Leave Group");
		LeaveGroupBtn.setBounds(711, 128, 143, 23);
		contentPane.add(LeaveGroupBtn);
		connection();
	}
	
	
	
	public String IPincrease(String MaxIP) {
		System.out.println(MaxIP);
		String data[]; 
		data = MaxIP.split("\\.");
		if(Integer.parseInt(data[3]) == 255) {
			int newIP = Integer.parseInt(data[2])+1;
			
			String newIPAdd = data[0]+"."+data[1]+"."+String.valueOf(newIP)+"."+String.valueOf(1);
			return newIPAdd;
		}
		
		int newIP = Integer.parseInt(data[3])+1;
		String newIPAdd = data[0]+"."+data[1]+"."+data[2]+"."+String.valueOf(newIP);
		return newIPAdd;
	}
	
	public int getRoomCreated(String Groups,String roomName) {
		
		String[] data = Groups.split(",");
		
		for(int i = 1; i < data.length; i+=2) {
			if(data[i].equals(roomName)) {
				return i-1;
			}
		}
		return -1;
	}
	
	public String getIP(String Groups, int index) {
		String[] data = Groups.split(",");
		System.out.println(Groups);
		return data[index];
	}
	
	public String addAddress(String Groups,String newAddress) {
		Groups = Groups + "," +newAddress;
		return Groups;
	}
	
	
	public void connection() {
		try
		{
			
			multicastGroup =  InetAddress.getByName(getIP(GroupS,0));
			System.out.print(multicastGroup);
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(multicastGroup);
			
			String message = userID + " joined";
			
			byte[] buf = message.getBytes();
			
			DatagramPacket dgpConnected = new DatagramPacket(buf,buf.length,multicastGroup,6789);
			multicastSocket.send(dgpConnected);
			new Thread(new Runnable() {
				
				@Override 
				public void run() {
					byte buf1[] = new byte[1000];
					DatagramPacket dgpRecevied = new DatagramPacket(buf1,buf1.length);
					while(true) {
						try {
							multicastSocket.receive(dgpRecevied);
							byte[] receiveData = dgpRecevied.getData();
							int length = dgpRecevied.getLength();
							
							String msg = new String(receiveData,0,length);
							System.out.print("Message " + msg );
							System.out.print(GroupS+"#######");
							if(msg.subSequence(0, 3).equals("228")) {
								System.out.print("Scuccess");
								GroupS = msg;
								
							}
							
							messageTextArea.append(msg + "\n");
						}catch(IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}).start();
		}catch(IOException ex) {
			
		}
	}
}