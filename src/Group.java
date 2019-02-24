import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {

	String IPAddress; // Group's IP Address
	String groupName; // Group's Name
	List<String> lastTenMessage = new ArrayList<>(); // Group's last ten messages
	List<User> userList = new ArrayList<>(); // List of Users inside group
	
	public Group(String IPAddress,String groupName) {
		this.IPAddress = IPAddress;
		this.groupName = groupName;
	}
	
	public String getIPAddress() {
		return IPAddress;
	}

	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getLastTenMessage() {
		return lastTenMessage;
	}

	public void setLastTenMessage(List<String> lastTenMessage) {
		this.lastTenMessage = lastTenMessage;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
	public void addUser(User user) {
		this.userList.add(user);
	}
	
	public String toString() {
		return this.groupName;
	}
	
}
