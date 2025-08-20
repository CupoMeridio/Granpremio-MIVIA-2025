package it.unisa.javaclienttorcs;

import java.util.*;

/**
 * Implementazione di un KD-Tree ottimizzato per il classificatore KNN.
 * Gestisce DataPointClassifier con classi discrete di azioni.
 */
public class KDTreeClassifier {
    
    /**
     * Nodo interno del KD-Tree per il classificatore.
     */
    private static class Node {
        DataPointClassifier dataPoint;
        Node left, right;
        int dimension;
        
        Node(DataPointClassifier dataPoint, int dimension) {
            this.dataPoint = dataPoint;
            this.dimension = dimension;
        }
    }
    
    private Node root;
    private final int dimensions;
    
    /**
     * Costruttore del KD-Tree per classificatore.
     * 
     * @param dimensions Numero di dimensioni delle features
     */
    public KDTreeClassifier(int dimensions) {
        this.dimensions = dimensions;
        this.root = null;
    }
    
    /**
     * Costruisce il KD-Tree dai punti dati di training.
     * 
     * @param points Lista dei punti dati di training
     */
    public void build(List<DataPointClassifier> points) {
        if (points == null || points.isEmpty()) {
            root = null;
            return;
        }
        
        // Crea copie dei punti per evitare modifiche ai dati originali
        List<DataPointClassifier> pointsCopy = new ArrayList<>();
        for (DataPointClassifier point : points) {
            pointsCopy.add(new DataPointClassifier(point));
        }
        
        root = buildRecursive(pointsCopy, 0);
    }
    
    /**
     * Costruzione ricorsiva del KD-Tree.
     * 
     * @param points Lista dei punti da inserire
     * @param depth Profondità corrente nell'albero
     * @return Nodo radice del sottoalbero
     */
    private Node buildRecursive(List<DataPointClassifier> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }
        
        if (points.size() == 1) {
            return new Node(points.get(0), depth % dimensions);
        }
        
        // Determina la dimensione di split
        int dimension = depth % dimensions;
        
        // Ordina i punti per la dimensione corrente
        points.sort((a, b) -> Double.compare(a.features[dimension], b.features[dimension]));
        
        // Trova il punto mediano
        int medianIndex = points.size() / 2;
        DataPointClassifier medianPoint = points.get(medianIndex);
        
        // Crea il nodo
        Node node = new Node(medianPoint, dimension);
        
        // Dividi ricorsivamente
        List<DataPointClassifier> leftPoints = new ArrayList<>(points.subList(0, medianIndex));
        List<DataPointClassifier> rightPoints = new ArrayList<>(points.subList(medianIndex + 1, points.size()));
        
        node.left = buildRecursive(leftPoints, depth + 1);
        node.right = buildRecursive(rightPoints, depth + 1);
        
        return node;
    }
    
    /**
     * Trova i K vicini più prossimi per un punto di query.
     * 
     * @param target Features del punto di query
     * @param k Numero di vicini da trovare
     * @return Lista dei K vicini più prossimi ordinati per distanza
     */
    public List<DataPointClassifier> findKNearestNeighbors(double[] target, int k) {
        if (root == null || k <= 0) {
            return new ArrayList<>();
        }
        
        // Coda di priorità per mantenere i K migliori vicini
        // Ordine decrescente per distanza (il più lontano in cima)
        PriorityQueue<DataPointClassifier> bestNeighbors = new PriorityQueue<>(
            (a, b) -> Double.compare(b.distance, a.distance)
        );
        
        searchKNN(root, target, k, bestNeighbors);
        
        // Converti in lista ordinata per distanza crescente
        List<DataPointClassifier> result = new ArrayList<>(bestNeighbors);
        result.sort((a, b) -> Double.compare(a.distance, b.distance));
        
        return result;
    }
    
    /**
     * Ricerca ricorsiva dei K vicini più prossimi.
     * 
     * @param node Nodo corrente
     * @param target Punto di query
     * @param k Numero di vicini desiderati
     * @param bestNeighbors Coda di priorità dei migliori vicini
     */
    private void searchKNN(Node node, double[] target, int k, PriorityQueue<DataPointClassifier> bestNeighbors) {
        if (node == null) {
            return;
        }
        
        // Calcola la distanza dal nodo corrente
        double distance = calculateDistance(node.dataPoint.features, target);
        
        // Crea una copia del punto dati con la distanza calcolata
        DataPointClassifier candidate = new DataPointClassifier(node.dataPoint);
        candidate.setDistance(distance);
        
        // Aggiungi alla coda se c'è spazio o se è migliore del peggiore
        if (bestNeighbors.size() < k) {
            bestNeighbors.offer(candidate);
        } else if (distance < bestNeighbors.peek().distance) {
            bestNeighbors.poll();
            bestNeighbors.offer(candidate);
        }
        
        // Determina quale sottoalbero esplorare per primo
        int dimension = node.dimension;
        boolean goLeft = target[dimension] < node.dataPoint.features[dimension];
        
        Node firstChild = goLeft ? node.left : node.right;
        Node secondChild = goLeft ? node.right : node.left;
        
        // Esplora il sottoalbero più promettente
        searchKNN(firstChild, target, k, bestNeighbors);
        
        // Controlla se vale la pena esplorare l'altro sottoalbero
        double dimensionDistance = Math.abs(target[dimension] - node.dataPoint.features[dimension]);
        
        if (bestNeighbors.size() < k || dimensionDistance < bestNeighbors.peek().distance) {
            searchKNN(secondChild, target, k, bestNeighbors);
        }
    }
    
    /**
     * Calcola la distanza euclidea tra due punti.
     * 
     * @param point1 Primo punto
     * @param point2 Secondo punto
     * @return Distanza euclidea
     */
    private double calculateDistance(double[] point1, double[] point2) {
        return euclideanDistance(point1, point2);
    }
    
    /**
     * Calcola la distanza euclidea tra due vettori di features.
     * 
     * @param features1 Primo vettore di features
     * @param features2 Secondo vettore di features
     * @return Distanza euclidea
     */
    private double euclideanDistance(double[] features1, double[] features2) {
        if (features1.length != features2.length) {
            throw new IllegalArgumentException("I vettori devono avere la stessa lunghezza");
        }
        
        double sum = 0.0;
        for (int i = 0; i < features1.length; i++) {
            double diff = features1[i] - features2[i];
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }
    
    /**
     * Verifica se il KD-Tree è vuoto.
     * 
     * @return true se l'albero è vuoto
     */
    public boolean isEmpty() {
        return root == null;
    }
    
    /**
     * Restituisce informazioni di debug sull'albero.
     * 
     * @return Stringa con informazioni di debug
     */
    public String getDebugInfo() {
        if (root == null) {
            return "KDTreeClassifier: vuoto";
        }
        
        int[] nodeCount = {0};
        int height = calculateHeight(root, nodeCount);
        
        return String.format("KDTreeClassifier: %d nodi, altezza %d, dimensioni %d", 
                           nodeCount[0], height, dimensions);
    }
    
    /**
     * Calcola l'altezza dell'albero e conta i nodi.
     * 
     * @param node Nodo corrente
     * @param nodeCount Array per contare i nodi (passato per riferimento)
     * @return Altezza del sottoalbero
     */
    private int calculateHeight(Node node, int[] nodeCount) {
        if (node == null) {
            return 0;
        }
        
        nodeCount[0]++;
        int leftHeight = calculateHeight(node.left, nodeCount);
        int rightHeight = calculateHeight(node.right, nodeCount);
        
        return 1 + Math.max(leftHeight, rightHeight);
    }
}