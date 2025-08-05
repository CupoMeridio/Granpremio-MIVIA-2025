package it.unisa.javaclienttorcs;

import java.util.*;

/**
 * Implementazione di un KD-Tree per la ricerca efficiente dei K vicini più prossimi.
 * Supporta solo distanza Euclidea.
 */
public class KDTree {
    
    private KDNode root;
    private int dimensions;
    
    /**
     * Nodo del KD-Tree
     */
    private static class KDNode {
        DataPoint dataPoint;
        KDNode left;
        KDNode right;
        int splitDimension;
        
        KDNode(DataPoint dataPoint, int splitDimension) {
            this.dataPoint = dataPoint;
            this.splitDimension = splitDimension;
        }
    }
    
    /**
     * Costruttore del KD-Tree
     * @param dataPoints Lista dei punti dati
     */
    public KDTree(List<DataPoint> dataPoints) {
        if (!dataPoints.isEmpty()) {
            this.dimensions = dataPoints.get(0).features.length;
            this.root = buildTree(new ArrayList<>(dataPoints), 0);
        }
    }

    
    /**
     * Costruisce ricorsivamente il KD-Tree
     * @param points Lista dei punti da inserire
     * @param depth Profondità corrente (determina la dimensione di split)
     * @return Il nodo radice del sottoalbero
     */
    private KDNode buildTree(List<DataPoint> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }
        
        if (points.size() == 1) {
            return new KDNode(points.get(0), depth % dimensions);
        }
        
        // Determina la dimensione su cui fare lo split
        int splitDim = depth % dimensions;
        
        // Ordina i punti sulla dimensione di split
        points.sort((a, b) -> Double.compare(a.features[splitDim], b.features[splitDim]));
        
        // Trova il punto mediano
        int medianIndex = points.size() / 2;
        DataPoint medianPoint = points.get(medianIndex);
        
        // Crea il nodo
        KDNode node = new KDNode(medianPoint, splitDim);
        
        // Costruisci ricorsivamente i sottoalberi
        node.left = buildTree(points.subList(0, medianIndex), depth + 1);
        node.right = buildTree(points.subList(medianIndex + 1, points.size()), depth + 1);
        
        return node;
    }
    
    /**
     * Trova i K vicini più prossimi al punto target
     * @param targetFeatures Le features del punto target
     * @param k Numero di vicini da trovare
     * @return Lista dei K vicini più prossimi ordinati per distanza
     */
    public List<DataPoint> findKNearestNeighbors(double[] targetFeatures, int k) {
        if (root == null || k <= 0) {
            return new ArrayList<>();
        }
        
        // Usa una PriorityQueue per mantenere i K migliori vicini
        // Ordina per distanza decrescente (il più lontano in cima)
        PriorityQueue<DataPoint> bestNeighbors = new PriorityQueue<>(
            (a, b) -> Double.compare(b.distance, a.distance)
        );
        
        searchKNN(root, targetFeatures, k, bestNeighbors);
        
        // Converti in lista e ordina per distanza crescente
        List<DataPoint> result = new ArrayList<>(bestNeighbors);
        result.sort((a, b) -> Double.compare(a.distance, b.distance));
        
        return result;
    }
    
    /**
     * Ricerca ricorsiva dei K vicini più prossimi
     * @param node Nodo corrente
     * @param target Features del punto target
     * @param k Numero di vicini da trovare
     * @param bestNeighbors Coda di priorità dei migliori vicini trovati finora
     */
    private void searchKNN(KDNode node, double[] target, int k, PriorityQueue<DataPoint> bestNeighbors) {
        if (node == null) {
            return;
        }
        
        // Calcola la distanza dal nodo corrente al target
        double distance = calculateDistance(node.dataPoint.features, target);
        
        // Crea una copia del punto con la distanza calcolata
        DataPoint candidate = new DataPoint(node.dataPoint);
        candidate.setDistance(distance);
        
        // Aggiungi il candidato se abbiamo spazio o se è migliore del peggiore
        if (bestNeighbors.size() < k) {
            bestNeighbors.offer(candidate);
        } else if (distance < bestNeighbors.peek().distance) {
            bestNeighbors.poll(); // Rimuovi il peggiore
            bestNeighbors.offer(candidate);
        }
        
        // Determina quale sottoalbero esplorare per primo
        int splitDim = node.splitDimension;
        double splitValue = node.dataPoint.features[splitDim];
        double targetValue = target[splitDim];
        
        KDNode firstChild, secondChild;
        if (targetValue <= splitValue) {
            firstChild = node.left;
            secondChild = node.right;
        } else {
            firstChild = node.right;
            secondChild = node.left;
        }
        
        // Esplora il sottoalbero più promettente
        searchKNN(firstChild, target, k, bestNeighbors);
        
        // Controlla se vale la pena esplorare l'altro sottoalbero
        double distanceToSplitPlane = Math.abs(targetValue - splitValue);
        
        if (bestNeighbors.size() < k || distanceToSplitPlane < bestNeighbors.peek().distance) {
            searchKNN(secondChild, target, k, bestNeighbors);
        }
    }
    
    /**
     * Calcola la distanza tra due punti usando la distanza euclidea
     * @param features1 Features del primo punto
     * @param features2 Features del secondo punto
     * @return La distanza euclidea calcolata
     */
    private double calculateDistance(double[] features1, double[] features2) {
        return euclideanDistance(features1, features2);
    }
    
    /**
     * Calcola la distanza Euclidea tra due punti
     */
    private double euclideanDistance(double[] features1, double[] features2) {
        double sum = 0.0;
        for (int i = 0; i < features1.length; i++) {
            double diff = features1[i] - features2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    

    
    /**
     * Trova il vicino più prossimo (caso speciale di K=1)
     * @param targetFeatures Features del punto target
     * @return Il vicino più prossimo o null se l'albero è vuoto
     */
    public DataPoint findNearestNeighbor(double[] targetFeatures) {
        List<DataPoint> neighbors = findKNearestNeighbors(targetFeatures, 1);
        return neighbors.isEmpty() ? null : neighbors.get(0);
    }
    
    /**
     * Restituisce il numero di nodi nell'albero
     * @return Il numero di nodi
     */
    public int size() {
        return countNodes(root);
    }
    
    /**
     * Conta ricorsivamente i nodi nell'albero
     */
    private int countNodes(KDNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    
    

}