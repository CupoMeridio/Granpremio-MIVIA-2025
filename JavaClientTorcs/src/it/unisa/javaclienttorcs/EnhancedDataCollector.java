package it.unisa.javaclienttorcs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * EnhancedDataCollector - Raccoglie tutti i sensori disponibili tranne quelli degli avversari
 * e tutte le azioni tranne meta
 */
public class EnhancedDataCollector {
    private List<EnhancedDataPoint> dataset;
    private PrintWriter writer;
    private BufferedWriter bufferedWriter;
    private boolean collecting;
    
    // Ottimizzazioni I/O - utilizza configurazione centralizzata
    private int recordCount = 0;
    private StringBuilder batchBuffer = new StringBuilder(IOConfig.STRINGBUILDER_BATCH);
    
    /**
     * Costruisce un nuovo raccoglitore di dati avanzato.
     * Inizializza il dataset interno e imposta lo stato di raccolta a false.
     */
    public EnhancedDataCollector() {
        this.dataset = new ArrayList<>();
        this.collecting = false;
    }
    
    /**
     * Avvia la raccolta dati su file specifico.
     * 
     * @param filename Nome del file CSV dove salvare i dati raccolti
     * @throws java.io.IOException Se si verificano errori durante la creazione del file
     */
    public void startCollection(String filename) throws IOException {
        if (collecting) {
            System.out.println("[WARNING] Raccolta dati già in corso. Fermando la raccolta precedente.");
            stopCollection();
        }
        
        File file = new File(filename);
        boolean fileExists = file.exists();
        
        this.recordCount = 0;
        this.batchBuffer.setLength(0); // Reset buffer
        
        // Ottimizzazione I/O: Buffer size dinamico da configurazione
        FileWriter fileWriter = new FileWriter(filename, true);
        int bufferSize = IOConfig.getOptimalBufferSize(file.length());
        this.bufferedWriter = new BufferedWriter(fileWriter, bufferSize);
        this.writer = new PrintWriter(bufferedWriter);
        
        // Scrivi header solo se il file è nuovo
        if (!fileExists) {
            writeHeaderOptimized();
        }
        
        collecting = true;
        System.out.println("[INFO] Raccolta dati avviata (I/O ottimizzato) su: " + filename);
    }
    
    /**
     * Registra tutti i sensori (tranne avversari) e tutte le azioni (tranne meta).
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale del veicolo
     * @param action Azione di controllo eseguita dal driver
     * @param targetSpeed Velocità target desiderata per questo momento
     */
    public void recordData(SensorModel sensors, Action action, double targetSpeed) {
        if (!collecting) return;
        
        EnhancedDataPoint point = new EnhancedDataPoint(sensors, action, targetSpeed);
        
        // Ottimizzazione memoria: gestione buffer circolare
        if (dataset.size() < IOConfig.MAX_IN_MEMORY_RECORDS) {
            dataset.add(point);
        } else {
            dataset.remove(0);
            dataset.add(point);
        }
        
        // Batch writing per performance migliori
        batchBuffer.append(point.toCSV()).append("\n");
        recordCount++;
        
        if (recordCount % IOConfig.BATCH_WRITE_SIZE == 0) {
            flushBatch();
        }
    }
    
    /**
     * Header ottimizzato per performance migliori
     */
    private void writeHeaderOptimized() {
        // Header pre-costruito per performance migliori
        String header = "track0,track1,track2,track3,track4,track5,track6,track7,track8,track9," +
                       "track10,track11,track12,track13,track14,track15,track16,track17,track18," +
                       "speedX,angleToTrackAxis,trackPosition," +
                       "lateralSpeed,currentLapTime,damage,distanceFromStartLine," +
                       "distanceRaced,fuelLevel,lastLapTime,RPM,zSpeed,z," +
                       "focus0,focus1,focus2,focus3,focus4," +
                       "wheelSpinFL,wheelSpinFR,wheelSpinRL,wheelSpinRR," +
                       "targetSpeed,steering,acceleration,brake,gear,clutch";
        
        writer.println(header);
    }
    
    /**
     * Flush del batch buffer per scrittura ottimizzata
     */
    private void flushBatch() {
        if (batchBuffer.length() > 0 && writer != null) {
            writer.print(batchBuffer.toString());
            writer.flush(); // Forza scrittura su disco
            batchBuffer.setLength(0); // Reset buffer
        }
    }
    

    
    /**
     * Converte enhanced dataset in formato standard.
     * 
     * @param enhancedFile Percorso del file enhanced dataset di input
     * @param standardFile Percorso del file standard dataset di output
     * @throws java.io.IOException Se si verificano errori durante la conversione
     */
    public void convertToStandardDataset(String enhancedFile, String standardFile) throws IOException {
        List<EnhancedDataPoint> enhancedData = loadEnhancedDataset(enhancedFile);
        
        try (PrintWriter humanWriter = new PrintWriter(new FileWriter(standardFile))) {
            // Header standard human_dataset
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 19; i++) {
                header.append("track").append(i).append(",");
            }
            header.append("speedX,angleToTrackAxis,trackPosition,targetSpeed,steering,acceleration,brake");
            humanWriter.println(header.toString());
            
            // Converti ogni punto
            for (EnhancedDataPoint point : enhancedData) {
                humanWriter.println(point.toStandardCSV());
            }
        }
        
        System.out.println("[INFO] Convertito " + enhancedData.size() + " punti in formato human_dataset.csv");
    }
    
    /**
     * Carica dataset enhanced
     */
    private List<EnhancedDataPoint> loadEnhancedDataset(String filename) throws IOException {
        List<EnhancedDataPoint> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (firstLine && line.contains("track0")) {
                        firstLine = false;
                        continue;
                    }
                    loaded.add(EnhancedDataPoint.fromCSV(line));
                    firstLine = false;
                }
            }
        }
        return loaded;
    }
    
    /**
     * Ferma la raccolta dati e chiude tutti i file aperti.
     * Esegue il flush finale del buffer e rilascia le risorse.
     */
    public void stopCollection() {
        if (!collecting) {
            return;
        }
        
        // Flush finale del batch buffer
        if (batchBuffer.length() > 0) {
            flushBatch();
        }
        
        if (writer != null) {
            try {
                writer.flush(); // Forza scrittura buffer
                writer.close();
                writer = null;
            } catch (Exception e) {
                System.err.println("[ERROR] Errore durante chiusura file: " + e.getMessage());
            }
        }
        
        if (bufferedWriter != null) {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                System.err.println("[ERROR] Errore chiusura BufferedWriter: " + e.getMessage());
            }
            bufferedWriter = null;
        }
        
        collecting = false;
        System.out.println("[INFO] Raccolta dati terminata (I/O ottimizzato). Punti raccolti: " + recordCount);
    }
    
    /**
     * Verifica se la raccolta dati è attualmente attiva.
     * 
     * @return true se la raccolta è in corso, false altrimenti
     */
    public boolean isCollecting() {
        return collecting;
    }
    
    /**
     * Chiude il raccoglitore di dati e rilascia tutte le risorse.
     * Equivalente a chiamare stopCollection().
     */
    public void close() {
        stopCollection();
    }
}

/**
 * Classe per rappresentare un punto dati completo
 */
class EnhancedDataPoint {
    // Sensori track (19)
    double[] trackSensors;
    
    // Sensori base
    double speedX;
    double angleToTrackAxis;
    double trackPosition;
    
    // Sensori avanzati
    double lateralSpeed;
    double currentLapTime;
    double damage;
    double distanceFromStartLine;
    double distanceRaced;
    double fuelLevel;
    double lastLapTime;
    double RPM;
    double zSpeed;
    double z;
    
    // Sensori focus
    double[] focusSensors;
    
    // Sensori ruote
    double[] wheelSpinVelocity;
    
    // Azioni
    double targetSpeed;
    double steering;
    double acceleration;
    double brake;
    int gear;
    double clutch;
    
    public EnhancedDataPoint(SensorModel sensors, Action action, double targetSpeed) {
        // Track sensors
        this.trackSensors = sensors.getTrackEdgeSensors().clone();
        
        // Base sensors
        this.speedX = sensors.getSpeed();
        this.angleToTrackAxis = sensors.getAngleToTrackAxis();
        this.trackPosition = sensors.getTrackPosition();
        
        // Advanced sensors (excluding opponent sensors)
        this.lateralSpeed = sensors.getLateralSpeed();
        this.currentLapTime = sensors.getCurrentLapTime();
        this.damage = sensors.getDamage();
        this.distanceFromStartLine = sensors.getDistanceFromStartLine();
        this.distanceRaced = sensors.getDistanceRaced();
        this.fuelLevel = sensors.getFuelLevel();
        this.lastLapTime = sensors.getLastLapTime();
        this.RPM = sensors.getRPM();
        this.zSpeed = sensors.getZSpeed();
        this.z = sensors.getZ();
        
        // Focus sensors
        this.focusSensors = sensors.getFocusSensors();
        
        // Wheel sensors
        this.wheelSpinVelocity = sensors.getWheelSpinVelocity();
        
        // Actions
        this.targetSpeed = targetSpeed;
        this.steering = action.steering;
        this.acceleration = action.accelerate;
        this.brake = action.brake;
        this.gear = action.gear;
        this.clutch = action.clutch;
    }
    
    // Pool di StringBuilder per ottimizzare allocazioni - utilizza IOConfig
    private static final ThreadLocal<StringBuilder> CSV_BUILDER_POOL = 
        ThreadLocal.withInitial(() -> new StringBuilder(IOConfig.STRINGBUILDER_CSV_FULL));
    
    private static final ThreadLocal<StringBuilder> STANDARD_CSV_BUILDER_POOL = 
        ThreadLocal.withInitial(() -> new StringBuilder(IOConfig.STRINGBUILDER_CSV_STANDARD));
    
    /**
     * Converte in formato CSV completo (ottimizzato)
     */
    public String toCSV() {
        StringBuilder sb = CSV_BUILDER_POOL.get();
        sb.setLength(0); // Reset del buffer riutilizzabile
        
        // Track sensors (19) - accesso diretto per performance
        sb.append(trackSensors[0]).append(',').append(trackSensors[1]).append(',').append(trackSensors[2]).append(',')
          .append(trackSensors[3]).append(',').append(trackSensors[4]).append(',').append(trackSensors[5]).append(',')
          .append(trackSensors[6]).append(',').append(trackSensors[7]).append(',').append(trackSensors[8]).append(',')
          .append(trackSensors[9]).append(',').append(trackSensors[10]).append(',').append(trackSensors[11]).append(',')
          .append(trackSensors[12]).append(',').append(trackSensors[13]).append(',').append(trackSensors[14]).append(',')
          .append(trackSensors[15]).append(',').append(trackSensors[16]).append(',').append(trackSensors[17]).append(',')
          .append(trackSensors[18]).append(',');
        
        // Base sensors - concatenazione ottimizzata
        sb.append(speedX).append(',').append(angleToTrackAxis).append(',').append(trackPosition).append(',');
        
        // Advanced sensors - concatenazione ottimizzata
        sb.append(lateralSpeed).append(',').append(currentLapTime).append(',').append(damage).append(',')
          .append(distanceFromStartLine).append(',').append(distanceRaced).append(',').append(fuelLevel).append(',')
          .append(lastLapTime).append(',').append(RPM).append(',').append(zSpeed).append(',').append(z).append(',');
        
        // Focus sensors (5) - accesso diretto
        sb.append(focusSensors[0]).append(',').append(focusSensors[1]).append(',').append(focusSensors[2]).append(',')
          .append(focusSensors[3]).append(',').append(focusSensors[4]).append(',');
        
        // Wheel sensors (4) - accesso diretto
        sb.append(wheelSpinVelocity[0]).append(',').append(wheelSpinVelocity[1]).append(',')
          .append(wheelSpinVelocity[2]).append(',').append(wheelSpinVelocity[3]).append(',');
        
        // Actions - concatenazione finale
        sb.append(targetSpeed).append(',').append(steering).append(',').append(acceleration).append(',')
          .append(brake).append(',').append(gear).append(',').append(clutch);
        
        return sb.toString();
    }
    
    /**
     * Converte in formato CSV standard ottimizzato per modello di guida autonoma
     * Sensori selezionati: track alternati, posizione/velocità essenziali, azioni principali
     */
    public String toStandardCSV() {
        StringBuilder sb = STANDARD_CSV_BUILDER_POOL.get();
        sb.setLength(0); // Reset del buffer riutilizzabile
        
        // Track sensors alternati (0,2,4,6,8,10,12,14,16,18) per copertura ottimale
        sb.append(trackSensors[0]).append(',').append(trackSensors[2]).append(',').append(trackSensors[4]).append(',')
          .append(trackSensors[6]).append(',').append(trackSensors[8]).append(',').append(trackSensors[10]).append(',')
          .append(trackSensors[12]).append(',').append(trackSensors[14]).append(',').append(trackSensors[16]).append(',')
          .append(trackSensors[18]).append(',');
        
        // Sensori di posizione e velocità essenziali
        sb.append(speedX).append(',').append(angleToTrackAxis).append(',').append(trackPosition).append(',')
          .append(distanceFromStartLine).append(',');
        
        // Azioni di controllo principali
        sb.append(steering).append(',').append(acceleration).append(',').append(brake);
        
        return sb.toString();
    }
    
    /**
     * Carica da formato CSV
     */
    public static EnhancedDataPoint fromCSV(String csv) {
        String[] parts = csv.split(",");
        EnhancedDataPoint point = new EnhancedDataPoint();
        
        int index = 0;
        
        // Track sensors (19)
        point.trackSensors = new double[19];
        for (int i = 0; i < 19; i++) {
            point.trackSensors[i] = Double.parseDouble(parts[index++]);
        }
        
        // Base sensors
        point.speedX = Double.parseDouble(parts[index++]);
        point.angleToTrackAxis = Double.parseDouble(parts[index++]);
        point.trackPosition = Double.parseDouble(parts[index++]);
        
        // Advanced sensors
        point.lateralSpeed = Double.parseDouble(parts[index++]);
        point.currentLapTime = Double.parseDouble(parts[index++]);
        point.damage = Double.parseDouble(parts[index++]);
        point.distanceFromStartLine = Double.parseDouble(parts[index++]);
        point.distanceRaced = Double.parseDouble(parts[index++]);
        point.fuelLevel = Double.parseDouble(parts[index++]);
        point.lastLapTime = Double.parseDouble(parts[index++]);
        point.RPM = Double.parseDouble(parts[index++]);
        point.zSpeed = Double.parseDouble(parts[index++]);
        point.z = Double.parseDouble(parts[index++]);
        
        // Focus sensors (5)
        point.focusSensors = new double[5];
        for (int i = 0; i < 5; i++) {
            point.focusSensors[i] = Double.parseDouble(parts[index++]);
        }
        
        // Wheel sensors (4)
        point.wheelSpinVelocity = new double[4];
        for (int i = 0; i < 4; i++) {
            point.wheelSpinVelocity[i] = Double.parseDouble(parts[index++]);
        }
        
        // Actions
        point.targetSpeed = Double.parseDouble(parts[index++]);
        point.steering = Double.parseDouble(parts[index++]);
        point.acceleration = Double.parseDouble(parts[index++]);
        point.brake = Double.parseDouble(parts[index++]);
        point.gear = Integer.parseInt(parts[index++]);
        point.clutch = Double.parseDouble(parts[index]);
        
        return point;
    }
    
    // Costruttore vuoto per fromCSV
    private EnhancedDataPoint() {}
}