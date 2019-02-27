
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Label;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField txtUsername;

	User user;
	private JPasswordField txtPassword;
	private GroupController groupController;

	/**
	 * Create the frame.
	 */
	public Login(ChatApp chatApp) {

		// Get Controller
		groupController = chatApp.getGroupController();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(100, 100, 312, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Username: ");
		lblNewLabel.setBounds(68, 57, 67, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Password: ");
		lblNewLabel_1.setBounds(68, 82, 67, 14);
		contentPane.add(lblNewLabel_1);

		txtUsername = new JTextField();
		txtUsername.setBounds(136, 54, 86, 20);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);

		JLabel labelImage = new JLabel("");
		labelImage.setBounds(103, 181, 86, 80);
		contentPane.add(labelImage);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(136, 82, 86, 20);
		contentPane.add(txtPassword);

		JLabel lblResult = new JLabel("");
		lblResult.setBounds(68, 122, 176, 14);
		contentPane.add(lblResult);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = txtUsername.getText();
				char[] passwordChar = txtPassword.getPassword();
				String password = "";

				for (int i = 0; i < passwordChar.length; i++) {
					password += String.valueOf(passwordChar[i]);
				}

				DBController dbCon = new DBController();
				if (txtUsername.getText().equals("")) {
					lblResult.setText("Please enter a username.");
				} else if (txtPassword.getText().equals("")) {
					lblResult.setText("Please enter a password.");
				} else {
					try {
						user = dbCon.getUser(username, password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (user != null) {
						System.out.println("User found");
						// Update UI, user object and user list using the Database result set
						chatApp.getLblUserName().setText("Logged in as: " + user.getUserName());
						groupController = chatApp.getGroupController();
						groupController.setCurrentUser(user);
						groupController.setCurrentActiveGroup(groupController.getCurrentUser().currentIP);
						System.out.println("Users personal list" + user.getGroupList().toString());
						// Update JList for Groups
						chatApp.getOnGoingGroups().setModel(groupController.convertGroupListToListModel());

						// Append logged in user into Global User List
						groupController.getGlobalUserList().add(user);
						System.out.println("Current User List" + groupController.getGlobalUserList().toString());
						System.out.println("Current user group list" + user.getGroupList().toString());
						groupController.sendUserData(groupController.getGlobalUserList());
						System.out.println("UPDATED USER LIST" + groupController.getGlobalUserList());
//						chatApp.getOnlineUsers().setModel(groupController.convertUserListToListModel());
						setVisible(false);
						dispose();
						// chatApp.loginBtn.setEnabled(false);
						chatApp.loginBtn.setVisible(false);
						chatApp.logoutBtn.setVisible(true);
						chatApp.registerUserBtn.setEnabled(false);
						chatApp.createGroupBtn.setVisible(true);
						chatApp.createGroup_txt.setVisible(true);

					} else {
						lblResult.setText("User is not found.");
					}
				}
			}
		});
		btnNewButton.setBounds(93, 147, 89, 23);
		contentPane.add(btnNewButton);

	}

	public void logInChat(String username, String password, ChatApp chatApp) {

		DBController dbCon = new DBController();

		try {
			user = dbCon.getUser(username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (user != null) {
			System.out.println("User found");
			// Update UI, user object and user list using the Database result set
			chatApp.getLblUserName().setText("Logged in as: " + user.getUserName());
			groupController = chatApp.getGroupController();
			groupController.setCurrentUser(user);
			groupController.setCurrentActiveGroup(groupController.getCurrentUser().currentIP);
			System.out.println("Users personal list" + user.getGroupList().toString());
			// Update JList for Groups
			chatApp.getOnGoingGroups().setModel(groupController.convertGroupListToListModel());

			// Append logged in user into Global User List
			groupController.getGlobalUserList().add(user);
			System.out.println("Current User List" + groupController.getGlobalUserList().toString());
			System.out.println("Current user group list" + user.getGroupList().toString());
			groupController.sendUserData(groupController.getGlobalUserList());
			System.out.println("UPDATED USER LIST" + groupController.getGlobalUserList());
//					chatApp.getOnlineUsers().setModel(groupController.convertUserListToListModel());
			setVisible(false);
			dispose();
			// chatApp.loginBtn.setEnabled(false);
			chatApp.loginBtn.setVisible(false);
			chatApp.logoutBtn.setVisible(true);
			chatApp.registerUserBtn.setEnabled(false);
			chatApp.createGroupBtn.setVisible(true);
			chatApp.createGroup_txt.setVisible(true);

		}
	}
}
