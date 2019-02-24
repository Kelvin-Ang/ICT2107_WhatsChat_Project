
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	/**
	 * Constructor for Non-Login
	 * @param messageTextArea
	 */
	public GroupController(JTextArea messageTextArea) {
		this.messageTextArea = messageTextArea;
		joinLobby();
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
						} catch (Exception e) {
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
							case "BroadcastGroups":
//								
//								System.out.println("recieving groups data");
//								System.out.println("New1 ## "+globalGroupList.toString());
//								// Remove all duplicated in received group from own globalGroupList
//								objectDataReceived.groupData.removeAll(globalGroupList);
//								
//								
//								System.out.println("New2 ## "+objectDataReceived.groupData.toString());
//								// Merge the rest of new received groups into own globalGroupList
//								globalGroupList.addAll(objectDataReceived.groupData);
								List<Group> tempGroupList = new ArrayList<>();
								System.out.println("New1 ## "+globalGroupList.toString());
								// Compare globalGroupList with receivedGroupList to update to the latest
								for(Group receivedGroup : objectDataReceived.groupData) {
									for(Group currentGroup : globalGroupList) {
										if(receivedGroup.groupName.equals(currentGroup.groupName)) {
											System.out.println("New adding "+tempGroupList.toString());
											break;
										}
									}
									tempGroupList.add(receivedGroup);
									System.out.println("New adding after not same "+tempGroupList.toString());
									
								}
								
								globalGroupList = new ArrayList<Group>(tempGroupList);;
								System.out.println("New2 ## "+tempGroupList.toString());		
								break;
							case "RequestGroups":
								// All clients to send out their global group list
								if(!currentUser.userName.equals(objectDataReceived.sender)) {
									sendGroupData(globalGroupList);
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
			sendingData.setCommand("Send");
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
	 * Function to broadcast current group list to all clients
	 * @param groupList
	 */
	public void sendGroupData(List<Group> groupList) {
		try {
			DataSend sendingData = new DataSend();
			sendingData.setGroupData(groupList);
			sendingData.setCommand("BroadcastGroups");
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
			sendingData.setCommand("RequestGroups");
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
			// globalGroupList was never used before
			if (globalGroupList == null || globalGroupList.isEmpty()) {
				// Set lobby as User's active group
				//currentUser = new User("Anonymous ", "230.1.1.1");
				// Add User into the group
				adminRoom.addUser(currentUser);
				// Adding the joined group into Global Group List held by all clients
				globalGroupList.add(adminRoom);
				// Store the joined group into the current user's Group List
				currentUser.groupList.add(adminRoom);
				// Send Datagram Packet for joining
//				sendMessage(currentUser.getUserName(), "has joined " + adminRoom.getGroupName());
				
			} else if (globalGroupList.size() == 1) {
				// No other clients has a more than the Lobby in their group list
				// There are clients who gave back a more updated group list
				System.out.println("I didn't get any chat back");
				//currentUser = new User("Anonymous" + Integer.toString(globalGroupList.get(0).getUserList().size()), "230.1.1.1");
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
			currentUser.groupList.add(groupToJoin);
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
}
