package it.unisa.javaclienttorcs;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller per guida manuale con tastiera.
 * Permette di guidare la macchina usando i tasti WASD per movimento e raccolta dati.
 * Implementazione con input real-time senza richiesta di invio.
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
    
    // Gestione tasti premuti in real-time
    private ConcurrentHashMap<Integer, Boolean> keysPressed = new ConcurrentHashMap<>();
    private KeyListenerFrame keyFrame;
    private AtomicBoolean running = new AtomicBoolean(true);
    
    public HumanController() {
        this.collector = new DataCollector("human_dataset.csv");
        initializeKeyListener();
        
        // Messaggio di avvio
        System.out.println("=== CONTROLLER UMANO ATTIVO ===");
        System.out.println("Controlli real-time attivati!");
        System.out.println("  W/S: Accelerazione/Frenata");
        System.out.println("  A/D: Sterzo Sinistra/Destra");
        System.out.println("  Q/E: Cambio marcia giù/sù");
        System.out.println("  C: Toggle raccolta dati ON/OFF");
        System.out.println("  R: Reset macchina");
        System.out.println("  P: Statistiche raccolta");
        System.out.println("  X: Esci");
        System.out.println("================================");
    }
    
    /**
     * Classe interna per gestire la finestra di ascolto tasti
     */
    private class KeyListenerFrame extends JFrame implements KeyListener {
        public KeyListenerFrame() {
            setTitle("TORCS Controller - Premi un tasto per guidare");
            setSize(400, 200);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setAlwaysOnTop(true);
            setLocationRelativeTo(null);
            
            // Aggiungi label con istruzioni
            JLabel label = new JLabel("<html><center>" +
                "<h3>CONTROLLER ATTIVO</h3>" +
                "<b>W/S/↑↓/8/2/I/K:</b> Accelerazione/Frenata<br>" +
                "<b>A/D/←→/4/6/J/L:</b> Sterzo Sinistra/Destra<br>" +
                "<b>Q/E:</b> Cambio marcia | " +
                "<b>C:</b> Raccolta dati ON/OFF<br>" +
                "<b>R:</b> Reset | <b>X:</b> Esci" +
                "</center></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            add(label);
            
            addKeyListener(this);
            setFocusable(true);
            requestFocusInWindow();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            keysPressed.put(keyCode, true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            keysPressed.put(keyCode, false);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Non usato
        }
    }
    
    private void initializeKeyListener() {
        SwingUtilities.invokeLater(() -> {
            keyFrame = new KeyListenerFrame();
            keyFrame.setVisible(true);
        });
    }
    
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        
        // Aggiorna i controlli in base ai tasti premuti
        updateControls();
        
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
    
    private void updateControls() {
        // Accelerazione/Frenata con W/S/Frecce/NumPad/IKJL
        if (isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_UP) || 
            isKeyPressed(KeyEvent.VK_NUMPAD8) || isKeyPressed(KeyEvent.VK_I)) {
            accelerate = Math.min(1.0, accelerate + 0.02);
            brake = 0.0;
            targetSpeed = Math.min(200, targetSpeed + 2);
        } else if (isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_DOWN) || 
                   isKeyPressed(KeyEvent.VK_NUMPAD2) || isKeyPressed(KeyEvent.VK_K)) {
            brake = Math.min(1.0, brake + 0.02);
            accelerate = Math.max(0.0, accelerate - 0.02);
            targetSpeed = Math.max(0, targetSpeed - 2);
        } else {
            // Rilascio graduale
            accelerate *= 0.98;
            brake *= 0.98;
        }
        
        // Sterzo con A/D/Frecce/NumPad/IKJL
        if (isKeyPressed(KeyEvent.VK_A) || isKeyPressed(KeyEvent.VK_LEFT) || 
            isKeyPressed(KeyEvent.VK_NUMPAD4) || isKeyPressed(KeyEvent.VK_J)) {
            steering = Math.min(1.0, steering + 0.05);
        } else if (isKeyPressed(KeyEvent.VK_D) || isKeyPressed(KeyEvent.VK_RIGHT) || 
                   isKeyPressed(KeyEvent.VK_NUMPAD6) || isKeyPressed(KeyEvent.VK_L)) {
            steering = Math.max(-1.0, steering - 0.05);
        } else {
            // Ritorno al centro
            steering *= 0.95;
        }
        
        // Cambio marcia con Q/E
        if (isKeyPressed(KeyEvent.VK_Q) && canTriggerAction(KeyEvent.VK_Q)) {
            gear = Math.max(-1, gear - 1);
            System.out.println("Marcia: " + gear);
        }
        if (isKeyPressed(KeyEvent.VK_E) && canTriggerAction(KeyEvent.VK_E)) {
            gear = Math.min(6, gear + 1);
            System.out.println("Marcia: " + gear);
        }
        
        // Toggle raccolta dati con C
        if (isKeyPressed(KeyEvent.VK_C) && canTriggerAction(KeyEvent.VK_C)) {
            collectingData = !collectingData;
            System.out.println("Raccolta dati: " + (collectingData ? "ON" : "OFF"));
            if (collectingData) {
                System.out.println("Inizio raccolta dati...");
            } else {
                System.out.println("Fine raccolta dati. Salvati in: " + collector.getFilename());
                collector.printDatasetStats();
            }
        }
        
        // Reset con R
        if (isKeyPressed(KeyEvent.VK_R) && canTriggerAction(KeyEvent.VK_R)) {
            System.out.println("Reset controlli");
            accelerate = 0.0;
            brake = 0.0;
            steering = 0.0;
            targetSpeed = 0.0;
            gear = 1;
        }
        
        // Esci con X
        if (isKeyPressed(KeyEvent.VK_X) && canTriggerAction(KeyEvent.VK_X)) {
            System.out.println("Chiusura controller...");
            running.set(false);
            System.exit(0);
        }
        
        // Statistiche con P
        if (isKeyPressed(KeyEvent.VK_P) && canTriggerAction(KeyEvent.VK_P)) {
            collector.printDatasetStats();
        }
    }
    

    
    private boolean isKeyPressed(int keyCode) {
        return keysPressed.getOrDefault(keyCode, false);
    }
    
    // Gestione toggle dei tasti
    private static final long KEY_COOLDOWN = 200; // millisecondi
    private ConcurrentHashMap<Integer, Long> lastPressedTimes = new ConcurrentHashMap<>();
    
    private boolean canTriggerAction(int keyCode) {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastPressedTimes.getOrDefault(keyCode, 0L);
        if (currentTime - lastTime > KEY_COOLDOWN) {
            lastPressedTimes.put(keyCode, currentTime);
            return true;
        }
        return false;
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
        if (keyFrame != null) {
            keyFrame.dispose();
        }
        if (collector != null) {
            collector.close();
        }
    }
    
    public void printDatasetStats() {
        collector.printDatasetStats();
    }
}