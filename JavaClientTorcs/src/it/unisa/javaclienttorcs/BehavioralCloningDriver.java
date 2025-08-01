package it.unisa.javaclienttorcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Driver che implementa behavioral cloning usando K-NN
 * Usa il dataset raccolto per imitare il comportamento umano
 */
public class BehavioralCloningDriver extends Controller {
    private List<DataPoint> dataset;
    private EnhancedDataCollectionManager dataManager;
    private int k = 5; // numero di vicini per K-NN
    private boolean collectingMode = false;
    private String datasetFile;
    
    public BehavioralCloningDriver() {
        this("dataset.csv");
    }
    
    public BehavioralCloningDriver(String datasetFilename) {
        this.datasetFile = datasetFilename;
        this.dataManager = new EnhancedDataCollectionManager();
        this.dataset = new ArrayList<>();
        
        // Dataset loading is now deferred until actually needed (in control method)
        // This prevents premature warnings when file doesn't exist
    }
    
    /**
     * Modalità raccolta dati: usa SimpleDriver per generare dati di training
     * @param enabled
     */
    public void setCollectingMode(boolean enabled) {
        this.collectingMode = enabled;
        if (enabled) {
            dataManager.startEnhancedCollection();
            System.out.println("[INFO] Modalità raccolta dati ATTIVA");
        } else {
            dataManager.stopCollection();
            // Ricarica dataset aggiornato
            try {
                dataset = loadDatasetFromHumanCSV();
            } catch (IOException e) {
                System.err.println("[ERRORE] Impossibile ricaricare dataset: " + e.getMessage());
            }
        }
    }
    
    /**
     * Implementazione del controllo basato su behavioral cloning
     * @param sensors
     * @return 
     */
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        
        if (collectingMode) {
            // In modalità raccolta, usa SimpleDriver per generare dati
            SimpleDriver simpleDriver = new SimpleDriver();
            Action manualAction = simpleDriver.control(sensors);
            
            // Registra la coppia osservazione-azione
            dataManager.recordData(sensors, manualAction.accelerate, manualAction);
            
            return manualAction;
        }
        
        // Load dataset on first use if not already loaded
        if (dataset.isEmpty()) {
            File datasetFileObj = new File(datasetFile);
            if (datasetFileObj.exists()) {
                try {
                    dataset = loadDatasetFromHumanCSV();
                } catch (IOException e) {
                    System.err.println("[ERRORE] Impossibile caricare dataset: " + e.getMessage());
                }
            } else {
                // Only warn when actually trying to use the dataset
                System.out.println("[WARN] Nessun dataset trovato, partenza da zero");
            }
        }
        
        if (dataset.isEmpty()) {
            // Fallback: usa SimpleDriver se non ci sono dati
            SimpleDriver fallback = new SimpleDriver();
            return fallback.control(sensors);
        }
        
        // Crea punto dati per lo stato corrente
        DataPoint current = createDataPoint(sensors, 0, 0);
        
        // Trova k vicini più simili
        List<DataPoint> neighbors = findKNearestNeighbors(current, k);
        
        if (neighbors.isEmpty()) {
            // Fallback: SimpleDriver
            SimpleDriver fallback = new SimpleDriver();
            return fallback.control(sensors);
        }
        
        // Calcola azione media dai vicini
        double avgTargetSpeed = 0;
        double avgSteer = 0;
        
        for (DataPoint neighbor : neighbors) {
            avgTargetSpeed += neighbor.targetSpeed;
            avgSteer += neighbor.steer;
        }
        
        avgTargetSpeed /= neighbors.size();
        avgSteer /= neighbors.size();
        
        // Applica azioni
        action.steering = (float) avgSteer;
        
        // Controllo velocità: accelera o frena per raggiungere target speed
        double currentSpeed = sensors.getSpeed();
        double speedDiff = avgTargetSpeed - currentSpeed;
        
        if (speedDiff > 0.1) {
            action.accelerate = (float) speedDiff; // accelera
            action.brake = 0;
        } else if (speedDiff < -0.1) {
            action.accelerate = 0;
            action.brake = (float) (-speedDiff); // frena
        } else {
            action.accelerate = 0.1f; // mantiene velocità
            action.brake = 0;
        }
        
        // Controllo cambio marcia
        action.gear = getGear(sensors);
        
        return action;
    }
    
    /**
     * Carica dataset dal file specificato (usa datasetFile)
     */
    private List<DataPoint> loadDatasetFromHumanCSV() throws IOException {
        List<DataPoint> loaded = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetFile))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Salta la prima riga (header) se contiene "track0"
                    if (firstLine && line.contains("track0")) {
                        firstLine = false;
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 24) { // 19 track + 3 sensori + 2 azioni
                        double[] track = new double[19];
                        for (int i = 0; i < 19; i++) {
                            track[i] = Double.parseDouble(parts[i]);
                        }
                        
                        double speedX = Double.parseDouble(parts[19]);
                        double angleToTrackAxis = Double.parseDouble(parts[20]);
                        double trackPosition = Double.parseDouble(parts[21]);
                        double targetSpeed = Double.parseDouble(parts[22]);
                        double steer = Double.parseDouble(parts[23]);
                        
                        loaded.add(new DataPoint(track, speedX, angleToTrackAxis, 
                                               trackPosition, targetSpeed, steer));
                    }
                    firstLine = false;
                }
            }
        }
        
        System.out.println("[INFO] Dataset caricato: " + loaded.size() + " punti");
        return loaded;
    }

    /**
     * Trova i k vicini più simili usando distanza euclidea
     */
    private List<DataPoint> findKNearestNeighbors(DataPoint query, int k) {
        // Calcola distanze per tutti i punti
        List<DataPoint> candidates = new ArrayList<>();
        
        for (DataPoint point : dataset) {
            double distance = query.distanceTo(point);
            
            // Filtra punti troppo distanti (data distribution mismatch)
            if (distance < 50.0) { // soglia empirica
                candidates.add(point);
            }
        }
        
        // Ordina per distanza
        candidates.sort((a, b) -> {
            double distA = query.distanceTo(a);
            double distB = query.distanceTo(b);
            return Double.compare(distA, distB);
        });
        
        // Restituisci i k più vicini
        int limit = Math.min(k, candidates.size());
        return candidates.subList(0, limit);
    }
    
    /**
     * Crea DataPoint dallo stato corrente
     */
    private DataPoint createDataPoint(SensorModel sensors, double targetSpeed, double steer) {
        double[] track = new double[19];
        System.arraycopy(sensors.getTrackEdgeSensors(), 0, track, 0, 19);
        
        return new DataPoint(
            track,
            sensors.getSpeed(),
            sensors.getAngleToTrackAxis(),
            sensors.getTrackPosition(),
            targetSpeed,
            steer
        );
    }
    
    /**
     * Logica cambio marcia semplificata
     */
    private int getGear(SensorModel sensors) {
        int gear = sensors.getGear();
        double rpm = sensors.getRPM();
        
        if (gear == 0) return 1; // partenza
        
        // Cambio marcia basato su RPM
        if (rpm > 19000 && gear < 6) {
            return gear + 1;
        } else if (rpm < 14000 && gear > 1) {
            return gear - 1;
        }
        
        return gear;
    }
    
    @Override
    public void reset() {
        System.out.println("[INFO] BehavioralCloningDriver resettato");
    }
    
    @Override
    public void shutdown() {
        if (dataManager.isCollecting()) {
            dataManager.stopCollection();
        }
        System.out.println("[INFO] BehavioralCloningDriver shutdown completato");
    }
    
    /**
     * Statistiche sul dataset
     */
    public void printDatasetStats() {
        System.out.println("[INFO] Dataset attuale: " + dataset.size() + " punti");
        
        if (!dataset.isEmpty()) {
            double avgSpeed = dataset.stream().mapToDouble(p -> p.targetSpeed).average().orElse(0);
            double avgSteer = dataset.stream().mapToDouble(p -> Math.abs(p.steer)).average().orElse(0);
            
            System.out.println("[INFO] Velocità target media: " + String.format("%.2f", avgSpeed));
            System.out.println("[INFO] Sterzata media (assoluta): " + String.format("%.2f", avgSteer));
        }
    }
    
    /**
     * Imposta il valore di k per K-NN
     * @param k
     */
    public void setK(int k) {
        this.k = k;
        System.out.println("[INFO] K-NN impostato a: " + k);
    }
}