package vue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Traitement {
	
	private static JTextArea txtCommandes;
	private static JTextField entreeCmd;
	private static JButton btnEnvoiCmd;
	
	private static boolean connecte = false;
	
	private static PrintStream ps;
	private static BufferedReader br;
	private static Socket socket;
	
	// Initialise les variables aux composants de l'interface et crée la communication avec le serveur FTP (socket)
	public static void execution(JTextArea texte, JTextField commande, JButton btn) {
		txtCommandes = texte;
		entreeCmd = commande;
		btnEnvoiCmd = btn;
		
		afficherMessage("Le Client FTP");
		
		try {
			socket = new Socket("localhost", 2121);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			msgServeur();
			
			ps = new PrintStream(socket.getOutputStream());
		} catch(Exception e) {
			if(e instanceof ConnectException || e instanceof SocketException) {
				entreeCmd.setEnabled(false);
				btnEnvoiCmd.setEnabled(false);
				afficherMessage("Le serveur FTP est déconnecté.");
			}
		}
	}
	
	// Exécute la commande STOR
	private static void cmdSTOR(String commande) throws IOException {
		// Si le nom du fichier contient un ou plusieurs '/'
		if(commande.split(" ")[1].contains("/")) {
			afficherMessage("Le nom du fichier est invalide");
		} else {
			String chemin = "root/" + commande.split(" ")[1];
			File fichier = new File(chemin);
			
			if(fichier.exists() && fichier.isFile()) {
				ps.println(commande);
				String dernierMsg = msgServeur();
				
				// Le fichier n'a pas pu être créé par le serveur
				if(dernierMsg.startsWith("2")) {
					afficherMessage("L'envoi a été abandonné");
				} else {
					// Récupération du port désigné par le serveur
					int port = Integer.parseInt(dernierMsg.split(" ")[1]);
					Socket socketSTOR = new Socket("localhost", port);
					
					byte[] buffer = new byte[4*1024];
					
					BufferedInputStream contenuFichier = new BufferedInputStream(new FileInputStream(fichier));
					OutputStream contenuSocket = socketSTOR.getOutputStream();
					
					int nbOctetsLus = -1;
					
					// Envoi du contenu du fichier par paquet de 4 Ko
					while((nbOctetsLus = contenuFichier.read(buffer)) > 0) {
						contenuSocket.write(buffer, 0, nbOctetsLus);
					}
					
					contenuFichier.close();
					contenuSocket.close();
					socketSTOR.close();
					
					afficherMessage("Le fichier " + fichier.getName() + " a bien été envoyé au serveur");
				}
			} else {
				afficherMessage("Le fichier " + fichier.getName() + " n'existe pas ou il s'agit d'un dossier");
			}
		}
	}
	
	// Exécute la commande GET
	private static void cmdGET(String commande) throws IOException {
		// Si le nom du fichier contient un ou plusieurs '/'
		if(commande.split(" ")[1].contains("/")) {
			afficherMessage("Le nom du fichier est invalide");
		} else {
			String chemin = "root/" + commande.split(" ")[1];
			File fichier = new File(chemin);
			
			
			ps.println(commande);
			String dernierMsg = msgServeur();
			
			// Le fichier n'existe pas côté serveur
			if(dernierMsg.startsWith("2")) {
				afficherMessage("Le fichier n'existe pas");
			} else {
				//Créer un nouveau fichier s'il n'existe pas
				fichier.createNewFile();
				
				// Récupération du port désigné par le serveur
				int port = Integer.parseInt(dernierMsg.split(" ")[1]);
				Socket socketGET = new Socket("localhost", port);
				
				byte[] buffer = new byte[4*1024];
				
				BufferedOutputStream contenuFichier = new BufferedOutputStream(new FileOutputStream(fichier));
				InputStream contenuSocket = socketGET.getInputStream();
				
				int nbOctetsLus = -1;
				
				// Envoi du contenu du fichier par paquet de 4 Ko
				while((nbOctetsLus = contenuSocket.read(buffer)) > 0) {
					contenuFichier.write(buffer, 0, nbOctetsLus);
				}
				
				contenuFichier.close();
				contenuSocket.close();
				socketGET.close();
				
				afficherMessage("Le fichier " + fichier.getName() + " a bien été téléchargé");
			}
		}
	}
	
	// Affiche les messages du serveur FTP et retourne la dernière ligne
	private static String msgServeur() throws IOException {
		String ligne;
		
		while(true) {
			ligne = br.readLine();
			
			if(!connecte && ligne.equals("1 Commande pass OK")) connecte = true;
			
			afficherMessage(ligne);
			if(ligne.startsWith("0") || ligne.startsWith("2"))
				break;
		}
		return ligne;
	}
	
	// Envoie la commande demandée au serveur FTP
	public static void envoyerCommande(String commande) throws IOException {
		if(commande.equals("bye")) {
			entreeCmd.setEnabled(false);
			btnEnvoiCmd.setEnabled(false);
			ps.println(commande);
			socket.close();
			afficherMessage("Arrêt de la communication avec le serveur FTP.");
		}
		
		// Si le client écrit la commande STOR, le traitement sera différent
		if(commande.split(" ")[0].equals("stor") || commande.split(" ")[0].equals("get")) {
			if(connecte) {
				if(commande.split(" ").length == 2 && commande.split(" ")[0].equals("stor")) {
					cmdSTOR(commande);
				} 
				else if(commande.split(" ").length == 2 && commande.split(" ")[0].equals("get")){
					cmdGET(commande);
				} else {
					afficherMessage("La commande " + commande.split(" ")[0] + " attend 2 arguments : " + commande.split(" ")[0] + " <fichier>");
				}
			} else {
				afficherMessage("2 Vous n'êtes pas connecté !");
			}
		} else {
			ps.println(commande);
			msgServeur();
		}
	}
	
	// Affiche le message demandé dans la zone d'affichage du texte
	private static void afficherMessage(String message) {
		txtCommandes.append(message + "\n");
	}
}