import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {

	String IPAddress;
	String groupName;
	List<String> lastTenMessage = new ArrayList<>();
	List<User> userList = new ArrayList<>(); 
	
	public Group(String IPAddress,String groupName) {
		this.IPAddress = IPAddress;
		this.groupName = groupName;
	}
}
