package it.unisa.javaclienttorcs;

/**
 * Implementazione del modello sensoriale basata su messaggi ricevuti da TORCS.
 * Questa classe converte i messaggi testuali ricevuti dal server TORCS in un modello
 * sensoriale strutturato che può essere utilizzato dai controller.
 */
public class MessageBasedSensorModel implements SensorModel {

	private final MessageParser message;

	/**
	 * Costruisce un modello sensoriale da un parser di messaggi esistente.
	 * 
	 * @param message Parser del messaggio contenente i dati sensoriali
	 */
	public MessageBasedSensorModel(MessageParser message) {
		this.message = message;
	}

	/**
	 * Costruisce un modello sensoriale da una stringa di messaggio.
	 * 
	 * @param strMessage Stringa del messaggio ricevuto da TORCS
	 */
	public MessageBasedSensorModel(String strMessage) {
		this.message = new MessageParser(strMessage);
	}

        /**
	 * Restituisce la velocità longitudinale della macchina.
	 * 
	 * @return Velocità in m/s lungo l'asse X del veicolo
	 */
	@Override
	public double getSpeed() {
		return (Double) message.getReading("speedX");
	}

	/**
	 * Restituisce l'angolo tra la direzione della macchina e l'asse della pista.
	 * 
	 * @return Angolo in radianti (-π a π)
	 */
        @Override
	public double getAngleToTrackAxis() {
		return (Double) message.getReading("angle");
	}

	/**
	 * Restituisce le letture dei sensori di distanza dai bordi della pista.
	 * 
	 * @return Array di 19 valori di distanza dai bordi (in metri)
	 */
        @Override
	public double[] getTrackEdgeSensors() {
		return (double[]) message.getReading("track");
	}

	/**
	 * Restituisce le letture dei sensori di focus direzionali.
	 * 
	 * @return Array di 5 valori di distanza nella direzione di focus
	 */
        @Override
	public double[] getFocusSensors() {
		return (double[]) message.getReading("focus");
	}

	/**
	 * Restituisce la marcia attualmente inserita.
	 * 
	 * @return Marcia corrente (-1=retromarcia, 0=folle, 1-6=marce avanti)
	 */
        @Override
	public int getGear() {
		return (int) (double) (Double) message.getReading("gear");
	}

	/**
	 * Restituisce le letture dei sensori di rilevamento avversari.
	 * 
	 * @return Array di 36 valori di distanza dagli avversari
	 */
        @Override
	public double[] getOpponentSensors() {
		return (double[]) message.getReading("opponents");
	}

	/**
	 * Restituisce la posizione attuale nella gara.
	 * 
	 * @return Posizione in classifica (1=primo, 2=secondo, etc.)
	 */
        @Override
	public int getRacePosition() {
		return (int) (double) (Double) message.getReading("racePos");
	}

        @Override
	public double getLateralSpeed() {
		return (Double) message.getReading("speedY");
	}

        @Override
	public double getCurrentLapTime() {
		return (Double) message.getReading("curLapTime");
	}

        @Override
	public double getDamage() {
		return (Double) message.getReading("damage");
	}

        @Override
	public double getDistanceFromStartLine() {
		return (Double) message.getReading("distFromStart");
	}

        @Override
	public double getDistanceRaced() {
		return (Double) message.getReading("distRaced");
	}

        @Override
	public double getFuelLevel() {
		return (Double) message.getReading("fuel");
	}

        @Override
	public double getLastLapTime() {
		return (Double) message.getReading("lastLapTime");
	}

        @Override
	public double getRPM() {
		return (Double) message.getReading("rpm");
	}

        @Override
	public double getTrackPosition() {
		return (Double) message.getReading("trackPos");
	}

        @Override
	public double[] getWheelSpinVelocity() {
		return (double[]) message.getReading("wheelSpinVel");
	}

        @Override
	public String getMessage() {
		return message.getMessage();
	}

        @Override
	public double getZ() {
		return (Double) message.getReading("z");
	}

        @Override
	public double getZSpeed() {
		return (Double) message.getReading("speedZ");
	}

}
