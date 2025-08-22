package it.unisa.javaclienttorcs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;

/**
 * Classe per la gestione di una socket UDP.
 * Implementa AutoCloseable per l'uso con try-with-resources.
 */
public class SocketUDPClient implements AutoCloseable {

    private static final int BUFFER_SIZE = 1024;
    private final DatagramSocket socket;
    private final InetAddress serverAddress;
    private final int serverPort;

    /**
     * Costruisce un UdpClient e crea la DatagramSocket.
     *
     * @param serverAddress L'indirizzo del server.
     * @param serverPort La porta del server.
     * @throws IOException Se la creazione della socket fallisce.
     */
    public SocketUDPClient(InetAddress serverAddress, int serverPort) throws IOException {
        Objects.requireNonNull(serverAddress, "L'indirizzo del server non può essere nullo.");
        if (serverPort <= 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Numero di porta non valido.");
        }
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.socket = new DatagramSocket(); 
    }

    /**
     * Invia un messaggio e attende una risposta dal server.
     *
     * @param message Il messaggio da inviare.
     * @return La stringa del messaggio ricevuto.
     * @throws IOException Se si verifica un errore di I/O.
     */
    public String sendAndReceive(String message) throws IOException {
        // Prepara il pacchetto per l'invio
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        socket.send(sendPacket);

        // Prepara il pacchetto per la ricezione
        byte[] receiveData = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        // Converte i dati ricevuti in una stringa e la restituisce
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    /**
     * Chiude la socket.
     */
    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Socket chiusa.");
        }
    }
}