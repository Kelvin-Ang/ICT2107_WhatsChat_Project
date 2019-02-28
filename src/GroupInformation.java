import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Group Information Class to perform group functions
 *
 */
public class GroupInformation extends JFrame {
	
	// Declare UI variables
	private JLabel lblGroupName, lblGroupParticipants;
	private JPanel contentPane;
	private JTextField txtGroupName;
	private JTable groupTable;
	private JButton btnEditName, btnActiveGroup, btnLeaveGroup;
	private JScrollPane scrollPaneGroup;
	
	// Declare value variables
	private GroupController groupController;
	private Group currentGroup;

	/**
	 * Create the application.
	 */
	public GroupInformation(ChatApp chatApp) {
		
		// Get Controller
		groupController = chatApp.getGroupController();
		
		/**
		 * Initiate the shell of the user interface
		 */
		initUI();

		/**
		 * Start of attaching logic into UI
		 */
		// On-click Listener for Edit Name Button
		btnEditName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(txtGroupName.getText());
			}
		});
		
		// On-click Listener for Active Group Button
		btnActiveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Active Group");
				// TODO Reconnect socket
				groupController.setCurrentActiveGroup(currentGroup);
			}
		});
		
		// On-click Listener for Leave Group Button
		btnLeaveGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Leaving Group");
				groupController.leaveGroup(currentGroup);
			}
		});
	}
	
	/**
	 * Function to populate the group's data into Group Information
	 * @param currentGroup
	 */
	public void setCurrentGroup(Group currentGroup) {
		/**
		 * Append Group Name into Change Group Name Text Field and store group
		 */
		txtGroupName.setText(currentGroup.getGroupName());
		this.currentGroup = currentGroup;
		
		/**
		 * Populate Data into JTable
		 */
		DefaultTableModel model = (DefaultTableModel) groupTable.getModel();
		model.setRowCount(0);
		List<User> userList = currentGroup.getUserList();
		System.out.println("Total list of users"+ currentGroup.getUserList().toString());
		// Instantiate Object for three columns
		Object rowData[] = new Object[3];
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).currentIP.equals(currentGroup.IPAddress)) {
				rowData[0] = "Active";
			} else {
				rowData[0] = "Not Active";
			}
			// User is in Global Online User List => User is online
			System.out.println("Total list of users for global"+ groupController.getGlobalUserList().toString());
			for (User globalOnlineUsers : groupController.getGlobalUserList()) {
				System.out.println("Comparing "+ userList.get(i).getUserName() + " AND " + globalOnlineUsers.getUserName());
				if (userList.get(i).getUserName().equals(globalOnlineUsers.getUserName())) {
					rowData[1] = "Online";
					break;
				} else {
					rowData[1] = "Offline";
				}
			}
			rowData[2] = userList.get(i).userName;
			model.addRow(rowData);
		}
		// Sort the table
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
		groupTable.setRowSorter(sorter);
	}
	
	/**
	 * Function to initialise the User Interface
	 */
	public void initUI() {
		/**
		 * User Interface
		 */
		// Content Pane
		setBounds(100, 100, 415, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Label for Group Name
		lblGroupName = new JLabel("Group Name");
		lblGroupName.setBounds(10, 10, 75, 25);
		contentPane.add(lblGroupName);
		
		// Text Field for Group Name
		txtGroupName = new JTextField();
		txtGroupName.setBounds(85, 10, 200, 25);
		contentPane.add(txtGroupName);
		txtGroupName.setColumns(10);
		
		// Button for Edit Name
		btnEditName = new JButton("Edit Name");
		btnEditName.setBounds(290, 10, 100, 25);
		contentPane.add(btnEditName);
		
		// Label for Group Participants
		lblGroupParticipants = new JLabel("Group Participants");
		lblGroupParticipants.setBounds(10, 40, 380, 25);
		contentPane.add(lblGroupParticipants);
		
		// Scroll Pane for Group Table
		scrollPaneGroup = new JScrollPane();
		scrollPaneGroup.setBounds(10, 70, 380, 250);
		contentPane.add(scrollPaneGroup);
		groupTable = new JTable();
		groupTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Status", "Online", "Participant Name"
			}
		));
		groupTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		scrollPaneGroup.setViewportView(groupTable);
		
		// Button for Set Active Group
		btnActiveGroup = new JButton("Set Active Group");
		btnActiveGroup.setBounds(45, 335, 150, 25);
		contentPane.add(btnActiveGroup);
		
		// Button for Leave Group
		btnLeaveGroup = new JButton("Leave Group");
		btnLeaveGroup.setBounds(200, 335, 150, 25);
		contentPane.add(btnLeaveGroup);
	}

	/**
	 * Function to close the Frame
	 */
	public void CloseFrame() {
		super.dispose();
	}
}
