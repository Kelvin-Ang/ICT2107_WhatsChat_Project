import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class GroupController {
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
	
	
//	public void connection() {
//		try {
//			groupList.add(adminRoom);
//			multicastGroup = InetAddress.getByName(groupList.get(0).IPAddress);
//			System.out.print(multicastGroup);
//			multicastSocket = new MulticastSocket(6789);
//			multicastSocket.joinGroup(multicastGroup);
//
//			String message = userID + " joined";
//
//			byte[] buf = message.getBytes();
//
//			DatagramPacket dgpConnected = new DatagramPacket(buf, buf.length, multicastGroup, 6789);
//			multicastSocket.send(dgpConnected);
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					byte buf1[] = new byte[1000];
//					DatagramPacket dgpRecevied = new DatagramPacket(buf1, buf1.length);
//					while (true) {
//						try {
//							multicastSocket.receive(dgpRecevied);
//							byte[] receiveData = dgpRecevied.getData();
//
//							int length = dgpRecevied.getLength();
//
//							String msg = new String(receiveData, 0, length);
//							
//							
//							if (msg.substring(0, 3).matches("^[a-zA-Z0-9_]*$")) {
//								messageTextArea.append(msg + "\n");
//							}
//							
//							if (!(msg.substring(0, 3).matches("^[a-zA-Z0-9_]*$"))) {
//								ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receiveData));
//								try {
//									groupList = (List<Group>) ois.readObject();
//
//								} catch (ClassNotFoundException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} finally {
//									ois.close();
//								}
//							}
//
//							
//
//						} catch (IOException ex) {
//							ex.printStackTrace();
//						}
//					}
//				}
//			}).start();
//		} catch (IOException ex) {
//
//		}
//	}

}
