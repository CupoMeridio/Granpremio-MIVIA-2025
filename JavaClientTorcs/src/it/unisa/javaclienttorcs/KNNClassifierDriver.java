package it.unisa.javaclienttorcs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Driver KNN Classificatore che utilizza un algoritmo K-Nearest Neighbors
 * per predire classi discrete di azioni di guida.
 */
public class KNNClassifierDriver extends Controller {
    
    private KDTreeClassifier kdTree;
    private List<DataPointClassifier> trainingData;
    private KNNConfig config;
    private double[] featureMin;
    private double[] featureMax;
    
    // Statistiche per debug
    private int totalPredictions = 0;
    private Map<DrivingAction, Integer> actionCounts = new HashMap<>();
    
    /**
     * Costruttore del driver KNN classificatore con dataset di default.
     */
    public KNNClassifierDriver() {
        this("human_dataset_discrete.csv"); // Default dataset discretizzato
    }
    
    /**
     * Costruttore del driver KNN classificatore con dataset personalizzato.
     * 
     * @param datasetPath Percorso del file dataset discretizzato
     */
    public KNNClassifierDriver(String datasetPath) {
        this.trainingData = new ArrayList<>();
        this.config = new KNNConfig(datasetPath);
        this.config.setClassifierMode(true); // Imposta modalità classificatore
        this.kdTree = null;
        
        // Inizializza contatori azioni
        for (DrivingAction action : DrivingAction.values()) {
            actionCounts.put(action, 0);
        }
        
        System.out.println("[KNN-CLASSIFIER] Driver inizializzato con configurazione:");
        System.out.println("[KNN-CLASSIFIER] - K = " + config.getK());
        System.out.println("[KNN-CLASSIFIER] - Normalizzazione = " + config.isNormalizeData());
        System.out.println("[KNN-CLASSIFIER] - Modalità = Classificatore");
        System.out.println("[KNN-CLASSIFIER] - Dataset = " + config.getDatasetPath());
    }
    
    /**
     * Metodo principale di controllo che determina l'azione da eseguire.
     * Utilizza il classificatore KNN per predire la classe di azione ottimale.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale del veicolo
     * @return Azione da eseguire basata sulla classe predetta
     */
    @Override
    public Action control(SensorModel sensors) {
        // Carica il dataset al primo utilizzo
        if (kdTree == null && trainingData.isEmpty()) {
            try {
                loadTrainingData();
                if (config.isNormalizeData()) {
                    calculateNormalizationParameters();
                    normalizeTrainingData();
                }
                buildKDTree();
            } catch (Exception e) {
                System.err.println("[KNN-CLASSIFIER] Errore nel caricamento del dataset: " + e.getMessage());
                return getDefaultAction(sensors);
            }
        }
        
        if (kdTree == null || trainingData.isEmpty()) {
            return getDefaultAction(sensors);
        }
        
        // Controlla se l'auto è fuori strada
        if (isOffTrack(sensors)) {
            return getOffTrackRecoveryAction(sensors);
        }
        
        // Estrai le features dai sensori
        double[] features = extractFeatures(sensors);
        if (config.isNormalizeData()) {
            features = normalizeFeatures(features);
        }
        
        // Trova i K vicini più prossimi
        List<DataPointClassifier> neighbors = kdTree.findKNearestNeighbors(features, config.getK());
        
        // Predici la classe di azione
        DrivingAction predictedAction = predictActionClass(neighbors);
        
        // Aggiorna statistiche
        totalPredictions++;
        actionCounts.put(predictedAction, actionCounts.get(predictedAction) + 1);
        
        // Converti la classe in azione e aggiungi gestione marce
        Action action = predictedAction.toAction();
        action.gear = getAutoGear(sensors);
        
        // Debug periodico
        if (totalPredictions % 100 == 0) {
            System.out.printf("[KNN-CLASSIFIER] Predizioni: %d | Azione: %s | Vicini: %d%n", 
                totalPredictions, predictedAction.getDescription(), neighbors.size());
        }
        
        return action;
    }
    
    /**
     * Carica i dati di training dal file CSV e li converte in classi discrete.
     */
    private void loadTrainingData() {
        String datasetPath = config.getDatasetPath();
        int totalSamples = 0;
        int validSamples = 0;
        
        System.out.println("[KNN-CLASSIFIER] Caricamento dataset: " + datasetPath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                totalSamples++;
                String[] values = line.split(",");
                
                try {
                    // Estrai features (sensori)
                    double[] features = new double[14]; // 10 sensori track + 4 sensori posizione
                    
                    // Track sensors (0,2,4,6,8,10,12,14,16,18)
                    for (int i = 0; i < 10; i++) {
                        features[i] = Double.parseDouble(values[i]);
                    }
                    
                    // Position sensors
                    features[10] = Double.parseDouble(values[10]); // speedX
                    features[11] = Double.parseDouble(values[11]); // angleToTrackAxis
                    features[12] = Double.parseDouble(values[12]); // trackPosition
                    features[13] = Double.parseDouble(values[13]); // distanceFromStartLine
                    
                    // Leggi direttamente la classe di azione dal dataset discretizzato (colonna 15, indice 14)
                    String actionClassName = values[14].trim();
                    DrivingAction actionClass = DrivingAction.valueOf(actionClassName);
                    
                    // Crea punto dati classificatore
                    DataPointClassifier dataPoint = new DataPointClassifier(features, actionClass);
                    trainingData.add(dataPoint);
                    validSamples++;
                    
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("[KNN-CLASSIFIER] Errore parsing riga " + totalSamples + ": " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Errore lettura dataset: " + e.getMessage(), e);
        }
        
        System.out.printf("[KNN-CLASSIFIER] Dataset caricato: %d/%d campioni validi%n", validSamples, totalSamples);
        
        // Stampa distribuzione delle classi
        printClassDistribution();
        
        if (validSamples == 0) {
            throw new RuntimeException("Nessun campione valido trovato nel dataset");
        }
    }
    
    /**
     * Stampa la distribuzione delle classi nel dataset.
     */
    private void printClassDistribution() {
        Map<DrivingAction, Integer> distribution = new HashMap<>();
        for (DrivingAction action : DrivingAction.values()) {
            distribution.put(action, 0);
        }
        
        for (DataPointClassifier point : trainingData) {
            distribution.put(point.getActionClass(), distribution.get(point.getActionClass()) + 1);
        }
        
        System.out.println("[KNN-CLASSIFIER] Distribuzione classi nel dataset:");
        for (Map.Entry<DrivingAction, Integer> entry : distribution.entrySet()) {
            if (entry.getValue() > 0) {
                double percentage = (entry.getValue() * 100.0) / trainingData.size();
                System.out.printf("[KNN-CLASSIFIER] - %s: %d (%.1f%%)%n", 
                    entry.getKey().getDescription(), entry.getValue(), percentage);
            }
        }
    }
    
    /**
     * Calcola i parametri di normalizzazione (min/max) per ogni feature.
     */
    private void calculateNormalizationParameters() {
        if (trainingData.isEmpty()) {
            return;
        }
        
        int numFeatures = trainingData.get(0).features.length;
        featureMin = new double[numFeatures];
        featureMax = new double[numFeatures];
        
        // Inizializza con il primo punto
        System.arraycopy(trainingData.get(0).features, 0, featureMin, 0, numFeatures);
        System.arraycopy(trainingData.get(0).features, 0, featureMax, 0, numFeatures);
        
        // Trova min e max per ogni feature
        for (DataPointClassifier point : trainingData) {
            for (int i = 0; i < numFeatures; i++) {
                featureMin[i] = Math.min(featureMin[i], point.features[i]);
                featureMax[i] = Math.max(featureMax[i], point.features[i]);
            }
        }
    }
    
    /**
     * Normalizza tutti i dati di training.
     */
    private void normalizeTrainingData() {
        for (DataPointClassifier point : trainingData) {
            point.features = normalizeFeatures(point.features);
        }
    }
    
    /**
     * Normalizza un array di features usando normalizzazione manuale ottimizzata.
     * 
     * @param features Array delle features da normalizzare
     * @return Array delle features normalizzate nel range [0, 10]
     */
    private double[] normalizeFeatures(double[] features) {
        if (!config.isNormalizeData()) {
            return features.clone();
        }
        
        double[] normalized = new double[features.length];
        
        for (int i = 0; i < features.length; i++) {
            if (i < 10) {
                // Track sensors: normalizzazione manuale [0, 200] -> [0, 10]
                if (features[i] < 0) {
                    normalized[i] = 0.0;
                } else {
                    normalized[i] = Math.min(features[i] / 200.0, 1.0) * 10.0;
                }
            } else if (i == 10) {
                // speedX: [0, 288] -> [0, 10]
                normalized[i] = Math.max(0.0, Math.min(features[i] / 288.0, 1.0)) * 10.0;
            } else if (i == 11) {
                // angleToTrackAxis: [-π, +π] -> [0, 10]
                double normalizedAngle = (features[i] + Math.PI) / (2 * Math.PI);
                normalized[i] = Math.max(0.0, Math.min(1.0, normalizedAngle)) * 10.0;
            } else if (i == 12) {
                // trackPosition: [-1, +1] -> [0, 10]
                double normalizedPos = (features[i] + 1.0) / 2.0;
                normalized[i] = Math.max(0.0, Math.min(1.0, normalizedPos)) * 10.0;
            } else if (i == 13) {
                // distanceFromStartLine: [0, 5784.10] -> [0, 10]
                normalized[i] = Math.max(0.0, Math.min(features[i] / 5784.10, 1.0)) * 10.0;
            } else {
                normalized[i] = features[i];
            }
        }
        return normalized;
    }
    
    /**
     * Costruisce il KD-Tree dai dati di training.
     */
    private void buildKDTree() {
        if (trainingData.isEmpty()) {
            throw new RuntimeException("Nessun dato di training disponibile per costruire il KD-Tree");
        }
        
        int dimensions = trainingData.get(0).features.length;
        kdTree = new KDTreeClassifier(dimensions);
        kdTree.build(trainingData);
        
        System.out.println("[KNN-CLASSIFIER] " + kdTree.getDebugInfo());
    }
    
    /**
     * Estrae le features dai sensori del veicolo.
     * 
     * @param sensors Modello sensoriale del veicolo
     * @return Array di 14 features estratte
     */
    private double[] extractFeatures(SensorModel sensors) {
        double[] features = new double[14];
        
        // Track sensors (0,2,4,6,8,10,12,14,16,18)
        double[] trackSensors = sensors.getTrackEdgeSensors();
        int[] selectedIndices = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
        
        for (int i = 0; i < 10; i++) {
            features[i] = trackSensors[selectedIndices[i]];
        }
        
        // Position sensors
        features[10] = sensors.getSpeed();
        features[11] = sensors.getAngleToTrackAxis();
        features[12] = sensors.getTrackPosition();
        features[13] = sensors.getDistanceFromStartLine();
        
        return features;
    }
    
    /**
     * Predice la classe di azione basandosi sui vicini più prossimi.
     * Utilizza voto di maggioranza con peso basato sulla distanza.
     * 
     * @param neighbors Lista dei vicini più prossimi
     * @return Classe di azione predetta
     */
    private DrivingAction predictActionClass(List<DataPointClassifier> neighbors) {
        if (neighbors.isEmpty()) {
            return DrivingAction.STRAIGHT_CRUISE; // Azione di default
        }
        
        // Mappa per contare i voti per ogni classe
        Map<DrivingAction, Double> votes = new HashMap<>();
        for (DrivingAction action : DrivingAction.values()) {
            votes.put(action, 0.0);
        }
        
        // Calcola voti pesati per distanza
        for (DataPointClassifier neighbor : neighbors) {
            double weight = 1.0 / (neighbor.distance + 1e-10); // Evita divisione per zero
            DrivingAction actionClass = neighbor.getActionClass();
            votes.put(actionClass, votes.get(actionClass) + weight);
        }
        
        // Trova la classe con il voto più alto
        DrivingAction bestAction = DrivingAction.STRAIGHT_CRUISE;
        double maxVotes = 0.0;
        
        for (Map.Entry<DrivingAction, Double> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                bestAction = entry.getKey();
            }
        }
        
        return bestAction;
    }
    
    /**
     * Determina la marcia ottimale in base ai giri motore (RPM).
     * 
     * @param sensors Modello sensoriale
     * @return Marcia raccomandata
     */
    private int getAutoGear(SensorModel sensors) {
        int currentGear = sensors.getGear();
        double rpm = sensors.getRPM();
        
        if (currentGear < 1)
            return 1;
        
        if (currentGear > 0 && currentGear < 6 && rpm >= 7500)
            return currentGear + 1;
        else if (currentGear > 1 && rpm <= 3000)
            return currentGear - 1;
        else
            return currentGear;
    }
    
    /**
     * Azione di default quando non ci sono dati o l'auto è fuori pista.
     * 
     * @param sensors Modello sensoriale
     * @return Azione di default
     */
    private Action getDefaultAction(SensorModel sensors) {
        Action action = new Action();
        action.steering = 0.0;
        action.accelerate = 0.3;
        action.brake = 0.0;
        action.gear = getAutoGear(sensors);
        
        // Evita ostacoli
        double[] trackSensors = sensors.getTrackEdgeSensors();
        if (trackSensors[9] > 0 && trackSensors[9] < 10) {
            action.brake = 0.8;
            action.accelerate = 0.0;
        }
        
        return action;
    }
    
    /**
     * Rileva se l'auto è fuori strada basandosi sui sensori di traccia
     */
    private boolean isOffTrack(SensorModel sensors) {
        double[] trackSensors = sensors.getTrackEdgeSensors();
        
        // Controlla se almeno uno dei sensori centrali rileva la pista
        // Usa una soglia più conservativa per evitare falsi positivi
        int onTrackCount = 0;
        for (int i = 6; i <= 12; i++) { // Sensori da -60° a +60°
            if (trackSensors[i] > 5.0) { // Soglia minima di 5 metri
                onTrackCount++;
            }
        }
        
        // Considera fuori strada solo se meno di 2 sensori rilevano la pista
        return onTrackCount < 2;
    }
    
    /**
     * Gestisce il recupero quando l'auto è fuori strada
     */
    private Action getOffTrackRecoveryAction(SensorModel sensors) {
        double[] trackSensors = sensors.getTrackEdgeSensors();
        double angle = sensors.getAngleToTrackAxis();
        double speed = sensors.getSpeed();
        
        // Trova la direzione verso la pista
        double maxDistance = -1;
        int bestSensorIndex = -1;
        
        for (int i = 0; i < trackSensors.length; i++) {
            if (trackSensors[i] > maxDistance) {
                maxDistance = trackSensors[i];
                bestSensorIndex = i;
            }
        }
        
        DrivingAction recoveryAction;
        
        // Determina l'intensità del recupero basata sulla distanza, velocità e angolo
        boolean isCloseToTrack = maxDistance > 10.0;
        boolean isHighSpeed = speed > 80.0;
        boolean isMediumSpeed = speed > 40.0;
        boolean isWrongAngle = Math.abs(angle) > 0.5; // Angolo maggiore di ~30 gradi
        
        // Determina la direzione considerando sia i sensori che l'angolo
        boolean shouldTurnLeft = false;
        boolean shouldTurnRight = false;
        
        // Logica basata sui sensori: sterza verso dove si trova la pista
        if (bestSensorIndex < 6) {
            shouldTurnLeft = true;
        } else if (bestSensorIndex > 12) {
            shouldTurnRight = true;
        }
        
        // Correzione basata sull'angolo: se l'auto è orientata male, correggi
        if (angle > 0.3) { // Auto orientata troppo a destra
            shouldTurnLeft = true;
            shouldTurnRight = false;
        } else if (angle < -0.3) { // Auto orientata troppo a sinistra
            shouldTurnRight = true;
            shouldTurnLeft = false;
        }
        
        // Determina l'azione finale
        if (shouldTurnLeft) {
            if (isCloseToTrack && !isWrongAngle) {
                recoveryAction = DrivingAction.OFFTRACK_GENTLE_LEFT;
            } else if (isMediumSpeed && !isWrongAngle) {
                recoveryAction = DrivingAction.OFFTRACK_MODERATE_LEFT;
            } else {
                recoveryAction = DrivingAction.OFFTRACK_STRONG_LEFT;
            }
        } else if (shouldTurnRight) {
            if (isCloseToTrack && !isWrongAngle) {
                recoveryAction = DrivingAction.OFFTRACK_GENTLE_RIGHT;
            } else if (isMediumSpeed && !isWrongAngle) {
                recoveryAction = DrivingAction.OFFTRACK_MODERATE_RIGHT;
            } else {
                recoveryAction = DrivingAction.OFFTRACK_STRONG_RIGHT;
            }
        } else {
            // Pista davanti o situazione ambigua
            if (isHighSpeed || isWrongAngle) {
                recoveryAction = DrivingAction.OFFTRACK_STRAIGHT_SLOW;
            } else {
                recoveryAction = DrivingAction.OFFTRACK_STRAIGHT_FAST;
            }
        }
        
        Action action = recoveryAction.toAction();
        action.gear = getAutoGear(sensors);
        
        // Debug per il recupero
        if (totalPredictions % 25 == 0) {
            System.out.printf("[KNN-CLASSIFIER] FUORI STRADA - Recupero: %s%n", recoveryAction.getDescription());
            System.out.printf("[KNN-CLASSIFIER] - Sensore migliore: %d | Distanza: %.1f | Velocità: %.1f | Angolo: %.2f%n", 
                bestSensorIndex, maxDistance, speed, angle);
            System.out.printf("[KNN-CLASSIFIER] - Decisione: TurnLeft=%b | TurnRight=%b%n", shouldTurnLeft, shouldTurnRight);
        }
        
        return action;
    }
    
    /**
     * Stampa statistiche finali del classificatore.
     */
    public void printFinalStatistics() {
        System.out.println("\n[KNN-CLASSIFIER] Statistiche finali:");
        System.out.println("[KNN-CLASSIFIER] Predizioni totali: " + totalPredictions);
        System.out.println("[KNN-CLASSIFIER] Distribuzione azioni predette:");
        
        for (Map.Entry<DrivingAction, Integer> entry : actionCounts.entrySet()) {
            if (entry.getValue() > 0) {
                double percentage = (entry.getValue() * 100.0) / totalPredictions;
                System.out.printf("[KNN-CLASSIFIER] - %s: %d (%.1f%%)%n", 
                    entry.getKey().getDescription(), entry.getValue(), percentage);
            }
        }
    }
    
    /**
     * Restituisce il numero di punti dati di training caricati.
     * 
     * @return Numero di punti dati di training
     */
    public int getTrainingDataSize() {
        return trainingData.size();
    }
    
    /**
     * Verifica se il driver è pronto per essere utilizzato.
     * 
     * @return true se il KD-Tree è costruito e i dati sono caricati
     */
    public boolean isReady() {
        return kdTree != null && !trainingData.isEmpty();
    }
    
    /**
     * Metodo chiamato per resettare lo stato del driver.
     * Implementazione richiesta dalla classe Controller.
     */
    @Override
    public void reset() {
        // Reset delle statistiche
        totalPredictions = 0;
        for (DrivingAction action : DrivingAction.values()) {
            actionCounts.put(action, 0);
        }
        
        System.out.println("[KNN-CLASSIFIER] Driver resettato");
    }
    
    /**
     * Metodo chiamato alla chiusura del driver.
     * Implementazione richiesta dalla classe Controller.
     */
    @Override
    public void shutdown() {
        printFinalStatistics();
        System.out.println("[KNN-CLASSIFIER] Driver terminato");
    }
}