
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JTextArea;


public class GroupController {
	
	// Declare UI variables
	JTextArea messageTextArea;
	
	// Declare value variables
	private InetAddress multicastGroup = null;
	private InetAddress multicastLobby = null;
	private MulticastSocket multicastSocket = null;
	private User currentUser;
	private List<User> globalUserList = new ArrayList<>();
	private static List<Group> globalGroupList = new ArrayList<>();
	Random rand = new Random();
	private ChatApp chatApp;
	
	// Declare constants for commands
	public static final String REQUEST_FOR_GROUPS = "RequestGroups";
	public static final String BROADCAST_GROUP_LIST = "BroadcastGroups";
	public static final String REQUEST_FOR_USERS = "RequestUsers";
	public static final String BROADCAST_USER_LIST = "BroadcastUsers";
	public static final String SEND_MESSAGE_TO_GROUP = "SendMessage";

	/**
	 * Constructor for Non-Login
	 * @param messageTextArea
	 */
	public GroupController(ChatApp chatApp) {
		this.chatApp = chatApp;
		messageTextArea = chatApp.getMessageTextArea();
		joinLobby();
	}
	
	/* TO-DO Login function to restore state by changing Controller's attributes */
	
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
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ois.close();
						}
						
						/**
						 * Determine the command and execute appropriate action
						 */
						switch(objectDataReceived.command) {
							case SEND_MESSAGE_TO_GROUP:
								// Only receieve packets from user's active group
								if (objectDataReceived.multicastGroupIP.equals(InetAddress.getByName(currentUser.currentIP))) {
									for(String data : objectDataReceived.stringData) {
										messageTextArea.append(data + "\n");
									}
								}
								break;
							case BROADCAST_GROUP_LIST:
								// Update global Group list to latest
								globalGroupList = new ArrayList<Group>(objectDataReceived.groupData);
								break;
							case REQUEST_FOR_GROUPS:
								// All clients to send out their global group list
								if(!currentUser.getUserName().equals(objectDataReceived.sender)) {
									sendGroupData(globalGroupList);
								}	
								break;
							case BROADCAST_USER_LIST:
								// Update global User list to latest
								globalUserList = new ArrayList<User>(objectDataReceived.userData);
								if (globalUserList.size() > 0) {
									chatApp.convertUserListToListModel();
								}
//								System.out.println("Printing out updated user list" + globalUserList.toString());
//								System.out.println("Printing out updated group list" + globalGroupList.toString());
								break;
							case REQUEST_FOR_USERS:
								// All clients to send out their global user list
								if(!currentUser.getUserName().equals(objectDataReceived.sender)) {
									sendUserData(globalUserList);
								}
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
		System.out.println("================ Send message function called ================");
		System.out.println("Displaying current globalList" + globalGroupList.toString());
		try {
			// Initialise SendData class
			DataSend sendingData = new DataSend();
			List<String> stringData = new ArrayList<>();

			// Packaging Datagram Packet into common language
			message = source + " : " + message;
			stringData.add(message);
			sendingData.setStringData(stringData);
			sendingData.setCommand(SEND_MESSAGE_TO_GROUP);
			sendingData.setSender(currentUser.userName);
			sendingData.setMulticastGroupIP(multicastGroup);
			byte[] buf = toByte(sendingData);
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpSend);
			if (!source.equals("System Error")) {
				// Storing every message to keep track of last ten messages
				storeGroupMessages(currentUser.currentIP, message);
			}
			System.out.println(multicastGroup);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to broadcast current user list to all clients
	 * @param groupList
	 */
	public void sendUserData(List<User> userList) {
		try {
			DataSend sendingData = new DataSend();
			sendingData.setUserData(userList);
			sendingData.setCommand(BROADCAST_USER_LIST);
			sendingData.setSender(currentUser.userName);
			sendingData.setMulticastGroupIP(multicastLobby);
			byte[] buf = toByte(sendingData);	
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastLobby, 6789);
			multicastSocket.send(dgpSend);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to request for current group list from all clients
	 * @param groupList
	 */
	public void getUserData() {
		System.out.println("requesting user data");
		try {
			DataSend sendingData = new DataSend();
			sendingData.setCommand(REQUEST_FOR_USERS);
			sendingData.setSender(currentUser.userName);
			sendingData.setMulticastGroupIP(multicastLobby);
			byte[] buf = toByte(sendingData);	
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastLobby, 6789);
			multicastSocket.send(dgpSend);
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
			sendingData.setCommand(BROADCAST_GROUP_LIST);
			sendingData.setSender(currentUser.userName);
			sendingData.setMulticastGroupIP(multicastLobby);
			byte[] buf = toByte(sendingData);	
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastLobby, 6789);
			multicastSocket.send(dgpSend);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to request for current group list from all clients
	 * @param groupList
	 */
	public void getGroupData() {
		System.out.println("requesting groups data");
		try {
			DataSend sendingData = new DataSend();
			sendingData.setCommand(REQUEST_FOR_GROUPS);
			sendingData.setSender(currentUser.userName);
			sendingData.setMulticastGroupIP(multicastLobby);
			byte[] buf = toByte(sendingData);	
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastLobby, 6789);
			multicastSocket.send(dgpSend);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function for every client to join Lobby
	 */
	public void joinLobby() {
		try {
			Group adminRoom = new Group("230.1.1.1", "Lobby");
			// Join Lobby
			multicastLobby = InetAddress.getByName(adminRoom.getIPAddress());
			multicastGroup = InetAddress.getByName(adminRoom.getIPAddress());
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(multicastLobby);
			currentUser = new User("Anonymous" + rand.nextInt(9999999), "230.1.1.1");
			
			newThread();
			// Request group data from other clients
			getGroupData();
			// Request user data from other clients
			getUserData();
			// globalGroupList was never used before
			if (globalGroupList == null || globalGroupList.isEmpty()) {
				// Set lobby as User's active group
				//currentUser = new User("Anonymous ", "230.1.1.1");
				// Add User into the group
				adminRoom.addUser(currentUser);
				// Adding the joined group into Global Group List held by all clients
				globalGroupList.add(adminRoom);
				// Store the joined group into the current user's Group List
				currentUser.groupList.add(adminRoom.getIPAddress());
				// Send Datagram Packet for joining
//				sendMessage(currentUser.getUserName(), "has joined " + adminRoom.getGroupName());
				
			} else {
				
			}
			
			new java.util.Timer().schedule( 
			        new java.util.TimerTask() {
			            @Override
			            public void run() {
			            	for(Group group : globalGroupList) {
			    				if(group.IPAddress.equals("230.1.1.1")) {
			    					displayGroupMessages(group);
			    				}
			    			}
			            }
			        }, 
			        1000 
			);
			
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to Join Group
	 */
	public void joinGroup(Group groupToJoin) {
		try {
			// Join group by IP Address and port
			multicastGroup = InetAddress.getByName(groupToJoin.IPAddress);
			multicastSocket.joinGroup(multicastGroup);
			// Set joined group as User's active group
			currentUser.setCurrentIP(groupToJoin.IPAddress);
			// Add User into the group
			groupToJoin.addUser(currentUser);
			// Adding the joined group into Global Group List held by all clients
			globalGroupList.add(groupToJoin);
			// Store the joined group into the current user's Group List
			currentUser.groupList.add(groupToJoin.getIPAddress());
			// Send Datagram Packet for joining
			sendMessage(currentUser.getUserName(), "has joined " + groupToJoin.getGroupName());
			newThread();
			// Broadcast updates of groupList to all clients
			sendGroupData(globalGroupList);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function to create group and append creator into group
	 * @param groupName
	 */
	public void createGroup(String groupName) {
		// Room already exist return user's current list without room creation
		if (!(getRoomCreated(globalGroupList, groupName) == -1)) {
			sendMessage("System Error", "Group name \"" + groupName + "\" is already taken");
		} else {
			// Room does not exist, return user's list with room created
			Group newGroup = new Group(IPincrease(globalGroupList.get(globalGroupList.size() - 1).IPAddress),groupName);
			joinGroup(newGroup);
		}	
	}
	
	/**
	 * Function to convert Group list into ListModel for JList
	 * @return
	 */
	public DefaultListModel<Group> convertGroupListToListModel(){
		// Initialize ListModel
		DefaultListModel<Group> currentGroupList = new DefaultListModel<Group>();
		// Populate ListModel with the current user's group list
		for(String userGroupIPAddress : currentUser.groupList) {
			for(Group group : globalGroupList) {
				// If the user's group list has same IP as group
				if (userGroupIPAddress.equals(group.getIPAddress())) {
					// Group is user's active group
					if (group.getIPAddress().equals(currentUser.getCurrentIP())) {
						// Append a <Active> tag at the back of the active group
						Group tempGroup = new Group(group.IPAddress, group.getGroupName() + " <Active>");
						currentGroupList.addElement(tempGroup);
					} else {
						currentGroupList.addElement(group);
					}
				}
			}
		}
		return currentGroupList;
	}
	
	/**
	 * Function to convert User list into ListModel for JList
	 * @return
	 */
	public DefaultListModel<User> convertUserListToListModel(){
		// Initialize ListModel
		DefaultListModel<User> currentUserList = new DefaultListModel<User>();
		// Populate ListModel with the current user's group list
		for (User user : globalUserList) {
			currentUserList.addElement(user);
		}
		return currentUserList;
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
	 * Function to store last ten message in group chat
	 * @param multiCastGroup
	 * @param message
	 * @throws UnknownHostException 
	 */
	public void storeGroupMessages(String groupIpAddress,String message) {
		
		//check for the Group in globalGroupList and update the group messages
		for(Group group : globalGroupList) {
			if(group.IPAddress.equals(groupIpAddress)) {
				System.out.println(groupIpAddress+" located");
				// Ensure that only 10 messages are stored
				if(group.lastTenMessage.size()>9) {
					group.lastTenMessage.remove(0);
				}
				group.lastTenMessage.add(message);
				// Broadcast updates of groupList to all clients
				sendGroupData(globalGroupList);
			}
		}
	}
	
	/**
	 * Function to Display messages in group when join
	 * @param group
	 */	
	public void displayGroupMessages(Group group) {
		messageTextArea.setText(null);
		for(String message : group.lastTenMessage) {
			messageTextArea.append(message+ "\n");
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
	
	public Group convertIPAddressToGroup(String IPAddress) {
		Group convertedGroup = null;
		for (Group group : globalGroupList) {
			if (group.getIPAddress().equals(IPAddress)) {
				convertedGroup = new Group(group);
			}
		}
		return convertedGroup;
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
	
	/**
	 * Function to check if user is in list
	 * @param userList
	 * @param user
	 * @return
	 */
	public boolean isInUserList(List<User> userList, User user) {
		for(User tempuser : userList) {
			if (tempuser.getUserName().equals(user.getUserName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Function to check if group is in list
	 * @param groupList
	 * @param group
	 * @return
	 */
	public boolean isInGroupList(List<Group> groupList, Group group) {
		for(Group tempgroup : groupList) {
			if (tempgroup.getIPAddress().equals(group.getIPAddress())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * START OF GETTERS AND SETTERS
	 */
	public User getCurrentUser() {
		return currentUser;
	}
	
	public void setCurrentUser(User user) {
		this.currentUser = user;
	}
	
	public List<User> getGlobalUserList() {
		return globalUserList;
	}
	public List<Group> getGlobalGroupList() {
		return globalGroupList;
	}
	/**
	 * END OF GETTERS AND SETTERS
	 */
}
