

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

public class DBController {
	
	private static Connection conn;
	
	public static Connection getConnection() throws Exception {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/2107db?useTimezone=true&serverTimezone=GMT";
			String username = "root";
			String password = "";
			Class.forName(driver);

			conn = DriverManager.getConnection(url, username, password);
			System.out.println("Connected");
			return conn;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static void createTable() throws Exception {
		try {
			PreparedStatement createTable = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS User(id int NOT NULL AUTO_INCREMENT, username varchar(20), password varchar(20), profilePic MEDIUMBLOB, IPAddress varchar(20), PRIMARY KEY(id))");
			createTable.executeUpdate();
			System.out.println("User table created");
			
			PreparedStatement createGroupTable = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS UserGroup(username varchar(20), IPAddress varchar(20), PRIMARY KEY(username, IPAddress))");
			createGroupTable.executeUpdate();
			System.out.println("Usergroup table created");
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public static Boolean existingUsernameExist(String username) throws Exception {
		PreparedStatement checkExistingUser = conn
				.prepareStatement("SELECT username from User WHERE username = '" + username + "'");
		ResultSet result = checkExistingUser.executeQuery();

		if (result.next()) {
			return true;
		}
		return false;
	}
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
	public static User getUser(String username, String password) throws Exception {
		PreparedStatement insertUser = conn
				.prepareStatement("SELECT * from User WHERE username = '" + username
						+ "' AND password = '" + password + "'");
		ResultSet result = insertUser.executeQuery();
		User user = null;
		if (result.next()) {
			user = new User(username, password);
		}
		if (user != null) {
			user.setGroupList(retrieveUserGroup(username));
			user.setCurrentIP(result.getString("IPAddress"));
			return user;
		}
		return null;
	}
	
	public static void updateUserIP(String username, String IPAddress) throws Exception {
		PreparedStatement updateUserIP = conn
				.prepareStatement("UPDATE User SET IPAddress = '" + IPAddress + "' WHERE username = '" + username + "'");
		updateUserIP.executeUpdate();
	}
	
	public static UserList getOnlineUsers(ArrayList<String> user) throws Exception {
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

	
	public static void createGroupTable() throws Exception {
		try {
			PreparedStatement createGroupTable = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS UserGroup(username varchar(20), IPAddress varchar(20), PRIMARY KEY(username, IPAddress))");
			createGroupTable.executeUpdate();
			System.out.println("Table created");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void insertUserGroupPair(String username, String IPAddress) throws Exception {
		System.out.println("Sending data into db");
		try {
			System.out.println("Sending data into db");
			PreparedStatement insertUserGroupPair = conn

					.prepareStatement("INSERT INTO UserGroup (username, IPAddress) VALUES ('" + username + "', '"
							+ IPAddress + "'");

			insertUserGroupPair.executeUpdate();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public static void deleteUserGroupPair(String username, String IPAddress) throws Exception {
		try {
			PreparedStatement deleteUserGroupPair = conn
					.prepareStatement("DELETE FROM UserGroup WHERE username = '" + username + "' AND IPAddress = '" + IPAddress + "'");
			deleteUserGroupPair.executeUpdate();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public static List<String> retrieveUserGroup(String username) throws Exception {
			PreparedStatement retrieveUserGroupPair = conn
					.prepareStatement("SELECT IPAddress from UserGroup WHERE username = '" + username + "'");
			ResultSet result = retrieveUserGroupPair.executeQuery();
			List<String> group = new ArrayList<String>();
			
			while (result.next()) {
				byte[] groupByte = result.getBytes("IPAddress");
				String groupIP = new String(groupByte);
				group.add(groupIP);
			}
		return group;
	}
	
	/**
	 * Function to close the SQL Connection
	 * @param myConn
	 * @throws SQLException
	 */
	public static void close(Connection myConn) throws SQLException {
		if (myConn != null) {
			myConn.close();
		}
	}
}