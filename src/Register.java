
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.Icon;
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
	static Register registerFrame;
	
	String image = null;
	private JLabel lblResult;
	private JPasswordField txtPassword;
	private JPasswordField txtCPassword;
	
	public Register(JLabel usernameText, JLabel imageLabel){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 450, 493);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnBrowsePcForImg = new JButton("Browse PC for image");
		btnBrowsePcForImg.setBounds(88, 272, 167, 20);
		label = new JLabel();
		label.setBounds(176, 178, 69, 69);
		
		String defaultpath = "src/Images/user.jpg";
		ImageIcon defaultimage = new ImageIcon(defaultpath);
		Image img = defaultimage.getImage();
		Image newImage = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon finalImage = new ImageIcon(newImage);
		label.setIcon(finalImage);
		image = defaultpath;
		
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
					ImageIcon finalImage = ResizeImage(path);
					label.setIcon(finalImage);
					image = path;
				}
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
		lblNewLabel_2.setBounds(50, 151, 120, 14);
		contentPane.add(lblNewLabel_2);
		
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
				String IPAddress = "230.1.1.1";
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
						Boolean insertSuccessful = dbCon.insertUser(username, password, image, IPAddress);
						if (insertSuccessful) {
							lblResult.setText("Account created.");
							imageLabel.setIcon(finalImage);
							usernameText.setText(username);
							setVisible(false);
							dispose();
						}
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
}
