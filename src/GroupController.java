import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class GroupController {
	
	// Declare UI variables
	JTextArea messageTextArea;
	
	// Declare value variables
	private InetAddress multicastGroup = null;
	private InetAddress multicastLobby = null;
	private MulticastSocket multicastSocket = null;
	private User currentUser;
	private Group adminRoom;
	private List<User> globalUserList = new ArrayList<>();
	private List<Group> globalGroupList = new ArrayList<>();
	private final String userID = "anonymous";

	/**
	 * Constructor for Non-Login
	 * @param messageTextArea
	 */
	public GroupController(JTextArea messageTextArea) {
		this.messageTextArea = messageTextArea;
		adminRoom = new Group("230.1.1.1", "Lobby");
		globalGroupList.add(adminRoom); // First element in List is always 230.1.1.1
		currentUser = new User(userID, "230.1.1.1");
		try {
			multicastLobby = InetAddress.getByName("230.1.1.1");
			multicastSocket = new MulticastSocket(6789);
		} catch (IOException e) {
			e.printStackTrace();
		}
		joinGroup(globalGroupList.get(0)); // Controller to join adminRoom
	}
	
	/* TO-DO Login function to restore state by changing Controller's attributes */
	
	/**
	 * Getter for Current User
	 * @return
	 */
	public User getCurrentUser() {
		return currentUser;
	}
	
	/**
	 * Listener for Datagram Packet
	 */
	public void newThread() {
		new Thread(new Runnable() {
			// Initialise DataSend class
			DataSend objectDataReceived;
			@Override
			public void run() {
				byte buf1[] = new byte[10000];
				DatagramPacket dgpRecevied = new DatagramPacket(buf1, buf1.length);
				while (true) {
					try {
						// Receive Datagram Packet
						multicastSocket.receive(dgpRecevied);
						byte[] receiveData = dgpRecevied.getData();
						ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receiveData));
						// Convert Datagram Packet to DataSend object
						try {
							objectDataReceived = (DataSend) ois.readObject();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} finally {
							ois.close();
						}
						
						/**
						 * Determine the command and execute appropriate action
						 */
						switch(objectDataReceived.command) {
							case "Send":
								// Only receieve packets from user's active group
								if (objectDataReceived.multicastGroupIP.equals(InetAddress.getByName(currentUser.currentIP))) {
									for(String data : objectDataReceived.stringData) {
										messageTextArea.append(data + "\n");
									}
								}
								break;
							case "BroadcastGroup":
								globalGroupList = objectDataReceived.groupData;
								break;
							default:
								break;
						}						
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * Function to send a Datagram Packet containing "Send" message over
	 * @param message
	 */
	public void sendMessage(String source, String message) {
		try {
			// Initialise SendData class
			DataSend sendingData = new DataSend();
			List<String> stringData = new ArrayList<>();

			// Packaging Datagram Packet into common language
			message = source + " : " + message;
			stringData.add(message);
			sendingData.setStringData(stringData);
			sendingData.setCommand("Send");
			sendingData.setMulticastGroupIP(multicastGroup);
			byte[] buf = toByte(sendingData);
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpSend);
			System.out.println(multicastGroup);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to broadcast current group list to all clients
	 * @param groupList
	 */
	public void sendGroupData(List<Group> groupList) {
		try {
			DataSend sendingData = new DataSend();
			sendingData.setGroupData(groupList);
			sendingData.setCommand("BroadcastGroup");
			byte[] buf = toByte(sendingData);	
			sendingData.setMulticastGroupIP(multicastLobby);
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastLobby, 6789);
			multicastSocket.send(dgpSend);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Initialize connection for constant broadcasting and send message for joining adminRoom
	 */
	public void joinGroup(Group groupToJoin) {
		try {
			// Join group by IP Address and port
			multicastGroup = InetAddress.getByName(groupToJoin.IPAddress);
			multicastSocket.joinGroup(multicastGroup);
			// Adding the joined group into Global Group List held by all clients
			globalGroupList.add(groupToJoin);
			// Store the joined group into the current user's Group List
			currentUser.groupList.add(groupToJoin);
			// Set joined group as User's active group
			currentUser.setCurrentIP(groupToJoin.IPAddress);
			// Send Datagram Packet for joining
			sendMessage(currentUser.getUserName(), "has joined " + groupToJoin.getGroupName());
			newThread();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to create group
	 * @param groupName
	 */
	public void createGroup(String groupName) {
		// Room already exist return user's current list without room creation
		if (!(getRoomCreated(globalGroupList, groupName) == -1)) {
			sendMessage("System Error", "Room name already taken");
		} else {
			// Room does not exist, return user's list with room created
			Group newGroup = new Group(IPincrease(globalGroupList.get(globalGroupList.size() - 1).IPAddress),groupName);
			joinGroup(newGroup);
			// Broadcast the creation of a new group to all clients
			sendGroupData(globalGroupList);
		}	
	}
	
	/**
	 * Function to convert Group list into ListModel for JList
	 * @return
	 */
	public DefaultListModel<Group> getCurrentUserGroupList(){
		// Initialize ListModel
		DefaultListModel<Group> currentGroupList = new DefaultListModel<Group>();
		// Populate ListModel with the current user's group list
		for(Group groups : currentUser.groupList) {
			// Append a <Active> tag at the back of the active group
			if (groups.IPAddress.equals(currentUser.currentIP)) {
				Group tempGroup = new Group(groups.IPAddress, groups.getGroupName() + " <Active>");
				currentGroupList.addElement(tempGroup);
			} else {
				currentGroupList.addElement(groups);
			}
		}
		return currentGroupList;
	}
	
	/**
	 * Function to increment IP to unique IP
	 * @param MaxIP
	 * @return
	 */
	public String IPincrease(String MaxIP) {
		String data[];
		data = MaxIP.split("\\.");
		// Fourth Octet reaches 255, increment third octet
		if (Integer.parseInt(data[3]) == 255) {
			int newIP = Integer.parseInt(data[2]) + 1;
			String newIPAdd = data[0] + "." + data[1] + "." + String.valueOf(newIP) + "." + String.valueOf(1);
			return newIPAdd;
		} else {
			// Increment fourth octet
			int newIP = Integer.parseInt(data[3]) + 1;
			String newIPAdd = data[0] + "." + data[1] + "." + data[2] + "." + String.valueOf(newIP);
			return newIPAdd;
		}	
	}

	/**
	 * Function to convert object to bytes for transmission
	 * @param datasend
	 * @return bytes ready to transmit
	 */
	public byte[] toByte(DataSend datasend) {
		byte[] buf = new byte[10000];
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(datasend);
			buf = bos.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return buf;
	}
	
	/**
	 * Function to get the position of any room
	 * @param groupList
	 * @param roomName
	 * @return Position of room, return -1 if room was never created
	 */
	public int getRoomCreated(List<Group> groupList, String roomName) {
		for (int x = 0; x < groupList.size(); x++) {
			if (groupList.get(x).groupName.equals(roomName)) {
				return x;
			}
		}
		return -1;
	}
	
	/**
	 * Function to add user into List of User
	 * @param currentList
	 * @param newUser
	 * @return
	 */
	public List<User> addUser(List<User> currentList, User newUser) {
		currentList.add(newUser);
		return currentList;
	}
	
	/**
	 * Function to remove user from List of User
	 * @param currentList
	 * @param oldUser
	 * @return
	 */
	public List<User> removeUser(List<User> currentList, User oldUser) {
		//loop through current user list to find user
		for (int i = 0; i < currentList.size(); i++) {
			if (currentList.get(i).getUserName().equals(oldUser.getUserName())) {
				currentList.remove(i);
			}
		}
		return currentList;
	}
}
