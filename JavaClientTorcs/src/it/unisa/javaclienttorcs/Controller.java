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

	public float[] initAngles() {
		float[] angles = new float[19];
		for (int i = 0; i < 19; ++i)
			angles[i] = -90 + i * 10;
		return angles;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public abstract Action control(SensorModel sensors);

	public abstract void reset(); // called at the beginning of each new trial

	public abstract void shutdown();

}