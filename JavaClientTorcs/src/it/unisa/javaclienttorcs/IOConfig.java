package it.unisa.javaclienttorcs;

/**
 * Configurazione centralizzata per ottimizzazioni I/O del progetto JavaClientTorcs
 * 
 * Questa classe contiene tutte le costanti e parametri per ottimizzare le performance
 * di lettura/scrittura file, comunicazione di rete e gestione memoria.
 * 
 * @author JavaClientTorcs Team
 * @version 2.0 - I/O Optimized
 */
public final class IOConfig {
    
    // Previeni istanziazione
    private IOConfig() {}
    
    // ========== CONFIGURAZIONE FILE I/O ==========
    
    /**
     * Dimensione buffer per lettura file (DatasetConverter, EnhancedDataCollector)
     * Valori ottimali: 8KB-64KB a seconda della dimensione file
     */
    public static final int FILE_BUFFER_SIZE_SMALL = 8192;   // 8KB per file < 1MB
    public static final int FILE_BUFFER_SIZE_MEDIUM = 32768; // 32KB per file 1-10MB
    public static final int FILE_BUFFER_SIZE_LARGE = 65536;  // 64KB per file > 10MB
    
    /**
     * Soglie per determinare dimensione buffer dinamica
     */
    public static final long FILE_SIZE_THRESHOLD_MEDIUM = 1_000_000;   // 1MB
    public static final long FILE_SIZE_THRESHOLD_LARGE = 10_000_000;    // 10MB
    
    /**
     * Dimensione batch per scrittura dati (EnhancedDataCollector)
     * Scrive ogni N record invece di uno alla volta
     */
    public static final int BATCH_WRITE_SIZE = 100;
    
    /**
     * Dimensione massima lista in memoria per evitare memory leak
     */
    public static final int MAX_IN_MEMORY_RECORDS = 10000;
    
    // ========== CONFIGURAZIONE NETWORK I/O ==========
    
    /**
     * Dimensioni buffer per comunicazione UDP (SocketHandler)
     */
    public static final int UDP_RECEIVE_BUFFER_SIZE = 2048;  // Buffer ricezione messaggi TORCS
    public static final int UDP_SOCKET_SEND_BUFFER = 8192;   // Buffer socket invio
    public static final int UDP_SOCKET_RECEIVE_BUFFER = 8192; // Buffer socket ricezione
    
    /**
     * Soglia dimensione messaggio per warning frammentazione
     * MTU Ethernet tipica (1500) - header UDP/IP (~100) = ~1400 byte sicuri
     */
    public static final int UDP_FRAGMENTATION_THRESHOLD = 1400;
    
    // ========== CONFIGURAZIONE STRINGBUILDER ==========
    
    /**
     * Capacità iniziale StringBuilder per diversi usi
     */
    public static final int STRINGBUILDER_CSV_FULL = 512;     // CSV completo EnhancedDataPoint
    public static final int STRINGBUILDER_CSV_STANDARD = 256; // CSV standard dataset
    public static final int STRINGBUILDER_BATCH = 8192;       // Buffer batch scrittura
    public static final int STRINGBUILDER_HEADER = 1024;      // Header CSV
    
    // ========== CONFIGURAZIONE PERFORMANCE ==========
    
    /**
     * Abilita/disabilita statistiche performance
     * Impatto minimo sulle performance, utile per debugging
     */
    public static final boolean ENABLE_PERFORMANCE_STATS = true;
    
    /**
     * Intervallo di flush forzato per writer (millisecondi)
     * 0 = solo flush manuale, >0 = flush periodico
     */
    public static final int FLUSH_INTERVAL_MS = 0; // Disabilitato per default
    
    /**
     * Abilita compressione dati per file grandi (futuro)
     */
    public static final boolean ENABLE_COMPRESSION = false;
    
    // ========== METODI UTILITY ==========
    
    /**
     * Calcola dimensione buffer ottimale basata sulla dimensione del file
     * 
     * @param fileSize dimensione file in byte
     * @return dimensione buffer ottimale
     */
    public static int getOptimalBufferSize(long fileSize) {
        if (fileSize > FILE_SIZE_THRESHOLD_LARGE) {
            return FILE_BUFFER_SIZE_LARGE;
        } else if (fileSize > FILE_SIZE_THRESHOLD_MEDIUM) {
            return FILE_BUFFER_SIZE_MEDIUM;
        } else {
            return FILE_BUFFER_SIZE_SMALL;
        }
    }
    
    /**
     * Restituisce configurazione I/O corrente come stringa
     * 
     * @return stringa con configurazione attuale
     */
    public static String getConfigSummary() {
        StringBuilder sb = new StringBuilder(512);
        sb.append("=== JavaClientTorcs I/O Configuration ===").append("\n");
        sb.append("File Buffers: ").append(FILE_BUFFER_SIZE_SMALL).append("/")
          .append(FILE_BUFFER_SIZE_MEDIUM).append("/").append(FILE_BUFFER_SIZE_LARGE).append(" byte\n");
        sb.append("Batch Size: ").append(BATCH_WRITE_SIZE).append(" records\n");
        sb.append("UDP Buffers: ").append(UDP_SOCKET_SEND_BUFFER).append("/")
          .append(UDP_SOCKET_RECEIVE_BUFFER).append(" byte\n");
        sb.append("Performance Stats: ").append(ENABLE_PERFORMANCE_STATS ? "Enabled" : "Disabled").append("\n");
        sb.append("Max Memory Records: ").append(MAX_IN_MEMORY_RECORDS).append("\n");
        return sb.toString();
    }
    
    /**
     * Valida la configurazione corrente
     * 
     * @return true se la configurazione è valida
     */
    public static boolean validateConfig() {
        // Controlli di sanità
        if (BATCH_WRITE_SIZE <= 0 || BATCH_WRITE_SIZE > 1000) {
            System.err.println("[ERROR] IOConfig: BATCH_WRITE_SIZE non valido: " + BATCH_WRITE_SIZE);
            return false;
        }
        
        if (MAX_IN_MEMORY_RECORDS <= 0 || MAX_IN_MEMORY_RECORDS > 100000) {
            System.err.println("[ERROR] IOConfig: MAX_IN_MEMORY_RECORDS non valido: " + MAX_IN_MEMORY_RECORDS);
            return false;
        }
        
        if (UDP_RECEIVE_BUFFER_SIZE <= 0 || UDP_RECEIVE_BUFFER_SIZE > 65536) {
            System.err.println("[ERROR] IOConfig: UDP_RECEIVE_BUFFER_SIZE non valido: " + UDP_RECEIVE_BUFFER_SIZE);
            return false;
        }
        
        return true;
    }
}