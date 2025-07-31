package it.unisa.javaclienttorcs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class SocketHandler {

	private InetAddress address;
	private int port;
	private DatagramSocket socket;
	private final boolean verbose;

	public SocketHandler(String host, int port, boolean verbose) {
		// Fase 1: Configurazione indirizzo server TORCS
		try {
			// Risoluzione hostname in indirizzo IP
			this.address = InetAddress.getByName(host);
			System.out.println("[INFO] SocketHandler: indirizzo server risolto correttamente - " + host + " -> " + address.getHostAddress());
		} catch (UnknownHostException e) {
			// Gestione errore: hostname non valido o non raggiungibile
			System.err.println("[ERRORE] SocketHandler: impossibile risolvere l'hostname del server TORCS - " + host);
			System.err.println("[ERRORE] Dettagli: " + e.getMessage());
			System.err.println("[ERRORE] Assicurarsi che TORCS sia in esecuzione e accessibile");

			throw new RuntimeException("Impossibile connettersi al server TORCS", e);
		}
		
		// Fase 2: Configurazione porta di comunicazione
		this.port = port;
		
		// Fase 3: Inizializzazione socket UDP
		// DatagramSocket per comunicazione non affidabile ma veloce
		try {
			socket = new DatagramSocket();
			System.out.println("[INFO] SocketHandler: socket UDP creato correttamente sulla porta " + socket.getLocalPort());
		} catch (SocketException e) {
			// Gestione errore: impossibile creare socket (porta occupata, permessi, ecc.)
			System.err.println("[ERRORE] SocketHandler: impossibile creare socket UDP");
			System.err.println("[ERRORE] Dettagli: " + e.getMessage());
			System.err.println("[ERRORE] Possibili cause: porta occupata, permessi insufficienti, firewall bloccante");

			throw new RuntimeException("Impossibile inizializzare la comunicazione UDP", e);
		}
		
		// Fase 4: Configurazione modalità verbose
		this.verbose = verbose;
	}

	public void send(String msg) {
		// Fase 1: Log debug (se attivato)
		if (verbose)
			System.out.println("[DEBUG] SocketHandler.send: invio messaggio - " + msg);
		
		// Fase 2: Preparazione dati per invio
		// Conversione stringa in array di byte
		try {
			byte[] buffer = msg.getBytes();
			
			// Fase 3: Creazione e invio pacchetto UDP
			// DatagramPacket contiene: dati, lunghezza, indirizzo destinatario, porta
			socket.send(new DatagramPacket(buffer, buffer.length, address, port));
			
		} catch (IOException e) {
			// Gestione errore: problemi di rete, pacchetto troppo grande, ecc.
			System.err.println("[ERRORE] SocketHandler.send: errore durante l'invio del messaggio");
			System.err.println("[ERRORE] Dettagli: " + e.getMessage());
			System.err.println("[ERRORE] Messaggio che causava l'errore: " + msg);

			if (verbose) {
				e.printStackTrace(); // Stacktrace solo in modalità verbose
			}
		}
	}

	public String receive() {
		try {
			// Fase 1: Preparazione buffer ricezione
			// 1024 byte dovrebbero essere sufficienti per messaggi TORCS
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			// Fase 2: Attesa ricezione pacchetto
			// Bloccante finché non arriva un pacchetto
			socket.receive(packet);
			
			// Fase 3: Conversione dati ricevuti in stringa
			// Usa solo la parte effettiva del pacchetto (non tutto il buffer)
			String received = new String(packet.getData(), 0, packet.getLength());
			
			// Fase 4: Log debug (se attivato)
			if (verbose)
				System.out.println("[DEBUG] SocketHandler.receive: ricevuto - " + received);
			
			return received;
			
		} catch (SocketTimeoutException se) {
			// Timeout scaduto: nessun pacchetto ricevuto entro il tempo specificato
			if (verbose)
				System.out.println("[WARN] SocketHandler.receive: timeout scaduto, nessun dato ricevuto");
		} catch (IOException e) {
			// Gestione errore: problemi di rete, socket chiuso, ecc.
			System.err.println("[ERRORE] SocketHandler.receive: errore durante la ricezione");
			System.err.println("[ERRORE] Dettagli: " + e.getMessage());
			System.err.println("[ERRORE] Possibili cause: connessione interrotta, socket chiuso, errore di rete");

			if (verbose) {
				e.printStackTrace(); // Stacktrace solo in modalità verbose
			}
		}
		return null; // Nessun dato ricevuto
	}

	public String receive(int timeout) {
		try {
			// Fase 1: Impostazione timeout ricezione
			// 0 = infinito, valore positivo = millisecondi di attesa
			socket.setSoTimeout(timeout);
			
			// Fase 2: Ricezione con timeout
			String received = receive();
			
			// Fase 3: Reset timeout a infinito
			// Importante per evitare timeout in ricezioni successive
			socket.setSoTimeout(0);
			
			return received;
			
		} catch (SocketException e) {
			// Gestione errore: impossibile modificare timeout del socket
			System.err.println("[ERRORE] SocketHandler.receive: impossibile modificare il timeout del socket");
			System.err.println("[ERRORE] Dettagli: " + e.getMessage());
			System.err.println("[ERRORE] Il socket potrebbe essere stato chiuso o in stato non valido");

			if (verbose) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void close() {
		// Chiusura pulita del socket
		// Libera la porta e le risorse di sistema
		if (socket != null && !socket.isClosed()) {
			socket.close();
			System.out.println("[INFO] SocketHandler: socket chiuso correttamente");
		} else {
			System.out.println("[WARN] SocketHandler: socket già chiuso o non inizializzato");
		}

	}

}
