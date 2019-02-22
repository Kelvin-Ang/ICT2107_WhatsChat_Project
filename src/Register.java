
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class Register extends JFrame {

	private JPanel contentPane, panel2;

	JButton btnBrowsePcForImg, nextButton;
	JLabel label;
	private JLabel lblProfilePicture;
	private JTextField txtName;
	private JLabel lblUserRegistration;
	static Register frame;
	
	String image = null;
	private JLabel lblResult;
	private JPasswordField txtPassword;
	private JPasswordField txtCPassword;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Register();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Register(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnBrowsePcForImg = new JButton("Browse PC for image");
		btnBrowsePcForImg.setBounds(88, 272, 167, 20);
		label = new JLabel();
		label.setBounds(176, 178, 69, 69);
		contentPane.setLayout(null);
		//label.setBounds(10, 10, 670, 250);
		getContentPane().add(btnBrowsePcForImg);
		getContentPane().add(label);

		btnBrowsePcForImg.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser file = new JFileChooser();
				file.setCurrentDirectory(new File(System.getProperty("user.home")));
				// filter the files
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "gif", "png");
				file.addChoosableFileFilter(filter);
				int result = file.showSaveDialog(null);
				// if the user click on save in Jfilechooser
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = file.getSelectedFile();
					String path = selectedFile.getAbsolutePath();
					label.setIcon(ResizeImage(path));
					image = path;
				}
				// if the user click on save in Jfilechooser

				else if (result == JFileChooser.CANCEL_OPTION) {
					System.out.println("No File Select");
				}
			}
		});

		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("username: ");
		lblNewLabel.setBounds(95, 74, 69, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Password: ");
		lblNewLabel_1.setBounds(95, 110, 69, 14);
		contentPane.add(lblNewLabel_1);
		
		lblProfilePicture = new JLabel("Profile picture: ");
		lblProfilePicture.setBounds(75, 211, 110, 14);
		contentPane.add(lblProfilePicture);
		
		txtName = new JTextField();
		txtName.setBounds(166, 71, 86, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		lblUserRegistration = new JLabel("User registration");
		lblUserRegistration.setBounds(115, 22, 100, 14);
		contentPane.add(lblUserRegistration);
		
		JLabel lblNewLabel_2 = new JLabel("Confirm Password: ");
		lblNewLabel_2.setBounds(54, 151, 110, 14);
		contentPane.add(lblNewLabel_2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(362, 400);
		setVisible(true);
		
		lblResult = new JLabel("");
		lblResult.setBounds(75, 305, 205, 14);
		contentPane.add(lblResult);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(166, 107, 86, 20);
		contentPane.add(txtPassword);
		
		txtCPassword = new JPasswordField();
		txtCPassword.setBounds(166, 148, 89, 20);
		contentPane.add(txtCPassword);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = txtName.getText();
				char[] passwordChar = txtPassword.getPassword();
				char[] cPasswordChar = txtCPassword.getPassword();
				String password = "";
				String cPassword = "";
				for (int i = 0; i < passwordChar.length; i++) {
					password += String.valueOf(passwordChar[i]);
				}
				for (int i = 0; i < cPasswordChar.length; i++) {
					cPassword += String.valueOf(cPasswordChar[i]);
				}
				
				if (username.equals(""))
					lblResult.setText("Please enter an username.");
				else if (password.equals("") && cPassword.equals(""))
					lblResult.setText("Please enter a password.");
				else if (!password.equals(cPassword))
					lblResult.setText("Passwords are not the same.");
				else if (image == null)
					lblResult.setText("Please upload a profile picture.");
				else {
					DBController dbCon = new DBController();
					try {
						dbCon.createTable();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						Boolean insertSuccessful = dbCon.insertUser(username, password, image);
						if (insertSuccessful)
							lblResult.setText("Account created.");
						else 
							lblResult.setText("Username existed");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnRegister.setBounds(129, 330, 89, 20);
		contentPane.add(btnRegister);
	}

	// Methode to resize imageIcon with the same size of a Jlabel
	public ImageIcon ResizeImage(String ImagePath) {
		ImageIcon MyImage = new ImageIcon(ImagePath);
		Image img = MyImage.getImage();
		Image newImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(newImg);
		return image;
	}
	
	void showNewScreen() {
		panel2 = new JPanel();
		nextButton = new JButton("NEXT");
		panel2.add(nextButton);
		 frame.remove(contentPane);
		 frame.setContentPane(panel2);
		 frame.validate();
         frame.repaint();
	}
}