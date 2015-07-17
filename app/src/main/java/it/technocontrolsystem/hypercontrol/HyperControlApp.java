package it.technocontrolsystem.hypercontrol;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;

/**
 *
 */
public class HyperControlApp extends Application {

    // Attenzione alle variabili statiche!
    // In Android, le variabili statiche possono essere ritenute
    // tra una sessione e l'altra della applicazione perché
    // il sistema non necessariamente distrugge il processo e ferma la JVM
    private static HyperControlApp instance;

    private Connection conn;
    private String lastConnectionError;  // ultima eccezione nella fase di connection
    private String lastSyncDBError;// ultima eccezione nella fase di sync db
    private ArrayList<OnConnectionStatusChangedListener> connectionStatusChangedListeners;
    private ArrayList<OnConnectivityChangedListener> connectivityChangedListeners;
    private boolean hasConnectivity;// se il device è connesso alla rete
    private boolean developer;  // se l'utente è un developer
    public static final String DEV_PASS="tcshc9213";

    public HyperControlApp() {
        instance = this;
        init();
    }

    /**
     * Reinizializza tutte le variabili.
     * In Android, le variabili statiche (in questo caso il Singleton della app,
     * con tutte le sue variabili - anche non statiche) possono essere ritenute
     * tra una sessione e l'altra della applicazione perché
     * il sistema non necessariamente distrugge il processo e ferma la JVM.
     * Quindi all'avvio della app il prima possibile le devo resettare.
     */
    public static void init() {
        instance.conn=null;
        instance.lastConnectionError=null;
        instance.lastSyncDBError=null;
        instance.connectionStatusChangedListeners=new ArrayList<>();
        instance.connectivityChangedListeners=new ArrayList<>();
        instance.hasConnectivity=false;
    }

    public static Context getContext() {
        return getInstance();
    }

    public static void setConnection(Connection newConn) {
        Connection oldConn = getInstance().conn;
        getInstance().conn = newConn;

        // check if changed and fire
        boolean fire = false;
        if ((oldConn == null) && (newConn != null)) {
            fire = true;
        } else {
            if ((oldConn != null) && (newConn == null)) {
                fire = true;
            } else {
                if ((oldConn != null) && (newConn != null)) {
                    if (oldConn != newConn) {
                        fire = true;
                    }
                }
            }
        }

        if (fire) {
            for (OnConnectionStatusChangedListener l : getInstance().connectionStatusChangedListeners) {
                l.connectionStatusChanged(getConnection());
            }
        }
    }

    public static Connection getConnection() {
        return getInstance().conn;
    }

    public static String getLastConnectionError() {
        return getInstance().lastConnectionError;
    }

    public static void setLastConnectionError(String lastConnectionError) {
        getInstance().lastConnectionError = lastConnectionError;
    }

    public static String getLastSyncDBError() {
        return getInstance().lastSyncDBError;
    }

    public static void setLastSyncDBError(String lastSyncDBError) {
        getInstance().lastSyncDBError = lastSyncDBError;
    }

    public static boolean hasConnectivity() {
        return instance.hasConnectivity;
    }

    /**
     * Regola il flag di connettività.
     * Se la connettività viene a mancare annulla la connessione
     */
    public static void setHasConnectivity(boolean newStatus) {
        boolean oldStatus = instance.hasConnectivity;
        if (newStatus != oldStatus) {
            instance.hasConnectivity = newStatus;

            if (!instance.hasConnectivity) {
                if(instance.conn!=null){
                    instance.conn.close();
                    instance.conn = null;
                }
            }

            for (OnConnectivityChangedListener l : instance.connectivityChangedListeners) {
                l.connectivityChanged(newStatus);
            }
        }
    }


    /**
     * Accoda una richiesta e attende la risposta corrispondente.
     * @param req richiesta da accodare
     * @return risposta corrispondente, null se in timeout o se
     * la connessione è chiusa
     */
    public static Response sendRequest(Request req) {
        Response resp=null;
        Connection conn = getConnection();
        if(conn!=null){
            if(conn.isOpen()){
                resp=conn.sendRequest(req);
            }
        }
        return resp;
    }


        public static void addOnConnectionStatusChangedListener(OnConnectionStatusChangedListener l) {
        getInstance().connectionStatusChangedListeners.add(l);
    }

    public interface OnConnectionStatusChangedListener {
        public void connectionStatusChanged(Connection conn);
    }

    public static void addOnConnectivityChangedListener(OnConnectivityChangedListener l) {
        getInstance().connectivityChangedListeners.add(l);
    }

    public interface OnConnectivityChangedListener {
        public void connectivityChanged(boolean newStatus);
    }

    public static boolean isDeveloper() {
        return instance.developer;
    }

    public static void setDeveloper(boolean developer) {
        instance.developer = developer;
    }

    public static HyperControlApp getInstance() {
        return instance;
    }


}
