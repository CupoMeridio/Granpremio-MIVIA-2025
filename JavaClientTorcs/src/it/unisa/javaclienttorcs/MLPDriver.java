/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.javaclienttorcs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class MLPDriver extends Controller {
    
    private SocketUDPClient socket;
    private final int port = 35567;
    
    	/* === COSTANTI PER IL CAMBIO MARCIA === */
	// RPM minimi per salire di marcia [per marcia 1-6]
	final int[] gearUp = { 4500, 5500, 6500, 6500, 7000, 0 };
	// RPM massimi per scalare di marcia [per marcia 1-6]
	final int[] gearDown = { 0, 2500, 3500, 4000, 4500, 5000 };
        
        /* === COSTANTI PER SISTEMA ABS === */
	// Raggio delle ruote [anteriore, anteriore, posteriore, posteriore]
	final float wheelRadius[] = { (float) 0.3179, (float) 0.3179, (float) 0.3276, (float) 0.3276 };
	// Slip massimo consentito prima di attivare ABS
	final float absSlip = (float) 2.0;
	// Range di funzionamento ABS
	final float absRange = (float) 3.0;
	// Velocità minima per attivare ABS
	final float absMinSpeed = (float) 3.0;
        
        /* === COSTANTI PER GESTIONE FRIZIONE === */
	// Valore massimo della frizione
	final float clutchMax = (float) 0.5;
	// Incremento/decremento della frizione per ciclo
	final float clutchDelta = (float) 0.05;
	// Range di funzionamento della frizione
	final float clutchRange = (float) 0.82;
	// Intervallo temporale per aggiornamenti frizione
	final float clutchDeltaTime = (float) 0.02;
	// Distanza percorsa per aggiornamento frizione
	final float clutchDeltaRaced = 10;
	// Decremento naturale della frizione
	final float clutchDec = (float) 0.01;
	// Modificatore massimo per frizione
	final float clutchMaxModifier = (float) 1.3;
	// Tempo massimo per frizione completa
	final float clutchMaxTime = (float) 1.5;
        
        
    
    public MLPDriver() {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            this.socket = new SocketUDPClient(serverAddress,port);
            
            // Test initial connectivity to MLP server
            System.out.println("[MLP] Testing connection to Python server on port " + port + "...");
            testServerConnection();
            System.out.println("[MLP] Successfully connected to Python server!");
            
        } catch (UnknownHostException ex) {
            System.err.println("[MLP ERROR] Cannot resolve localhost address!");
            Logger.getLogger(MLPDriver.class.getName()).log(Level.SEVERE, "Cannot resolve localhost", ex);
            throw new RuntimeException("MLP Server connection failed: Cannot resolve localhost", ex);
        } catch (IOException ex) {
            System.err.println("[MLP ERROR] Cannot connect to Python server on port " + port + "!");
            System.err.println("[MLP ERROR] Please ensure the Python MLP server is running.");
            Logger.getLogger(MLPDriver.class.getName()).log(Level.SEVERE, "Cannot connect to MLP server", ex);
            throw new RuntimeException("MLP Server connection failed: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Tests the connection to the MLP Python server by sending a test message.
     * @throws IOException if the server is not responding
     */
    private void testServerConnection() throws IOException {
        try {
            // Send a simple test message to verify server is responding
            String testResponse = socket.sendAndReceive("test");
            if (testResponse == null || testResponse.trim().isEmpty()) {
                throw new IOException("Server responded with empty message");
            }
        } catch (IOException ex) {
            throw new IOException("MLP Python server is not responding on port " + port + ". " +
                                "Please start the server using: python mlpDriver/mlpDrive.py", ex);
        }
    }
    
    /**
     * Determina la marcia ottimale in base ai giri motore (RPM).
     * 
     * @param sensors Modello sensoriale contenente lo stato attuale della macchina
     * @return Marcia raccomandata (-1 per retromarcia, 0 per folle, 1-6 per marce avanti)
     */
    private int getGear(SensorModel sensors) {
	// Recupera marcia e RPM attuali dalla sensoristica
	int gear = sensors.getGear();
	double rpm = sensors.getRPM();

	// Gestione marcia 0 (folle) - imposta sempre 1ª marcia
	if (gear < 1)
            return 1;
		
	// Logica upshift: se RPM supera soglia per marcia attuale
	if (gear < 6 && rpm >= gearUp[gear - 1])
            return gear + 1;
	// Logica downshift: se RPM sotto soglia per marcia attuale
	else if (gear > 1 && rpm <= gearDown[gear - 1])
            return gear - 1;
	else
	// Nessun cambio marcia necessario
            return gear;
	}
    
    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        action.accelerate = 0;
        action.brake = 0;
        action.steering = 0;
        
        // Determina marcia ottimale in base a RPM e velocità
	int gear = getGear(sensors);
        
        try {
            System.out.println("Invio Sensori: "+ sensors);
            //track0,track2,track4,track6,track8,
            //track10,track12,track14,track16,track18,speedX,angleToTrackAxis,
            //trackPosition,distanceFromStartLine,
            StringBuilder sensori_da_inviare = new StringBuilder();
            
            sensori_da_inviare.append("(").append(sensors.getTrackEdgeSensors()[0])
                                           .append(",").append(sensors.getTrackEdgeSensors()[2])
                                           .append(",").append(sensors.getTrackEdgeSensors()[4])
                                           .append(",").append(sensors.getTrackEdgeSensors()[6])
                                           .append(",").append(sensors.getTrackEdgeSensors()[8])
                                           .append(",").append(sensors.getTrackEdgeSensors()[10])
                                           .append(",").append(sensors.getTrackEdgeSensors()[12])
                                           .append(",").append(sensors.getTrackEdgeSensors()[14])
                                           .append(",").append(sensors.getTrackEdgeSensors()[16])
                                           .append(",").append(sensors.getTrackEdgeSensors()[18])
                                           .append(",").append(sensors.getSpeed())
                                           .append(",").append(sensors.getAngleToTrackAxis())
                                           .append(",").append(sensors.getTrackPosition()).append(",")
                                           .append(sensors.getDistanceFromStartLine()).append(")");
            
            String risposta = socket.sendAndReceive(sensori_da_inviare.toString());
            // steering,acceleration,brake
            double[] arrayDiDouble = Arrays.stream(risposta.split(","))
                                     .mapToDouble(Double::parseDouble)
                                     .toArray();
            
            action.steering = arrayDiDouble[0];
            action.accelerate = arrayDiDouble[1];
            action.brake = arrayDiDouble[2];
            action.gear = gear;
            
            System.out.println("Ricevo:" + risposta);
           
        } catch (IOException ex) {
            Logger.getLogger(MLPDriver.class.getName()).log(Level.SEVERE, null, ex);
            return action;
        }
        
        return action;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void shutdown() {
        System.out.println("Chiusura della socket per il  processo col modello MLP");
        try {
            socket.sendAndReceive("Chiudi");
        } catch (IOException ex) {
            socket.close();
            Logger.getLogger(MLPDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        socket.close();
    }
    
}
