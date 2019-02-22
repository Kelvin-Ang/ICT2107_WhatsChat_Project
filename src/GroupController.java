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
	
	
	JTextArea messageTextArea;
	
	public GroupController(JTextArea messageTextArea) {
		this.messageTextArea = messageTextArea;
	}
	
	InetAddress multicastGroup = null;
	MulticastSocket multicastSocket = null;
	private String userID = "anonymous";
	
	List<User> userList = new ArrayList<>();
	String onlineUser[] = { "" };
	
	List<Group> groupList = new ArrayList<>();
	Group adminRoom = new Group("230.1.1.1", "AdminRoom");
	
	User CurrentUser = new User(userID); 
	
	
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

	public int checkUserAvailable(List<User> userList, String name) {
		for (int x = 0; x < userList.size(); x++) {
			if (userList.get(x).userName.equals(name)) {
				return x;
			}
		}
		return -1;
	}

	public String[] addUser(String[] currentList, String newUser) {
		int currentSize = currentList.length;
		int newSize = currentSize + 1;
		String[] tempArray = new String[newSize];
		for (int i = 0; i < currentSize; i++) {
			tempArray[i] = currentList[i];
		}
		tempArray[newSize - 1] = newUser;
		return tempArray;
	}

	public static BufferedImage decodeToImage(String imageString) throws IOException {
		BufferedImage image = null;
		byte[] imageByte;
		BASE64Decoder decoder = new BASE64Decoder();
		imageByte = decoder.decodeBuffer(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();
		return image;
	}

	public static String encodeToString(File imageFile, String type) throws IOException {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
 
        BufferedImage image = ImageIO.read(imageFile);
        ImageIO.write(image, type, bos);
        byte[] imageBytes = bos.toByteArray();
 
        BASE64Encoder encoder = new BASE64Encoder();
        imageString = encoder.encode(imageBytes);
 
        bos.close();
        return imageString;
    }
	
	
	public void connection() {
		DataSend sendingData = new DataSend();
		List<String> stringData = new ArrayList<>();
		
		
		try {
			groupList.add(adminRoom);
			multicastGroup = InetAddress.getByName(groupList.get(0).IPAddress);
			System.out.print(multicastGroup);
			multicastSocket = new MulticastSocket(6789);
			multicastSocket.joinGroup(multicastGroup);

			
			sendingData.setCommand("Send");
			
			String message = CurrentUser.userName + " joined";
			
			stringData.add(message);
			sendingData.setStringData(stringData);

			byte[] buf = toByte(sendingData);

			DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpConnected);
			newThread();
		} catch (IOException ex) {

		}
	}
	
	public void userJoinRoom() {
		DataSend sendingData = new DataSend();
		List<String> stringData = new ArrayList<>();
		
		try {
			sendingData.setCommand("Send");
			
			String message = CurrentUser.userName + " joined";
			
			stringData.add(message);
			sendingData.setStringData(stringData);

			byte[] buf = toByte(sendingData);

			DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpConnected);
			newThread();
		} catch (IOException ex) {

		}
	}
	
	
	public void newThread() {
		new Thread(new Runnable() {

			DataSend objectDataRecieved;
			@Override
			public void run() {
				byte buf1[] = new byte[10000];
				DatagramPacket dgpRecevied = new DatagramPacket(buf1, buf1.length);
				while (true) {
					try {
						multicastSocket.receive(dgpRecevied);
						byte[] receiveData = dgpRecevied.getData();

						int length = dgpRecevied.getLength();

						String msg = new String(receiveData, 0, length);
						
						ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receiveData));
						try {
							objectDataRecieved = (DataSend) ois.readObject();
							

						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							ois.close();
						}
						
						if (objectDataRecieved.command.equals("Send")) {
							for(String data : objectDataRecieved.stringData) {
								messageTextArea.append(data + "\n");
							}
							
						}
						
						

						

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
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
	
	
	public void sendMessage(String message) {
		try {
			DataSend sendingData = new DataSend();
			List<String> stringData = new ArrayList<>();
			
			
			String msg = message;
			msg = CurrentUser.userName + " : " + msg;
			stringData.add(msg);
			sendingData.setStringData(stringData);
			sendingData.setCommand("Send");
			byte[] buf = toByte(sendingData);
			
			
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpSend);
			System.out.println(multicastGroup);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendGroupData(List<Group> groupList) {
		try {
			DataSend sendingData = new DataSend();
			sendingData.setGroupData(groupList);
			sendingData.setCommand("Create");
			byte[] buf = toByte(sendingData);
			
			 multicastGroup = InetAddress.getByName(groupList.get(0).IPAddress);
			DatagramPacket dgpSend = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
			multicastSocket.send(dgpSend);
			System.out.println(multicastGroup);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	
	public DefaultListModel<Group> createGroup(String groupName) {
		DefaultListModel<Group> currentGroupList = new DefaultListModel();
		
		try {

			if (!(getRoomCreated(groupList, groupName) == -1)) {
				
				sendMessage("Room Created");
				for(Group groups : CurrentUser.groupList) {
					currentGroupList.addElement(groups);
				}
				
				return currentGroupList;
			}

			Group newGroup = new Group(IPincrease(groupList.get(groupList.size() - 1).IPAddress),groupName);
			groupList.add(newGroup);
			CurrentUser.groupList.add(newGroup);
			
			sendGroupData(groupList);

			InetAddress AdminGroup = InetAddress.getByName(groupList.get(getRoomCreated(groupList,groupName)).IPAddress);
			System.out.print(AdminGroup);
			// multicastSocket = new MulticastSocket(6789);

			multicastSocket.joinGroup(AdminGroup);

			userJoinRoom();
			newThread();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		
		
		for(Group groups : CurrentUser.groupList) {
			currentGroupList.addElement(groups);
		}
		
		return currentGroupList;

	}
	

}
