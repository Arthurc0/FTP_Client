package vue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {
	
	// Affichage des messages du serveur
	private static void msgServeur(BufferedReader br) throws IOException {
		String ligne;
		
		while(true) {
			ligne = br.readLine();
			System.out.println(ligne);
			if(ligne.split(" ")[0].equals("0") || ligne.split(" ")[0].equals("2"))
				break;
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Le Client FTP");
		
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
			ps.println(commande);
			msgServeur(br);
		}
		ps.println("bye");
		System.out.println("Arrêt de la communication avec le serveur FTP.");
		
		scan.close();
		socket.close();
	}

}