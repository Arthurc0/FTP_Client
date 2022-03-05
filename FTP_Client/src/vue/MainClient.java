package vue;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class MainClient extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollTexte;
	private static JTextArea txtCommandes;
	private static JTextField txtCommande;
	private static JButton btnEnvoi;
	private ArrayList<String> historique;
	private int idHistorique = 0;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainClient frame = new MainClient();
					frame.setVisible(true);
					Traitement.execution(txtCommandes, txtCommande, btnEnvoi);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainClient() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainClient.class.getResource("/vue/icone.png")));
		setTitle("Client FTP");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 510, 607);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getScrollTexte());
		contentPane.add(getTxtCommande());
		contentPane.add(getBtnEnvoi());
		
		historique = new ArrayList<String>();
		
		addWindowListener(new WindowAdapter() {
			public void windowOpened( WindowEvent e ) {
				txtCommande.requestFocus();
			}
		});
	}
	private JScrollPane getScrollTexte() {
		if (scrollTexte == null) {
			scrollTexte = new JScrollPane();
			scrollTexte.setBounds(10, 11, 475, 502);
			scrollTexte.setViewportView(getTxtCommandes());
		}
		return scrollTexte;
	}
	private JTextArea getTxtCommandes() {
		if (txtCommandes == null) {
			txtCommandes = new JTextArea();
			
			txtCommandes.setWrapStyleWord(true);
			txtCommandes.setLineWrap(true);
			txtCommandes.setFont(new Font("Consolas", Font.PLAIN, 15));
			txtCommandes.setEditable(false);
		}
		return txtCommandes;
	}
	private JTextField getTxtCommande() {
		if (txtCommande == null) {
			txtCommande = new JTextField();
			
			txtCommande.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					
					// Parcours des commandes précédentes (historique)
					if(e.getKeyCode() == KeyEvent.VK_UP) {
						if(idHistorique > 0) {
							idHistorique--;
							txtCommande.setText(historique.get(idHistorique));
						}
					} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
						if(historique.size() > 0 && idHistorique < historique.size()) {
							idHistorique++;
							
							if(idHistorique == historique.size())
								txtCommande.setText("");
							else
								txtCommande.setText(historique.get(idHistorique));
						}
					} else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
						if(txtCommande.getText().length() == 0)
							idHistorique = historique.size();
					}
					
					if(envoiPossible()) {
						if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							envoyerCommande(txtCommande.getText());
						}
					}
				}
			});
			
			txtCommande.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					if(txtCommande.getText().equals("Envoyer une commande...")) {
						txtCommande.setForeground(Color.BLACK);
						txtCommande.setText("");
					}
				}
				@Override
				public void focusLost(FocusEvent e) {
					if(txtCommande.getText().length() == 0) {
						txtCommande.setForeground(Color.DARK_GRAY);
						txtCommande.setText("Envoyer une commande...");
					}
				}
			});
			txtCommande.setText("Envoyer une commande...");
			txtCommande.setForeground(Color.DARK_GRAY);
			txtCommande.setFont(new Font("Consolas", Font.PLAIN, 15));
			txtCommande.setBounds(10, 524, 427, 32);
			txtCommande.setColumns(10);
		}
		return txtCommande;
	}
	private JButton getBtnEnvoi() {
		if (btnEnvoi == null) {
			btnEnvoi = new JButton("");
			btnEnvoi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					envoyerCommande(txtCommande.getText());
				}
			});
			btnEnvoi.setToolTipText("Envoyer la commande");
			btnEnvoi.setEnabled(false);
			btnEnvoi.setIcon(new ImageIcon(MainClient.class.getResource("/vue/icone_envoi.png")));
			btnEnvoi.setBounds(447, 524, 38, 32);
		}
		return btnEnvoi;
	}

	// L'envoi d'une commande est possible lorsque le champ n'est pas vide
	private boolean envoiPossible() {
		boolean res = txtCommande.getText().trim().length() != 0;
		btnEnvoi.setEnabled(res);
		return res;
	}
	
	private void envoyerCommande(String commande) {
		txtCommande.setText("");
		envoiPossible();
		historique.add(commande);
		idHistorique = historique.size();
		
		if(commande.equals("clear")) {
			txtCommandes.setText("");
		} else {
			txtCommandes.append(commande + "\n");
			
			if(commande.equals("help")) {
				txtCommandes.append("Informations concernant l'ensemble des commandes exécutables sur ce logiciel.\n");
				txtCommandes.append("Un argument entouré de crochets est un argument facultatif.\n\n");
				
				txtCommandes.append("- user nom         Envoie le nom de compte du client\n");
				txtCommandes.append("- pass mdp         Envoie le mot de passe du compte du client\n");
				txtCommandes.append("- cd [chemin]      Accède au chemin demandé s'il existe\n");
				txtCommandes.append("- ls [chemin]      Affiche le contenu du dossier demandé s'il existe\n");
				txtCommandes.append("- pwd              Affiche le chemin relatif au dossier courant\n");
				txtCommandes.append("- stor fichier     Envoie un fichier présent dans le dossier du client (root), vers le dossier courant du serveur\n");
				txtCommandes.append("- get fichier      Télécharge un fichier présent dans le dossier courant du serveur, vers le dossier du client (root)\n");
				txtCommandes.append("- mkdir dossier    Crée le dossier demandé s'il n'existe pas\n");
				txtCommandes.append("- rmdir dossier    Supprime le dossier demandé s'il est vide\n");
				txtCommandes.append("- clear            Efface le texte affiché sur la sortie\n\n");
				txtCommandes.append("- bye              Arrête la communication avec le serveur FTP\n\n");
			} else {
				try {
					Traitement.envoyerCommande(commande);
				} catch (IOException e) {
					
				}
			}
		}
		
		// Auto scroll
		txtCommandes.setCaretPosition(txtCommandes.getDocument().getLength());
	}
}