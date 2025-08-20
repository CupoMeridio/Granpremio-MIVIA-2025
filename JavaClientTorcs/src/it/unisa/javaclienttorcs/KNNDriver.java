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
     * Costruttore con parametri di default (usa dataset umano)
     */
    public KNNDriver() {
        this("human_dataset.csv"); // Default dataset
    }
    
    /**
     * Costruttore semplificato che accetta solo il percorso del dataset.
     * Il caricamento dei dati viene rimandato al primo utilizzo per evitare errori prematuri.
     * 
     * @param datasetFilename Nome del file dataset
     */
    public KNNDriver(String datasetFilename) {
        this.config = new KNNConfig(datasetFilename);
        this.trainingData = new ArrayList<>();
    }
    /**
     * Metodo principale di controllo che determina l'azione da eseguire basandosi sui sensori.
     * Utilizza l'algoritmo K-Nearest Neighbors per predire l'azione ottimale.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale del veicolo
     * @return Azione da eseguire (sterzo, accelerazione, freno, marcia)
     */
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
        int totalSamples = 0;
        int validSamples = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                totalSamples++;
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
                validSamples++;
                
                // Target actions (steering, acceleration, brake)
                double steering = Double.parseDouble(values[14]);
                double acceleration = Double.parseDouble(values[15]);
                double brake = Double.parseDouble(values[16]);
                
                trainingData.add(new DataPoint(features, steering, acceleration, brake));
            }
            
            if (config.isEnableLogging()) {
                System.out.println("[KNN] Caricati " + validSamples + "/" + totalSamples + " punti di training da " + datasetPath);
            }
            
        } catch (IOException e) {
            System.err.println("[KNN] Errore nel caricamento del dataset: " + e.getMessage());
        }
    }
    
    /**
     * Calcola i parametri di normalizzazione (min e max) per ogni feature
     * dai dati di training. Mantenuto per uso futuro anche se attualmente
     * tutti i sensori utilizzano normalizzazione manuale.
     */
    private void calculateNormalizationParameters() {
        if (trainingData.isEmpty()) return;
        
        int featureCount = trainingData.get(0).features.length;
        featureMin = new double[featureCount];
        featureMax = new double[featureCount];
        
        // Inizializza con il primo punto
        System.arraycopy(trainingData.get(0).features, 0, featureMin, 0, featureCount);
        System.arraycopy(trainingData.get(0).features, 0, featureMax, 0, featureCount);
        
        // Trova min e max per tutti i sensori
        for (DataPoint point : trainingData) {
            for (int i = 0; i < featureCount; i++) {
                featureMin[i] = Math.min(featureMin[i], point.features[i]);
                featureMax[i] = Math.max(featureMax[i], point.features[i]);
            }
        }
    }

    /**
     * Normalizza tutti i dati di training usando normalizzazione manuale ottimizzata.
     * Tutti i sensori utilizzano normalizzazione manuale con range [0, 10]:
     * - Track sensors (0-9): [0, 200] -> [0, 10]
     * - speedX (10): [0, 288] -> [0, 10]
     * - angleToTrackAxis (11): [-π, +π] -> [0, 10]
     * - trackPosition (12): [-1, +1] -> [0, 10]
     * - distanceFromStartLine (13): [0, 5784.10] -> [0, 10]
     */
    private void normalizeTrainingData() {
        for (DataPoint point : trainingData) {
            point.features = normalizeFeatures(point.features);
        }
    }
    
    /**
     * Normalizza un array di features usando normalizzazione manuale con range ottimizzato.
     * Applica normalizzazione manuale per tutti i sensori con valori fissi predefiniti.
     * Range ottimizzato [0, 10] per bilanciare granularità e performance.
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
                // Track sensors (indici 0-9): normalizzazione manuale con range [0, 200] -> [0, 10]
                if (features[i] < 0) {
                    normalized[i] = 0.0; // Valore fuori pista
                } else {
                    // Normalizza da [0, 200] a [0, 10]
                    normalized[i] = Math.min(features[i] / 200.0, 1.0) * 10.0;
                }
            } else if (i == 10) {
                // speedX: normalizza da [0, 288] a [0, 10]
                normalized[i] = Math.max(0.0, Math.min(features[i] / 288.0, 1.0)) * 10.0;
            } else if (i == 11) {
                // angleToTrackAxis: normalizza da [-π, +π] a [0, 10]
                double normalizedAngle = (features[i] + Math.PI) / (2 * Math.PI);
                normalized[i] = Math.max(0.0, Math.min(1.0, normalizedAngle)) * 10.0;
            } else if (i == 12) {
                // trackPosition: normalizza da [-1, +1] a [0, 10]
                double normalizedPos = (features[i] + 1.0) / 2.0;
                normalized[i] = Math.max(0.0, Math.min(1.0, normalizedPos)) * 10.0;
            } else if (i == 13) {
                // distanceFromStartLine: normalizza da [0, 5784.10] a [0, 10]
                normalized[i] = Math.max(0.0, Math.min(features[i] / 5784.10, 1.0)) * 10.0;
            } else {
                // Altri sensori (se presenti): mantieni valore originale
                normalized[i] = features[i];
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
     * Estrae le features dai sensori del veicolo.
     * Seleziona 10 sensori di pista specifici e 4 sensori di posizione.
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
     * Predice l'azione da eseguire basandosi sui vicini più prossimi.
     * Utilizza media pesata basata sulla distanza euclidea inversa.
     * 
     * @param neighbors Lista dei vicini più prossimi
     * @param sensors Modello sensoriale per informazioni aggiuntive
     * @return Azione predetta (sterzo, accelerazione, freno, marcia)
     */
    private Action predictAction(List<DataPoint> neighbors, SensorModel sensors) {
        if (neighbors.isEmpty()) {
            return getDefaultAction(sensors);
        }
        
        double weightedSteering, weightedAcceleration, weightedBrake;
        
        if (config.isUseWeightedVoting()) {
            // Media pesata basata sulla distanza euclidea inversa
            double totalWeight = 0.0;
            weightedSteering = 0.0;
            weightedAcceleration = 0.0;
            weightedBrake = 0.0;
            
            for (DataPoint neighbor : neighbors) {
                double weight = 1.0 / (neighbor.distance + 1e-10);
                
                totalWeight += weight;
                weightedSteering += neighbor.steering * weight;
                weightedAcceleration += neighbor.acceleration * weight;
                weightedBrake += neighbor.brake * weight;
            }
            
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
        
        // Debug: stampa informazioni quando lo sterzo è significativo
        if (Math.abs(action.steering) > 0.3) {
            System.out.printf("[DEBUG] Sterzo: %.3f | TrackPos: %.3f | AngleToTrack: %.3f | Vicini: %d%n", 
                action.steering, sensors.getTrackPosition(), sensors.getAngleToTrackAxis(), neighbors.size());
            
            System.out.print("[DEBUG] Sterzo vicini: ");
            for (int i = 0; i < Math.min(3, neighbors.size()); i++) {
                System.out.printf("%.3f ", neighbors.get(i).steering);
            }
            System.out.println();
        }
        
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
        if (currentGear > 0 && currentGear < 6 && rpm >= 7500)
            return currentGear + 1;
        // Logica downshift: se RPM sotto soglia (solo marce avanti)
        else if (currentGear > 1 && rpm <= 3000)
            return currentGear - 1;
        else
            return currentGear; // Mantieni marcia attuale
    }
    
    /**
     * Azione di default quando non ci sono dati o l'auto è fuori pista.
     * Implementa un comportamento semplificato simile a SimpleDriver:
     * - Accelerazione moderata (0.3) per rientrare in pista
     * - Sterzo neutro
     * - Gestione automatica delle marce
     * - Evitamento ostacoli di base
     */
    private Action getDefaultAction(SensorModel sensors) {
       //System.out.println("-------------------------------Sensori----------------------\n "+ sensors.toString() +"\n---------------------------------------------------");
        Action action = new Action();
        
        // Comportamento di base: vai dritto con accelerazione moderata
        // Accelerazione 0.3 è la stessa usata da SimpleDriver per casi fuori pista
        action.steering = 0.0;
        action.accelerate = 0.3;
        action.brake = 0.0;
        
        // Aggiungi gestione automatica delle marce anche per l'azione di default
        action.gear = getAutoGear(sensors);
        
        // Evita ostacoli (solo se i sensori sono affidabili)
        double[] trackSensors = sensors.getTrackEdgeSensors();
        if (trackSensors[9] > 0 && trackSensors[9] < 10) { // Sensore frontale valido e vicino
            action.brake = 0.8;
            action.accelerate = 0.0;
        }
        
        return action;
    }
    
    /**
     * Resetta lo stato del driver per una nuova sessione.
     */
    @Override
    public void reset() {
        if (config.isEnableLogging()) {
            System.out.println("[KNN] Driver reset");
        }
    }
    
    /**
     * Esegue la pulizia delle risorse quando il driver viene terminato.
     */
    @Override
    public void shutdown() {
        // Cleanup se necessario
    }
    
    /**
     * Restituisce la configurazione corrente del KNN.
     * 
     * @return Configurazione KNN
     */
    public KNNConfig getConfig() {
        return config;
    }
    
    /**
     * Aggiorna la configurazione del KNN e ricostruisce il modello se necessario.
     * 
     * @param newConfig Nuova configurazione
     */
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
     * @return true se il KD-tree è costruito e i dati sono caricati
     */
    public boolean isReady() {
        return kdTree != null && !trainingData.isEmpty();
    }
    

}