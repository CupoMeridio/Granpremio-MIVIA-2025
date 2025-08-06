package it.unisa.javaclienttorcs;

public class MessageBasedSensorModel implements SensorModel {

	private final MessageParser message;

	public MessageBasedSensorModel(MessageParser message) {
		this.message = message;
	}

	public MessageBasedSensorModel(String strMessage) {
		this.message = new MessageParser(strMessage);
	}

        @Override
	public double getSpeed() {
		return (Double) message.getReading("speedX");
	}

        @Override
	public double getAngleToTrackAxis() {
		return (Double) message.getReading("angle");
	}

        @Override
	public double[] getTrackEdgeSensors() {
		return (double[]) message.getReading("track");
	}

        @Override
	public double[] getFocusSensors() {
		return (double[]) message.getReading("focus");
	}

        @Override
	public int getGear() {
		return (int) (double) (Double) message.getReading("gear");
	}

        @Override
	public double[] getOpponentSensors() {
		return (double[]) message.getReading("opponents");
	}

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
