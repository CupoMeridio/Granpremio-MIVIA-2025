package it.unisa.javaclienttorcs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Test per il classificatore KNN.
 * Verifica il funzionamento del sistema di classificazione con dati sintetici.
 */
public class KNNClassifierTest {
    
    /**
     * Test principale del classificatore KNN.
     */
    public static void main(String[] args) {
        System.out.println("[TEST] Inizio test del classificatore KNN");
        
        try {
            // Test 1: Verifica enum DrivingAction
            testDrivingActionEnum();
            
            // Test 2: Verifica DataPointClassifier
            testDataPointClassifier();
            
            // Test 3: Verifica KDTreeClassifier
            testKDTreeClassifier();
            
            // Test 4: Test integrazione completa
            testFullIntegration();
            
            System.out.println("\n[TEST] Tutti i test completati con successo!");
            
        } catch (Exception e) {
            System.err.println("[TEST] Errore durante i test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test dell'enum DrivingAction e conversioni.
     */
    private static void testDrivingActionEnum() {
        System.out.println("\n[TEST] Test 1: DrivingAction enum");
        
        // Test conversioni da azioni continue
        testActionConversion(0.0, 0.8, 0.0, "STRAIGHT_ACCELERATE");
        testActionConversion(-0.5, 0.3, 0.0, "LEFT_LIGHT_ACCELERATE");
        testActionConversion(0.5, 0.3, 0.0, "RIGHT_LIGHT_ACCELERATE");
        testActionConversion(0.0, 0.0, 0.8, "EMERGENCY_BRAKE");
        testActionConversion(0.0, 0.2, 0.0, "STRAIGHT_CRUISE");
        
        // Test conversione da classe a Action
        DrivingAction action = DrivingAction.LEFT_SHARP_ACCELERATE;
        Action torcsAction = action.toAction();
        System.out.printf("[TEST] %s -> steering=%.2f, accel=%.2f, brake=%.2f%n", 
            action.getDescription(), torcsAction.steering, torcsAction.accelerate, torcsAction.brake);
        
        System.out.println("[TEST] Test 1 completato");
    }
    
    /**
     * Test helper per conversioni di azioni.
     */
    private static void testActionConversion(double steering, double accel, double brake, String expectedClass) {
        DrivingAction result = DrivingAction.fromContinuous(steering, accel, brake);
        System.out.printf("[TEST] (%.2f, %.2f, %.2f) -> %s (atteso: %s) %s%n", 
            steering, accel, brake, result.name(), expectedClass, 
            result.name().equals(expectedClass) ? "✓" : "✗");
    }
    
    /**
     * Test della classe DataPointClassifier.
     */
    private static void testDataPointClassifier() {
        System.out.println("\n[TEST] Test 2: DataPointClassifier");
        
        double[] features = {10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0, 60.0, 0.5, 0.0, 100.0};
        DrivingAction actionClass = DrivingAction.STRAIGHT_ACCELERATE;
        
        DataPointClassifier point = new DataPointClassifier(features, actionClass);
        
        System.out.printf("[TEST] Features length: %d%n", point.features.length);
        System.out.printf("[TEST] Action class: %s%n", point.getActionClass().getDescription());
        System.out.printf("[TEST] Distance: %.3f%n", point.distance);
        
        // Test setDistance
        point.setDistance(5.5);
        System.out.printf("[TEST] Distance dopo set: %.3f%n", point.distance);
        
        System.out.println("[TEST] Test 2 completato");
    }
    
    /**
     * Test del KDTreeClassifier con dati sintetici.
     */
    private static void testKDTreeClassifier() {
        System.out.println("\n[TEST] Test 3: KDTreeClassifier");
        
        // Crea dataset sintetico
        List<DataPointClassifier> trainingData = createSyntheticDataset();
        
        // Costruisci KD-Tree
        KDTreeClassifier kdTree = new KDTreeClassifier(14);
        kdTree.build(trainingData);
        
        System.out.println("[TEST] " + kdTree.getDebugInfo());
        
        // Test ricerca vicini
        double[] queryFeatures = {15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0, 60.0, 65.0, 0.3, 0.1, 200.0};
        List<DataPointClassifier> neighbors = kdTree.findKNearestNeighbors(queryFeatures, 3);
        
        System.out.printf("[TEST] Trovati %d vicini per la query%n", neighbors.size());
        for (int i = 0; i < neighbors.size(); i++) {
            DataPointClassifier neighbor = neighbors.get(i);
            System.out.printf("[TEST] Vicino %d: %s (distanza: %.3f)%n", 
                i+1, neighbor.getActionClass().getDescription(), neighbor.distance);
        }
        
        System.out.println("[TEST] Test 3 completato");
    }
    
    /**
     * Test di integrazione completa con simulazione di predizione.
     */
    private static void testFullIntegration() {
        System.out.println("\n[TEST] Test 4: Integrazione completa");
        
        // Crea dataset più grande
        List<DataPointClassifier> trainingData = createLargerSyntheticDataset();
        
        // Costruisci KD-Tree
        KDTreeClassifier kdTree = new KDTreeClassifier(14);
        kdTree.build(trainingData);
        
        System.out.println("[TEST] " + kdTree.getDebugInfo());
        
        // Simula predizioni multiple
        Map<DrivingAction, Integer> predictionCounts = new HashMap<>();
        for (DrivingAction action : DrivingAction.values()) {
            predictionCounts.put(action, 0);
        }
        
        int numTests = 20;
        for (int i = 0; i < numTests; i++) {
            // Genera features casuali
            double[] features = generateRandomFeatures();
            
            // Trova vicini
            List<DataPointClassifier> neighbors = kdTree.findKNearestNeighbors(features, 5);
            
            // Simula predizione (voto di maggioranza)
            DrivingAction prediction = predictActionClass(neighbors);
            predictionCounts.put(prediction, predictionCounts.get(prediction) + 1);
            
            if (i < 5) { // Mostra solo i primi 5 test
                System.out.printf("[TEST] Test %d: Predizione = %s (da %d vicini)%n", 
                    i+1, prediction.getDescription(), neighbors.size());
            }
        }
        
        // Mostra distribuzione predizioni
        System.out.printf("\n[TEST] Distribuzione predizioni su %d test:%n", numTests);
        for (Map.Entry<DrivingAction, Integer> entry : predictionCounts.entrySet()) {
            if (entry.getValue() > 0) {
                double percentage = (entry.getValue() * 100.0) / numTests;
                System.out.printf("[TEST] - %s: %d (%.1f%%)%n", 
                    entry.getKey().getDescription(), entry.getValue(), percentage);
            }
        }
        
        System.out.println("[TEST] Test 4 completato");
    }
    
    /**
     * Crea un dataset sintetico per i test.
     */
    private static List<DataPointClassifier> createSyntheticDataset() {
        List<DataPointClassifier> data = new ArrayList<>();
        
        // Scenario 1: Rettilineo - accelerazione
        data.add(new DataPointClassifier(
            new double[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 80, 0.0, 0.0, 100}, 
            DrivingAction.STRAIGHT_ACCELERATE));
        
        // Scenario 2: Curva a sinistra
        data.add(new DataPointClassifier(
            new double[]{30, 25, 20, 15, 10, 15, 20, 25, 30, 35, 60, -0.3, -0.5, 200}, 
            DrivingAction.LEFT_LIGHT_ACCELERATE));
        
        // Scenario 3: Curva a destra
        data.add(new DataPointClassifier(
            new double[]{35, 30, 25, 20, 15, 10, 15, 20, 25, 30, 60, 0.3, 0.5, 300}, 
            DrivingAction.RIGHT_LIGHT_ACCELERATE));
        
        // Scenario 4: Ostacolo vicino - frenata
        data.add(new DataPointClassifier(
            new double[]{5, 8, 10, 15, 20, 25, 30, 35, 40, 45, 90, 0.0, 0.0, 400}, 
            DrivingAction.EMERGENCY_BRAKE));
        
        // Scenario 5: Crociera
        data.add(new DataPointClassifier(
            new double[]{40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 50, 0.0, 0.0, 500}, 
            DrivingAction.STRAIGHT_CRUISE));
        
        return data;
    }
    
    /**
     * Crea un dataset sintetico più grande per test di integrazione.
     */
    private static List<DataPointClassifier> createLargerSyntheticDataset() {
        List<DataPointClassifier> data = new ArrayList<>();
        
        // Aggiungi variazioni per ogni tipo di azione
        for (int i = 0; i < 10; i++) {
            // Rettilineo accelerazione
            data.add(new DataPointClassifier(
                new double[]{50+i, 50+i, 50+i, 50+i, 50+i, 50+i, 50+i, 50+i, 50+i, 50+i, 80+i*2, 0.0, 0.0, 100+i*10}, 
                DrivingAction.STRAIGHT_ACCELERATE));
            
            // Curve a sinistra
            data.add(new DataPointClassifier(
                new double[]{30-i, 25-i, 20-i, 15-i, 10+i, 15+i, 20+i, 25+i, 30+i, 35+i, 60+i, -0.3-i*0.05, -0.5, 200+i*20}, 
                DrivingAction.LEFT_LIGHT_ACCELERATE));
            
            // Curve a destra
            data.add(new DataPointClassifier(
                new double[]{35+i, 30+i, 25+i, 20+i, 15+i, 10-i, 15-i, 20-i, 25-i, 30-i, 60+i, 0.3+i*0.05, 0.5, 300+i*20}, 
                DrivingAction.RIGHT_LIGHT_ACCELERATE));
            
            // Frenate
            data.add(new DataPointClassifier(
                new double[]{5+i, 8+i, 10+i, 15+i, 20+i, 25+i, 30+i, 35+i, 40+i, 45+i, 90-i*2, 0.0, 0.0, 400+i*15}, 
                DrivingAction.EMERGENCY_BRAKE));
        }
        
        return data;
    }
    
    /**
     * Genera features casuali per test.
     */
    private static double[] generateRandomFeatures() {
        double[] features = new double[14];
        
        // Track sensors (0-9): valori tra 5 e 100
        for (int i = 0; i < 10; i++) {
            features[i] = 5 + Math.random() * 95;
        }
        
        // Speed: 0-120
        features[10] = Math.random() * 120;
        
        // Angle: -π a +π
        features[11] = (Math.random() - 0.5) * 2 * Math.PI;
        
        // Track position: -1 a +1
        features[12] = (Math.random() - 0.5) * 2;
        
        // Distance: 0-1000
        features[13] = Math.random() * 1000;
        
        return features;
    }
    
    /**
     * Simula predizione di classe basata sui vicini (voto di maggioranza).
     */
    private static DrivingAction predictActionClass(List<DataPointClassifier> neighbors) {
        if (neighbors.isEmpty()) {
            return DrivingAction.STRAIGHT_CRUISE;
        }
        
        Map<DrivingAction, Double> votes = new HashMap<>();
        for (DrivingAction action : DrivingAction.values()) {
            votes.put(action, 0.0);
        }
        
        // Voto pesato per distanza
        for (DataPointClassifier neighbor : neighbors) {
            double weight = 1.0 / (neighbor.distance + 1e-10);
            DrivingAction actionClass = neighbor.getActionClass();
            votes.put(actionClass, votes.get(actionClass) + weight);
        }
        
        // Trova classe con voto massimo
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
}