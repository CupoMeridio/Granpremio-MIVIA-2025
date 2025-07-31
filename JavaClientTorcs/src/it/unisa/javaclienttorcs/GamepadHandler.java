package it.unisa.javaclienttorcs;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

/**
 * Classe per gestire l'input da gamepad utilizzando la libreria Jamepad.
 */
public class GamepadHandler {
    private final ControllerManager controllers;
    private final int controllerIndex;
    
    /**
     * Costruttore della classe GamepadHandler.
     * @param controllerIndex Indice del controller da utilizzare (0 per il primo controller)
     */
    public GamepadHandler(int controllerIndex) {
        this.controllerIndex = controllerIndex;
        this.controllers = new ControllerManager();
        this.controllers.initSDLGamepad();
    }
    
    /**
     * Aggiorna lo stato del controller.
     * Deve essere chiamato ogni frame prima di leggere gli input.
     */
    public void update() {
        controllers.update();
    }
    
    /**
     * Ottiene lo stato corrente del controller.
     * @return Lo stato del controller
     */
    public ControllerState getState() {
        return controllers.getState(controllerIndex);
    }
    
    /**
     * Verifica se il pulsante A è premuto.
     * @return true se il pulsante A è premuto, false altrimenti
     */
    public boolean isAPressed() {
        return getState().a;
    }
    
    /**
     * Verifica se il pulsante B è premuto.
     * @return true se il pulsante B è premuto, false altrimenti
     */
    public boolean isBPressed() {
        return getState().b;
    }
    
    /**
     * Verifica se il pulsante X è premuto.
     * @return true se il pulsante X è premuto, false altrimenti
     */
    public boolean isXPressed() {
        return getState().x;
    }
    
    /**
     * Verifica se il pulsante Y è premuto.
     * @return true se il pulsante Y è premuto, false altrimenti
     */
    public boolean isYPressed() {
        return getState().y;
    }
    
    /**
     * Verifica se il pulsante Start è premuto.
     * @return true se il pulsante Start è premuto, false altrimenti
     */
    public boolean isStartPressed() {
        return getState().start;
    }
    
    /**
     * Verifica se il pulsante Back/Select è premuto.
     * @return true se il pulsante Back/Select è premuto, false altrimenti
     */
    public boolean isBackPressed() {
        return getState().back;
    }
    
    /**
     * Verifica se il bumper sinistro è premuto.
     * @return true se il bumper sinistro è premuto, false altrimenti
     */
    public boolean isLeftBumperPressed() {
        return getState().lb;
    }
    
    /**
     * Verifica se il bumper destro è premuto.
     * @return true se il bumper destro è premuto, false altrimenti
     */
    public boolean isRightBumperPressed() {
        return getState().rb;
    }
    
    /**
     * Ottiene il valore del trigger sinistro (0.0 - 1.0).
     * @return Valore del trigger sinistro
     */
    public float getLeftTrigger() {
        return getState().leftTrigger;
    }
    
    /**
     * Ottiene il valore del trigger destro (0.0 - 1.0).
     * @return Valore del trigger destro
     */
    public float getRightTrigger() {
        return getState().rightTrigger;
    }
    
    /**
     * Ottiene il valore orizzontale dell'analogico sinistro (-1.0 - 1.0).
     * @return Valore orizzontale dell'analogico sinistro
     */
    public float getLeftStickX() {
        return getState().leftStickX;
    }
    
    /**
     * Ottiene il valore verticale dell'analogico sinistro (-1.0 - 1.0).
     * @return Valore verticale dell'analogico sinistro
     */
    public float getLeftStickY() {
        return getState().leftStickY;
    }
    
    /**
     * Ottiene il valore orizzontale dell'analogico destro (-1.0 - 1.0).
     * @return Valore orizzontale dell'analogico destro
     */
    public float getRightStickX() {
        return getState().rightStickX;
    }
    
    /**
     * Ottiene il valore verticale dell'analogico destro (-1.0 - 1.0).
     * @return Valore verticale dell'analogico destro
     */
    public float getRightStickY() {
        return getState().rightStickY;
    }
    
    /**
     * Verifica se la croce direzionale su è premuta.
     * @return true se la croce direzionale su è premuta, false altrimenti
     */
    public boolean isDPadUpPressed() {
        return getState().dpadUp;
    }
    
    /**
     * Verifica se la croce direzionale giù è premuta.
     * @return true se la croce direzionale giù è premuta, false altrimenti
     */
    public boolean isDPadDownPressed() {
        return getState().dpadDown;
    }
    
    /**
     * Verifica se la croce direzionale sinistra è premuta.
     * @return true se la croce direzionale sinistra è premuta, false altrimenti
     */
    public boolean isDPadLeftPressed() {
        return getState().dpadLeft;
    }
    
    /**
     * Verifica se la croce direzionale destra è premuta.
     * @return true se la croce direzionale destra è premuta, false altrimenti
     */
    public boolean isDPadRightPressed() {
        return getState().dpadRight;
    }
    
    /**
     * Verifica se il controller è connesso.
     * @return true se il controller è connesso, false altrimenti
     */
    public boolean isConnected() {
        return getState().isConnected;
    }
    
    /**
     * Chiude la connessione con i controller.
     * Da chiamare quando il gamepad non è più necessario.
     */
    public void close() {
        controllers.quitSDLGamepad();
    }

    @Override
    public String toString() {
        return "GamepadHandler{" + "controllers=" + controllers + ", controllerIndex=" + controllerIndex + '}';
    }
    
}
