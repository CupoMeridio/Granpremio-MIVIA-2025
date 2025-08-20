package it.unisa.javaclienttorcs;

import java.util.StringTokenizer;
import it.unisa.javaclienttorcs.Controller.Stage;
import java.lang.reflect.InvocationTargetException;

/**
 * Client principale per la connessione con TORCS (The Open Racing Car Simulator).
 * Questa classe gestisce l'intera comunicazione tra il driver intelligente e il simulatore.
 * Il client riceve dati sensoriali dalla macchina e invia comandi di controllo.
 */
public class Client {

	// Timeout in millisecondi per le operazioni di ricezione UDP
	private static final int UDP_TIMEOUT = 10000;
	
	// Parametri di connessione al server TORCS
	private static int port;              // Porta di connessione (default: 3001)
	private static String host;           // Indirizzo del server (default: localhost)
	private static String clientId;       // Identificativo del client per il server
	private static boolean verbose;       // Modalità verbose per debug
	private static int maxEpisodes;       // Numero massimo di episodi/gare
	private static int maxSteps;          // Numero massimo di passi per episodio (0 = illimitato)
	private static Stage stage;           // Fase della competizione (WARMUP, QUALIFYING, RACE)
	private static String trackName;      // Nome del tracciato corrente

	/**
	 * Metodo principale di avvio del client.
	 * 
	 * @param args Array di parametri configurabili:
	 *   - args[0]: Nome completo della classe del controller/driver (es. "it.unisa.javaclienttorcs.SimpleDriver")
	 *   - port:N - Porta di connessione (default: 3001)
	 *   - host:ADDRESS - Indirizzo del server (default: localhost)
	 *   - id:ClientID - Identificativo del client (default: SCR)
	 *   - verbose:on/off - Modalità debug (default: off)
	 *   - maxEpisodes:N - Numero di gare (default: 1)
	 *   - maxSteps:N - Passi massimi per gara (0 = illimitato)
	 *   - stage:N - 0=WARMUP, 1=QUALIFYING, 2=RACE
	 *   - trackName:name - Nome del tracciato
	 */
	public static void main(String[] args) {
		// Fase 1: Parsing dei parametri da riga di comando
		parseParameters(args);
		
		// Fase 2: Inizializzazione connessione socket con TORCS
		SocketHandler mySocket = new SocketHandler(host, port, verbose);
		String inMsg;

		// Fase 3: Caricamento dinamico del controller/driver specificato
		Controller driver = load(args[0]);
		if (driver == null) {
			System.err.println("[ERRORE] Impossibile caricare il controller: " + args[0]);
			System.exit(1);
		}
		
                if( driver instanceof MLPDriver){
                    
                }
		
		// Gestione speciale per HumanController
		if (driver instanceof HumanController humanController) {
			// Controlla se è richiesta la modalità raccolta dati
			boolean collectData = false;
			for (String arg : args) {
				if (arg.equals("--collect")) {
					collectData = true;
					break;
				}
			}
			
			if (collectData) {
				humanController.setCollectingMode(true);
				System.out.println("[INFO] Modalità raccolta dati attivata per HumanController");
			}
		}
		
		// Gestione speciale per KNNDriver con dataset personalizzato
		if (driver instanceof KNNDriver) {
			// Cerca il nome del dataset negli argomenti
			String datasetFile = null;
			for (int i = 1; i < args.length; i++) {
				if (!args[i].contains(":") && !args[i].startsWith("--")) {
					datasetFile = args[i];
					break;
				}
			}
			
			if (datasetFile != null) {
				// Ricrea il KNNDriver con il dataset specificato
				try {
					driver = new KNNDriver(datasetFile);
					System.out.println("[INFO] KNNDriver inizializzato con dataset: " + datasetFile);
				} catch (Exception e) {
					System.err.println("[ERRORE] Impossibile inizializzare KNNDriver con dataset " + datasetFile + ": " + e.getMessage());
					System.err.println("[INFO] Utilizzo configurazione di default");
				}
			}
		}
		

		
		driver.setStage(stage);
	driver.setTrackName(trackName);

		/* Costruzione stringa di inizializzazione per TORCS */
		// Recupera gli angoli dei sensori dal controller
		float[] angles = driver.initAngles();
		String initStr = clientId + "(init";
		// Aggiunge tutti gli angoli alla stringa di inizializzazione
		for (int i = 0; i < angles.length; i++) {
			initStr = initStr + " " + angles[i];
		}
		initStr = initStr + ")";

		// Fase 4: Loop principale delle gare/episodi
		long curEpisode = 0;
		boolean shutdownOccurred = false;
		do {

			/*
			 * Fase 5: Identificazione del client con il server TORCS
			 * Continua a inviare finché non riceve conferma di identificazione
			 */
			do {
				mySocket.send(initStr);
				inMsg = mySocket.receive(UDP_TIMEOUT);
			} while (inMsg == null || !inMsg.contains("***identified***"));

			/*
			 * Fase 6: Loop di guida attiva per l'episodio corrente
			 */
			long currStep = 0;
			while (true) {
				/*
				 * Ricezione dello stato di gioco da TORCS
				 */
				inMsg = mySocket.receive(UDP_TIMEOUT);

				if (inMsg != null) {
					/*
					 * Controllo fine gara: server shutdown
					 */
					if (inMsg.contains("***shutdown***")) {
						shutdownOccurred = true;
						System.out.println("[INFO] Client: server TORCS ha inviato comando di spegnimento");
						System.out.println("[INFO] Client: chiusura controllata in corso...");
						break;
					}

					/*
					 * Controllo restart: inizio nuova gara
					 */
					if (inMsg.contains("***restart***")) {
						driver.reset(); // Resetta lo stato del driver
						if (verbose)
							System.out.println("[INFO] Client: server TORCS ha inviato comando di riavvio");
						System.out.println("[INFO] Client: preparazione per nuovo episodio...");
						break;
					}

					/*
					 * Fase 7: Elaborazione decisione di controllo
					 */
					Action action = new Action();
					if (currStep < maxSteps || maxSteps == 0)
						// Usa il controller per determinare l'azione
						action = driver.control(new MessageBasedSensorModel(inMsg));
					else
						// Forza restart se raggiunto limite passi
						action.restartRace = true;

					currStep++;
					mySocket.send(action.toString());
				} else if (verbose) {
					// Silent timeout handling - only show in verbose mode
					System.out.println("[INFO] Client: timeout ricezione dati dal server TORCS (normale per UDP)");
				}
			}

		} while (++curEpisode < maxEpisodes && !shutdownOccurred);

		/*
		 * Fase 8: Chiusura e cleanup finale
		 */
		driver.shutdown(); // Cleanup del driver
		mySocket.close();   // Chiusura connessione
		System.out.println("[INFO] Client: shutdown completato");
		System.out.println("[INFO] Client: chiusura client TORCS - arrivederci!");

	}

	/**
	 * Metodo per analizzare e impostare i parametri di configurazione passati da riga di comando.
	 * Imposta valori di default per tutti i parametri opzionali.
	 * 
	 * @param args Array di stringhe contenenti i parametri nel formato "chiave:valore"
	 */
	private static void parseParameters(String[] args) {
		/* 
		 * Fase 1: Impostazione valori di default per tutti i parametri
		 * Questi valori vengono usati se non specificati nella riga di comando
		 */
		port = 3001;                    // Porta di default per connessione TORCS
		host = "localhost";             // Server locale di default
		clientId = "SCR";               // Identificatore client di default
		verbose = false;                // Modalità silenziosa di default
		maxEpisodes = 1;                // Un solo episodio di default
		maxSteps = 0;                   // Nessun limite di passi di default (0 = infinito)
		stage = Stage.UNKNOWN;          // Stadio di gara sconosciuto di default
		trackName = "unknown";          // Nome pista sconosciuto di default

		/*
		 * Fase 2: Parsing parametri da riga di comando
		 * Formato atteso: parametro:valore
		 * Es: port:3001 host:localhost verbose:on
		 */
		for (int i = 1; i < args.length; i++) {
			// Gestione flag speciali come --collect e --collect-data
			if (args[i].equals("--collect") || args[i].equals("--collect-data")) {
				// Il flag viene gestito separatamente nel main
				continue;
			}
			
			// Gestione parametri nel formato chiave:valore
			if (args[i].contains(":")) {
				// Divisione ogni argomento in entità e valore usando ':' come separatore
				StringTokenizer st = new StringTokenizer(args[i], ":");
				if (st.countTokens() >= 2) {
					String entity = st.nextToken();    // Nome del parametro
					String value = st.nextToken();     // Valore del parametro
					
					// Fase 3: Assegnazione valori in base al parametro
					if (entity.equals("port")) {
						port = Integer.parseInt(value);  // Porta di connessione al server
					}
					if (entity.equals("host")) {
						host = value;                    // Hostname o IP del server TORCS
					}
					if (entity.equals("id")) {
						clientId = value;                // Identificatore univoco del client
					}
					if (entity.equals("verbose")) {
                                            // Gestione booleana per modalità verbose
                                            switch (value) {
                                                case "on" -> verbose = true;              // Attiva output dettagliato
                                                case "off" -> verbose = false;             // Mantiene modalità silenziosa
                                                default -> {
                                                    System.err.println("[WARN] Parametri: opzione non valida - " + entity + ":" + value);
                                                    System.err.println("[WARN] Parametri: verrà utilizzato il valore di default");
                                                    System.exit(0);
                                                }
                                            }
					}
					if (entity.equals("stage")) {
			
						stage = Stage.fromInt(Integer.parseInt(value));  // 0=WARMUP, 1=QUALIFYING, 2=RACE
					}
					if (entity.equals("trackName")) {
						trackName = value;               // Nome specifico della pista
					}
					if (entity.equals("maxEpisodes")) {
						maxEpisodes = Integer.parseInt(value);  // Numero massimo di gare
						if (maxEpisodes <= 0) {
							System.err.println("[WARN] Parametri: opzione non valida - " + entity + ":" + value);
							System.err.println("[WARN] Parametri: verrà utilizzato il valore di default");
							System.exit(0);
						}
					}
					if (entity.equals("maxSteps")) {
						maxSteps = Integer.parseInt(value);     // Limite passi per episodio
						if (maxSteps < 0) {
							System.err.println("[WARN] Parametri: opzione non valida - " + entity + ":" + value);
							System.err.println("[WARN] Parametri: verrà utilizzato il valore di default");
							System.exit(0);
						}
					}
				} else {
					System.err.println("[WARN] Parametri: formato non valido - " + args[i]);
					System.err.println("[WARN] Parametri: formato atteso parametro:valore");
				}
			} else if (i == 1 && !args[i].startsWith("--") && !args[i].contains(":")) {
                            // Il secondo argomento può essere il nome del file dataset (gestito nel main)

			} else {
				System.err.println("[WARN] Parametri: parametro non riconosciuto - " + args[i]);
				System.err.println("[WARN] Parametri: parametro ignorato");
			}
		}
	}

        /**
	 * Carica dinamicamente una classe controller utilizzando la reflection.
	 * Se il caricamento fallisce per qualsiasi motivo, viene utilizzato SimpleDriver come fallback.
	 * 
	 * @param name Nome completo della classe controller da caricare
	 * @return Istanza del controller richiesto o SimpleDriver in caso di errore
	 */
	private static Controller load(String name) {
            Controller controller;

            try {
                // Usa la reflection moderna per creare l'istanza
                controller = (Controller) Class.forName(name)
                                               .getDeclaredConstructor()
                                               .newInstance();

            } catch (ClassNotFoundException e) {
                System.err.println("[ERRORE] Controller: classe driver non trovata - " + name);
				System.err.println("[ERRORE] Controller: verrà utilizzato il driver di default (SimpleDriver)");
                controller = new SimpleDriver();

            } catch (InstantiationException e) {
                System.err.println("[ERRORE] Controller: errore istanziazione classe - " + name);
				System.err.println("[ERRORE] Dettagli: classe astratta o interfaccia non istanziabile");
				System.err.println("[ERRORE] Controller: verrà utilizzato il driver di default (SimpleDriver)");
                controller = new SimpleDriver();

            } catch (IllegalAccessException e) {
                System.err.println("[ERRORE] Controller: accesso illegale alla classe - " + name);
				System.err.println("[ERRORE] Dettagli: costruttore non accessibile (private/protected)");
				System.err.println("[ERRORE] Controller: verrà utilizzato il driver di default (SimpleDriver)");
                controller = new SimpleDriver();

            } catch (InvocationTargetException e) {
                System.err.println("[ERRORE] Controller: eccezione nel costruttore - " + name);
				System.err.println("[ERRORE] Dettagli: verificare stacktrace completo per dettagli");
				System.err.println("[ERRORE] Controller: verrà utilizzato il driver di default (SimpleDriver)");
                controller = new SimpleDriver();

            } catch (NoSuchMethodException e) {
                System.err.println("[ERRORE] Controller: costruttore senza argomenti mancante - " + name);
				System.err.println("[ERRORE] Dettagli: la classe richiede un costruttore pubblico senza parametri");
				System.err.println("[ERRORE] Controller: verrà utilizzato il driver di default (SimpleDriver)");
                controller = new SimpleDriver();
            }

            return controller;
        }

}
