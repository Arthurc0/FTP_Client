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
	public static char msgServeur;
	
	private JPanel contentPane;
	private JLabel lblNewLabel;

	private static final long serialVersionUID = 1L;
	private static JLabel lblErrorMessage;

	private static JButton btnConnect;
	private static JTextField textUser;
	private static JPasswordField textPass;
	private JLabel lblNewLabel_1;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new Connexion();
				frame.setVisible(true);
				Traitement.execute();
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						if (Traitement.serveurConnecte)
							Traitement.envoyerCommande("bye", "");
					}
				});
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
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						validerConnexion();
					}
				}
			});
			
			btnConnect.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					validerConnexion();
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
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						validerConnexion();
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

	public JPasswordField getTextPass() {
		if (textPass == null) {
			textPass = new JPasswordField();

			textPass.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						validerConnexion();
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

	// Affiche le message demandé
	public static void afficherMessage(String message) {
		lblErrorMessage.setText(message);
	}

	public static void desactiverForm() {
		textUser.setEnabled(false);
		textPass.setEnabled(false);
		btnConnect.setEnabled(false);
	}
	
	private void validerConnexion() {
		String user = textUser.getText();
		String pass = new String(textPass.getPassword());

		// Si les deux champs sont remplis
		if(!user.isBlank() && !pass.isEmpty()) {
			Traitement.envoyerCommande("user", user);
			
			if(msgServeur == '0') {
				Traitement.envoyerCommande("pass", pass);
				
				if(msgServeur == '0') {
					Traitement.connecte = true;
					
					MainClient mainFrame = new MainClient();
					mainFrame.setVisible(true);
					mainFrame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							if(Traitement.serveurConnecte)
								Traitement.envoyerCommande("bye", "");
						}
					});
					this.dispose();
				} else {
					if(Traitement.serveurConnecte)
						afficherMessage("Le mot de passe est incorrect");
				}
			} else {
				if(Traitement.serveurConnecte)
					afficherMessage("Le login n'existe pas");
			}
		} else {
			if(user.isBlank())
				afficherMessage("Le login n'est pas renseigné");
			else
				afficherMessage("Le mot de passe n'est pas renseigné");
		}
	}
	private JLabel getLblNewLabel_1() {
		if (lblNewLabel_1 == null) {
			lblNewLabel_1 = new JLabel("Mot de passe");
			lblNewLabel_1.setBounds(147, 104, 124, 13);
		}
		return lblNewLabel_1;
	}
}