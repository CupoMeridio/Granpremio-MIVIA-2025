package it.unisa.javaclienttorcs;

/**
 * Interfaccia che rappresenta tutti i dati sensoriali disponibili dalla macchina in TORCS.
 * Questa interfaccia fornisce accesso completo alle informazioni di stato del veicolo
 * e dell'ambiente circostante.
 * 
 * I sensori sono suddivisi in tre categorie:
 * 1. Informazioni base sulla macchina e sulla pista
 * 2. Informazioni sugli avversari (solo per gare multi-car)
 * 3. Informazioni aggiuntive di dettaglio
 */
public interface SensorModel {

	/* === INFORMAZIONI BASE SULLA MACCHINA E SULLA PISTA === */

	/** Velocità attuale della macchina in km/h
     * @return  */
	public double getSpeed();

	/** Angolo tra l'asse della macchina e l'asse della pista (in radianti)
     * @return  */
	public double getAngleToTrackAxis();

	/** Array con le distanze rilevate dai sensori laterali (19 sensori da -90° a +90°)
     * @return  */
	public double[] getTrackEdgeSensors();

	/** Sensori direzionali per rilevare ostacoli in specifiche direzioni
     * @return  */
	public double[] getFocusSensors();

	/** Posizione della macchina sulla pista: -1 (bordo sinistro) .. 0 (centro) .. 1 (bordo destro)
     * @return  */
	public double getTrackPosition();

	/** Marcia attualmente selezionata: -1 (R) .. 0 (N) .. 1-6 (marce)
     * @return  */
	public int getGear();

	/* === INFORMAZIONI SUGLI AVVERSARI (solo gare multi-car) === */

	/** Distanze rilevate dai sensori per rilevare avversari
     * @return  */
	public double[] getOpponentSensors();

	/** Posizione attuale nella classifica (1 = primo posto)
     * @return  */
	public int getRacePosition();

	/* === INFORMAZIONI AGGIUNTIVE DI DETTAGLIO === */

	/** Velocità laterale della macchina (deriva)
     * @return  */
	public double getLateralSpeed();

	/** Tempo impiegato nel giro attuale in secondi
     * @return  */
	public double getCurrentLapTime();

	/** Livello di danno della macchina (0 = intatta, valore crescente con danni)
     * @return  */
	public double getDamage();

	/** Distanza dalla linea di partenza lungo la pista
     * @return  */
	public double getDistanceFromStartLine();

	/** Distanza totale percorsa nella gara
     * @return  */
	public double getDistanceRaced();

	/** Livello attuale del carburante
     * @return  */
	public double getFuelLevel();

	/** Tempo impiegato nel giro precedente in secondi
     * @return  */
	public double getLastLapTime();

	/** Giri per minuto del motore (RPM)
     * @return  */
	public double getRPM();

	/** Velocità di rotazione di ogni ruota (per rilevare slittamenti)
     * @return  */
	public double[] getWheelSpinVelocity();

	/** Velocità verticale della macchina
     * @return  */
	public double getZSpeed();

	/** Altezza della macchina dal suolo
     * @return  */
	public double getZ();

	/** Messaggio di testo inviato dal server (per comunicazioni)
     * @return  */
	public String getMessage();

}
