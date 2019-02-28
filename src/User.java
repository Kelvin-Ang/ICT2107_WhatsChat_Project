import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

	String userName; // Name of user
	String password; // Password of user
	String currentIP; // Current active group
	List<String> groupList = new ArrayList<>(); // IP Address of the groups the user is in
	
	public User(String userName) {
		this.userName = userName;
	}
	
	public User(String userName, String currentIP) {
		this.userName = userName;
		this.currentIP = currentIP;
	}
	
	public User(User user) {
		// TODO Auto-generated constructor stub
		this.currentIP = user.getCurrentIP();
		this.groupList = user.getGroupList();
		this.password = user.getPassword();
		this.userName = user.getUserName();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		groupList.add(0, "230.1.1.1");
		this.groupList = groupList;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return this.userName;
	}
}
