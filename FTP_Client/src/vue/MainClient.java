package vue;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

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

public class MainClient extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JLabel lblClient;
	private JLabel lblServeur;
	private JSplitPane splitClientServeur;
	private JScrollPane scrollClient;
	private JScrollPane scrollServeur;
	private JScrollPane scrollLog;
	private JTextArea txtLog;
	private JTree treeClient;
	private JTree treeServeur;
	private JPopupMenu menuClient;
	private JMenuItem mntmClientEnvoyerFichier;
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

	private JLabel getLblClient() {
		if (lblClient == null) {
			lblClient = new JLabel("Fichiers du client");
			lblClient.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblClient.setBounds(10, 11, 120, 19);
		}
		return lblClient;
	}

	private JLabel getLblServeur() {
		if (lblServeur == null) {
			lblServeur = new JLabel("Fichiers du serveur");
			lblServeur.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblServeur.setBounds(836, 13, 128, 15);
		}
		return lblServeur;
	}

	private JSplitPane getSplitClientServeur() {
		if (splitClientServeur == null) {
			splitClientServeur = new JSplitPane();
			splitClientServeur.setResizeWeight(0.4);
			splitClientServeur.setBounds(10, 41, 954, 347);
			splitClientServeur.setLeftComponent(getScrollClient());
			splitClientServeur.setRightComponent(getScrollServeur());
		}
		return splitClientServeur;
	}

	private JScrollPane getScrollClient() {
		if (scrollClient == null) {
			scrollClient = new JScrollPane();
			scrollClient.setViewportView(getTreeClient());
		}
		return scrollClient;
	}

	private JScrollPane getScrollServeur() {
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

	private JTextArea getTxtLog() {
		if (txtLog == null) {
			txtLog = new JTextArea();
			txtLog.setEditable(false);
		}
		return txtLog;
	}

	private JTree getTreeClient() {
		if (treeClient == null) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
	        addChilds(root, "root");
			treeClient = new JTree(new DefaultTreeModel(root));
			addPopup(treeClient, getMenuClient());
		}
		return treeClient;
	}

	private JTree getTreeServeur() {
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
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	private JMenuItem getMntmClientEnvoyerFichier() {
		if (mntmClientEnvoyerFichier == null) {
			mntmClientEnvoyerFichier = new JMenuItem("Envoyer le fichier");
		}
		return mntmClientEnvoyerFichier;
	}

	private JMenuItem getMntmClientActualiser() {
		if (mntmClientActualiser == null) {
			mntmClientActualiser = new JMenuItem("Actualiser");
		}
		return mntmClientActualiser;
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
	
	private JLabel getLblLog() {
		if (lblLog == null) {
			lblLog = new JLabel("Message du serveur");
			lblLog.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblLog.setBounds(10, 407, 141, 19);
		}
		return lblLog;
	}
	
	
	
	
	
    private void addChilds(DefaultMutableTreeNode rootNode, String path) {
        File[] files = new File(path).listFiles();
        for(File file:files) {
            if(file.isDirectory()) {
                DefaultMutableTreeNode subDirectory = new DefaultMutableTreeNode(file.getName());
                addChilds(subDirectory, file.getAbsolutePath());
                rootNode.add(subDirectory);
            } else {
                rootNode.add(new DefaultMutableTreeNode(file.getName()));
            }
        }
    }
}