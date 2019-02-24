

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
import java.util.Map;

import javax.swing.ImageIcon;

public class DBController {
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

	public static void createTable() throws Exception {
		try {
			Connection conn = getConnection();
			PreparedStatement createTable = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS User(id int NOT NULL AUTO_INCREMENT, username varchar(20), password varchar(20), profilePic MEDIUMBLOB, PRIMARY KEY(id))");
			createTable.executeUpdate();
			System.out.println("Table created");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
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
	public static Boolean insertUser(String username, String password, String image) throws Exception {
			if (existingUsernameExist(username)) {
				return false;
			}
			else {
				try {
				FileInputStream is = new FileInputStream(new File(image));
				Connection conn = getConnection();
				PreparedStatement insertUser = conn
						.prepareStatement("INSERT INTO User (username, password, profilePic) VALUES ('" + username + "', '"
								+ password + "', ?)");
				insertUser.setBinaryStream(1, is);
				insertUser.executeUpdate();
			} catch (Exception e) {
				System.out.println(e);
			}
			return true;
		}
	}
	public static User getUser(String username, String password) throws Exception {
		Connection conn = getConnection();
		PreparedStatement insertUser = conn
				.prepareStatement("SELECT * from User WHERE username = '" + username
						+ "' AND password = '" + password + "'");
		ResultSet result = insertUser.executeQuery();

		if (result.next()) {
			byte[] imageByte = result.getBytes("profilePic");
			ImageIcon image = new ImageIcon(imageByte);
			Image im = image.getImage();
			User user = new User(username, password, im);
			return user;
		}
		return null;
	}
	public static UserList getAll() throws Exception {
		Connection conn = getConnection();
		PreparedStatement insertUser = conn
				.prepareStatement("SELECT * from User");
		ResultSet result = insertUser.executeQuery();

		ArrayList<String> nameList = new ArrayList<String>();
		UserList userPair;
		
		Map<String, Image> userList = new HashMap<>();
		 
		while (result.next()) {
			byte[] imageByte = result.getBytes("profilePic");
			byte[] usernameByte = result.getBytes("username");
			String name = new String(usernameByte);
			ImageIcon image = new ImageIcon(imageByte);
			Image im = image.getImage();
			userList.put(name, im);
			nameList.add(name);
		}
		userPair = new UserList(nameList, userList);
		return userPair;
	}
}