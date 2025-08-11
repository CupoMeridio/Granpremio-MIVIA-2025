package it.unisa.javaclienttorcs;

/**
 * Classe astratta base per tutti i controller/driver per TORCS.
 * Definisce l'interfaccia comune che ogni driver deve implementare.
 * 
 * Questa classe fornisce i metodi fondamentali per:
 * - Inizializzare i parametri del driver
 * - Controllare la macchina in base ai dati sensoriali
 * - Gestire il reset tra una gara e l'altra
 * - Eseguire operazioni di cleanup alla fine
 */
public abstract class Controller {

	public enum Stage {

		WARMUP, QUALIFYING, RACE, UNKNOWN;

		static Stage fromInt(int value) {
                    return switch (value) {
                        case 0 -> WARMUP;
                        case 1 -> QUALIFYING;
                        case 2 -> RACE;
                        default -> UNKNOWN;
                    };
		}
	};

	private Stage stage;
	private String trackName;

	/**
	 * Inizializza gli angoli dei sensori di distanza dalla pista.
	 * Restituisce un array di 19 angoli da -90° a +90° con incrementi di 10°.
	 * 
	 * @return Array di angoli in gradi per i sensori di distanza
	 */
	public float[] initAngles() {
		float[] angles = new float[19];
		for (int i = 0; i < 19; ++i)
			angles[i] = -90 + i * 10;
		return angles;
	}

	/**
	 * Restituisce la fase attuale della gara.
	 * 
	 * @return Fase corrente (WARMUP, QUALIFYING, RACE, UNKNOWN)
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Imposta la fase attuale della gara.
	 * 
	 * @param stage Nuova fase della gara
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Restituisce il nome della pista attuale.
	 * 
	 * @return Nome della pista
	 */
	public String getTrackName() {
		return trackName;
	}

	/**
	 * Imposta il nome della pista attuale.
	 * 
	 * @param trackName Nome della pista
	 */
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	/**
	 * Metodo principale di controllo che deve essere implementato da ogni driver.
	 * Riceve i dati sensoriali e restituisce l'azione da compiere.
	 * 
	 * @param sensors Modello sensoriale con tutti i dati della macchina
	 * @return Azione di controllo da inviare a TORCS
	 */
	public abstract Action control(SensorModel sensors);

	/**
	 * Metodo chiamato all'inizio di ogni nuova prova/gara.
	 * Deve resettare lo stato interno del driver.
	 */
	public abstract void reset();

	/**
	 * Metodo chiamato alla fine dell'esecuzione per operazioni di cleanup.
	 */
	public abstract void shutdown();

}