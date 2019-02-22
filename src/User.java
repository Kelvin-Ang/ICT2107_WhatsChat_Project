import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

	String userName;
	String password;
	String currentIP;
	Image profilePic;
	List<Group> groupList = new ArrayList<>();
	
	public User(String userName, String password, Image profilePic) {
		this.userName = userName;
		this.password = password;
		this.profilePic = profilePic;
	}
	public String getName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public Image getProfilePic() {
		return profilePic;
	}
}
