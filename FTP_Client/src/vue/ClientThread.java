package vue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class ClientThread implements Runnable {
	
	@Override
	public void run() {
		//Thread.currentThread().setName(String.valueOf(Client.idClient - 1));
		try {
			Traitement.socket = new Socket("localhost", 2121);
			Traitement.br = new BufferedReader(new InputStreamReader(Traitement.socket.getInputStream()));
			
			Traitement.msgServeurString();
			
			Traitement.ps = new PrintStream(Traitement.socket.getOutputStream());
			Traitement.serveurConnecte = true;
			
			while(true) {
				
			}
		} catch(Exception e) {
			System.out.println("yooo");
			Traitement.serveurConnecte = false;
			/*if(e instanceof ConnectException || e instanceof SocketException) {
				Connexion.setMsgServeur("Le serveur FTP est déconnecté");

				Traitement.serveurConnecte = false;
				
				if(!Traitement.connecte) {
					// Désactiver les composants qui ne doivent plus être utilisables
					Traitement.frameConnexion.getBtnConnect().setEnabled(false);
					Traitement.frameConnexion.getTextUser().setEnabled(false);
					Traitement.frameConnexion.getTextPass().setEnabled(false);
					Traitement.frameConnexion.getLblErrorMessage().setText("Le serveur n'est pas connecté");
				}
				else {
					Traitement.afficherMessage("Le serveur FTP est déconnecté");
					
					// Désactiver les composants qui ne doivent plus être utilisables
					Traitement.frameMain.getLblClient().setEnabled(false);
					Traitement.frameMain.getLblServeur().setEnabled(false);
					Traitement.frameMain.getSplitClientServeur().setEnabled(false);
					Traitement.frameMain.getScrollClient().setEnabled(false);
					Traitement.frameMain.getTreeClient().setEnabled(false);
					Traitement.frameMain.getScrollServeur().setEnabled(false);
					Traitement.frameMain.getTreeServeur().setEnabled(false);
				}
			}*/
		}
	}
}