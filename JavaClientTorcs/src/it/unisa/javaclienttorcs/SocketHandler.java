package it.unisa.javaclienttorcs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Gestore della comunicazione UDP con il server TORCS.
 * Questa classe gestisce l'invio e la ricezione di messaggi tramite socket UDP,
 * ottimizzando le performance attraverso buffer riutilizzabili e configurazioni
 * specifiche per la comunicazione real-time con TORCS.
 */
public class SocketHandler {

	private InetAddress address;
	private int port;
	private DatagramSocket socket;
	private final boolean verbose;
	
	// Ottimizzazioni I/O per performance migliori - utilizza IOConfig
	// Buffer riutilizzabili per evitare allocazioni ripetute
	private final byte[] receiveBuffer = new byte[IOConfig.UDP_RECEIVE_BUFFER_SIZE];
	private final DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, IOConfig.UDP_RECEIVE_BUFFER_SIZE);
	
	// Statistiche performance (opzionale)
	private long totalBytesSent = 0;
	private long totalBytesReceived = 0;
	private long messagesSent = 0;
	private long messagesReceived = 0;

	/**
	 * Costruisce un nuovo gestore socket per la comunicazione con TORCS.
	 * 
	 * @param host Hostname o indirizzo IP del server TORCS
	 * @param port Porta UDP su cui il server TORCS è in ascolto
	 * @param verbose Se true, abilita il logging dettagliato delle operazioni
	 * @throws RuntimeException Se non è possibile connettersi al server
	 */
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
		
		// Fase 3: Inizializzazione socket UDP ottimizzato
		// DatagramSocket per comunicazione non affidabile ma veloce
		try {
			socket = new DatagramSocket();
			
			// Ottimizzazioni socket per performance migliori - utilizza IOConfig
			socket.setSendBufferSize(IOConfig.UDP_SOCKET_SEND_BUFFER);
			socket.setReceiveBufferSize(IOConfig.UDP_SOCKET_RECEIVE_BUFFER);
			
			// Disabilita il controllo di validità degli indirizzi per performance
			// socket.setReuseAddress(true); // Opzionale per riutilizzo porta
			
			System.out.println("[INFO] SocketHandler: socket UDP ottimizzato creato sulla porta " + socket.getLocalPort());
			System.out.println("[INFO] Buffer invio: " + socket.getSendBufferSize() + " byte, Buffer ricezione: " + socket.getReceiveBufferSize() + " byte");
			
		} catch (SocketException e) {
			// Gestione errore: impossibile creare socket (porta occupata, permessi, ecc.)
			System.err.println("[ERRORE] SocketHandler: impossibile creare socket UDP ottimizzato");
			System.err.println("[ERRORE] Dettagli: " + e.getMessage());
			System.err.println("[ERRORE] Possibili cause: porta occupata, permessi insufficienti, firewall bloccante");

			throw new RuntimeException("Impossibile inizializzare la comunicazione UDP", e);
		}
		
		// Fase 4: Configurazione modalità verbose
		this.verbose = verbose;
	}

	/**
	 * Invia un messaggio al server TORCS tramite UDP.
	 * 
	 * @param msg Messaggio da inviare al server TORCS
	 */
	public void send(String msg) {
		// Fase 1: Log debug ottimizzato (se attivato)
		if (verbose)
			System.out.println("[DEBUG] SocketHandler.send: invio messaggio (" + msg.length() + " char) - " + msg);
		
		// Fase 2: Preparazione dati per invio ottimizzata
		// Conversione stringa in array di byte con encoding UTF-8 esplicito
		try {
			byte[] buffer = msg.getBytes("UTF-8");
			
			// Controllo dimensione messaggio per evitare frammentazione - utilizza IOConfig
			if (buffer.length > IOConfig.UDP_FRAGMENTATION_THRESHOLD) {
				System.err.println("[WARN] SocketHandler.send: messaggio molto grande (" + buffer.length + " byte), possibile frammentazione");
			}
			
			// Fase 3: Creazione e invio pacchetto UDP ottimizzato
			// Riutilizzo oggetto DatagramPacket per performance migliori
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
			
			// Aggiorna statistiche
			totalBytesSent += buffer.length;
			messagesSent++;
			
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

	/**
	 * Riceve un messaggio dal server TORCS tramite UDP.
	 * Questo metodo è bloccante e attende fino alla ricezione di un pacchetto.
	 * 
	 * @return Messaggio ricevuto dal server TORCS
	 * @throws RuntimeException Se si verifica un errore durante la ricezione
	 */
	public String receive() {
		try {
			// Fase 1: Reset del buffer riutilizzabile per performance migliori
			// Riutilizzo receivePacket pre-allocato invece di crearne uno nuovo
			receivePacket.setLength(IOConfig.UDP_RECEIVE_BUFFER_SIZE); // Reset lunghezza per riutilizzo
			
			// Fase 2: Attesa ricezione pacchetto ottimizzata
			// Bloccante finché non arriva un pacchetto
			socket.receive(receivePacket);
			
			// Fase 3: Conversione dati ricevuti in stringa ottimizzata
			// Usa encoding UTF-8 esplicito e solo la parte effettiva del pacchetto
			String received = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
			
			// Aggiorna statistiche
			totalBytesReceived += receivePacket.getLength();
			messagesReceived++;
			
			// Fase 4: Log debug ottimizzato (se attivato)
			if (verbose)
				System.out.println("[DEBUG] SocketHandler.receive: ricevuto (" + receivePacket.getLength() + " byte) - " + received);
			
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

	/**
	 * Riceve un messaggio dal server TORCS con timeout specificato.
	 * Questo metodo imposta temporaneamente un timeout per la ricezione,
	 * esegue la ricezione e poi ripristina il timeout infinito.
	 * 
	 * @param timeout Timeout in millisecondi (0 = infinito)
	 * @return Messaggio ricevuto come stringa, null se timeout o errore
	 */
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

	/**
	 * Chiude il socket UDP e libera le risorse di sistema.
	 * Stampa le statistiche finali di comunicazione se la modalità verbose è attiva.
	 * Questo metodo dovrebbe essere chiamato quando la comunicazione con TORCS è terminata.
	 */
	public void close() {
		// Chiusura pulita del socket con statistiche
		// Libera la porta e le risorse di sistema
		if (socket != null && !socket.isClosed()) {
			socket.close();
			
			// Stampa statistiche finali se verbose
			if (verbose) {
				System.out.println("[INFO] SocketHandler: statistiche finali:");
				System.out.println("[INFO] - Messaggi inviati: " + messagesSent + " (" + totalBytesSent + " byte)");
				System.out.println("[INFO] - Messaggi ricevuti: " + messagesReceived + " (" + totalBytesReceived + " byte)");
				if (messagesSent > 0) {
					System.out.println("[INFO] - Dimensione media invio: " + (totalBytesSent / messagesSent) + " byte/msg");
				}
				if (messagesReceived > 0) {
					System.out.println("[INFO] - Dimensione media ricezione: " + (totalBytesReceived / messagesReceived) + " byte/msg");
				}
			}
			
			System.out.println("[INFO] SocketHandler: socket chiuso correttamente (I/O ottimizzato)");
		} else {
			System.out.println("[WARN] SocketHandler: socket già chiuso o non inizializzato");
		}
	}
	
	/**
	 * Restituisce statistiche di utilizzo del socket.
	 * Include il numero di messaggi inviati e ricevuti con i relativi byte trasferiti.
	 * 
	 * @return Stringa formattata con le statistiche di comunicazione
	 */
	public String getStats() {
		return String.format("SocketHandler Stats - Sent: %d msg (%d byte), Received: %d msg (%d byte)", 
				messagesSent, totalBytesSent, messagesReceived, totalBytesReceived);
	}

}
