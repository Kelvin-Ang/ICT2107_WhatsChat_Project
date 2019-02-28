import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

public class ChatApp extends JFrame {

	// Declare UI variables
	private JPanel contentPane;
	JList<Group> onGoingGroups;
	JList<User> onlineUsers;
	static JTextArea messageTextArea;
	JButton createGroupBtn, sendMessageBtn, registerUserBtn, loginBtn, logoutBtn;
	JTextField createGroup_txt, sendMessage_txt;
	private JLabel lblUserName, imageLabel, lblGroups, lblOnlineUser, lblConversations;
	private JScrollPane scrollPane, scrollPane_1, scrollPane_2;

	// Declare value variables
	GroupController groupController;
	DBController dbCon;
	Login login;
	Register register;
	GroupInformation groupInformation;
	MouseAdapter mouseAdapter;
	MouseListener[] mouseArray;

	// Users variables
	UserList userList;
	DefaultListModel userModel;
	ArrayList<String> online;
	JList nameJList;
	private Map<String, Image> imageMap;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
					messageTextArea = new JTextArea();
					ChatApp frame = new ChatApp();
					frame.setVisible(true);
					frame.setTitle("WhatsChat");
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatApp() {

		/**
		 * Initiate the shell of the user interface
		 */
		initUI();

		/**
		 * Instantiate logic holders
		 */
		userModel = new DefaultListModel();
		online = new ArrayList<String>();
		dbCon = new DBController();
		try {
			DBController.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		groupController = new GroupController(this);
		login = new Login(this);
		register = new Register(this);
		groupInformation = new GroupInformation(this);
		

		/**
		 * Start of attaching logic into UI
		 */
		// Start of Group List
		onGoingGroups.setModel(convertGroupListToListModel());
		onGoingGroups.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("static-access")
			public void mouseClicked(MouseEvent evt) {
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					// Double-click detected
					int index = list.locationToIndex(evt.getPoint());
					groupInformation.setCurrentGroup(groupController
							.convertIPAddressToGroup(groupController.getCurrentUser().getGroupList().get(index)));
					groupInformation.setVisible(true);
					groupInformation
							.setTitle(groupController
									.convertIPAddressToGroup(groupController.getCurrentUser().getGroupList().get(index))
									.getGroupName() + " Information");
				}
			}
		});
		// End of Group List

		// Set Current User Name at top right
		lblUserName.setText(groupController.getCurrentUser().getUserName());

		// On-click Listener for Register Button
		registerUserBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				register.setVisible(true);
				register.setTitle("Register");
				register.setLocationRelativeTo(null);
			}
		});

		// On-click Listener for Create Group Button
		createGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				groupController.createGroup(createGroup_txt.getText());
				onGoingGroups.setModel(convertGroupListToListModel());
			}
		});

		// On-click Listener for Send Message Button
		sendMessageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				groupController.sendMessage(groupController.getCurrentUser().getUserName(), sendMessage_txt.getText());
				sendMessage_txt.setText("");
			}
		});

		// On-click Listener for Login Button
		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login.setVisible(true);
				login.setTitle("Login");
				login.setLocationRelativeTo(null);
			}
		});

		// On-click Listener for Logout Button
		logoutBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login.setVisible(true);
				login.setTitle("Login");
				login.setLocationRelativeTo(null);
			}
		});
		
		// Add Window Listener for window close
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent et) {
				System.out.println("Windows closing");
				// Going offline, remove from Global User List
				for (User findUserToDelete : groupController.getGlobalUserList()) {
					if (findUserToDelete.getUserName().equals(groupController.getCurrentUser().getUserName())) {
						groupController.getGlobalUserList().remove(findUserToDelete);
						break;
					}
				}
				System.out.println("Final user list" + groupController.getGlobalUserList());
				// Broadcast the change that user has went offline
				groupController.sendUserData(groupController.getGlobalUserList());
				// Broadcast the decrement in host
				groupController.notifyOutgoingHostData();
				// Detected that it is the last client, save state into Database
				if (groupController.getHostPingCount() == 1) {
					// Insert every group from Global Group List into Database
					for (Group groupToSave : groupController.getGlobalGroupList()) {
						if (!groupToSave.getIPAddress().equals("230.1.1.1")) {
							// For every group that is not Lobby, get every user
							for (User userToSave : groupToSave.getUserList()) {
								try {
									dbCon.saveStateToDatabase(userToSave.getUserName(), groupToSave.getIPAddress(), groupToSave.getGroupName());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					
				}
			}
		});
		/**
		 * End of attaching logic into UI
		 */

		/**
		 * Start of Enter Key Listeners
		 */
		createGroup_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					groupController.createGroup(createGroup_txt.getText());
					onGoingGroups.setModel(convertGroupListToListModel());
				}
			}
		});

		sendMessage_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					groupController.sendMessage(groupController.getCurrentUser().getUserName(),
							sendMessage_txt.getText());
					sendMessage_txt.setText("");
				}
			}
		});
		/**
		 * End of Enter Key Listeners
		 */
	}

	// Function to instantiate the User interface shell
	public void initUI() {
		/**
		 * Start of user interface
		 */
		// Content Pane
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 880, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Scroll Pane for Online User List
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 120, 180, 250);
		contentPane.add(scrollPane);
		nameJList = new JList(); // Set empty model shell
		scrollPane.setViewportView(nameJList);

		// Scroll Pane 1 for Group List
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(220, 120, 180, 250);
		contentPane.add(scrollPane_1);
		onGoingGroups = new JList<Group>(); // Set empty model shell
		onGoingGroups.setFont(new Font("helvitica", Font.PLAIN, 16));
		scrollPane_1.setViewportView(onGoingGroups);

		// Scroll Pane 2 for Message Text Area
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(430, 120, 420, 250);
		contentPane.add(scrollPane_2);
		scrollPane_2.setViewportView(messageTextArea);

		// Label for Online User
		lblOnlineUser = new JLabel("Online Users");
		lblOnlineUser.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOnlineUser.setBounds(10, 90, 180, 25);
		contentPane.add(lblOnlineUser);

		// Label for Group
		lblGroups = new JLabel("Groups");
		lblGroups.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblGroups.setBounds(220, 90, 180, 25);
		contentPane.add(lblGroups);

		// Label for Conversation
		lblConversations = new JLabel("Conversation");
		lblConversations.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblConversations.setBounds(430, 90, 420, 25);
		contentPane.add(lblConversations);

		// Label for Username
		lblUserName = new JLabel("");
		lblUserName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblUserName.setBounds(650, 10, 200, 25);
		contentPane.add(lblUserName);

		// Button for Register
		registerUserBtn = new JButton("Register");
		registerUserBtn.setBounds(10, 10, 150, 25);
		contentPane.add(registerUserBtn);

		// Button for Create Group
		createGroupBtn = new JButton("Create Group");
		createGroupBtn.setBounds(10, 50, 150, 25);
		contentPane.add(createGroupBtn);

		// Text Field for Create Group
		createGroup_txt = new JTextField();
		createGroup_txt.setColumns(10);
		createGroup_txt.setBounds(170, 50, 170, 25);
		contentPane.add(createGroup_txt);

		// Button for Send Message
		sendMessageBtn = new JButton("Send Message");
		sendMessageBtn.setBounds(722, 380, 128, 25);
		contentPane.add(sendMessageBtn);

		// Text Field for Send Message
		sendMessage_txt = new JTextField();
		sendMessage_txt.setColumns(10);
		sendMessage_txt.setBounds(10, 380, 706, 25);
		contentPane.add(sendMessage_txt);

		// Button for Login
		loginBtn = new JButton("Login");
		loginBtn.setBounds(170, 10, 150, 25);
		contentPane.add(loginBtn);

		// Button for Logout
		logoutBtn = new JButton("Log out");
		logoutBtn.setBounds(170, 10, 150, 25);
		contentPane.add(logoutBtn);
		logoutBtn.setVisible(false);
	}

	public void convertUserListToListModel() {
		try {
			// Start with an empty list
			ArrayList<String> newOnlineList = new ArrayList<String>();
			DefaultListModel newUserModel = new DefaultListModel();
			UserList newUserList = new UserList();

			System.out.println("Global User to convert list" + groupController.getGlobalUserList().toString());
			// Loop through global user list to update list to query database
			for (User user : groupController.getGlobalUserList()) {
				newOnlineList.add(user.getUserName());
			}
			
			newUserList = dbCon.getOnlineUsers(newOnlineList);
			
			// Append fetched results into userModel
			System.out.println("Getting name list " + newUserList.getNameList());
			for (String name : newUserList.getNameList()) {
				newUserModel.addElement(name);
			}

			System.out.println("usermodel data " + newUserModel.toString());

			// Set data into User Interface
			nameJList.setModel(newUserModel);
			imageMap = newUserList.getUserList();
			mouseArray = nameJList.getMouseListeners();
			System.out.println("Mouse Listeners before add " + mouseArray.length);
			if (mouseArray.length == 2) {
				nameJList.addMouseListener(		
						// On-click listener to Group Invite by clicking user
						mouseAdapter = new MouseAdapter() {
							@SuppressWarnings("static-access")
							public void mouseClicked(MouseEvent evt) {
								JList list = (JList) evt.getSource();
								if (evt.getClickCount() == 2) {
									int index = list.locationToIndex(evt.getPoint());
									int option = JOptionPane.showConfirmDialog(null, "Do you want to invite "
											+ groupController.getGlobalUserList().get(index) + " to the group?", "Group Invitation",
											JOptionPane.YES_NO_OPTION);
									// if option is yes
									if (option == 0) {
										// Join
										System.out.println("current active group" + groupController.getCurrentUser().getCurrentIP().toString());
										groupController.sendInvite(groupController.getGlobalUserList().get(index).toString(),
										groupController.convertIPAddressToGroup(groupController.getCurrentUser().getCurrentIP()));
									}
									// if option is no
									else {
										// Do nothing / don't join

									}
								}

							}
						});
			}
			System.out.println("Mouse Listeners " + mouseArray.length);
			while (imageMap == null) {
				// Lock
				System.out.println("Image WAITING...");
			}
			nameJList.setCellRenderer(new listRenderer());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Function to convert Group list into ListModel for JList
	 * 
	 * @return
	 */
	public DefaultListModel<Group> convertGroupListToListModel() {
		// Initialize ListModel
		DefaultListModel<Group> currentGroupList = new DefaultListModel<Group>();
		// Populate ListModel with the current user's group list
		for (String userGroupIPAddress : groupController.getCurrentUser().getGroupList()) {
			for (Group group : groupController.getGlobalGroupList()) {
				// If the user's group list has same IP as group
				if (userGroupIPAddress.equals(group.getIPAddress())) {
					// Group is user's active group
					if (group.getIPAddress().equals(groupController.getCurrentUser().getCurrentIP())) {
						// Append a <Active> tag at the back of the active group
						Group tempGroup = new Group(group.IPAddress, group.getGroupName() + " <Active>");
						currentGroupList.addElement(tempGroup);
					} else {
						currentGroupList.addElement(group);
					}
				}
			}
		}
		System.out.println("Current user group list" + currentGroupList);
		return currentGroupList;
	}
	
	public class listRenderer extends DefaultListCellRenderer {
		Font font = new Font("helvitica", Font.PLAIN, 16);

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Image myImg = imageMap.get((String) value).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
			ImageIcon image = new ImageIcon(myImg);
			label.setIcon(image);
			label.setHorizontalTextPosition(JLabel.RIGHT);
			label.setFont(font);
			return label;
		}
	}

	/**
	 * START OF GETTERS AND SETTERS
	 */
	public JList<Group> getOnGoingGroups() {
		return onGoingGroups;
	}

	public JList<User> getOnlineUsers() {
		return onlineUsers;
	}

	public JLabel getLblUserName() {
		return lblUserName;
	}

	public JLabel getImageLabel() {
		return imageLabel;
	}

	public GroupController getGroupController() {
		return groupController;
	}

	public static JTextArea getMessageTextArea() {
		return messageTextArea;
	}
	
	public GroupInformation getGroupInformation() {
		return groupInformation;
	}

	public DBController getDbCon() {
		return dbCon;
	}
	/**
	 * END OF GETTERS AND SETTERS
	 */
}