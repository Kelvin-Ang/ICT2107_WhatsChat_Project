import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class UserList {
	private ArrayList<String> nameList;
	private Map<String, Image> userList = new HashMap<>();
	
	UserList(ArrayList<String> nameList, Map<String, Image> userList){
		this.nameList = nameList;
		this.userList = userList;
	}
	public ArrayList<String> getNameList(){
		return nameList;
	}
	public void setNameList(ArrayList<String> nameList){
		this.nameList = nameList;
	}
	public Map<String, Image> getUserList(){
		return userList;
	}
	
	public void clearAll() {
		this.nameList.clear();
		this.userList.clear();
	}
	
	public boolean isEmpty() {
		return nameList.isEmpty();
	}
}
