package it.technocontrolsystem.hypercontrol.communication;

/**
 * Created by Alex on 05/07/15.
 */
public class NetworkUnavailableException extends Exception {

    public NetworkUnavailableException() {
        super("Connessione non disponibile. Attiva una connessione.");
    }

}
