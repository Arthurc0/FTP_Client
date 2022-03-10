package vue;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.Toolkit;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainClient extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JLabel lblClient;
	private JLabel lblServeur;
	private JSplitPane splitClientServeur;
	private JScrollPane scrollClient;
	private JScrollPane scrollServeur;
	private JScrollPane scrollLog;
	public static JTextArea txtLog;
	private JTree treeClient;
	private JTree treeServeur;
	private JPopupMenu menuClient;
	private static JMenuItem mntmClientEnvoyerFichier;
	private JMenuItem mntmClientActualiser;
	private JPopupMenu menuServeur;
	private JMenuItem mntmServeurCreerDossier;
	private JMenuItem mntmServeurSupprimerDossier;
	private JMenuItem mntmServeurTelecharger;
	private JMenuItem mntmServeurActualiser;
	private JLabel lblLog;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainClient frame = new MainClient();
					frame.setVisible(true);
					Traitement.execute(frame);
					
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							// Si la communication avec le serveur est toujours en cours
							if(Traitement.serveurConnecte)
								Traitement.envoyerCommande("bye", "");
						}
					});
					
					Traitement.envoyerCommande("user", "arthur");
					Traitement.envoyerCommande("pass", "mdpArthur");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainClient() {
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainClient.class.getResource("/vue/icone.png")));
		setTitle("Client FTP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 980, 607);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getLblClient());
		contentPane.add(getLblServeur());
		contentPane.add(getSplitClientServeur());
		contentPane.add(getScrollLog());
		contentPane.add(getLblLog());
	}

	public JLabel getLblClient() {
		if (lblClient == null) {
			lblClient = new JLabel("Fichiers du client");
			lblClient.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblClient.setBounds(10, 11, 120, 19);
		}
		return lblClient;
	}

	public JLabel getLblServeur() {
		if (lblServeur == null) {
			lblServeur = new JLabel("Fichiers du serveur");
			lblServeur.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblServeur.setBounds(836, 13, 128, 15);
		}
		return lblServeur;
	}

	public JSplitPane getSplitClientServeur() {
		if (splitClientServeur == null) {
			splitClientServeur = new JSplitPane();
			splitClientServeur.setResizeWeight(0.4);
			splitClientServeur.setBounds(10, 41, 954, 347);
			splitClientServeur.setLeftComponent(getScrollClient());
			splitClientServeur.setRightComponent(getScrollServeur());
		}
		return splitClientServeur;
	}

	public JScrollPane getScrollClient() {
		if (scrollClient == null) {
			scrollClient = new JScrollPane();
			scrollClient.setViewportView(getTreeClient());
		}
		return scrollClient;
	}

	public JScrollPane getScrollServeur() {
		if (scrollServeur == null) {
			scrollServeur = new JScrollPane();
			scrollServeur.setViewportView(getTreeServeur());
		}
		return scrollServeur;
	}

	private JScrollPane getScrollLog() {
		if (scrollLog == null) {
			scrollLog = new JScrollPane();
			scrollLog.setBounds(10, 437, 954, 130);
			scrollLog.setViewportView(getTxtLog());
		}
		return scrollLog;
	}

	public JTextArea getTxtLog() {
		if (txtLog == null) {
			txtLog = new JTextArea();
			txtLog.setFont(new Font("Monospaced", Font.PLAIN, 15));
			txtLog.setEditable(false);
		}
		return txtLog;
	}

	public JTree getTreeClient() {
		if (treeClient == null) {
			DefaultMutableTreeNode rootClient = new DefaultMutableTreeNode("root");
			ajouterNoeuds(rootClient, "root");
			treeClient = new JTree(new DefaultTreeModel(rootClient));
			treeClient.setRootVisible(false);
			treeClient.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent arg0) {
					if(treeClient.getLastSelectedPathComponent() != null) {
						String chemin = "root/" + treeClient.getLastSelectedPathComponent().toString();
						
						File f = new File(chemin);
						activerItemsClient(f.isFile() && f.exists());
					} else {
						activerItemsClient(false);
					}
					
				}
			});
			addPopup(treeClient, getMenuClient());
		}
		return treeClient;
	}
	
	public JTree getTreeServeur() {
		if (treeServeur == null) {
			treeServeur = new JTree();
			addPopup(treeServeur, getMenuServeur());
		}
		return treeServeur;
	}

	private JPopupMenu getMenuClient() {
		if (menuClient == null) {
			menuClient = new JPopupMenu();
			menuClient.setLabel("");
			menuClient.add(getMntmClientEnvoyerFichier());
			menuClient.add(getMntmClientActualiser());
		}
		return menuClient;
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		JTree tree = (JTree)component;
		
		component.addMouseListener(new MouseAdapter() {
			
			public void mouseReleased(MouseEvent e) {
				// Clic sur les éléments de l'arbre avec le clic droit
				int elementSelectionne = tree.getRowForLocation(e.getX(), e.getY());
				TreePath cheminElement = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(cheminElement);
				
                if(elementSelectionne > -1) {
                	tree.setSelectionRow(elementSelectionne);
                } else {
                	activerItemsClient(false);
                }
				
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				if(Traitement.serveurConnecte)
					popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private JMenuItem getMntmClientEnvoyerFichier() {
		if (mntmClientEnvoyerFichier == null) {
			mntmClientEnvoyerFichier = new JMenuItem("Envoyer le fichier");
			mntmClientEnvoyerFichier.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Traitement.envoyerCommande("stor", treeClient.getLastSelectedPathComponent().toString());
				}
			});
			mntmClientEnvoyerFichier.setEnabled(false);
		}
		return mntmClientEnvoyerFichier;
	}

	private JMenuItem getMntmClientActualiser() {
		if (mntmClientActualiser == null) {
			mntmClientActualiser = new JMenuItem("Actualiser");
			mntmClientActualiser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					actualiserClient();
				}
			});
		}
		return mntmClientActualiser;
	}
	
	public void actualiserClient() {
		DefaultTreeModel model = (DefaultTreeModel)treeClient.getModel();
		DefaultMutableTreeNode rootClient = (DefaultMutableTreeNode)model.getRoot();
		rootClient.removeAllChildren();
		
		ajouterNoeuds(rootClient, "root");
		model.reload(rootClient);
	}
	
	private JPopupMenu getMenuServeur() {
		if (menuServeur == null) {
			menuServeur = new JPopupMenu();
			menuServeur.add(getMntmServeurCreerDossier());
			menuServeur.add(getMntmServeurSupprimerDossier());
			menuServeur.add(getMntmServeurTelecharger());
			menuServeur.add(getMntmServeurActualiser());
		}
		return menuServeur;
	}

	private JMenuItem getMntmServeurCreerDossier() {
		if (mntmServeurCreerDossier == null) {
			mntmServeurCreerDossier = new JMenuItem("Créer un dossier");
		}
		return mntmServeurCreerDossier;
	}

	private JMenuItem getMntmServeurSupprimerDossier() {
		if (mntmServeurSupprimerDossier == null) {
			mntmServeurSupprimerDossier = new JMenuItem("Supprimer le dossier");
		}
		return mntmServeurSupprimerDossier;
	}

	private JMenuItem getMntmServeurTelecharger() {
		if (mntmServeurTelecharger == null) {
			mntmServeurTelecharger = new JMenuItem("Télécharger le fichier");
		}
		return mntmServeurTelecharger;
	}

	private JMenuItem getMntmServeurActualiser() {
		if (mntmServeurActualiser == null) {
			mntmServeurActualiser = new JMenuItem("Actualiser");
		}
		return mntmServeurActualiser;
	}
	
	public JLabel getLblLog() {
		if (lblLog == null) {
			lblLog = new JLabel("Message du serveur");
			lblLog.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblLog.setBounds(10, 407, 141, 19);
		}
		return lblLog;
	}
	
	private static void activerItemsClient(boolean val) {
		mntmClientEnvoyerFichier.setEnabled(val);
	}
	
	private void ajouterNoeuds(DefaultMutableTreeNode noeudParent, String chemin) {
        File[] fichiers = new File(chemin).listFiles();
        
        for(File fichier : fichiers) {
        	DefaultMutableTreeNode noeud = new DefaultMutableTreeNode(fichier.getName());
        	
            if(fichier.isFile()) {
            	noeudParent.add(noeud);
            }
        }
    }
}