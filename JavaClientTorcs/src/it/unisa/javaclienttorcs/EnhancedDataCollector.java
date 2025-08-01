package it.unisa.javaclienttorcs;

import java.io.*;
import java.util.*;

/**
 * EnhancedDataCollector - Raccoglie tutti i sensori disponibili tranne quelli degli avversari
 * e tutte le azioni tranne meta
 */
public class EnhancedDataCollector {
    private List<EnhancedDataPoint> dataset;
    private PrintWriter writer;
    private boolean collecting;
    
    public EnhancedDataCollector() {
        this.dataset = new ArrayList<>();
        this.collecting = false;
    }
    
    /**
     * Avvia la raccolta dati su file specifico
     * @param filename
     * @throws java.io.IOException
     */
    public void startCollection(String filename) throws IOException {
        File file = new File(filename);
        boolean fileExists = file.exists();
        
        writer = new PrintWriter(new FileWriter(filename, true)); // append mode
        
        // Scrivi header solo se il file è nuovo
        if (!fileExists) {
            writer.println(getEnhancedHeader());
        }
        
        collecting = true;
        System.out.println("[INFO] Raccolta dati avviata su: " + filename);
    }
    
    /**
     * Registra tutti i sensori (tranne avversari) e tutte le azioni (tranne meta)
     * @param sensors
     * @param action
     * @param targetSpeed
     */
    public void recordData(SensorModel sensors, Action action, double targetSpeed) {
        if (!collecting) return;
        
        EnhancedDataPoint point = new EnhancedDataPoint(sensors, action, targetSpeed);
        dataset.add(point);
        
        // Salva su file CSV
        writer.println(point.toCSV());
        writer.flush();
    }
    
    /**
     * Genera header completo con tutti i sensori e azioni
     */
    private String getEnhancedHeader() {
        StringBuilder header = new StringBuilder();
        
        // 19 sensori track
        for (int i = 0; i < 19; i++) {
            header.append("track").append(i).append(",");
        }
        
        // Sensori base
        header.append("speedX,angleToTrackAxis,trackPosition,");
        
        // Sensori avanzati (tranne avversari)
        header.append("lateralSpeed,currentLapTime,damage,distanceFromStartLine,");
        header.append("distanceRaced,fuelLevel,lastLapTime,RPM,zSpeed,z,");
        
        // Sensori focus
        header.append("focus0,focus1,focus2,focus3,focus4,");
        
        // Sensori ruote
        header.append("wheelSpinFL,wheelSpinFR,wheelSpinRL,wheelSpinRR,");
        
        // Azioni (tranne meta)
        header.append("targetSpeed,steering,acceleration,brake,gear,clutch");
        
        return header.toString();
    }
    
    /**
     * Converte enhanced dataset in formato human_dataset.csv standard
     * @param enhancedFile
     * @param humanFile
     * @throws java.io.IOException
     */
    public void convertToHumanDataset(String enhancedFile, String humanFile) throws IOException {
        List<EnhancedDataPoint> enhancedData = loadEnhancedDataset(enhancedFile);
        
        try (PrintWriter humanWriter = new PrintWriter(new FileWriter(humanFile))) {
            // Header standard human_dataset
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 19; i++) {
                header.append("track").append(i).append(",");
            }
            header.append("speedX,angleToTrackAxis,trackPosition,targetSpeed,steer");
            humanWriter.println(header.toString());
            
            // Converti ogni punto
            for (EnhancedDataPoint point : enhancedData) {
                humanWriter.println(point.toHumanCSV());
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
    
    public void stopCollection() {
        if (writer != null) {
            writer.close();
        }
        collecting = false;
        System.out.println("[INFO] Raccolta dati terminata. Punti raccolti: " + dataset.size());
    }
    
    public boolean isCollecting() {
        return collecting;
    }
    
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
    
    /**
     * Converte in formato CSV completo
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        
        // Track sensors (19)
        for (double sensor : trackSensors) {
            sb.append(sensor).append(",");
        }
        
        // Base sensors
        sb.append(speedX).append(",")
          .append(angleToTrackAxis).append(",")
          .append(trackPosition).append(",");
        
        // Advanced sensors
        sb.append(lateralSpeed).append(",")
          .append(currentLapTime).append(",")
          .append(damage).append(",")
          .append(distanceFromStartLine).append(",")
          .append(distanceRaced).append(",")
          .append(fuelLevel).append(",")
          .append(lastLapTime).append(",")
          .append(RPM).append(",")
          .append(zSpeed).append(",")
          .append(z).append(",");
        
        // Focus sensors (5)
        for (double focus : focusSensors) {
            sb.append(focus).append(",");
        }
        
        // Wheel sensors (4)
        for (double wheel : wheelSpinVelocity) {
            sb.append(wheel).append(",");
        }
        
        // Actions
        sb.append(targetSpeed).append(",")
          .append(steering).append(",")
          .append(acceleration).append(",")
          .append(brake).append(",")
          .append(gear).append(",")
          .append(clutch);
        
        return sb.toString();
    }
    
    /**
     * Converte in formato CSV standard human_dataset
     */
    public String toHumanCSV() {
        StringBuilder sb = new StringBuilder();
        
        // Solo i 19 sensori track
        for (double sensor : trackSensors) {
            sb.append(sensor).append(",");
        }
        
        // Base sensors
        sb.append(speedX).append(",")
          .append(angleToTrackAxis).append(",")
          .append(trackPosition).append(",")
          .append(targetSpeed).append(",")
          .append(steering);
        
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