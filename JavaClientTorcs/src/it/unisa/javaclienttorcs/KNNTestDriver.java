package it.unisa.javaclienttorcs;

import java.io.File;

/**
 * Driver di test per il sistema KNN
 * Permette di testare facilmente diverse configurazioni del KNN
 */
public class KNNTestDriver {
    
    public static void main(String[] args) {
        // Percorsi dei dataset
        String autoDatasetPath = "auto_dataset.csv";
        String humanDatasetPath = "human_dataset.csv";
        
        // Verifica esistenza dei file
        if (!new File(autoDatasetPath).exists() && !new File(humanDatasetPath).exists()) {
            System.err.println("[ERROR] Nessun dataset trovato. Esegui prima la raccolta dati.");
            return;
        }
        
        System.out.println("=== Test del Sistema KNN ===");
        
        // Test 1: Configurazione automatica
        System.out.println("\n1. Test con dataset automatico:");
        testKNNConfiguration(KNNConfig.forAutoDataset(), "Auto-Dataset");
        
        // Test 2: Configurazione umana
        if (new File(humanDatasetPath).exists()) {
            System.out.println("\n2. Test con dataset umano:");
            testKNNConfiguration(KNNConfig.forHumanDataset(), "Human-Dataset");
        }
        
        System.out.println("\n=== Test completati ===");
    }
    
    /**
     * Testa una configurazione KNN specifica
     */
    private static void testKNNConfiguration(KNNConfig config, String testName) {
        try {
            System.out.println("\n--- Test: " + testName + " ---");
            System.out.println("Configurazione: " + config);
            
            long startTime = System.currentTimeMillis();
            
            // Crea il driver KNN
            KNNDriver knnDriver = new KNNDriver(config);
            
            long initTime = System.currentTimeMillis() - startTime;
            
            // Verifica che il driver sia pronto
            if (!knnDriver.isReady()) {
                System.err.println("[ERROR] Driver KNN non pronto!");
                return;
            }
            
            System.out.println("[SUCCESS] Driver inizializzato in " + initTime + "ms");
            System.out.println("[INFO] Punti di training caricati: " + knnDriver.getTrainingDataSize());
            
            // Test di predizione con dati di esempio
            testPrediction(knnDriver, testName);
            
        } catch (Exception e) {
            System.err.println("[ERROR] Test fallito per " + testName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Testa la predizione con dati di esempio
     */
    private static void testPrediction(KNNDriver knnDriver, String testName) {
        try {
            // Crea un sensore di esempio (simulato)
            SensorModel testSensor = createTestSensor();
            
            long startTime = System.nanoTime();
            
            // Esegui predizione
            Action predictedAction = knnDriver.control(testSensor);
            
            long predictionTime = System.nanoTime() - startTime;
            
            System.out.println("[PREDICTION] Tempo di predizione: " + (predictionTime / 1000000.0) + "ms");
            System.out.println("[PREDICTION] Azione predetta:");
            System.out.println("  - Steering: " + String.format("%.4f", predictedAction.steering));
            System.out.println("  - Acceleration: " + String.format("%.4f", predictedAction.accelerate));
            System.out.println("  - Brake: " + String.format("%.4f", predictedAction.brake));
            
        } catch (Exception e) {
            System.err.println("[ERROR] Predizione fallita per " + testName + ": " + e.getMessage());
        }
    }
    
    /**
     * Crea un sensore di test con valori realistici
     */
    private static SensorModel createTestSensor() {
        // Crea un'implementazione mock di SensorModel per i test
        return new SensorModel() {
            @Override
            public double getSpeed() { return 80.0; }
            
            @Override
            public double getAngleToTrackAxis() { return 0.1; }
            
            @Override
            public double[] getTrackEdgeSensors() {
                return new double[]{
                    100.0, 95.0, 90.0, 85.0, 80.0, 75.0, 70.0, 65.0, 60.0, 55.0,
                    50.0, 55.0, 60.0, 65.0, 70.0, 75.0, 80.0, 85.0, 90.0
                };
            }
            
            @Override
            public double[] getFocusSensors() {
                return new double[]{200.0, 180.0, 150.0, 180.0, 200.0};
            }
            
            @Override
            public double getTrackPosition() { return 0.2; }
            
            @Override
            public int getGear() { return 3; }
            
            @Override
            public double[] getOpponentSensors() { return new double[36]; }
            
            @Override
            public int getRacePosition() { return 1; }
            
            @Override
            public double getLateralSpeed() { return 0.5; }
            
            @Override
            public double getCurrentLapTime() { return 45.2; }
            
            @Override
            public double getDamage() { return 0.0; }
            
            @Override
            public double getDistanceFromStartLine() { return 1500.0; }
            
            @Override
            public double getDistanceRaced() { return 3000.0; }
            
            @Override
            public double getFuelLevel() { return 50.0; }
            
            @Override
            public double getLastLapTime() { return 44.8; }
            
            @Override
            public double getRPM() { return 6000.0; }
            
            @Override
            public double[] getWheelSpinVelocity() {
                return new double[]{15.0, 15.0, 15.0, 15.0};
            }
            
            @Override
            public double getZSpeed() { return 0.1; }
            
            @Override
            public double getZ() { return 0.35; }
            
            @Override
            public String getMessage() { return ""; }
        };
    }
    
    /**
     * Mostra le statistiche comparative tra diverse configurazioni
     */
    public static void compareConfigurations() {
        System.out.println("\n=== Confronto Configurazioni ===");
        
        String[] configNames = {"Auto-Dataset", "Human-Dataset"};
        KNNConfig[] configs = {
            KNNConfig.forAutoDataset(),
            KNNConfig.forHumanDataset()
        };
        
        System.out.printf("%-20s %-10s %-15s %-12s %-10s %-15s%n", 
                         "Configurazione", "K", "Distanza", "Normaliz.", "Pesato", "Dataset");
        System.out.println("-".repeat(85));
        
        for (int i = 0; i < configNames.length; i++) {
            KNNConfig config = configs[i];
            System.out.printf("%-20s %-10d %-15s %-12s %-10s %-15s%n",
                             configNames[i],
                             config.getK(),
                             config.getDistanceMetric().toString(),
                             config.isNormalizeData() ? "Sì" : "No",
                             config.isUseWeightedVoting() ? "Sì" : "No",
                             config.getDatasetPath());
        }
    }
    

}