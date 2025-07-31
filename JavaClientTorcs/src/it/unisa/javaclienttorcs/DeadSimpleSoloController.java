package it.unisa.javaclienttorcs;

public class DeadSimpleSoloController extends Controller {

	final double targetSpeed = 15;

        @Override
	public Action control(SensorModel sensorModel) {
		Action action = new Action();
		if (sensorModel.getSpeed() < targetSpeed) {
			action.accelerate = 1;
		}
		if (sensorModel.getAngleToTrackAxis() < 0) {
			action.steering = -0.1;
		} else {
			action.steering = 0.1;
		}
		action.gear = 1;
		return action;
	}

        @Override
	public void reset() {
		System.out.println("[INFO] DeadSimpleSoloController: reset per nuova gara");

	}

        @Override
	public void shutdown() {
		System.out.println("[INFO] DeadSimpleSoloController: chiusura controller");

	}
}
