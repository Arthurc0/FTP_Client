package vue;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Connexion extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnConnect;
	private JTextField textUser;
	private JLabel lblErrorMessage;
	
	private static String msgServeur;
	private static Connexion frame;
	private JPasswordField textPass;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Connexion();
					frame.setVisible(true);
					Traitement.execute(frame, false);
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							// Si la communication avec le serveur est toujours en cours
							if(Traitement.serveurConnecte)
								Traitement.envoyerCommande("bye", "");
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Connexion() {
		setTitle("Connexion - Client FTP");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Connexion.class.getResource("/vue/images/icone.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getBtnConnect());
		contentPane.add(getTextUser());
		contentPane.add(getLblErrorMessage());
		contentPane.add(getTextPass());
		contentPane.add(getLblNewLabel());
		contentPane.add(getLblNewLabel_1());
	}
	
	
	public JButton getBtnConnect() {
		if (btnConnect == null) {
			btnConnect = new JButton("Se connecter");
			
			btnConnect.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER){
						btnConnect.doClick();
					}
				}
			});
			btnConnect.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
						Traitement.envoyerCommande("user", getTextUser().getText());
						
						if(msgServeur.startsWith("0")){
							Traitement.envoyerCommande("pass", new String(getTextPass().getPassword()));
						}

						if(msgServeur.startsWith("0")){
							Traitement.connecte = true;
							
							//getLblErrorMessage().setText(new String(getTextPass().getPassword()));
							getLblErrorMessage().setText("Correct");

							MainClient mainFrame = new MainClient();
							mainFrame.setVisible(true);
							Traitement.execute(mainFrame, true);
							
							mainFrame.addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosing(WindowEvent e) {
									// Si la communication avec le serveur est toujours en cours
									if(Traitement.serveurConnecte)
										Traitement.envoyerCommande("bye", "");
								}
							});
							
							frame.dispose();
							
						} else if(msgServeur.startsWith("2")){
							getLblErrorMessage().setText("Login ou mot de passe incorrect");
						} else {
							getLblErrorMessage().setText("Erreur de communication avec le serveur");
						}
				}
			});
			btnConnect.setBounds(147, 164, 200, 20);
		}
		return btnConnect;
	}
	public JTextField getTextUser() {
		if (textUser == null) {
			textUser = new JTextField();
			textUser.addKeyListener(new KeyAdapter() {
				
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER){
						textUser.transferFocus();
					}
				}
				
			});
			textUser.setBounds(147, 74, 200, 20);
			textUser.setColumns(10);
		}
		return textUser;
	}
	public JLabel getLblErrorMessage() {
		if (lblErrorMessage == null) {
			lblErrorMessage = new JLabel("");
			lblErrorMessage.setHorizontalAlignment(SwingConstants.CENTER);
			lblErrorMessage.setForeground(Color.RED);
			lblErrorMessage.setBounds(20, 190, 456, 20);
		}
		return lblErrorMessage;
	}
	
	public String getMsgServeur() {
		return msgServeur;
	}

	public static void setMsgServeur(String msgServeur) {
		Connexion.msgServeur = msgServeur;
	}
	public JPasswordField getTextPass() {
		if (textPass == null) {
			textPass = new JPasswordField();
			
			textPass.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER){
						btnConnect.doClick();
					}
				}
			});
			textPass.setBounds(147, 120, 200, 19);
		}
		return textPass;
	}
	private JLabel getLblNewLabel() {
		if (lblNewLabel == null) {
			lblNewLabel = new JLabel("Login");
			lblNewLabel.setBounds(147, 57, 45, 13);
		}
		return lblNewLabel;
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("Mot de passe");
			lblNewLabel_1.setBounds(147, 104, 105, 13);
		}
		return lblNewLabel_1;
	}
}
