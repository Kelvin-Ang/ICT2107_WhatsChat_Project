import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {

	String IPAddress;
	String groupName;
	List<String> lastTenMessage = new ArrayList<>();
	List<User> userList = new ArrayList<>(); 
	
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

	public Group(String IPAddress,String groupName) {
		this.IPAddress = IPAddress;
		this.groupName = groupName;
	}
	
	public String toString() {
		return this.groupName;
	}
	
}
