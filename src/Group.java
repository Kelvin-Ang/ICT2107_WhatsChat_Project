import java.io.Serializable;

public class Group implements Serializable {

	String IPAddress;
	String groupName;
	String lastTenMessage;
	
	public Group(String IPAddress,String groupName) {
		this.IPAddress = IPAddress;
		this.groupName = groupName;
	}
}
