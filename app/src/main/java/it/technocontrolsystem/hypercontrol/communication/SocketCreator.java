package it.technocontrolsystem.hypercontrol.communication;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Runnable per la creazione del socket in un thread separato.
 * Al termine della esecuzione, isDone() ritorna true.
 * - se il socket è stato creato entro il timeout previsto,
 * getSocket() ritorna il socket
 * - se il socket non è stato creato, getSocket() ritorna null
 * e getException() ritorna l'eccezione generata.
 */
class SocketCreator implements Runnable {

    boolean done = false;
    String address;
    int port;
    Socket socket;
    Exception exception;


    SocketCreator(String address, int port) {
        this.address = address;
        this.port = port;
    }


    @Override
    public void run() {


        try {

            socket = new Socket();
            socket.setSoTimeout(10000); // read timeout
            InetSocketAddress iAddr = new InetSocketAddress(address, port);
            socket.connect(iAddr, 5000);   // connect timeout

        } catch (Exception e) {
            exception = e;
            socket = null;
            e.printStackTrace();
        }

        done = true;

    }


    public Exception getException() {
        return exception;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isDone() {
        return done;
    }
}
