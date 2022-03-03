package vue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class MainClient {
	
	private static boolean connecte = false;
	
	// Exécution de la commande STOR
	private static void cmdSTOR(PrintStream ps, String commande, BufferedReader br) throws IOException {
		// Si le nom du fichier contient un ou plusieurs '/'
		if(commande.split(" ")[1].contains("/")) {
			System.out.println("Le nom du fichier est invalide");
		} else {
			String chemin = "root/" + commande.split(" ")[1];
			File fichier = new File(chemin);
			
			if(fichier.exists() && fichier.isFile()) {
				ps.println(commande);
				String dernierMsg = msgServeur(br);
				
				// Le fichier n'a pas pu être créé par le serveur
				if(dernierMsg.startsWith("2")) {
					System.out.println("L'envoi a été abandonné");
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
					
					System.out.println("Le fichier " + fichier.getName() + " a bien été envoyé au serveur");
				}
			} else {
				System.out.println("Le fichier " + fichier.getName() + " n'existe pas ou il s'agit d'un dossier");
			}
		}
	}
	
	// Affichage des messages du serveur et retour de la dernière ligne
	private static String msgServeur(BufferedReader br) throws IOException {
		String ligne;
		
		while(true) {
			ligne = br.readLine();
			
			if(!connecte && ligne.equals("1 Commande pass OK")) connecte = true;
			
			System.out.println(ligne);
			if(ligne.startsWith("0") || ligne.startsWith("2"))
				break;
		}
		return ligne;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Le Client FTP");
		
		try {
			Socket socket = new Socket("localhost", 2121);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			msgServeur(br);
			
			PrintStream ps = new PrintStream(socket.getOutputStream());
			Scanner scan = new Scanner(System.in);

			String commande;
			
			// Envoi des commandes
			while(true) {
				if((commande = scan.nextLine()).equals("bye")) {
					break;
				}
				
				// Si le client écrit la commande STOR, le traitement sera différent
				if(commande.split(" ")[0].equals("stor")) {
					if(connecte) {
						if(commande.split(" ").length == 2) {
							cmdSTOR(ps, commande, br);
						} else {
							System.out.println("La commande STOR attend 2 arguments : STOR <fichier>");
						}
					} else {
						System.out.println("2 Vous n'êtes pas connecté !");
					}
				} else {
					ps.println(commande);
					msgServeur(br);
				}
			}
			ps.println("bye");
			System.out.println("Arrêt de la communication avec le serveur FTP.");
			
			scan.close();
			socket.close();
		} catch(Exception e) {
			if(e instanceof ConnectException || e instanceof SocketException) {
				System.out.println("Le serveur FTP est déconnecté.");
			}
		}
	}

}