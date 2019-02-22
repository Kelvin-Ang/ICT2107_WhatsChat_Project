import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

	String userName;
	String phoneNo;
	String profilePic;
	String currentIP;
	List<Group> groupList = new ArrayList<>();
	
	public User(String userName) {
		this.userName = userName;
	}
}
