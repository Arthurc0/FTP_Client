package vue;

import java.awt.EventQueue;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class MainClient extends JFrame{
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
	private JPopupMenu menuClient;
	private static JMenuItem mntmClientEnvoyerFichier;
	private JMenuItem mntmClientActualiser;
	private JLabel lblLog;
	private JLabel lblPwd;
	
	private JList<Object> treeServeur;
	private static String msgServeur;

	private DefaultListModel<Object> ListeServeur = new DefaultListModel<Object>();
	private JPopupMenu menuServeur;
	private JMenuItem mntmServeurTelechargerFichier;
	private JMenuItem mntmServeurActualiser;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public MainClient() {
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainClient.class.getResource("/vue/images/icone.png")));
		setTitle("Client FTP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 980, 607);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getLblClient());
		contentPane.add(getLblServeur());
		contentPane.add(getLblLog());
		contentPane.add(getScrollLog());
		contentPane.add(getSplitClientServeur());
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
	
	private JScrollPane getScrollLog() {
		if (scrollLog == null) {
			scrollLog = new JScrollPane();
			scrollLog.setBounds(10, 437, 954, 130);
			scrollLog.setViewportView(getTxtLog());
		}
		return scrollLog;
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
			scrollServeur.setColumnHeaderView(getLblPwd());
		}
		return scrollServeur;
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
			addPopupClient(treeClient, getMenuClient());
		}
		return treeClient;
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
	
	private static void addPopupClient(Component component, final JPopupMenu popup) {
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
	
	private static void addPopupServeur(Component component, final JPopupMenu popup) {
		JList tree = (JList)component;
		
		component.addMouseListener(new MouseAdapter() {
			
			public void mouseReleased(MouseEvent e) {
				
				int elementSelectionne = tree.locationToIndex(e.getPoint());
				
				if(elementSelectionne != -1)
					tree.setSelectedIndex(elementSelectionne);
								
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


	//Construire les données du serveur
	private static class donneeServeur{
		private String type;
		private String name;
		private Icon icon;
		
		donneeServeur(String type, String name){
			this.type = type;
			this.name = name;
		}

		public Icon getIcon() {
			return icon;
		}

		public void setIcon(Icon icon) {
			this.icon = icon;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
		
	}
	
	public JList<Object> getTreeServeur() {
		if (treeServeur == null) {
			treeServeur = new JList<Object>(ListeServeur);
			treeServeur.setFont(new Font("Tahoma", Font.PLAIN, 10));
			treeServeur.setForeground(Color.BLACK);
			treeServeur.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			addPopupServeur(treeServeur, getMenuServeur());
			
			remplirListeServeur();
			
			treeServeur.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					//S'assurer d'un double click
					if (e.getClickCount() == 2 && !e.isConsumed()) {
					     e.consume();
						if(!treeServeur.isSelectionEmpty()) {
							donneeServeur donnee = (donneeServeur) treeServeur.getSelectedValue();
							if(donnee.getType().equals("d")) {
								Traitement.envoyerCommande("cd", donnee.toString());
								actualiserPwd();
								remplirListeServeur();
							}

							if(donnee.getType().equals("r")) {
								Traitement.envoyerCommande("cd", "..");
								actualiserPwd();
								remplirListeServeur();
							}
						}
					}
				}
			});
			
			//Click droit
			treeServeur.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if(treeServeur.getSelectedValue() != null) {
						donneeServeur donnee = (donneeServeur)treeServeur.getSelectedValue();
						mntmServeurTelechargerFichier.setEnabled(donnee.getType() == "f");
					}
					else
						mntmServeurTelechargerFichier.setEnabled(false);
					
				}
			});

		}
		return treeServeur;
	}
	
	private void remplirListeServeur() {
		
		Traitement.envoyerCommande("ls", "");
		String elements[] = msgServeur.split(" ");
		ListeServeur.removeAllElements();
		
		if(getLblPwd().getText().split("/").length > 2)
			ListeServeur.addElement(new donneeServeur("r", ".."));
		
		//Afficher contenu du dossier courant
		for(int i=2; i<elements.length; i++) {
			ListeServeur.addElement(new donneeServeur(elements[i].split("-")[0], elements[i].split("-")[1]));
		}
	}
	
	public static String getMsgServeur() {
		return msgServeur;
	}

	public static void setMsgServeur(String msgServeur) {
		MainClient.msgServeur = msgServeur;
	}
	
	private JLabel getLblPwd() {
		if (lblPwd == null) {
			Traitement.envoyerCommande("pwd", "");
			lblPwd = new JLabel(msgServeur.split(" ")[1]);
			lblPwd.setBackground(UIManager.getColor("InternalFrame.inactiveTitleGradient"));

		}
		return lblPwd;
	}
	
	private void actualiserPwd() {

		Traitement.envoyerCommande("pwd", "");
		lblPwd.setText(msgServeur.split(" ")[1]);
	}
	
	private JPopupMenu getMenuServeur() {
		if (menuServeur == null) {
			menuServeur = new JPopupMenu();
			menuServeur.add(getMntmServeurTelechargerFichier());
			menuServeur.add(getMntmServeurActualiser());
		}
		return menuServeur;
	}
	
	private JMenuItem getMntmServeurTelechargerFichier() {
		if (mntmServeurTelechargerFichier == null) {
			mntmServeurTelechargerFichier = new JMenuItem("Télécharger le fichier");
		}
		return mntmServeurTelechargerFichier;
	}
	
	private JMenuItem getMntmServeurActualiser() {
		if (mntmServeurActualiser == null) {
			mntmServeurActualiser = new JMenuItem("Actualiser");
			mntmServeurActualiser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					actualiserPwd();
					remplirListeServeur();
				}
			});
		}
		return mntmServeurActualiser;
	}
}