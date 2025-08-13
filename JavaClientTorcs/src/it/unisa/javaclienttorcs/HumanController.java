package it.unisa.javaclienttorcs;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;

/**
 * Controller per guida manuale con tastiera e gamepad.
 * Permette di guidare la macchina usando i tasti WASD per movimento e raccolta dati,
 * oppure tramite gamepad con analogici per sterzo e accelerazione/frenata.
 * Implementazione con input real-time senza richiesta di invio.
 */
public class HumanController extends Controller {
    private final GamepadHandler gamepad = new GamepadHandler(0);
            
    
    // Parametri di controllo
    private double targetSpeed = 0.0;
    private double steering = 0.0;
    private double accelerate = 0.0;
    private double brake = 0.0;
    private int gear = 1;
    
    // Parametri per sterzo gamepad
    private static final float STEERING_DEADZONE = 0.02f;  // Minima per massima responsività
    private static final float STEERING_SENSITIVITY = 0.5f; // Ridotta per controllo più preciso e graduale
    
    // === COSTANTI PER IL CAMBIO MARCIA AUTOMATICO (da SimpleDriver) ===
    // RPM minimi per salire di marcia [per marcia 1-6]
    private static final int[] gearUp = { 5000, 6000, 6000, 6500, 7000, 0 };
    // RPM massimi per scalare di marcia [per marcia 1-6]
    private static final int[] gearDown = { 0, 2500, 3000, 3000, 3500, 3500 };
    
    // Stato per raccolta dati
    private final EnhancedDataCollectionManager dataManager;
    private boolean collectingData = false;
    private boolean autoGearMode = true;
    private boolean absMode = true;
    private boolean steeringAssist = false;
    private boolean autoClutch = true;
    
    // Gestione tasti premuti in real-time
    private final ConcurrentHashMap<Integer, Boolean> keysPressed = new ConcurrentHashMap<>();
    private KeyListenerFrame keyFrame;
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    /**
     * Costruisce un nuovo controller umano per la guida manuale.
     * Inizializza il sistema di raccolta dati avanzata, il listener per la tastiera
     * e il gamepad, e stampa le istruzioni di controllo.
     */
    public HumanController() {
        this.dataManager = new EnhancedDataCollectionManager();
        initializeKeyListener();
        
        // Messaggio di avvio
        System.out.println("=== CONTROLLER UMANO ATTIVO ===");
        System.out.println("Controlli tastiera e gamepad attivi!");
        System.out.println("  TASTIERA:");
        System.out.println("    Accelerazione/Frenata: W/S ↑↓ 8/2 I/K");
        System.out.println("    Sterzo Sinistra/Destra: A/D ←→ 4/6 J/L");
        System.out.println("    Cambio marcia manuale: Q/E (solo se manuale)");
        System.out.println("    Toggle cambio automatico: G");
        System.out.println("    Toggle ABS: V");
        System.out.println("    Toggle assistenza sterzo: B");
        System.out.println("    Toggle frizione automatica: N");
        System.out.println("    Raccolta dati enhanced: C (ON/OFF)");
        System.out.println("    Reset: R");
        System.out.println("    Esci: X");
        System.out.println("  GAMEPAD:");
        System.out.println("    Sterzo: Left Stick X");
        System.out.println("    Accelerazione/Avanti: Right Trigger");
        System.out.println("    Frenata/Retromarcia: Left Trigger");
        System.out.println("    Cambio marcia manuale: LB (-) / RB (+)");
        System.out.println("    Toggle cambio automatico: BACK");
        System.out.println("    Toggle ABS: B");
        System.out.println("    Toggle assistenza sterzo: Y");
        System.out.println("    Toggle frizione automatica: A");
        System.out.println("    Raccolta dati enhanced: START");
        System.out.println("  Cambio marcia: " + (autoGearMode ? "AUTOMATICO" : "MANUALE") + " (default: AUTOMATICO)");
        System.out.println("================================");
    }
    
    /**
     * Classe interna statica per gestire la finestra di ascolto tasti.
     * Resa statica per evitare il "leaking this to constructor" warning.
     */
    private static class KeyListenerFrame extends JFrame implements KeyListener {
        private final ConcurrentHashMap<Integer, Boolean> keysPressed;
        
        public KeyListenerFrame(ConcurrentHashMap<Integer, Boolean> keysPressed, HumanController controller) {
            this.keysPressed = keysPressed;
            setTitle("TORCS Controller - Premi un tasto per guidare");
            setSize(600, 600);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setAlwaysOnTop(true);
            setLocationRelativeTo(null);
            
            // Aggiungi WindowListener per gestire la chiusura della finestra
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("[INFO] Chiusura controller richiesta dall'utente");
                    controller.shutdown();
                    System.exit(0);
                }
            });
            
            // Aggiungi label con istruzioni
            JLabel label = new JLabel("<html><div style='padding: 20px; font-size: 10px;'>" +
                "<h2 style='text-align: center; margin-bottom: 15px;'>🎮 TORCS CONTROLLER</h2>" +
                "<div style='margin-bottom: 10px;'><b>🚗 MOVIMENTO TASTIERA:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>W/S</b> | <b>↑↓</b> | <b>8/2</b> | <b>I/K</b> → Accelerazione/Frenata</div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>A/D</b> | <b>←→</b> | <b>4/6</b> | <b>J/L</b> → Sterzo Sinistra/Destra</div>" +
                "<div style='margin-bottom: 10px;'><b>🎮 MOVIMENTO GAMEPAD:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>Left Stick X</b> → Sterzo analogico</div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>Right Trigger</b> → Accelerazione/Avanti | <b>Left Trigger</b> → Frenata/Retromarcia</div>" +
                "<div style='margin-bottom: 10px;'><b>⚙️ CONTROLLI AVANZATI:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>Q/E</b> | <b>LB/RB</b> → Cambio marcia manuale (solo modalità manuale)</div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>G</b> | <b>BACK</b> → Toggle cambio automatico</div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>V</b> | <b>B</b> → Toggle ABS</div>" +
                "<div style='margin-left: 15px; margin-bottom: 5px;'>" +
                "• <b>B</b> | <b>Y</b> → Toggle assistenza sterzo</div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>N</b> | <b>A</b> → Toggle frizione automatica</div>" +
                "<div style='margin-bottom: 10px;'><b>💾 DATI:</b></div>" +
                "<div style='margin-left: 15px; margin-bottom: 15px;'>" +
                "• <b>C</b> | <b>START</b> → Raccolta dati enhanced ON/OFF</div>" +
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
            keyFrame = new KeyListenerFrame(keysPressed, this);
            keyFrame.setVisible(true);
        });
    }
    
    /**
     * Metodo principale di controllo che gestisce l'input dell'utente e produce un'azione.
     * Combina input da tastiera e gamepad, applica assistenze come ABS e sterzo assistito,
     * gestisce il cambio automatico/manuale e registra i dati per la raccolta.
     * 
     * @param sensors Modello sensoriale contenente i dati attuali del veicolo
     * @return Azione di controllo da inviare a TORCS
     */
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        
        // Aggiorna i controlli in base ai tasti premuti
        updateControls();
        
        // Applica adattamento velocità per sterzo da tastiera
        double adaptedSteering = applySpeedAdaptation(sensors, steering);
        
        // Applica assistenza sterzo se attiva
        double finalSteering = adaptedSteering;
        if (steeringAssist) {
            finalSteering = getAssistedSteering(sensors, adaptedSteering);
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
            dataManager.recordData(sensors, targetSpeed, action);
        }
        
        return action;
    }
    
    /**
     * Determina la marcia ottimale in base ai giri motore (RPM) per modalità automatica.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale della macchina
     * @return Marcia raccomandata (-1 per retromarcia, 0 per folle, 1-6 per marce avanti)
     */
    /**
     * Determina la marcia ottimale utilizzando il sistema avanzato del SimpleDriver.
     * Implementa cambio automatico intelligente con soglie RPM ottimizzate per ogni marcia.
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale della macchina
     * @return Marcia raccomandata (-1 per retromarcia, 0 per folle, 1-6 per marce avanti)
     */
    private int getGear(SensorModel sensors) {
        int currentGear = sensors.getGear();
        double rpm = sensors.getRPM();
        double speed = sensors.getSpeed(); // km/h
        
        // === GESTIONE RETROMARCIA ARCADE-STYLE ===
        // Input da tastiera
        boolean isBackwardPressed = isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_DOWN) || 
                                   isKeyPressed(KeyEvent.VK_NUMPAD2) || isKeyPressed(KeyEvent.VK_K);
        boolean isForwardPressed = isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_UP) || 
                                  isKeyPressed(KeyEvent.VK_NUMPAD8) || isKeyPressed(KeyEvent.VK_I);
        
        // Input da gamepad - aggiunta retromarcia con trigger
        float leftTrigger = gamepad.getLeftTrigger();
        float rightTrigger = gamepad.getRightTrigger();
        boolean gamepadBackwardPressed = leftTrigger > 0.3; // Left trigger per retromarcia
        boolean gamepadForwardPressed = rightTrigger > 0.3; // Right trigger per avanti
        
        // Combina input tastiera e gamepad
        isBackwardPressed = isBackwardPressed || gamepadBackwardPressed;
        isForwardPressed = isForwardPressed || gamepadForwardPressed;
        
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
        
        // === SISTEMA CAMBIO AUTOMATICO AVANZATO (da SimpleDriver) ===
        // Gestione marcia 0 (folle) - imposta sempre 1ª marcia
        if (currentGear < 1)
            return 1;
        
        // Logica upshift: se RPM supera soglia specifica per marcia attuale
        if (currentGear < 6 && rpm >= gearUp[currentGear - 1])
            return currentGear + 1;
        // Logica downshift: se RPM sotto soglia specifica per marcia attuale
        else if (currentGear > 1 && rpm <= gearDown[currentGear - 1])
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
     * Adatta la sensibilità dello sterzo in base alla velocità per migliorare
     * la guidabilità ad alte velocità, specialmente con controlli da tastiera.
     * 
     * @param sensors Modello sensoriale con dati attuali
     * @param rawSteering Sterzo grezzo dall'input [-1, 1]
     * @return Sterzo adattato alla velocità
     */
    private double applySpeedAdaptation(SensorModel sensors, double rawSteering) {
        double speed = sensors.getSpeed(); // km/h
        
        // Parametri di adattamento
        final double LOW_SPEED_THRESHOLD = 50.0;  // Sotto questa velocità, sensibilità normale
        final double HIGH_SPEED_THRESHOLD = 150.0; // Sopra questa velocità, sensibilità minima
        final double MIN_SENSITIVITY = 0.3;        // Sensibilità minima (30% del normale)
        
        // Calcola il fattore di riduzione basato sulla velocità
        double speedFactor = 1.0;
        if (speed > LOW_SPEED_THRESHOLD) {
            if (speed >= HIGH_SPEED_THRESHOLD) {
                speedFactor = MIN_SENSITIVITY;
            } else {
                // Interpolazione lineare tra soglia bassa e alta
                double speedRatio = (speed - LOW_SPEED_THRESHOLD) / (HIGH_SPEED_THRESHOLD - LOW_SPEED_THRESHOLD);
                speedFactor = 1.0 - (speedRatio * (1.0 - MIN_SENSITIVITY));
            }
        }
        
        return rawSteering * speedFactor;
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
    
    /**
     * Calcola il valore di sterzo dal gamepad con deadzone e curva di sensibilità.
     * @return Valore di sterzo filtrato [-1, 1]
     */
    private float getSteer() {
        float stickX = -gamepad.getLeftStickX();
        
        // Deadzone
        if (Math.abs(stickX) < STEERING_DEADZONE) {
            stickX = 0;
        } else {
            // Rimuovi deadzone e scala il valore rimanente
            stickX = (stickX - Math.signum(stickX) * STEERING_DEADZONE) / (1.0f - STEERING_DEADZONE);
        }
        
        // Applica curva di sensibilità
        float rawSteering = applySteeringCurve(stickX, STEERING_SENSITIVITY);
        
        return rawSteering;
    }
    
    /**
     * Applica una curva di sensibilità lineare per controllo più graduale.
     * @param input Input grezzo [-1, 1]
     * @param sensitivity Sensibilità della curva
     * @return Valore con curva applicata
     */
    private float applySteeringCurve(float input, float sensitivity) {
        return input * sensitivity;
    }
    
    private void updateControls() {
        // Aggiorna lo stato del gamepad
        gamepad.update();
        
        // Input da gamepad (priorità agli analogici)
        float leftStickX = gamepad.getLeftStickX();
        float rightTrigger = gamepad.getRightTrigger();
        float leftTrigger = gamepad.getLeftTrigger();
        
        // Input da tastiera
        boolean isForwardPressed = isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_UP) || 
                                  isKeyPressed(KeyEvent.VK_NUMPAD8) || isKeyPressed(KeyEvent.VK_I);
        boolean isBackwardPressed = isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_DOWN) || 
                                   isKeyPressed(KeyEvent.VK_NUMPAD2) || isKeyPressed(KeyEvent.VK_K);
        boolean isLeftPressed = isKeyPressed(KeyEvent.VK_A) || isKeyPressed(KeyEvent.VK_LEFT) || 
                               isKeyPressed(KeyEvent.VK_NUMPAD4) || isKeyPressed(KeyEvent.VK_J);
        boolean isRightPressed = isKeyPressed(KeyEvent.VK_D) || isKeyPressed(KeyEvent.VK_RIGHT) || 
                                isKeyPressed(KeyEvent.VK_NUMPAD6) || isKeyPressed(KeyEvent.VK_L);
        
        // Sterzo: priorità al gamepad, fallback tastiera
        // Documentazione TORCS: -1 = sterzo completo a destra, +1 = sterzo completo a sinistra
        if (Math.abs(leftStickX) > 0.05) {
            steering = getSteer(); // Gamepad con deadzone, curva e filtro
        } else if (isLeftPressed) {
            steering = Math.min(1.0, steering + 0.05); // Sinistra = +1
        } else if (isRightPressed) {
            steering = Math.max(-1.0, steering - 0.05); // Destra = -1
        } else {
            // Ritorno al centro
            steering *= 0.95;
        }
        
        // Accelerazione/Frenata: priorità al gamepad, fallback tastiera
        if (rightTrigger > 0.1 || leftTrigger > 0.1) {
            // Gamepad: right trigger = accelerazione, left trigger = frenata
            accelerate = rightTrigger;
            brake = leftTrigger;
            
            if (gear == -1) {
                // In reverse: inverti i comandi del gamepad
                accelerate = leftTrigger;
                brake = rightTrigger;
                targetSpeed = accelerate > 0 ? Math.max(-30, targetSpeed - accelerate) : 0;
            } else {
                targetSpeed = accelerate > 0 ? Math.min(200, targetSpeed + accelerate * 2) : 0;
            }
        } else {
            // Tastiera
            if (gear == -1) {
                // In reverse: backward key accelerates reverse, forward key brakes
                if (isBackwardPressed) {
                    accelerate = Math.min(1.0, accelerate + 0.003);
                    brake = Math.max(0.0, brake - 0.01);
                    targetSpeed = Math.max(-20, targetSpeed - 0.3);
                } else if (isForwardPressed) {
                    brake = Math.min(1.0, brake + 0.01);
                    accelerate = Math.max(0.0, accelerate - 0.01);
                    targetSpeed = Math.min(0, targetSpeed + 1);
                } else {
                    accelerate *= 0.90;
                    brake *= 0.95;
                }
            } else {
                // Normal forward movement
                if (isForwardPressed) {
                    accelerate = Math.min(1.0, accelerate + 0.01);
                    brake = Math.max(0.0, brake - 0.02);
                    targetSpeed = Math.min(150, targetSpeed + 1);
                } else if (isBackwardPressed) {
                    brake = Math.min(1.0, brake + 0.01);
                    accelerate = Math.max(0.0, accelerate - 0.01);
                    targetSpeed = Math.max(0, targetSpeed - 1);
                } else {
                    accelerate *= 0.95;
                    brake *= 0.95;
                }
            }
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
        
        // Controlli gamepad
        boolean gamepadA = gamepad.isAPressed();
        boolean gamepadB = gamepad.isBPressed();
        boolean gamepadY = gamepad.isYPressed();
        boolean gamepadStart = gamepad.isStartPressed();
        boolean gamepadBack = gamepad.isBackPressed();
        boolean gamepadLB = gamepad.isLeftBumperPressed();
        boolean gamepadRB = gamepad.isRightBumperPressed();
        
        // Toggle cambio marcia automatico con G o gamepad BACK
        if ((isKeyPressed(KeyEvent.VK_G) && canTriggerAction(KeyEvent.VK_G)) || 
            (gamepadBack && canTriggerAction(1000))) {
            autoGearMode = !autoGearMode;
            System.out.println("Cambio marcia: " + (autoGearMode ? "AUTOMATICO" : "MANUALE"));
        }
        
        // Toggle ABS con V o gamepad B
        if ((isKeyPressed(KeyEvent.VK_V) && canTriggerAction(KeyEvent.VK_V)) || 
            (gamepadB && canTriggerAction(1001))) {
            absMode = !absMode;
            System.out.println("ABS: " + (absMode ? "ATTIVO" : "DISATTIVO"));
        }
        
        // Toggle assistenza sterzo con B o gamepad Y
        if ((isKeyPressed(KeyEvent.VK_B) && canTriggerAction(KeyEvent.VK_B)) || 
            (gamepadY && canTriggerAction(1002))) {
            steeringAssist = !steeringAssist;
            System.out.println("Assistenza sterzo: " + (steeringAssist ? "ATTIVA" : "DISATTIVA"));
        }
        
        // Toggle frizione automatica con N o gamepad A
        if ((isKeyPressed(KeyEvent.VK_N) && canTriggerAction(KeyEvent.VK_N)) || 
            (gamepadA && canTriggerAction(1003))) {
            autoClutch = !autoClutch;
            System.out.println("Frizione automatica: " + (autoClutch ? "ATTIVA" : "DISATTIVA"));
        }
        
        // Cambio marcia manuale con Q/E o gamepad LB/RB
        if (!autoGearMode) {
            if ((isKeyPressed(KeyEvent.VK_Q) && canTriggerAction(KeyEvent.VK_Q)) || 
                (gamepadLB && canTriggerAction(1004))) {
                gear = Math.max(-1, gear - 1);
                System.out.println("Marcia: " + gear);
            }
            if ((isKeyPressed(KeyEvent.VK_E) && canTriggerAction(KeyEvent.VK_E)) || 
                (gamepadRB && canTriggerAction(1005))) {
                gear = Math.min(6, gear + 1);
                System.out.println("Marcia: " + gear);
            }
        }
        
        // Toggle raccolta dati con C o gamepad START
        if ((isKeyPressed(KeyEvent.VK_C) && canTriggerAction(KeyEvent.VK_C)) || 
            (gamepadStart && canTriggerAction(1006))) {
            collectingData = !collectingData;
            System.out.println("Raccolta dati: " + (collectingData ? "ON" : "OFF"));
            if (collectingData) {
                dataManager.startEnhancedCollection();
                System.out.println("Inizio raccolta dati enhanced...");
            } else {
                dataManager.stopAndConvert();
                System.out.println("Fine raccolta dati. File creati: enhanced_dataset.csv e human_dataset.csv");
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
        

    }
    

    
    private boolean isKeyPressed(int keyCode) {
        return keysPressed.getOrDefault(keyCode, false);
    }
    
    // Gestione toggle dei tasti
    private static final long KEY_COOLDOWN = 200; // millisecondi
    private final ConcurrentHashMap<Integer, Long> lastPressedTimes = new ConcurrentHashMap<>();
    
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
            dataManager.startEnhancedCollection();
            System.out.println("[INFO] Raccolta dati enhanced attivata automaticamente via parametro --collect");
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
        
        // Conversione automatica dei dati raccolti alla chiusura
        if (collectingData) {
            System.out.println("[INFO] Conversione automatica dei dati raccolti...");
            dataManager.stopAndConvert();
            collectingData = false;
        }
    }
    

}