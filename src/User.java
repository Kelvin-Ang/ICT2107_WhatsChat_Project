import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

	String userName; // Name of user
	String password; // Password of user
	String currentIP; // Current active group
	Image profilePic; // Display picture of user
	List<String> groupList = new ArrayList<>(); // IP Address of the groups the user is in
	
	public User(String userName, String currentIP) {
		this.userName = userName;
		this.currentIP = currentIP;
	}
	
	public User(String userName, String password, Image profilePic) {
		this.userName = userName;
		this.password = password;
		this.profilePic = profilePic;
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
		this.groupList = groupList;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProfilePic(Image profilePic) {
		this.profilePic = profilePic;
	}

	public String getPassword() {
		return password;
	}
	public Image getProfilePic() {
		return profilePic;
	}
	
	@Override
	public String toString() {
		return this.userName;
	}
}
