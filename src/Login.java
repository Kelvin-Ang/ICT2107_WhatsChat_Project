

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
	ChatApp chatApp;
	
	User user;
	private JPasswordField txtPassword;
	private GroupController groupController;
	
	/**
	 * Create the frame.
	 */
	public Login(JLabel usernameText, JLabel imageLabel) {
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
				if(txtUsername.getText().equals("")) {
					lblResult.setText("Please enter a username.");
				}
				else if(txtPassword.getText().equals("")) {
					lblResult.setText("Please enter a password.");
				}
				else {
					try {
						user = dbCon.getUser(username, password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (user != null) {
						Image myImg = user.getProfilePic().getScaledInstance(labelImage.getWidth(), labelImage.getHeight(), Image.SCALE_SMOOTH);
						ImageIcon image = new ImageIcon(myImg);
						imageLabel.setIcon(image);
						System.out.println("User found");
						// Update UI, user object and user list using the Database result set
						usernameText.setText("Logged in as: " + user.getUserName());
						chatApp = chatApp.getInstance();
						groupController = chatApp.getGroupController();
						groupController.getCurrentUser().setUserName(user.getUserName());
						groupController.getCurrentUser().setPassword(user.getPassword());
						groupController.getCurrentUser().setProfilePic(myImg);
						groupController.getCurrentUser().setCurrentIP(user.getCurrentIP());
						groupController.getCurrentUser().setGroupList(user.getGroupList());
						// Update JList for Groups
						
						chatApp.getOnGoingGroups().setModel(groupController.convertGroupListToListModel());
						
						// Append logged in user into Global User List
						groupController.getGlobalUserList().add(user);
						groupController.sendUserData(groupController.getGlobalUserList());
						setVisible(false);
						dispose();
					}
					else{
						lblResult.setText("User is not found.");
					}
				}
			}
		});
		btnNewButton.setBounds(93, 147, 89, 23);
		contentPane.add(btnNewButton);
	}
}
