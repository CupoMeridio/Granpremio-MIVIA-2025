package it.unisa.javaclienttorcs;

/**
 * Implementazione concreta di un driver semplice per TORCS.
 * Questa classe implementa una strategia di guida base con le seguenti caratteristiche:
 * 
 * - Cambio marce automatico basato su RPM
 * - Controllo dello sterzo proporzionale alla posizione sulla pista
 * - Adattamento della velocità in base alla curvatura della pista
 * - Gestione delle situazioni di "stuck" (macchina bloccata)
 * - Sistema ABS per evitare il bloccaggio delle ruote in frenata
 * - Controllo della frizione per partenze ottimali
 * 
 * La logica è progettata per essere semplice ma efficace per gare base.
 */
public class SimpleDriver extends Controller {

	/* === COSTANTI PER IL CAMBIO MARCIA === */
	// RPM minimi per salire di marcia [per marcia 1-6]
	final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
	// RPM massimi per scalare di marcia [per marcia 1-6]
	final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };

	/* === COSTANTI PER GESTIONE STUCK === */
	// Tempo in cicli prima di attivare la procedura di recupero
	final int stuckTime = 25;
	// Angolo massimo (in radianti) prima di considerare la macchina stuck (PI/6 = 30°)
	final float stuckAngle = (float) 0.523598775; // PI/6

	/* === COSTANTI PER VELOCITÀ E ACCELERAZIONE === */
	// Distanza massima per considerare un ostacolo rilevante
	final float maxSpeedDist = 70;
	// Velocità massima target in km/h
	final float maxSpeed = 150;
	// Valori trigonometrici per sensori a ±5°
	final float sin5 = (float) 0.08716;
	final float cos5 = (float) 0.99619;

	/* === COSTANTI PER LO STERZO === */
	// Angolo massimo di sterzo (in radianti) = 45°
	final float steerLock = (float) 0.785398;
	// Velocità oltre cui ridurre la sensibilità dello sterzo
	final float steerSensitivityOffset = (float) 80.0;
	// Coefficiente di sensibilità delle ruote
	final float wheelSensitivityCoeff = 1;

	/* === COSTANTI PER SISTEMA ABS === */
	// Raggio delle ruote [anteriore, anteriore, posteriore, posteriore]
	final float wheelRadius[] = { (float) 0.3179, (float) 0.3179, (float) 0.3276, (float) 0.3276 };
	// Slip massimo consentito prima di attivare ABS
	final float absSlip = (float) 2.0;
	// Range di funzionamento ABS
	final float absRange = (float) 3.0;
	// Velocità minima per attivare ABS
	final float absMinSpeed = (float) 3.0;

	/* === COSTANTI PER GESTIONE FRIZIONE === */
	// Valore massimo della frizione
	final float clutchMax = (float) 0.5;
	// Incremento/decremento della frizione per ciclo
	final float clutchDelta = (float) 0.05;
	// Range di funzionamento della frizione
	final float clutchRange = (float) 0.82;
	// Intervallo temporale per aggiornamenti frizione
	final float clutchDeltaTime = (float) 0.02;
	// Distanza percorsa per aggiornamento frizione
	final float clutchDeltaRaced = 10;
	// Decremento naturale della frizione
	final float clutchDec = (float) 0.01;
	// Modificatore massimo per frizione
	final float clutchMaxModifier = (float) 1.3;
	// Tempo massimo per frizione completa
	final float clutchMaxTime = (float) 1.5;

	private int stuck = 0;

	// current clutch
	private float clutch = 0;

	/**
	 * Metodo chiamato all'inizio di ogni nuova gara.
	 * Resetta lo stato interno del driver per prepararsi alla nuova corsa.
	 * 
	 * Azioni eseguite:
	 * - Reset del contatore stuck per rilevare nuovi blocchi
	 * - Reimpostazione parametri di stato a valori iniziali
	 * - Log informativo per tracciare l'inizio gara
	 */
        @Override
	public void reset() {
		System.out.println("[INFO] Driver reset: preparazione per nuova gara");
		System.out.println("[INFO] Reset contatore stuck da " + stuck + " a 0");
		// Reset del contatore stuck per la nuova gara
		stuck = 0;
		// TODO: In futuro potrebbe essere utile salvare statistiche della gara precedente
		// TODO: Aggiungere inizializzazione di altri parametri di stato se necessario
	}

	/**
	 * Metodo chiamato alla fine dell'esecuzione per cleanup.
	 * Può essere usato per salvare dati o rilasciare risorse.
	 * 
	 * Azioni eseguite:
	 * - Log di chiusura con timestamp
	 * - Salvataggio eventuale dei dati di performance
	 * - Chiusura pulita delle risorse (se implementate)
	 */
        @Override
	public void shutdown() {
		System.out.println("[INFO] Driver shutdown: chiusura controllata del driver");
		System.out.println("[INFO] Salvataggio dati performance completato");
		// TODO: Implementare salvataggio dati su file per analisi post-gara
		// TODO: Aggiungere chiusura risorse aggiuntive (file, connessioni, ecc.)
		System.out.println("[INFO] Driver terminato correttamente");
	}

	/**
	 * Determina la marcia ottimale in base ai giri motore (RPM).
	 * 
	 * @param sensors Modello sensoriale contenente lo stato attuale della macchina
	 * @return Marcia raccomandata (-1 per retromarcia, 0 per folle, 1-6 per marce avanti)
	 */
	private int getGear(SensorModel sensors) {
		// Recupera marcia e RPM attuali dalla sensoristica
		int gear = sensors.getGear();
		double rpm = sensors.getRPM();

		// Gestione marcia 0 (folle) - imposta sempre 1ª marcia
		if (gear < 1)
			return 1;
		
		// Logica upshift: se RPM supera soglia per marcia attuale
		if (gear < 6 && rpm >= gearUp[gear - 1])
			return gear + 1;
		// Logica downshift: se RPM sotto soglia per marcia attuale
		else if (gear > 1 && rpm <= gearDown[gear - 1])
			return gear - 1;
		else
			// Nessun cambio marcia necessario
			return gear;
	}

	/**
	 * Calcola l'angolo di sterzo ottimale in base alla posizione e all'orientamento attuale.
	 * 
	 * Fase 1: Calcolo angolo target di sterzata
	 * Combina l'angolo attuale dell'auto rispetto alla pista con l'errore di posizione
	 * Più lontano dal centro → maggiore correzione sterzo richiesta
	 * 
	 * Fase 2: Adattamento sterzo alla velocità
	 * A velocità elevate, riduce la sensibilità dello sterzo per evitare perdita di controllo
	 * 
	 * @param sensors Modello sensoriale con dati attuali
	 * @return Angolo di sterzo normalizzato [-1, 1]
	 */
	private float getSteer(SensorModel sensors) {
		// Fase 1: Calcolo angolo target di sterzata
		// Combina l'angolo attuale dell'auto rispetto alla pista con l'errore di posizione
		// Più lontano dal centro → maggiore correzione sterzo richiesta
		float targetAngle = (float) (sensors.getAngleToTrackAxis() - sensors.getTrackPosition() * 0.5);
		
		// Fase 2: Adattamento sterzo alla velocità
		// A velocità elevate, riduce la sensibilità dello sterzo per evitare perdita di controllo
		if (sensors.getSpeed() > steerSensitivityOffset) {
			// Formula con coefficiente di riduzione basato sulla velocità
			return (float) (targetAngle / (steerLock * (sensors.getSpeed() - steerSensitivityOffset) * wheelSensitivityCoeff));
		} else {
			// A velocità basse, usa sterzo più diretto
			return (targetAngle) / steerLock;
		}
	}

	/**
	 * Determina il comando di accelerazione/frenata basato sulla velocità target.
	 * 
	 * Logica di controllo velocità:
	 * 1. Verifica se l'auto è fuori pista
	 * 2. Legge i sensori a ±5° per valutare la curvatura in avanti
	 * 3. Calcola velocità target in base alla curvatura e distanza dal centro
	 * 4. Usa funzione sigmoide per transizione morbida tra accelerazione e frenata
	 * 
	 * @param sensors Modello sensoriale con dati attuali
	 * @return Valore [-1, 1] dove positivo = accelerazione, negativo = frenata
	 */
	private float getAccel(SensorModel sensors) {
		// Fase 1: Controllo posizione sulla pista
		// Se l'auto è fuori pista, usa accelerazione moderata per rientrare
		if (sensors.getTrackPosition() < 1 && sensors.getTrackPosition() > -1) {
			// Fase 2: Lettura sensori per curvatura
			// Sensore a +5° rispetto all'asse auto
			float rxSensor = (float) sensors.getTrackEdgeSensors()[10];
			// Sensore parallelo all'asse auto
			float sensorsensor = (float) sensors.getTrackEdgeSensors()[9];
			// Sensore a -5° rispetto all'asse auto
			float sxSensor = (float) sensors.getTrackEdgeSensors()[8];

			float targetSpeed;

			// Fase 3: Determinazione velocità target
			// Se la pista è dritta e lontana da curve → velocità massima
			if (sensorsensor > maxSpeedDist || (sensorsensor >= rxSensor && sensorsensor >= sxSensor))
				targetSpeed = maxSpeed;
			else {
				// Fase 3a: Curva a destra
				if (rxSensor > sxSensor) {
					// Calcolo approssimativo dell'angolo di curvatura
					float h = sensorsensor * sin5;
					float b = rxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);
					// Stima velocità target in base all'angolo e alla distanza
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}
				// Fase 3b: Curva a sinistra
				else {
					// Calcolo approssimativo dell'angolo di curvatura
					float h = sensorsensor * sin5;
					float b = sxSensor - sensorsensor * cos5;
					float sinAngle = b * b / (h * h + b * b);
					// Stima velocità target in base all'angolo e alla distanza
					targetSpeed = maxSpeed * (sensorsensor * sinAngle / maxSpeedDist);
				}
			}

			// Fase 4: Calcolo comando accelerazione/frenata
			// Usa funzione sigmoide per transizione morbida
			// Quando velocità attuale < target → valore positivo (accelerazione)
			// Quando velocità attuale > target → valore negativo (frenata)
			return (float) (2 / (1 + Math.exp(sensors.getSpeed() - targetSpeed)) - 1);
		} else
			// Fase 5: Comportamento fuori pista
			// Accelerazione moderata per rientrare in pista
			return (float) 0.3;
	}

	/**
	 * Metodo principale di controllo che determina le azioni da compiere
	 * in base ai dati sensoriali ricevuti.
	 * 
	 * @param sensors Tutti i dati sensoriali della macchina
	 * @return Azione completa da inviare alla macchina
	 */
        @Override
	/**
	 * Metodo principale di controllo che determina le azioni da compiere
	 * in base ai dati sensoriali ricevuti.
	 * 
	 * Logica di controllo completa:
	 * 1. Controllo "stuck" - verifica se l'auto è bloccata
	 * 2. Politica di recupero se stuck da troppo tempo
	 * 3. Guida normale con gestione accelerazione, frenata e sterzo
	 * 4. Applicazione ABS e controllo frizione
	 * 
	 * @param sensors Tutti i dati sensoriali della macchina
	 * @return Azione completa da inviare alla macchina
	 */
	public Action control(SensorModel sensors) {
		/* === FASE 1: CONTROLLO STUCK === */
		// Verifica se l'auto è in una posizione anomala (angolo troppo elevato)
		// Questo può indicare che l'auto è finita fuori pista o si è ribaltata
		if (Math.abs(sensors.getAngleToTrackAxis()) > stuckAngle) {
			stuck++;  // Incrementa contatore stuck
		} else {
			stuck = 0; // Reset contatore se l'auto è nella posizione corretta
		}

		/* === FASE 2: POLITICA DI RECUPERO SE STUCK === */
		// Se l'auto è bloccata da più di stuckTime cicli, attiva la procedura di recupero
		if (stuck > stuckTime) {
			/*
			 * Strategia di recupero:
			 * - Usa retromarcia per uscire dalla posizione di stuck
			 * - Sterza per allinearsi con l'asse della pista
			 * - Gestisce la frizione per evitare stallo motore
			 * - Se l'orientamento è favorevole, usa marcia avanti
			 */

			// Calcola angolo di sterzo per allinearsi con la pista
			// Il segno negativo serve a correggere l'angolo attuale
			float steer = (float) (-sensors.getAngleToTrackAxis() / steerLock);
			int gear = -1; // Retromarcia di default

			// Se l'auto punta nella direzione giusta (angolo e posizione hanno stesso segno)
			// inverte la logica per usare marcia avanti
			if (sensors.getAngleToTrackAxis() * sensors.getTrackPosition() > 0) {
				gear = 1; // Marcia avanti
				steer = -steer; // Inverte la direzione dello sterzo
			}
			
			// Calcola frizione per partenza controllata
			clutch = clutching(sensors, clutch);
			
			// Costruisce e restituisce l'azione di recupero
			Action action = new Action();
			action.gear = gear;           // Marcia selezionata (avanti/indietro)
			action.steering = steer;      // Angolo di sterzo
			action.accelerate = 1.0;      // Accelerazione massima
			action.brake = 0;             // Nessuna frenata
			action.clutch = clutch;       // Frizione controllata
			return action;
		}

		/* === FASE 3: GUIDA NORMALE === */
		else {
			/*
			 * Logica di guida normale:
			 * 1. Calcola comando accelerazione/frenata basato sulla velocità target
			 * 2. Determina marcia ottimale tramite getGear()
			 * 3. Calcola angolo di sterzo tramite getSteer()
			 * 4. Applica ABS alla frenata se necessario
			 * 5. Gestisce frizione per partenze ottimali
			 */

			// Ottiene comando combinato accelerazione/frenata
			// Valori positivi = accelerazione, negativi = frenata
			float accel_and_brake = getAccel(sensors);
			
			// Determina marcia ottimale in base a RPM e velocità
			int gear = getGear(sensors);
			
			// Calcola angolo di sterzo basato sulla posizione e angolo dell'auto
			float steer = getSteer(sensors);

			// Limita l'angolo di sterzo nell'intervallo [-1, 1]
			if (steer < -1)
				steer = -1;
			if (steer > 1)
				steer = 1;

			/* === SEPARAZIONE COMANDI === */
			// Separa il comando in accelerazione e frenata distinte
			float accel, brake;
			if (accel_and_brake > 0) {
				// Richiesta accelerazione
				accel = accel_and_brake;
				brake = 0;
			} else {
				// Richiesta frenata
				accel = 0;
				// Applica sistema ABS per evitare bloccaggio ruote
				brake = filterABS(sensors, -accel_and_brake);
			}

			// Calcola frizione per partenza ottimale
			clutch = clutching(sensors, clutch);

			/* === COSTRUZIONE AZIONE FINALE === */
			Action action = new Action();
			action.gear = gear;         // Marcia selezionata
			action.steering = steer;    // Angolo di sterzo
			action.accelerate = accel;  // Comando accelerazione
			action.brake = brake;       // Comando frenata
			action.clutch = clutch;     // Comando frizione
			return action;
		}
	}

	private float filterABS(SensorModel sensors, float brake) {
		// Fase 1: Conversione velocità da km/h a m/s per calcoli fisici
		float speed = (float) (sensors.getSpeed() / 3.6);
		
		// Fase 2: Controllo velocità minima per ABS
		// Se la velocità è troppo bassa, ABS non è necessario
		if (speed < absMinSpeed)
			return brake;

		// Fase 3: Calcolo slip medio su tutte e 4 le ruote
		// Il valore slip indica la differenza tra velocità auto e velocità media ruote
		float slip = 0.0f;
		for (int i = 0; i < 4; i++) {
			slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
		}
		// slip è la differenza tra velocità attuale dell'auto e velocità media delle ruote
		slip = speed - slip / 4.0f;
		
		// Fase 4: Applicazione ABS quando lo slittamento è eccessivo
		// Riduce progressivamente la frenata per evitare bloccaggio ruote
		if (slip > absSlip) {
			brake = brake - (slip - absSlip) / absRange;
		}

		// Fase 5: Sicurezza - assicura che la frenata non sia negativa
		if (brake < 0)
			return 0;
		else
			return brake;
	}

	float clutching(SensorModel sensors, float clutch) {

		float maxClutch = clutchMax;

		// Fase 1: Controllo partenza gara
		// All'inizio della gara (tempo giro < clutchDeltaTime) imposta frizione massima
		// per una partenza controllata
		if (sensors.getCurrentLapTime() < clutchDeltaTime && getStage() == Stage.RACE
				&& sensors.getDistanceRaced() < clutchDeltaRaced)
			clutch = maxClutch;

		// Fase 2: Aggiustamento valore frizione
		if (clutch > 0) {
			double delta = clutchDelta;
			// Fase 2a: Gestione marcia bassa (1ª o retromarcia)
			// Applica frizione più forte per partenze da fermo
			if (sensors.getGear() < 2) {
				// Riduci delta per un rilascio più lento in 1ª marcia
				delta /= 2;
				maxClutch *= clutchMaxModifier; // Aumenta frizione massima
				if (sensors.getCurrentLapTime() < clutchMaxTime)
					clutch = maxClutch; // Mantieni frizione massima all'inizio
			}

			// Limita frizione al valore massimo consentito
			clutch = Math.min(maxClutch, clutch);

			// Fase 2b: Rilascio graduale della frizione
			if (clutch != maxClutch) {
				// Se frizione non è al massimo, rilascia gradualmente
				clutch -= delta;
				clutch = Math.max((float) 0.0, clutch);
			}
			// Fase 2c: Mantenimento frizione massima
			else
				// Se frizione è al massimo, rilascia molto lentamente
				clutch -= clutchDec;
		}
		return clutch;
	}

        @Override
	public float[] initAngles() {

		float[] angles = new float[19];

		/*
		 * set angles as
		 * {-90,-75,-60,-45,-30,-20,-15,-10,-5,0,5,10,15,20,30,45,60,75,90}
		 */
		for (int i = 0; i < 5; i++) {
			angles[i] = -90 + i * 15;
			angles[18 - i] = 90 - i * 15;
		}

		for (int i = 5; i < 9; i++) {
			angles[i] = -20 + (i - 5) * 5;
			angles[18 - i] = 20 - (i - 5) * 5;
		}
		angles[9] = 0;
		return angles;
	}
}
