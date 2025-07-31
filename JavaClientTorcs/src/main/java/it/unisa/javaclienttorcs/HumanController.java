package it.unisa.javaclienttorcs;

import javax.swing.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
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
    private boolean autoGearMode = true;
    private boolean absMode = true;
    private boolean steeringAssist = false;
    private boolean autoClutch = true;
    
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
        System.out.println("  Accelerazione/Frenata: W/S ↑↓ 8/2 I/K");
        System.out.println("  Sterzo Sinistra/Destra: A/D ←→ 4/6 J/L");
        System.out.println("  Cambio marcia manuale: Q/E (solo se manuale)");
        System.out.println("  Toggle cambio automatico: G");
        System.out.println("  Raccolta dati: C (ON/OFF)");
        System.out.println("  Reset: R");
        System.out.println("  Statistiche: P");
        System.out.println("  Esci: X");
        System.out.println("  Cambio marcia: " + (autoGearMode ? "AUTOMATICO" : "MANUALE") + " (default: AUTOMATICO)");
        System.out.println("================================");
    }
    
    /**
     * Classe interna per gestire la finestra di ascolto tasti
     */
    private class KeyListenerFrame extends JFrame implements KeyListener {
        public KeyListenerFrame() {
            setTitle("TORCS Controller - Premi un tasto per guidare");
            setSize(600, 600);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setAlwaysOnTop(true);
            setLocationRelativeTo(null);
            
            // Aggiungi label con istruzioni
            JLabel label = new JLabel("<html><div style='padding: 20px; font-size: 12px;'>" +
                "<h2 style='text-align: center; margin-bottom: 15px;'>🎮 TORCS CONTROLLER</h2>" +
                "<div style='margin-bottom: 10px;'><b>🚗 MOVIMENTO:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>W/S</b> | <b>↑↓</b> | <b>8/2</b> | <b>I/K</b> → Accelerazione/Frenata</div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>A/D</b> | <b>←→</b> | <b>4/6</b> | <b>J/L</b> → Sterzo Sinistra/Destra</div>" +
                "<div style='margin-bottom: 10px;'><b>⚙️ CONTROLLI AVANZATI:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>Q/E</b> → Cambio marcia manuale (solo modalità manuale)</div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>G</b> → Toggle cambio automatico</div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>V</b> → Toggle ABS</div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>B</b> → Toggle assistenza sterzo</div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>N</b> → Toggle frizione automatica</div>" +
                "<div style='margin-bottom: 10px;'><b>💾 DATI:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>C</b> → Raccolta dati ON/OFF</div>" +
                "<div style='margin-bottom: 10px;'><b>🔄 GENERALI:</b></div>" +
                "<div style='margin-left: 15px;'>" +
                "• <b>R</b> → Reset controlli | <b>X</b> → Esci</div>" +
                "</div></html>");
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
        
        // Applica assistenza sterzo se attiva
        double finalSteering = steering;
        if (steeringAssist) {
            finalSteering = getAssistedSteering(sensors, steering);
        }
        
        // Applica i controlli correnti
        action.accelerate = Math.max(0, Math.min(1, accelerate));
        action.brake = Math.max(0, Math.min(1, brake));
        action.steering = Math.max(-1, Math.min(1, finalSteering));
        
        // Gestione cambio marcia (automatico o manuale)
        if (autoGearMode) {
            action.gear = getGear(sensors);
            gear = action.gear; // Aggiorna lo stato interno
        } else {
            action.gear = gear;
        }
        
        // Applica ABS se attivo
        if (absMode) {
            action.brake = filterABS(sensors, action.brake);
        }
        
        // Gestione frizione automatica se attiva
        if (autoClutch) {
            action.clutch = getAutoClutch(sensors);
        } else {
            action.clutch = 0; // Nessuna frizione
        }
        
        // Raccogli dati se in modalità raccolta
        if (collectingData) {
            collector.recordData(sensors, targetSpeed, finalSteering);
        }
        
        return action;
    }
    
    /**
     * Determina la marcia ottimale in base ai giri motore (RPM) per modalità automatica.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale della macchina
     * @return Marcia raccomandata (-1 per retromarcia, 0 per folle, 1-6 per marce avanti)
     */
    private int getGear(SensorModel sensors) {
        int currentGear = sensors.getGear();
        double rpm = sensors.getRPM();
        double speed = sensors.getSpeed(); // km/h
        
        // Arcade-style reverse: switch to reverse when pressing backward at low speed
        boolean isBackwardPressed = isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_DOWN) || 
                                   isKeyPressed(KeyEvent.VK_NUMPAD2) || isKeyPressed(KeyEvent.VK_K);
        boolean isForwardPressed = isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_UP) || 
                                  isKeyPressed(KeyEvent.VK_NUMPAD8) || isKeyPressed(KeyEvent.VK_I);
        
        // Allow immediate reverse when stopped or very low speed
        if (speed < 5.0) {
            if (isBackwardPressed && currentGear >= 0) {
                return -1; // Switch to reverse
            }
        }
        
        // Allow immediate forward acceleration from reverse regardless of speed
        if (isForwardPressed && currentGear == -1) {
            return 1; // Switch back to first gear from reverse immediately
        }
        
        // If currently in reverse, don't perform automatic gear shifting
        if (currentGear == -1) {
            return -1; // Stay in reverse
        }
        
        // Gestione marcia 0 (folle) - imposta sempre 1ª marcia
        if (currentGear < 1)
            return 1;
        
        // Logica upshift: se RPM supera soglia (solo marce avanti)
        if (currentGear > 0 && currentGear < 6 && rpm >= 7000)
            return currentGear + 1;
        // Logica downshift: se RPM sotto soglia (solo marce avanti)
        else if (currentGear > 1 && rpm <= 3000)
            return currentGear - 1;
        else
            // Nessun cambio marcia necessario
            return currentGear;
    }

    /**
     * Applica il sistema ABS per prevenire il bloccaggio delle ruote durante la frenata.
     * 
     * @param sensors Modello sensoriale con dati attuali
     * @param brake Valore di frenata [0-1]
     * @return Valore di frenata con ABS applicato
     */
    private float filterABS(SensorModel sensors, double brake) {
        final float absSlip = 2.0f;
        final float absRange = 3.0f;
        final float absMinSpeed = 3.0f;
        final float[] wheelRadius = {0.3179f, 0.3179f, 0.3276f, 0.3276f};
        
        float speed = (float)(sensors.getSpeed() / 3.6);
        
        // Se la velocità è troppo bassa, ABS non è necessario
        if (speed < absMinSpeed)
            return (float)brake;

        // Calcolo slip medio su tutte e 4 le ruote
        float slip = 0.0f;
        for (int i = 0; i < 4; i++) {
            slip += sensors.getWheelSpinVelocity()[i] * wheelRadius[i];
        }
        slip = speed - slip / 4.0f;
        
        // Riduce la frenata se lo slittamento è eccessivo
        if (slip > absSlip) {
            brake = brake - (slip - absSlip) / absRange;
        }
        
        return Math.max(0.0f, Math.min(1.0f, (float)brake));
    }

    /**
     * Fornisce assistenza allo sterzo per una guida più fluida e precisa.
     * Corregge automaticamente l'angolo di sterzo in base alla posizione sulla pista.
     * 
     * @param sensors Modello sensoriale con dati attuali
     * @param manualSteering Sterzo manuale dell'utente [-1, 1]
     * @return Sterzo corretto con assistenza
     */
    private double getAssistedSteering(SensorModel sensors, double manualSteering) {
        final float steerLock = 0.785398f; // 45° in radianti
        final float steerSensitivityOffset = 80.0f;
        
        // Calcola correzione basata sulla posizione e angolo dell'auto
        float trackPos = (float)sensors.getTrackPosition();
        float angle = (float)sensors.getAngleToTrackAxis();
        
        // Correzione automatica per mantenere la macchina centrata
        float correction = angle - trackPos * 0.5f;
        
        // Adatta la correzione alla velocità
        float speed = (float)sensors.getSpeed();
        if (speed > steerSensitivityOffset) {
            correction = correction / (steerLock * (speed - steerSensitivityOffset));
        }
        
        // Combina sterzo manuale con correzione assistita
        double assistedSteering = manualSteering + correction * 0.3;
        
        return Math.max(-1.0, Math.min(1.0, assistedSteering));
    }

    /**
     * Gestisce automaticamente la frizione per partenze ottimali.
     * 
     * @param sensors Modello sensoriale con dati attuali
     * @return Valore frizione [0-1]
     */
    private float getAutoClutch(SensorModel sensors) {
        final float clutchMax = 0.5f;
        final float clutchDelta = 0.05f;
        final float clutchDeltaTime = 0.02f;
        final float clutchDeltaRaced = 10.0f;
        
        float clutch = 0.0f;
        
        // Controllo partenza gara
        if (sensors.getCurrentLapTime() < clutchDeltaTime && 
            getStage() == Stage.RACE && 
            sensors.getDistanceRaced() < clutchDeltaRaced) {
            clutch = clutchMax;
        }
        
        // Gestione frizione in base a RPM
        double rpm = sensors.getRPM();
        if (rpm < 2000) {
            clutch = Math.min(clutchMax, clutch + clutchDelta);
        } else if (rpm > 4000) {
            clutch = Math.max(0.0f, clutch - clutchDelta);
        }
        
        return Math.max(0.0f, Math.min(1.0f, clutch));
    }
    
    private void updateControls() {
        // Accelerazione/Frenata con W/S/Frecce/NumPad/IKJL
        boolean isForwardPressed = isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_UP) || 
                                  isKeyPressed(KeyEvent.VK_NUMPAD8) || isKeyPressed(KeyEvent.VK_I);
        boolean isBackwardPressed = isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_DOWN) || 
                                   isKeyPressed(KeyEvent.VK_NUMPAD2) || isKeyPressed(KeyEvent.VK_K);
        
        if (gear == -1) {
            // In reverse: backward key accelerates reverse, forward key brakes
            if (isBackwardPressed) {
                accelerate = Math.min(1.0, accelerate + 0.005); // Very gradual acceleration for reverse
                brake = 0.0;
                targetSpeed = Math.max(-30, targetSpeed - 0.5); // Much slower reverse target
            } else if (isForwardPressed) {
                brake = Math.min(1.0, brake + 0.02);
                accelerate = Math.max(0.0, accelerate - 0.02);
                targetSpeed = Math.min(0, targetSpeed + 2);
            } else {
                // Rilascio graduale
                accelerate *= 0.92; // More aggressive decay for reverse
                brake *= 0.98;
            }
        } else {
            // Normal forward movement
            if (isForwardPressed) {
                accelerate = Math.min(1.0, accelerate + 0.02);
                brake = 0.0;
                targetSpeed = Math.min(200, targetSpeed + 2);
            } else if (isBackwardPressed) {
                brake = Math.min(1.0, brake + 0.02);
                accelerate = Math.max(0.0, accelerate - 0.02);
                targetSpeed = Math.max(0, targetSpeed - 2);
            } else {
                // Rilascio graduale
                accelerate *= 0.98;
                brake *= 0.98;
            }
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
        
        // Toggle cambio marcia automatico con G
        if (isKeyPressed(KeyEvent.VK_G) && canTriggerAction(KeyEvent.VK_G)) {
            autoGearMode = !autoGearMode;
            System.out.println("Cambio marcia: " + (autoGearMode ? "AUTOMATICO" : "MANUALE"));
        }
        
        // Toggle ABS con V
        if (isKeyPressed(KeyEvent.VK_V) && canTriggerAction(KeyEvent.VK_V)) {
            absMode = !absMode;
            System.out.println("ABS: " + (absMode ? "ATTIVO" : "DISATTIVO"));
        }
        
        // Toggle assistenza sterzo con B
        if (isKeyPressed(KeyEvent.VK_B) && canTriggerAction(KeyEvent.VK_B)) {
            steeringAssist = !steeringAssist;
            System.out.println("Assistenza sterzo: " + (steeringAssist ? "ATTIVA" : "DISATTIVA"));
        }
        
        // Toggle frizione automatica con N
        if (isKeyPressed(KeyEvent.VK_N) && canTriggerAction(KeyEvent.VK_N)) {
            autoClutch = !autoClutch;
            System.out.println("Frizione automatica: " + (autoClutch ? "ATTIVA" : "DISATTIVA"));
        }
        
        // Cambio marcia manuale con Q/E (solo se modalità manuale)
        if (!autoGearMode) {
            if (isKeyPressed(KeyEvent.VK_Q) && canTriggerAction(KeyEvent.VK_Q)) {
                gear = Math.max(-1, gear - 1);
                System.out.println("Marcia: " + gear);
            }
            if (isKeyPressed(KeyEvent.VK_E) && canTriggerAction(KeyEvent.VK_E)) {
                gear = Math.min(6, gear + 1);
                System.out.println("Marcia: " + gear);
            }
        }
        
        // Toggle raccolta dati con C
        if (isKeyPressed(KeyEvent.VK_C) && canTriggerAction(KeyEvent.VK_C)) {
            collectingData = !collectingData;
            System.out.println("Raccolta dati: " + (collectingData ? "ON" : "OFF"));
            if (collectingData) {
                try {
                    collector.startCollection("human_dataset.csv");
                    System.out.println("Inizio raccolta dati...");
                } catch (IOException e) {
                    System.err.println("Errore nell'avviare la raccolta dati: " + e.getMessage());
                }
            } else {
                collector.stopCollection();
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
            try {
                collector.startCollection("human_dataset.csv");
                System.out.println("[INFO] Raccolta dati attivata automaticamente via parametro --collect");
            } catch (IOException e) {
                System.err.println("Errore nell'avviare la raccolta dati: " + e.getMessage());
            }
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
        autoGearMode = true; // Default: cambio automatico
        absMode = true;
        steeringAssist = false;
        autoClutch = true;
        keysPressed.clear();
        System.out.println("Controlli avanzati resettati - ABS e frizione automatica ATTIVI, assistenza sterzo DISATTIVA");
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