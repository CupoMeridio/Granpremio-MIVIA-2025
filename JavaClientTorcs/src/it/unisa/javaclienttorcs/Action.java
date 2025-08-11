package it.unisa.javaclienttorcs;

/**
 * Rappresenta tutte le azioni di controllo che possono essere inviate alla macchina in TORCS.
 * Questa classe contiene tutti i parametri di controllo che il driver può modificare.
 * 
 * I valori sono normalizzati tra 0 e 1 per accelerazione/freni/frizione, e tra -1 e 1 per lo sterzo.
 */
public class Action {

	// Controlli principali della macchina
	public double accelerate = 0; // Accelerazione: 0 (nessuna) .. 1 (massima accelerazione)
	public double brake = 0;      // Frenata: 0 (nessuna) .. 1 (frenata massima)
	public double clutch = 0;     // Frizione: 0 (frizione completamente aperta) .. 1 (frizione chiusa)
	public int gear = 0;        // Marcia: -1 (retromarcia) .. 0 (folle) .. 6 (sesta marcia)
	public double steering = 0;   // Sterzo: -1 (sinistra massima) .. 0 (centro) .. 1 (destra massima)
	
	// Comandi speciali
	public boolean restartRace = false; // Se true, richiede il restart della gara
	public int focus = 360;               // Angolo di focus per sensori direzionali [-90°, 90°], 360 = disabilitato

        @Override
	public String toString() {
		// Costruzione del messaggio da inviare a TORCS
		// Il formato è: (accel valore) (brake valore) (clutch valore) (gear valore) (steer valore) (meta valore) (focus valore)
		
		limitValues();
		return "(accel " + accelerate + ") " + "(brake " + brake + ") " + "(clutch " + clutch + ") " + "(gear " + gear
				+ ") " + "(steer " + steering + ") " + "(meta " + (restartRace ? 1 : 0) + ") " + "(focus " + focus
				+ ")";
	}

	public void limitValues() {
		// Fase 1: Limitazione accelerazione
		// Assicura che il valore sia nell'intervallo [0,1]
		// 0 = nessuna accelerazione, 1 = accelerazione massima
		accelerate = Math.max(0, Math.min(1, accelerate));
		
		// Fase 2: Limitazione frenata
		// Assicura che il valore sia nell'intervallo [0,1]
		// 0 = nessuna frenata, 1 = frenata massima
		brake = Math.max(0, Math.min(1, brake));
		
		// Fase 3: Limitazione frizione
		// Assicura che il valore sia nell'intervallo [0,1]
		// 0 = frizione completamente rilasciata, 1 = frizione completamente premuta
		clutch = Math.max(0, Math.min(1, clutch));
		
		// Fase 4: Limitazione sterzo
		// Assicura che il valore sia nell'intervallo [-1,1]
		// -1 = sterzo sinistra massimo, 0 = dritto, 1 = sterzo destra massimo
		steering = Math.max(-1, Math.min(1, steering));
		
		// Fase 5: Limitazione marcia
		// Assicura che il valore sia nell'intervallo [-1,6]
		// -1 = retromarcia, 0 = folle, 1-6 = marce avanti
		gear = Math.max(-1, Math.min(6, gear));
	}
}
