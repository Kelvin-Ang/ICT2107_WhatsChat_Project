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
	private JPanel contentPane;
	private JTextField txtGroupName;
	private JTable groupTable;
	

	// Declare value variables
	private InetAddress adminGroup = null;
	private MulticastSocket adminSocket = null;
	private Group currentGroup;
	public static GroupInformation obj = null;
	private GroupController groupController;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
					GroupInformation window = new GroupInformation();
					window.setVisible(true);
					window.setTitle("Group Information");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setGroupController(GroupController groupController) {
		this.groupController = groupController;
	}
	
	public Group getCurrentGroup() {
		return currentGroup;
	}

	/**
	 * Create the application.
	 */
	public GroupInformation() {
		
		/**
		 * User Interface
		 */
		setBounds(100, 100, 415, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblGroupName = new JLabel("Group Name");
		lblGroupName.setBounds(10, 10, 75, 25);
		contentPane.add(lblGroupName);
		
		txtGroupName = new JTextField();
		txtGroupName.setText("Hi");
		txtGroupName.setBounds(85, 10, 200, 25);
		contentPane.add(txtGroupName);
		txtGroupName.setColumns(10);
		
		JButton btnEditName = new JButton("Edit Name");
		btnEditName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				DataSend.editGroupName()
				System.out.println(txtGroupName.getText());
			}
		});
		btnEditName.setBounds(290, 10, 100, 25);
		contentPane.add(btnEditName);
		
		JLabel lblGroupParticipants = new JLabel("Group Participants");
		lblGroupParticipants.setBounds(10, 40, 380, 25);
		contentPane.add(lblGroupParticipants);
		
		JScrollPane scrollPaneGroup = new JScrollPane();
		scrollPaneGroup.setBounds(10, 70, 380, 250);
		contentPane.add(scrollPaneGroup);
		
		groupTable = new JTable();
		groupTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Status", "Participant Name"
			}
		));
		groupTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		scrollPaneGroup.setViewportView(groupTable);
		
		JButton btnNewButton = new JButton("Set Active Group");
		btnNewButton.setBounds(45, 335, 150, 25);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Leave Group");
		btnNewButton_1.setBounds(200, 335, 150, 25);
		contentPane.add(btnNewButton_1);
	}
	
	/**
	 * Function to populate the group's data into Group Information
	 * @param currentGroup
	 */
	public void setCurrentGroup(Group currentGroup) {
		txtGroupName.setText(currentGroup.getGroupName());
		/**
		 * Populate Data into JTable
		 */
		DefaultTableModel model = (DefaultTableModel) groupTable.getModel();
		model.setRowCount(0);
		List<User> userList = currentGroup.getUserList();
		Object rowData[] = new Object[2];
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).currentIP.equals(currentGroup.IPAddress)) {
				rowData[0] = "Active";
			} else {
				rowData[0] = "Not Active";
			}
			rowData[1] = userList.get(i).userName;
			model.addRow(rowData);
		}
		// Sort the table
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
		groupTable.setRowSorter(sorter);
	}

	
	/**
	 * Function to close the Frame
	 */
	public void CloseFrame() {
		super.dispose();
	}
	
	/**
	 * Implement Singleton pattern to ensure only one instance can be called
	 * @return
	 */
	public static GroupInformation getObj() {
		if (obj == null) {
			obj = new GroupInformation();
			obj.setLocationRelativeTo(null);
		}
		return obj;
	}
}
