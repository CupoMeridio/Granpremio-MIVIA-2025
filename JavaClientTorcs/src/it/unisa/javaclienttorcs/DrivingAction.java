package it.unisa.javaclienttorcs;

/**
 * Enumerazione delle azioni di guida discrete per il classificatore KNN.
 * Ogni azione rappresenta una combinazione specifica di sterzo, accelerazione e freno.
 */
public enum DrivingAction {
    // Azioni dritte
    STRAIGHT_ACCELERATE("Dritto + Accelera", 0.0, 1.0, 0.0),
    STRAIGHT_CRUISE("Dritto + Crociera", 0.0, 0.4, 0.0),
    STRAIGHT_BRAKE("Dritto + Frena", 0.0, 0.0, 0.8),
    
    // Curve leggere a sinistra
    LEFT_LIGHT_ACCELERATE("Sinistra Leggera + Accelera", -0.15, 0.8, 0.0),
    LEFT_LIGHT_CRUISE("Sinistra Leggera + Crociera", -0.15, 0.4, 0.0),
    LEFT_LIGHT_BRAKE("Sinistra Leggera + Frena", -0.15, 0.0, 0.5),
    
    // Curve moderate a sinistra
    LEFT_MODERATE_ACCELERATE("Sinistra Moderata + Accelera", -0.4, 0.6, 0.0),
    LEFT_MODERATE_CRUISE("Sinistra Moderata + Crociera", -0.4, 0.3, 0.0),
    LEFT_MODERATE_BRAKE("Sinistra Moderata + Frena", -0.4, 0.0, 0.6),
    
    // Curve strette a sinistra
    LEFT_SHARP_ACCELERATE("Sinistra Stretta + Accelera", -0.8, 0.4, 0.0),
    LEFT_SHARP_CRUISE("Sinistra Stretta + Crociera", -0.8, 0.2, 0.0),
    LEFT_SHARP_BRAKE("Sinistra Stretta + Frena", -1.0, 0.0, 0.8),
    
    // Curve leggere a destra
    RIGHT_LIGHT_ACCELERATE("Destra Leggera + Accelera", 0.15, 0.8, 0.0),
    RIGHT_LIGHT_CRUISE("Destra Leggera + Crociera", 0.15, 0.4, 0.0),
    RIGHT_LIGHT_BRAKE("Destra Leggera + Frena", 0.15, 0.0, 0.5),
    
    // Curve moderate a destra
    RIGHT_MODERATE_ACCELERATE("Destra Moderata + Accelera", 0.4, 0.6, 0.0),
    RIGHT_MODERATE_CRUISE("Destra Moderata + Crociera", 0.4, 0.3, 0.0),
    RIGHT_MODERATE_BRAKE("Destra Moderata + Frena", 0.4, 0.0, 0.6),
    
    // Curve strette a destra
    RIGHT_SHARP_ACCELERATE("Destra Stretta + Accelera", 0.8, 0.4, 0.0),
    RIGHT_SHARP_CRUISE("Destra Stretta + Crociera", 0.8, 0.2, 0.0),
    RIGHT_SHARP_BRAKE("Destra Stretta + Frena", 1.0, 0.0, 0.8),
    
    // Azioni di emergenza
    EMERGENCY_BRAKE("Frenata di Emergenza", 0.0, 0.0, 1.0),
    EMERGENCY_LEFT("Evitamento Sinistra", -0.8, 0.0, 0.3),
    EMERGENCY_RIGHT("Evitamento Destra", 0.8, 0.0, 0.3),
    
    // Azioni di recupero fuori strada graduali
    OFFTRACK_GENTLE_LEFT("Recupero Dolce Sinistra", -0.2, 0.4, 0.0),
    OFFTRACK_GENTLE_RIGHT("Recupero Dolce Destra", 0.2, 0.4, 0.0),
    OFFTRACK_MODERATE_LEFT("Recupero Moderato Sinistra", -0.4, 0.5, 0.0),
    OFFTRACK_MODERATE_RIGHT("Recupero Moderato Destra", 0.4, 0.5, 0.0),
    OFFTRACK_STRONG_LEFT("Recupero Forte Sinistra", -0.6, 0.3, 0.1),
    OFFTRACK_STRONG_RIGHT("Recupero Forte Destra", 0.6, 0.3, 0.1),
    OFFTRACK_STRAIGHT_SLOW("Recupero Dritto Lento", 0.0, 0.3, 0.0),
    OFFTRACK_STRAIGHT_FAST("Recupero Dritto Veloce", 0.0, 0.6, 0.0);
    
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
        DrivingAction bestAction = STRAIGHT_CRUISE;
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