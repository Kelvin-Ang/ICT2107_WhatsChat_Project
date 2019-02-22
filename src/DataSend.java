import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSend implements Serializable {
	
	public String command;
	public List<String> stringData = new ArrayList<>();
	public List<User> userData = new ArrayList<>();
	public List<Group> groupData = new ArrayList<>();
	
	
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setStringData(List<String> stringData) {
		this.stringData = stringData;
		
	}
	public void setUserData(List<User> userData) {
		this.userData = userData;
	}
	public void setGroupData(List<Group> groupData) {
		this.groupData = groupData;
	}
	
		

}
