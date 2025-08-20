package it.unisa.javaclienttorcs;

/**
 * Enumerazione ottimizzata delle azioni di guida discrete per il classificatore KNN.
 * Ridotta da 31 a 15 azioni per evitare overfitting e migliorare la generalizzazione.
 * Ogni azione rappresenta una combinazione specifica di sterzo, accelerazione e freno.
 * 
 * Ottimizzazioni applicate:
 * - Eliminazione di azioni troppo simili (es. ACCELERATE vs CRUISE per stesse curve)
 * - Consolidamento delle azioni di recupero fuori strada
 * - Mantenimento della copertura funzionale completa
 */
public enum DrivingAction {
    // === AZIONI DRITTE (3 azioni) ===
    STRAIGHT_FAST("Dritto Veloce", 0.0, 0.8, 0.0),
    STRAIGHT_NORMAL("Dritto Normale", 0.0, 0.4, 0.0),
    STRAIGHT_BRAKE("Dritto + Frena", 0.0, 0.0, 0.7),
    
    // === CURVE SINISTRA (3 azioni) ===
    LEFT_GENTLE("Sinistra Dolce", -0.2, 0.6, 0.0),
    LEFT_MODERATE("Sinistra Moderata", -0.5, 0.4, 0.0),
    LEFT_SHARP("Sinistra Stretta", -0.8, 0.2, 0.1),
    
    // === CURVE DESTRA (3 azioni) ===
    RIGHT_GENTLE("Destra Dolce", 0.2, 0.6, 0.0),
    RIGHT_MODERATE("Destra Moderata", 0.5, 0.4, 0.0),
    RIGHT_SHARP("Destra Stretta", 0.8, 0.2, 0.1),
    
    // === AZIONI DI EMERGENZA (3 azioni) ===
    EMERGENCY_BRAKE("Frenata di Emergenza", 0.0, 0.0, 1.0),
    EMERGENCY_LEFT("Evitamento Sinistra", -0.7, 0.0, 0.4),
    EMERGENCY_RIGHT("Evitamento Destra", 0.7, 0.0, 0.4),
    
    // === RECUPERO FUORI STRADA (3 azioni) ===
    OFFTRACK_LEFT("Recupero Sinistra", -0.4, 0.4, 0.0),
    OFFTRACK_RIGHT("Recupero Destra", 0.4, 0.4, 0.0),
    OFFTRACK_STRAIGHT("Recupero Dritto", 0.0, 0.5, 0.0);
    
    private final String description;
    private final double steering;
    private final double acceleration;
    private final double brake;
    
    /**
     * Costruttore per un'azione di guida.
     * 
     * @param description Descrizione dell'azione
     * @param steering Valore di sterzo (-1.0 a 1.0)
     * @param acceleration Valore di accelerazione (0.0 a 1.0)
     * @param brake Valore di freno (0.0 a 1.0)
     */
    DrivingAction(String description, double steering, double acceleration, double brake) {
        this.description = description;
        this.steering = steering;
        this.acceleration = acceleration;
        this.brake = brake;
    }
    
    /**
     * Restituisce la descrizione dell'azione.
     * 
     * @return Descrizione dell'azione
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Restituisce il valore di sterzo per questa azione.
     * 
     * @return Valore di sterzo (-1.0 a 1.0)
     */
    public double getSteering() {
        return steering;
    }
    
    /**
     * Restituisce il valore di accelerazione per questa azione.
     * 
     * @return Valore di accelerazione (0.0 a 1.0)
     */
    public double getAcceleration() {
        return acceleration;
    }
    
    /**
     * Restituisce il valore di freno per questa azione.
     * 
     * @return Valore di freno (0.0 a 1.0)
     */
    public double getBrake() {
        return brake;
    }
    
    /**
     * Converte un'azione continua in un'azione discreta.
     * Trova l'azione discreta più vicina basandosi sulla distanza euclidea.
     * 
     * @param steering Valore di sterzo continuo
     * @param acceleration Valore di accelerazione continuo
     * @param brake Valore di freno continuo
     * @return Azione discreta più vicina
     */
    public static DrivingAction fromContinuous(double steering, double acceleration, double brake) {
        DrivingAction bestAction = STRAIGHT_NORMAL;
        double minDistance = Double.MAX_VALUE;
        
        for (DrivingAction action : values()) {
            double distance = Math.sqrt(
                Math.pow(steering - action.steering, 2) +
                Math.pow(acceleration - action.acceleration, 2) +
                Math.pow(brake - action.brake, 2)
            );
            
            if (distance < minDistance) {
                minDistance = distance;
                bestAction = action;
            }
        }
        
        return bestAction;
    }
    
    /**
     * Converte questa azione discreta in un oggetto Action.
     * 
     * @return Oggetto Action corrispondente
     */
    public Action toAction() {
        Action action = new Action();
        action.steering = this.steering;
        action.accelerate = this.acceleration;
        action.brake = this.brake;
        return action;
    }
    
    /**
     * Restituisce una rappresentazione stringa dell'azione.
     * 
     * @return Stringa rappresentativa dell'azione
     */
    @Override
    public String toString() {
        return String.format("%s [S:%.1f A:%.1f B:%.1f]", 
            description, steering, acceleration, brake);
    }
}