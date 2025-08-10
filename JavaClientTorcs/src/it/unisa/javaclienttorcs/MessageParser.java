package it.unisa.javaclienttorcs;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Parser per i messaggi ricevuti dal server TORCS.
 * Questa classe analizza i messaggi testuali contenenti i dati sensoriali
 * e li converte in una struttura dati facilmente accessibile.
 * 
 * I messaggi TORCS hanno il formato: (nome_sensore valore1 valore2 ...)
 * dove alcuni sensori restituiscono valori singoli e altri array di valori.
 */
public class MessageParser {
    // Mappa moderna per associare nomi e valori delle letture
    private Map<String, Object> table = new HashMap<>();
    private String message;

    /**
     * Costruisce un parser per il messaggio specificato.
     * Il messaggio viene immediatamente analizzato e i dati sensoriali
     * vengono estratti e memorizzati in una mappa interna.
     * 
     * @param message Messaggio ricevuto dal server TORCS da analizzare
     */
    public MessageParser(String message) {
        // Memorizza il messaggio originale ricevuto da TORCS
        this.message = message;

        // Fase 1: Tokenizzazione iniziale
        // Il messaggio è composto da più letture racchiuse tra parentesi tonde
        StringTokenizer mt = new StringTokenizer(message, "(");
        while (mt.hasMoreElements()) {
            String reading = mt.nextToken();
            // Rimuoviamo la parentesi chiusa finale se presente
            int endOfMessage = reading.indexOf(")");
            if (endOfMessage > 0) {
                reading = reading.substring(0, endOfMessage);
            }

            // Fase 2: Parsing di ogni singola lettura
            StringTokenizer rt = new StringTokenizer(reading, " ");
            if (rt.countTokens() < 2) {
                // Lettura non valida (manca nome o valore), ignorata
            } else {
                String readingName = rt.nextToken();
                Object readingValue;

                // Fase 3: Determinazione del tipo di dato
                // Alcuni sensori restituiscono array di valori, altri valori singoli
                if (readingName.equals("opponents") || readingName.equals("track")
                        || readingName.equals("wheelSpinVel") || readingName.equals("focus")) {
                    // Fase 1: Creazione array di double per valori multipli
                // Inizializziamo l'array con la dimensione corretta basata sui token rimanenti
                readingValue = new double[rt.countTokens()];
                int position = 0;
                
                // Fase 2: Parsing sequenziale dei valori
                // Convertiamo ogni token stringa in valore numerico double
                while (rt.hasMoreElements()) {
                    String nextToken = rt.nextToken();
                    try {
                        ((double[]) readingValue)[position] = Double.parseDouble(nextToken);
                    } catch (NumberFormatException e) {
                        System.err.println("[ERRORE] MessageParser: errore parsing valore '" + nextToken + "' per sensore '" + readingName + "'");
													System.err.println("[ERRORE] Messaggio completo: " + message);
													System.err.println("[ERRORE] Impostato valore di default: 0.0");
													//
                        ((double[]) readingValue)[position] = 0.0;
                    }
                    position++;
                }
                } else {
                    // Lettura con un singolo valore: convertiamo in Double
                    String token = rt.nextToken();
                    try {
                        readingValue = Double.valueOf(token);
                    } catch (NumberFormatException e) {
                        System.err.println("[ERRORE] MessageParser: errore parsing array '" + token + "' per sensore '" + readingName + "'");
								System.err.println("[ERRORE] Messaggio completo: " + message);
								System.err.println("[ERRORE] Impostato valore di default: 0.0");

                        readingValue = 0.0;
                    }
                }

                // Fase 4: Salvataggio nella mappa
                // La mappa associa il nome del sensore al valore (singolo o array)
                table.put(readingName, readingValue);
            }
        }
    }

    /**
     * Stampa tutti i dati sensoriali estratti dal messaggio.
     * Utile per debug e verifica del parsing.
     */
    public void printAll() {
        for (Map.Entry<String, Object> entry : table.entrySet()) {
            System.out.print(entry.getKey() + ":  ");
            
			// System.out.println("[DEBUG] Sensore: " + entry.getKey() + " = " + entry.getValue());
        }
    }

    /**
     * Restituisce il valore di un sensore specifico.
     * 
     * @param key Nome del sensore di cui ottenere il valore
     * @return Valore del sensore (Double per valori singoli, double[] per array)
     *         o null se il sensore non esiste
     */
    public Object getReading(String key) {
        return table.get(key);
    }

    /**
     * Restituisce il messaggio originale ricevuto da TORCS.
     * 
     * @return Stringa del messaggio originale non processato
     */
    public String getMessage() {
        return message;
    }
}
