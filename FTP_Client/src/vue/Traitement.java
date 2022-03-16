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
import java.net.Socket;
import java.net.SocketException;

public class Traitement {
	
	public static boolean serveurConnecte = false;
	public static boolean connecte = false;
	
	private static BufferedReader br;
	private static PrintStream ps;
	
	// Initialise les variables aux composants de l'interface et crée la communication avec le serveur FTP (socket)
	public static void execute() {
		Socket socket = null;
		try {
			socket = new Socket("localhost", 2121);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ps = new PrintStream(socket.getOutputStream());
			
			msgServeur();
			serveurConnecte = true;
		} catch (Exception e) {
			Connexion.desactiverForm();
			
			if(e instanceof SocketException)
				Connexion.afficherMessage("Le serveur n'est pas connecté");
			else
				Connexion.afficherMessage("Problème de communication avec le serveur");
		}
	}
	
	// Affiche les messages du serveur FTP et retourne la dernière ligne
	public static String msgServeur() {
		String ligne = "";
		
		try {
			while(true) {
				ligne = br.readLine();
				
				if(ligne.startsWith("0") || ligne.startsWith("2")) {
					if(!connecte)
						Connexion.msgServeur = ligne.charAt(0);
					else
						MainClient.setMsgServeur(ligne);
					break;
				}
			}
		} catch (IOException e) {
			serveurConnecte = false;
			if(!connecte) {
				Connexion.desactiverForm();
				Connexion.afficherMessage("Erreur de réception de la réponse du serveur, serveur déconnecté");
			} else {
				MainClient.desactiverComposants();
				MainClient.afficherMessage("Erreur de réception de la réponse du serveur, serveur déconnecté");
			}
		}
		return ligne;
	}
	
	// Envoie la commande demandée au serveur FTP
	public static void envoyerCommande(String commande, String chemin) {
		// Les commandes ne seront pas envoyées si le serveur n'est pas connecté
		if(serveurConnecte) {
			if(commande.equals("bye")) {
				ps.println(commande);
			} else {
				if(commande.equals("stor")) {
					cmdSTOR(commande, chemin);
				} else if(commande.equals("get")) {
					//cmdGET(commande, chemin);
				} else {
					ps.println(commande + " " + chemin);
					msgServeur();
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
				MainClient.afficherMessage("L'envoi a été abandonné");
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
					
					MainClient.afficherMessage("Le fichier " + fichier.getName() + " a bien été envoyé au serveur");
				} catch(IOException e) {
					MainClient.afficherMessage("Erreur de communication avec le serveur");
				}
			}
		} else {
			MainClient.afficherMessage("Le fichier " + fichier.getName() + " n'existe pas ou il s'agit d'un dossier");
		}
	}
	
	// Exécute la commande GET
	private static void cmdGET(String commande) {
		// Si le nom du fichier contient un ou plusieurs '/'
		if(commande.split(" ")[1].contains("/")) {
			MainClient.afficherMessage("Le nom du fichier est invalide");
		} else {
			String chemin = "root/" + commande.split(" ")[1];
			File fichier = new File(chemin);
			
			ps.println(commande);
			String dernierMsg = msgServeur();
			
			// Le fichier n'existe pas côté serveur
			if(dernierMsg.startsWith("2")) {
				MainClient.afficherMessage("Le fichier n'existe pas");
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
					
					MainClient.afficherMessage("Le fichier " + fichier.getName() + " a bien été téléchargé");
				} catch(IOException e) {
					MainClient.afficherMessage("Erreur de communication avec le serveur");
				}
			}
		}
	}
}