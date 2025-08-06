package it.unisa.javaclienttorcs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * KNN Driver che utilizza un algoritmo K-Nearest Neighbors basato su KD-tree
 * per predire le azioni di guida basandosi sui dati di training.
 */
public class KNNDriver extends Controller {
    
    private KDTree kdTree;
    private List<DataPoint> trainingData;
    private KNNConfig config;
    private double[] featureMin;
    private double[] featureMax;
    

    
    /**
     * Costruttore del KNN Driver con configurazione
     * @param config Configurazione del KNN
     */
    public KNNDriver(KNNConfig config) {
        this.config = config;
        this.trainingData = new ArrayList<>();
        
        config.validate();
        
        if (config.isEnableLogging()) {
            System.out.println("[KNN] Inizializzazione con configurazione: " + config);
        }
        
        loadTrainingData();
        if (config.isNormalizeData()) {
            calculateNormalizationParameters();
            normalizeTrainingData();
        }
        buildKDTree();
    }
    
    /**
     * Costruttore con parametri di default (usa dataset automatico)
     */
    public KNNDriver() {
        this("auto_dataset.csv"); // Default dataset
    }
    
    /**
     * Costruttore semplificato che accetta solo il percorso del dataset
     * @param datasetFilename Nome del file dataset
     */
    public KNNDriver(String datasetFilename) {
        this.config = new KNNConfig(datasetFilename);
        this.trainingData = new ArrayList<>();
        
        // Rimanda il caricamento del dataset al primo utilizzo per evitare errori prematuri
        // loadTrainingData();
        // if (config.isNormalizeData()) {
        //     calculateNormalizationParameters();
        //     normalizeTrainingData();
        // }
        // buildKDTree();
    }
    

    

    
    @Override
    public Action control(SensorModel sensors) {
        // Carica il dataset al primo utilizzo se non è già stato caricato
        if (kdTree == null && trainingData.isEmpty()) {
            try {
                loadTrainingData();
                if (config.isNormalizeData()) {
                    calculateNormalizationParameters();
                    normalizeTrainingData();
                }
                buildKDTree();
            } catch (Exception e) {
                System.err.println("[KNN] Errore nel caricamento del dataset: " + e.getMessage());
                return getDefaultAction(sensors);
            }
        }
        
        if (kdTree == null || trainingData.isEmpty()) {
            // Fallback: comportamento di base se non ci sono dati
            return getDefaultAction(sensors);
        }
        
        // Estrai le features dai sensori
        double[] features = extractFeatures(sensors);
        
        // Normalizza le features se necessario
        if (config.isNormalizeData()) {
            features = normalizeFeatures(features);
        }
        
        // Trova i k vicini più prossimi
        List<DataPoint> neighbors = kdTree.findKNearestNeighbors(features, config.getK());
        
        // Predici l'azione basandoti sui vicini
        return predictAction(neighbors, sensors);
    }
    
    /**
     * Carica i dati di training dal file CSV
     */
    private void loadTrainingData() {
        String datasetPath = config.getDatasetPath();
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                
                // Estrai features (sensori) e target (azioni)
                double[] features = new double[14]; // 10 sensori track + 4 sensori posizione
                
                // Track sensors (0,2,4,6,8,10,12,14,16,18)
                for (int i = 0; i < 10; i++) {
                    features[i] = Double.parseDouble(values[i]);
                }
                
                // Position sensors (speedX, angleToTrackAxis, trackPosition, distanceFromStartLine)
                features[10] = Double.parseDouble(values[10]); // speedX
                features[11] = Double.parseDouble(values[11]); // angleToTrackAxis
                features[12] = Double.parseDouble(values[12]); // trackPosition
                features[13] = Double.parseDouble(values[13]); // distanceFromStartLine
                
                // Target actions (steering, acceleration, brake)
                double steering = Double.parseDouble(values[14]);
                double acceleration = Double.parseDouble(values[15]);
                double brake = Double.parseDouble(values[16]);
                
                trainingData.add(new DataPoint(features, steering, acceleration, brake));
            }
            
            // Carica tutti i dati disponibili senza limiti
            
            if (config.isEnableLogging()) {
                System.out.println("[KNN] Caricati " + trainingData.size() + " punti di training da " + datasetPath);
            }
            
        } catch (IOException e) {
            System.err.println("[KNN] Errore nel caricamento del dataset: " + e.getMessage());
        }
    }
    
    /**
     * Calcola i parametri per la normalizzazione (min-max scaling)
     */
    private void calculateNormalizationParameters() {
        if (trainingData.isEmpty()) return;
        
        int featureCount = trainingData.get(0).features.length;
        featureMin = new double[featureCount];
        featureMax = new double[featureCount];
        
        // Inizializza con il primo punto
        System.arraycopy(trainingData.get(0).features, 0, featureMin, 0, featureCount);
        System.arraycopy(trainingData.get(0).features, 0, featureMax, 0, featureCount);
        
        // Trova min e max per ogni feature
        for (DataPoint point : trainingData) {
            for (int i = 0; i < featureCount; i++) {
                featureMin[i] = Math.min(featureMin[i], point.features[i]);
                featureMax[i] = Math.max(featureMax[i], point.features[i]);
            }
        }
    }
    
    /**
     * Normalizza i dati di training
     */
    private void normalizeTrainingData() {
        for (DataPoint point : trainingData) {
            point.features = normalizeFeatures(point.features);
        }
    }
    
    /**
     * Normalizza un array di features usando min-max scaling
     */
    private double[] normalizeFeatures(double[] features) {
        if (!config.isNormalizeData()) {
            return features.clone();
        }
        
        double[] normalized = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            if (featureMax[i] - featureMin[i] != 0) {
                normalized[i] = (features[i] - featureMin[i]) / (featureMax[i] - featureMin[i]);
            } else {
                normalized[i] = 0.0; // Se min == max, la feature è costante
            }
        }
        return normalized;
    }
    
    /**
     * Costruisce il KD-tree dai dati di training
     */
    private void buildKDTree() {
        kdTree = new KDTree(trainingData);
        if (config.isEnableLogging()) {
            System.out.println("[KNN] KD-tree costruito con " + trainingData.size() + " nodi");
        }
    }
    
    /**
     * Estrae le features dai sensori
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
     * Predice l'azione basandosi sui vicini più prossimi
     */
    private Action predictAction(List<DataPoint> neighbors, SensorModel sensors) {
        if (neighbors.isEmpty()) {
            return getDefaultAction(sensors);
        }
        
        double weightedSteering, weightedAcceleration, weightedBrake;
        
        if (config.isUseWeightedVoting()) {
            // Media pesata delle azioni dei vicini
            double totalWeight = 0.0;
            weightedSteering = 0.0;
            weightedAcceleration = 0.0;
            weightedBrake = 0.0;
            
            for (DataPoint neighbor : neighbors) {
                double weight = 1.0 / (neighbor.distance + config.getMinWeight());
                totalWeight += weight;
                
                weightedSteering += neighbor.steering * weight;
                weightedAcceleration += neighbor.acceleration * weight;
                weightedBrake += neighbor.brake * weight;
            }
            
            // Normalizza i pesi
            if (totalWeight > 0) {
                weightedSteering /= totalWeight;
                weightedAcceleration /= totalWeight;
                weightedBrake /= totalWeight;
            }
        } else {
            // Media semplice (voto uniforme)
            weightedSteering = 0.0;
            weightedAcceleration = 0.0;
            weightedBrake = 0.0;
            
            for (DataPoint neighbor : neighbors) {
                weightedSteering += neighbor.steering;
                weightedAcceleration += neighbor.acceleration;
                weightedBrake += neighbor.brake;
            }
            
            int count = neighbors.size();
            weightedSteering /= count;
            weightedAcceleration /= count;
            weightedBrake /= count;
        }
        
        // Crea e restituisci l'azione
        Action action = new Action();
        action.steering = Math.max(-1.0, Math.min(1.0, weightedSteering));
        action.accelerate = Math.max(0.0, Math.min(1.0, weightedAcceleration));
        action.brake = Math.max(0.0, Math.min(1.0, weightedBrake));
        
        // Aggiungi gestione automatica delle marce
        action.gear = getAutoGear(sensors);
        
        return action;
    }
    
    /**
     * Determina la marcia ottimale in base ai giri motore (RPM) per modalità automatica.
     * Utilizza la stessa logica del HumanController per garantire coerenza.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale della macchina
     * @return Marcia raccomandata (-1 per retromarcia, 0 per folle, 1-6 per marce avanti)
     */
    private int getAutoGear(SensorModel sensors) {
        int currentGear = sensors.getGear();
        double rpm = sensors.getRPM();
        
        // Gestione marcia 0 (folle) - imposta sempre 1ª marcia
        if (currentGear < 1)
            return 1;
        
        // Logica upshift: se RPM supera soglia (solo marce avanti)
        if (currentGear > 0 && currentGear < 6 && rpm >= 19000)
            return currentGear + 1;
        // Logica downshift: se RPM sotto soglia (solo marce avanti)
        else if (currentGear > 1 && rpm <= 7000)
            return currentGear - 1;
        else
            return currentGear; // Mantieni marcia attuale
    }
    
    /**
     * Azione di default quando non ci sono dati
     */
    private Action getDefaultAction(SensorModel sensors) {
        Action action = new Action();
        
        // Comportamento di base: vai dritto con accelerazione moderata
        action.steering = 0.0;
        action.accelerate = 0.3;
        action.brake = 0.0;
        
        // Aggiungi gestione automatica delle marce anche per l'azione di default
        action.gear = getAutoGear(sensors);
        
        // Evita ostacoli
        double[] trackSensors = sensors.getTrackEdgeSensors();
        if (trackSensors[9] < 10) { // Sensore frontale
            action.brake = 0.8;
            action.accelerate = 0.0;
        }
        
        return action;
    }
    
    @Override
    public void reset() {
        // Reset se necessario
    }
    
    @Override
    public void shutdown() {
        // Cleanup se necessario
    }
    
    // Getters e setters
    public KNNConfig getConfig() {
        return config;
    }
    
    public void updateConfig(KNNConfig newConfig) {
        newConfig.validate();
        boolean needsRebuild = !config.getDatasetPath().equals(newConfig.getDatasetPath());
        
        this.config = newConfig;
        
        if (needsRebuild) {
            trainingData.clear();
            loadTrainingData();
            if (config.isNormalizeData()) {
                calculateNormalizationParameters();
                normalizeTrainingData();
            }
            buildKDTree();
        }
    }
    
    public int getTrainingDataSize() {
        return trainingData.size();
    }
    
    public boolean isReady() {
        return kdTree != null && !trainingData.isEmpty();
    }
    

}