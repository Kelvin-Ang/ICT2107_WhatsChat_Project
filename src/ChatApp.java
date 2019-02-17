
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
import javax.swing.ListModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.Font;
import javax.swing.Box; // import the HashMap class

public class ChatApp extends JFrame {

	private JPanel contentPane;
	InetAddress multicastGroup = null;
	MulticastSocket multicastSocket = null;
	private String userID = "Unknown";

	JList groups, onlineUsers;
	JTextArea messageTextArea;
	JButton createGroupBtn, editGroupNameBtn, sendMessageBtn, registerUserBtn, LeaveGroupBtn, closeGroupBtn;
	JTextField registerUser_txt, createGroup_txt, editGroupName_txt, sendMessage_txt;

	List<Group> groupList = new ArrayList<>();
	Group adminRoom = new Group("230.1.1.1", "AdminRoom");
	
	
	List<User> userList = new ArrayList<>();
	String onlineUser[] = {""};

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

		registerUserBtn = new JButton("Register User");
		registerUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(!(checkUserAvailable(userList,registerUser_txt.getText()) == -1)) {
					try {
						String msg = "User Existed Choose New Name";
						byte[] buf = msg.getBytes();
						DatagramPacket dgp = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
						multicastSocket.send(dgp);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					return;
				}
				User newUser = new User(registerUser_txt.getText());
				userList.add(newUser);
				
				try {
					String msg = userID + " : is change to " + registerUser_txt.getText() ; 
					byte[] buf = msg.getBytes();
					DatagramPacket dgp = new DatagramPacket(buf,buf.length,multicastGroup,6789);
					multicastSocket.send(dgp);
				}
				catch(IOException ex) {
					ex.printStackTrace();
				}

				userID = registerUser_txt.getText();
				onlineUser = addUser(onlineUser,userID);
				onlineUsers = new JList(onlineUser);
				scrollPane.setViewportView(onlineUsers);
				
			}
		});
		registerUserBtn.setBounds(23, 26, 138, 23);
		contentPane.add(registerUserBtn);

		registerUser_txt = new JTextField();
		registerUser_txt.setBounds(171, 27, 170, 20);
		contentPane.add(registerUser_txt);
		registerUser_txt.setColumns(10);

		createGroupBtn = new JButton("Create Group");
		createGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {

					if (!(getRoomCreated(groupList,createGroup_txt.getText()) == -1)) {
						try {
							String msg = "Room Created";
							byte[] buf = msg.getBytes();
							DatagramPacket dgp = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
							multicastSocket.send(dgp);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
						return;
					}
					
					Group newGroup = new Group(IPincrease(groupList.get(groupList.size()-1).IPAddress),createGroup_txt.getText());
					groupList.add(newGroup);

					try {
						
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
					    ObjectOutputStream oos = new ObjectOutputStream(bos);
					    oos.writeObject(groupList);
					    byte[] buf = bos.toByteArray();
						
						DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
						multicastSocket.send(dgpSend);
						System.out.println(multicastGroup);

					} catch (IOException ex) {
						ex.printStackTrace();
					}

					multicastGroup = InetAddress.getByName(groupList.get(getRoomCreated(groupList,createGroup_txt.getText())).IPAddress);
					System.out.print(multicastGroup);
					// multicastSocket = new MulticastSocket(6789);

					multicastSocket.joinGroup(multicastGroup);

					String message = userID + " joined";

					byte[] buf = message.getBytes();

					DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
					multicastSocket.send(dgpConnected);
					new Thread(new Runnable() {

						@Override
						public void run() {
							byte buf1[] = new byte[1000];
							DatagramPacket dgpRecevied = new DatagramPacket(buf1, buf1.length);
							while (true) {
								try {
									multicastSocket.receive(dgpRecevied);
									byte[] receiveData = dgpRecevied.getData();
									int length = dgpRecevied.getLength();

									String msg = new String(receiveData, 0, length);
									
									if(msg.substring(0,3).matches("^[a-zA-Z0-9_]*$")) {
										messageTextArea.append(msg + "\n");
									}
								} catch (IOException ex) {
									ex.printStackTrace();
								}
							}
						}
					}).start();
				

				} catch (IOException ex) {
					ex.printStackTrace();
				} 

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
		sendMessageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msg = sendMessage_txt.getText();
					msg = userID + " : " + msg;
					byte[] buf = msg.getBytes();
					DatagramPacket dgpSend = new DatagramPacket(buf,buf.length,multicastGroup,6789);
					multicastSocket.send(dgpSend);
					System.out.println(multicastGroup);
					
				}catch(IOException ex) {
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

		LeaveGroupBtn = new JButton("Leave Group");
		LeaveGroupBtn.setBounds(711, 128, 143, 23);
		contentPane.add(LeaveGroupBtn);
		connection();
	}

	public String IPincrease(String MaxIP) {
		System.out.println(MaxIP);
		String data[];
		data = MaxIP.split("\\.");
		if (Integer.parseInt(data[3]) == 255) {
			int newIP = Integer.parseInt(data[2]) + 1;

			String newIPAdd = data[0] + "." + data[1] + "." + String.valueOf(newIP) + "." + String.valueOf(1);
			return newIPAdd;
		}

		int newIP = Integer.parseInt(data[3]) + 1;
		String newIPAdd = data[0] + "." + data[1] + "." + data[2] + "." + String.valueOf(newIP);
		return newIPAdd;
	}

	public int getRoomCreated(List<Group> groupList, String roomName) {
		for (int x = 0; x < groupList.size(); x++) {
			if (groupList.get(x).groupName.equals(roomName)) {
				return x;
			}
		}
		return -1;

	}

	public List<Group> addAddress(List<Group> groupList, Group newAddress) {
		groupList.add(newAddress);
		return groupList;
	}
	
	
	public int checkUserAvailable(List<User> userList,String name) {
		for (int x = 0; x < userList.size(); x++) {
			if (userList.get(x).userName.equals(name)) {
				return x;
			}
		}
		return -1;
	}
	
	public String[] addUser(String[] currentList,String newUser) {
		int currentSize = currentList.length;
	    int newSize = currentSize + 1;
	    String[] tempArray = new String[ newSize ];
	    for (int i=0; i < currentSize; i++)
	    {
	        tempArray[i] = currentList [i];
	    }
	    tempArray[newSize- 1] = newUser;
	    return tempArray; 
	}

	public void connection() {
		try {
			groupList.add(adminRoom);
			multicastGroup = InetAddress.getByName(groupList.get(0).IPAddress);
			System.out.print(multicastGroup);
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(multicastGroup);

			String message = userID + " joined";

			byte[] buf = message.getBytes();

			DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpConnected);
			new Thread(new Runnable() {

				@Override
				public void run() {
					byte buf1[] = new byte[1000];
					DatagramPacket dgpRecevied = new DatagramPacket(buf1, buf1.length);
					while (true) {
						try {
							multicastSocket.receive(dgpRecevied);
							byte[] receiveData = dgpRecevied.getData();
							
							int length = dgpRecevied.getLength();

							String msg = new String(receiveData, 0, length);

							if(msg.substring(0,3).matches("^[a-zA-Z0-9_]*$")) {
								messageTextArea.append(msg + "\n");
							}
							
							
							ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receiveData));
							try {
							    groupList = (List<Group>) ois.readObject();
							   
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
							    ois.close();
							}
							
							
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}).start();
		} catch (IOException ex) {

		}
	}
}