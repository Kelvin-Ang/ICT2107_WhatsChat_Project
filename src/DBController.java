import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

public class DBController {
	
	/**
	 * Function to connect to MySQL Database
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/2107db?useTimezone=true&serverTimezone=GMT";
			String username = "root";
			String password = "";
			Class.forName(driver);

			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connected");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * Function to execute initial create table in MySQL
	 * @throws Exception
	 */
	public static void createTable() throws Exception {
		try {
			Connection conn = getConnection();
			PreparedStatement createTable = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS User(id int NOT NULL AUTO_INCREMENT, username varchar(20), password varchar(20), profilePic MEDIUMBLOB, IPAddress varchar(20), PRIMARY KEY(id))");
			createTable.executeUpdate();
			System.out.println("User table created");
			
			PreparedStatement createGroupTable = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS UserGroup(username varchar(20), IPAddress varchar(20), groupName varchar(50), PRIMARY KEY(username, IPAddress))");
			createGroupTable.executeUpdate();
			System.out.println("Usergroup table created");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Function to check if user exist in Database
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static Boolean existingUsernameExist(String username) throws Exception {
		Connection conn = getConnection();
		PreparedStatement checkExistingUser = conn
				.prepareStatement("SELECT username from User WHERE username = '" + username + "'");
		ResultSet result = checkExistingUser.executeQuery();

		if (result.next()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Function to insert user into Database
	 * @param username
	 * @param password
	 * @param image
	 * @param IPAddress
	 * @return
	 * @throws Exception
	 */
	public static Boolean insertUser(String username, String password, String image, String IPAddress) throws Exception {
			if (existingUsernameExist(username)) {
				return false;
			}
			else {
				try {
				FileInputStream is = new FileInputStream(new File(image));
				Connection conn = getConnection();
				PreparedStatement insertUser = conn
						.prepareStatement("INSERT INTO User (username, password, profilePic, IPAddress) VALUES ('" + username + "', '"
								+ password + "', ?, '" + IPAddress +"')");
				insertUser.setBinaryStream(1, is);
				insertUser.executeUpdate();
			} catch (Exception e) {
				System.out.println(e);
			}
			return true;
		}
	}
	
	/**
	 * Function to get an user from the Database and return a User object if there is no states
	 * @param username
	 * @param password
	 * @return User object
	 * @throws Exception
	 */
	public static User getUser(String username, String password) throws Exception {
		Connection conn = getConnection();
		PreparedStatement fetchUser = conn
				.prepareStatement("SELECT * from User WHERE username = '" + username
						+ "' AND password = '" + password + "'");
		ResultSet result = fetchUser.executeQuery();
		User user = null;
		
		if (result.next()) {
			byte[] usernameByte = result.getBytes("username");
			String retrievedName = new String(usernameByte);
			if(caseSensitiveCheck(retrievedName, username)) {
				user = new User(username, password);
				List<String> groupList = new ArrayList<String>();
				user.setGroupList(groupList);
				user.setCurrentIP(result.getString("IPAddress"));
				return user;
			}	
		}
		return null;
	}
	
	/**
	 * Function for case sensitive check
	 * @param retrieveUsername
	 * @param enteredUsername
	 * @return
	 */
	public static Boolean caseSensitiveCheck(String retrieveUsername, String enteredUsername) {
		for (int i = 0; i < retrieveUsername.length(); i++) {
			if (!retrieveUsername.substring(i,  i + 1).equals(enteredUsername.substring(i,  i + 1))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Function to save the final state of the last client into database
	 * @param username
	 * @param IPAddress
	 * @param groupName
	 * @throws Exception
	 */
	public static void saveStateToDatabase(String username, String IPAddress, String groupName) throws Exception {
		try {
			Connection conn = getConnection();
			PreparedStatement insertUserGroupPair = conn
					.prepareStatement("INSERT INTO UserGroup (username, IPAddress, groupName) VALUES ('" + username + "', '"
							+ IPAddress + "', '" + groupName + "')");
			insertUserGroupPair.executeUpdate();
			System.out.println("db execution of statement completed");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * Function to convert String array into UserList
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static UserList getOnlineUsers(ArrayList<String> user) throws Exception {
		Connection conn = getConnection();
		UserList userPair;
		ArrayList<String> nameList = new ArrayList<String>();
		Map<String, Image> userList = new HashMap<>();
		
		for (int i = 0; i < user.size(); i++) {
			PreparedStatement insertUser = conn
					.prepareStatement("SELECT * from User WHERE username = '" + user.get(i) + "'");
			ResultSet result = insertUser.executeQuery();
			
			if (result.next()) {
				byte[] imageByte = result.getBytes("profilePic");
				byte[] usernameByte = result.getBytes("username");
				String name = new String(usernameByte);
				ImageIcon image = new ImageIcon(imageByte);
				Image im = image.getImage();
				userList.put(name, im);
				nameList.add(name);
			}
		}
		userPair = new UserList(nameList, userList);
		return userPair;
	}

//	public static List<Group> reconstructGlobalGroupList(List<Group> uniqueGroup) throws Exception {
//		Connection conn = getConnection();
//		// Loop through the entire list
//		for (Group fetchGroupUsers : uniqueGroup) {
//			// Select all the username inside the group.
//			PreparedStatement getUsersInsideGroup = conn
//					.prepareStatement("SELECT username FROM usergroup WHERE groupName = '" + fetchGroupUsers.getGroupName() + "'");
//			ResultSet result = getUsersInsideGroup.executeQuery();
//			
//			while (result.next()) {
//				String usernameToConstruct = result.getString("")
//			}
//		}
//		PreparedStatement retrieveUserGroupPair = conn
//				.prepareStatement("SELECT IPAddress from UserGroup WHERE username = '" + username + "'");
//		ResultSet result = retrieveUserGroupPair.executeQuery();
//		List<String> group = new ArrayList<String>();
//		
//		while (result.next()) {
//			byte[] groupByte = result.getBytes("IPAddress");
//			String groupIP = new String(groupByte);
//			group.add(groupIP);
//		}
//		return group;
//	}
	
	/**
	 * Function to get all unique names in usergroup table
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<String> getDistinctUser() throws Exception {
		Connection conn = getConnection();
		PreparedStatement checkExistingUser = conn
				.prepareStatement("SELECT DISTINCT username from UserGroup");
		ResultSet result = checkExistingUser.executeQuery();

		ArrayList<String> usernameList = new ArrayList<String>();

		while (result.next()) {
			String username = result.getString("username");
			usernameList.add(username);
		}
		return usernameList;
	}
	
	/**
	 * Get all unique groupName in UserGroup Table
	 * @return
	 * @throws Exception
	 */
	public static List<Group> getDistinctGroup() throws Exception {
		Connection conn = getConnection();
		PreparedStatement checkExistingUser = conn
				.prepareStatement("SELECT DISTINCT IPAddress, groupName from UserGroup");
		ResultSet result = checkExistingUser.executeQuery();

		List<Group> globalGroupList = new ArrayList<Group>();
		Group tempGroup = null;
		
		while (result.next()) {
			String IPAddress = result.getString("IPAddress");
			String groupName = result.getString("groupName");
			tempGroup = new Group(IPAddress, groupName);
			globalGroupList.add(tempGroup);
		}
		return globalGroupList;
	}
	
	/**
	 * Function to check if usergroup table is empty
	 * @return
	 * @throws Exception
	 */
	public static boolean isUserGroupEmpty() throws Exception {
		Connection conn = getConnection();
		PreparedStatement checkExistingUser = conn
				.prepareStatement("SELECT COUNT(*) FROM usergroup");
		ResultSet result = checkExistingUser.executeQuery();
		int count = 0;
		while (result.next()) {
			count = result.getInt(1);
		}
		if (count == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void clearUserGroup() throws Exception {
		Connection conn = getConnection();
		PreparedStatement clearUserGroupTable = conn
				.prepareStatement("DELETE FROM usergroup");
		clearUserGroupTable.executeUpdate();
	}
}