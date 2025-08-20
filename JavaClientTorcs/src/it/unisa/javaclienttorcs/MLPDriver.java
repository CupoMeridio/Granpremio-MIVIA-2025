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
    private final int port = 5005;
    
    public MLPDriver() {
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            this.socket = new SocketUDPClient(serverAddress,port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MLPDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MLPDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public Action control(SensorModel sensors) {
        Action action = new Action();
        action.accelerate = 0;
        action.brake = 0;
        action.steering = 0;
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
