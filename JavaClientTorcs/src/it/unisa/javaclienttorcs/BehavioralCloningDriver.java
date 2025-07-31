package it.unisa.javaclienttorcs;

import java.io.IOException;
import java.util.*;

/**
 * Driver che implementa behavioral cloning usando K-NN
 * Usa il dataset raccolto per imitare il comportamento umano
 */
public class BehavioralCloningDriver extends Controller {
    private List<DataPoint> dataset;
    private DataCollector collector;
    private int k = 5; // numero di vicini per K-NN
    private boolean collectingMode = false;
    private String datasetFile = "dataset.csv";
    
    public BehavioralCloningDriver() {
        this("dataset.csv");
    }
    
    public BehavioralCloningDriver(String datasetFilename) {
        this.datasetFile = datasetFilename;
        this.collector = new DataCollector();
        this.dataset = new ArrayList<>();
        
        // Carica dataset esistente se disponibile
        try {
            dataset = collector.loadDataset(datasetFile);
            System.out.println("[INFO] BehavioralCloningDriver inizializzato con " + dataset.size() + " esempi");
        } catch (IOException e) {
            System.out.println("[WARN] Nessun dataset trovato, partenza da zero");
        }
    }
    
    /**
     * Modalità raccolta dati: usa SimpleDriver per generare dati di training
     */
    public void setCollectingMode(boolean enabled) {
        this.collectingMode = enabled;
        if (enabled) {
            try {
                collector.startCollection(datasetFile);
                System.out.println("[INFO] Modalità raccolta dati ATTIVA");
            } catch (IOException e) {
                System.err.println("[ERRORE] Impossibile avviare raccolta dati: " + e.getMessage());
            }
        } else {
            collector.stopCollection();
            // Ricarica dataset aggiornato
            try {
                dataset = collector.loadDataset(datasetFile);
            } catch (IOException e) {
                System.err.println("[ERRORE] Impossibile ricaricare dataset: " + e.getMessage());
            }
        }
    }
    
    /**
     * Implementazione del controllo basato su behavioral cloning
     */
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        
        if (collectingMode) {
            // In modalità raccolta, usa SimpleDriver per generare dati
            SimpleDriver simpleDriver = new SimpleDriver();
            Action manualAction = simpleDriver.control(sensors);
            
            // Registra la coppia osservazione-azione
            collector.recordData(sensors, manualAction.accelerate, manualAction.steering);
            
            return manualAction;
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
        for (int i = 0; i < 19; i++) {
            track[i] = sensors.getTrackEdgeSensors()[i];
        }
        
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
        if (rpm > 7000 && gear < 6) {
            return gear + 1;
        } else if (rpm < 3000 && gear > 1) {
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
        if (collector.isCollecting()) {
            collector.stopCollection();
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
     */
    public void setK(int k) {
        this.k = k;
        System.out.println("[INFO] K-NN impostato a: " + k);
    }
}