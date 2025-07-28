package it.unisa.javaclienttorcs;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller per guida manuale con tastiera.
 * Permette di guidare la macchina usando i tasti WASD per movimento e raccolta dati.
 */
public class HumanController extends Controller {
    
    // Parametri di controllo
    private double targetSpeed = 0.0;
    private double steering = 0.0;
    private double accelerate = 0.0;
    private double brake = 0.0;
    private int gear = 1;
    
    // Stato per raccolta dati
    private DataCollector collector;
    private boolean collectingData = false;
    private boolean dataCollectionStarted = false;
    
    // Thread per input da tastiera
    private Thread inputThread;
    private AtomicBoolean running = new AtomicBoolean(true);
    
    public HumanController() {
        this.collector = new DataCollector("human_dataset.csv");
    }
    
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        
        // Inizia thread di input solo una volta
        if (!dataCollectionStarted) {
            startInputThread();
            dataCollectionStarted = true;
        }
        
        // Applica i controlli correnti
        action.accelerate = Math.max(0, Math.min(1, accelerate));
        action.brake = Math.max(0, Math.min(1, brake));
        action.steering = Math.max(-1, Math.min(1, steering));
        action.gear = gear;
        
        // Raccogli dati se in modalità raccolta
        if (collectingData) {
            collector.recordData(sensors, targetSpeed, steering);
        }
        
        return action;
    }
    
    private void startInputThread() {
        inputThread = new Thread(() -> {
            System.out.println("=== CONTROLLER UMANO ATTIVO ===");
            System.out.println("Comandi:");
            System.out.println("  ↑/↓/W/S/8/2: Accelerazione/Frenata");
            System.out.println("  ←/→/A/D/4/6: Sterzo Sinistra/Destra");
            System.out.println("  I/J/K/L: Alternative per frecce");
            System.out.println("  Q/E: Cambio marcia giù/sù");
            System.out.println("  R: Reset macchina (se stuck)");
            System.out.println("  C: Toggle raccolta dati (ON/OFF)");
            System.out.println("  P: Stampa statistiche raccolta");
            System.out.println("  X: Esci");
            System.out.println("================================");
            
            while (running.get()) {
                try {
                    if (System.in.available() > 0) {
                        int input = System.in.read();
                        handleAdvancedInput(input);
                    }
                    Thread.sleep(50); // Riduci CPU usage
                } catch (Exception e) {
                    System.err.println("Errore input: " + e.getMessage());
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }
    
    private void handleInput(char input) {
        input = Character.toUpperCase(input);
        
        switch (input) {
            case 'I':
            case 'W':
            case '8':
                accelerate = Math.min(1.0, accelerate + 0.2);
                brake = 0.0;
                targetSpeed = Math.min(200, targetSpeed + 10);
                System.out.println("Accelerazione (↑): " + String.format("%.1f", accelerate));
                break;
                
            case 'K':
            case 'S':
            case '2':
                brake = Math.min(1.0, brake + 0.2);
                accelerate = 0.0;
                targetSpeed = Math.max(0, targetSpeed - 10);
                System.out.println("Frenata (↓): " + String.format("%.1f", brake));
                break;
                
            case 'J':
            case 'A':
            case '4':
                steering = Math.max(-1.0, steering - 0.1);
                System.out.println("Sterzo (←): " + String.format("%.1f", steering));
                break;
                
            case 'L':
            case 'D':
            case '6':
                steering = Math.min(1.0, steering + 0.1);
                System.out.println("Sterzo (→): " + String.format("%.1f", steering));
                break;
                
            case 'Q': // Cambio marcia giù
                gear = Math.max(-1, gear - 1);
                System.out.println("Marcia: " + gear);
                break;
                
            case 'E': // Cambio marcia su
                gear = Math.min(6, gear + 1);
                System.out.println("Marcia: " + gear);
                break;
                
            case 'C': // Toggle raccolta dati
                collectingData = !collectingData;
                System.out.println("Raccolta dati: " + (collectingData ? "ON" : "OFF"));
                if (collectingData) {
                    System.out.println("Inizio raccolta dati...");
                } else {
                    System.out.println("Fine raccolta dati. Salvati in: " + collector.getFilename());
                    collector.printDatasetStats();
                }
                break;
                
            case 'R': // Reset
                System.out.println("Reset controlli");
                accelerate = 0.0;
                brake = 0.0;
                steering = 0.0;
                targetSpeed = 0.0;
                gear = 1;
                break;
                
            case 'P': // Statistiche
                collector.printDatasetStats();
                break;
                
            case 'X': // Esci
                System.out.println("Chiusura controller...");
                running.set(false);
                break;
                
            default:
                // Rilascio tasti - torna a zero gradualmente
                accelerate *= 0.95;
                brake *= 0.95;
                steering *= 0.9;
                break;
        }
    }
    
    private void handleAdvancedInput(int input) throws Exception {
        // Gestione freccette (arrow keys)
        if (input == 27) { // Escape sequence (freccette)
            if (System.in.available() >= 2) {
                int next1 = System.in.read();
                int next2 = System.in.read();
                
                if (next1 == 91) { // '[' in escape sequence
                    switch (next2) {
                        case 65: // Freccia su (↑)
                            accelerate = Math.min(1.0, accelerate + 0.2);
                            brake = 0.0;
                            targetSpeed = Math.min(200, targetSpeed + 10);
                            System.out.println("Accelerazione (↑): " + String.format("%.1f", accelerate));
                            return;
                            
                        case 66: // Freccia giù (↓)
                            brake = Math.min(1.0, brake + 0.2);
                            accelerate = 0.0;
                            targetSpeed = Math.max(0, targetSpeed - 10);
                            System.out.println("Frenata (↓): " + String.format("%.1f", brake));
                            return;
                            
                        case 67: // Freccia destra (→)
                            steering = Math.min(1.0, steering + 0.1);
                            System.out.println("Sterzo (→): " + String.format("%.1f", steering));
                            return;
                            
                        case 68: // Freccia sinistra (←)
                            steering = Math.max(-1.0, steering - 0.1);
                            System.out.println("Sterzo (←): " + String.format("%.1f", steering));
                            return;
                    }
                }
            }
        } else {
            // Gestione tasti normali
            handleInput((char) input);
        }
    }
    
    public void setCollectingMode(boolean enabled) {
        this.collectingData = enabled;
        if (enabled) {
            System.out.println("Modalità raccolta dati attivata per HumanController");
        }
    }
    
    @Override
    public void reset() {
        System.out.println("[INFO] HumanController: reset per nuova gara");
        // Resetta controlli ma non i dati raccolti
        accelerate = 0.0;
        brake = 0.0;
        steering = 0.0;
        targetSpeed = 0.0;
        gear = 1;
    }
    
    @Override
    public void shutdown() {
        System.out.println("[INFO] HumanController: chiusura controller");
        running.set(false);
        if (inputThread != null) {
            inputThread.interrupt();
        }
        if (collector != null) {
            collector.close();
        }
    }
    
    public void printDatasetStats() {
        collector.printDatasetStats();
    }
}