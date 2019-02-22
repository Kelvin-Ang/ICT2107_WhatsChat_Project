import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

	String userName; // Name of user
	String password; // Password of user
	String currentIP; // Current active group
	Image profilePic; // Display picture of user
	List<Group> groupList = new ArrayList<>(); // Groups that the user is in
	
	public User(String userName) {
		this.userName = userName;
	}
	
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
	
	@Override
	public String toString() {
		return this.userName;
	}
}
