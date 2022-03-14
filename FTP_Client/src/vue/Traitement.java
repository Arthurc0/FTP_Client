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

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Traitement {
	
	private static JTextArea txtLog;
	private static String txtConnexion;
	
	public static boolean serveurConnecte = false;
	public static boolean connecte = false;
	
	private static PrintStream ps;
	private static BufferedReader br;
	private static Socket socket;
	
	
	// Initialise les variables aux composants de l'interface et crée la communication avec le serveur FTP (socket)
	public static void execute(JFrame frame, boolean connecte) {
		Connexion frameConnexion = null;
		MainClient frameMain = null;
		
		if(!connecte) {
			frameConnexion = (Connexion)frame;
			txtConnexion = frameConnexion.getMsgServeur();
		}
		else {
			frameMain = (MainClient)frame;
			txtLog = frameMain.getTxtLog();
			
			afficherMessage("Le Client FTP");
		}
		
		try {
			if(!connecte) {
				socket = new Socket("localhost", 2121);
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				msgServeurString();
				
				ps = new PrintStream(socket.getOutputStream());
				serveurConnecte = true;
			}
			
		} catch(Exception e) {
			if(e instanceof ConnectException || e instanceof SocketException) {
				Connexion.setMsgServeur("Le serveur FTP est déconnecté");

				serveurConnecte = false;
				
				if(!connecte) {
					// Désactiver les composants qui ne doivent plus être utilisables
					frameConnexion.getBtnConnect().setEnabled(false);
					frameConnexion.getTextUser().setEnabled(false);
					frameConnexion.getTextPass().setEnabled(false);
					frameConnexion.getLblErrorMessage().setText("Le serveur n'est pas connecté");
				}
				else {
					afficherMessage("Le serveur FTP est déconnecté");
					
					// Désactiver les composants qui ne doivent plus être utilisables
					frameMain.getLblClient().setEnabled(false);
					frameMain.getLblServeur().setEnabled(false);
					frameMain.getSplitClientServeur().setEnabled(false);
					frameMain.getScrollClient().setEnabled(false);
					frameMain.getTreeClient().setEnabled(false);
					frameMain.getScrollServeur().setEnabled(false);
					frameMain.getTreeServeur().setEnabled(false);
				}
			}
		}
	}
	
	
	// Exécute la commande STOR
	private static void cmdSTOR(String commande, String chemin) {
		File fichier = new File("root/" + chemin);
		
		if(fichier.exists() && fichier.isFile()) {
			ps.println(commande + " " + chemin);
			String dernierMsg = msgServeur();
			
			// Le fichier n'a pas pu être créé par le serveur
			if(dernierMsg.startsWith("2")) {
				afficherMessage("L'envoi a été abandonné");
			} else {
				try {
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
				} catch(IOException e) {
					afficherMessage("Erreur de communication avec le serveur");
				}
			}
		} else {
			afficherMessage("Le fichier " + fichier.getName() + " n'existe pas ou il s'agit d'un dossier");
		}
	}
	
	// Exécute la commande GET
	private static void cmdGET(String commande) {
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
				try {
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
				} catch(IOException e) {
					afficherMessage("Erreur de communication avec le serveur");
				}
			}
		}
	}
	
	// Affiche les messages du serveur FTP et retourne la dernière ligne
	public static String msgServeur() {
		String ligne;
		
		while(true) {
			try {
				ligne = br.readLine();
				
				afficherMessage(ligne);
				if(ligne.startsWith("0") || ligne.startsWith("2"))
					break;
				
			} catch (IOException e) {
				afficherMessage("Erreur de réception de la réponse du serveur");
			}
		}
		return ligne;
	}
	
	// Affiche les messages du serveur FTP à l'interface de connexion et retourne la dernière ligne
	public static String msgServeurString() {
		String ligne;
		
		while(true) {
			try {
				ligne = br.readLine();
				
				if(ligne.startsWith("0") || ligne.startsWith("2")) {
					if(!connecte)
						Connexion.setMsgServeur(ligne);
					else
						MainClient.setMsgServeur(ligne);
					break;
				}
					
				
			} catch (IOException e) {
				Connexion.setMsgServeur("Erreur de réception de la réponse du serveur");
			}
		}
		return ligne;
	}
	
	// Envoie la commande demandée au serveur FTP
	public static void envoyerCommande(String commande, String chemin){
		/*
		try {
			if(socket.getInputStream().read() == -1) {
				serveurConnecte = false;
			}
		} catch (IOException e1) {
			serveurConnecte = false;
		}
		*/
		if(serveurConnecte) {
			
			if(commande.equals("bye")) {
				ps.println(commande);
				
				try {
					socket.close();
				} catch (IOException e) {
					
				}
			} else {
				if(commande.equals("stor")) {
					cmdSTOR(commande, chemin);
				} else if(commande.equals("get")) {
					//cmdGET(commande, chemin);
				} else if(commande.equals("user") || commande.equals("pass") || commande.equals("pwd") || commande.equals("ls")) {
					ps.println(commande + " " + chemin);
					msgServeurString();
				} else {
					ps.println(commande + " " + chemin);
					msgServeur();
				}
			}
		}
		else {
			System.out.println("Serveur deconnecté");
		}
	}
	
	// Affiche le message demandé dans la zone d'affichage du texte
	private static void afficherMessage(String message) {
		txtLog.append(message + "\n");
		
		// Auto scroll
		txtLog.setCaretPosition(txtLog.getDocument().getLength());
	}
	
}