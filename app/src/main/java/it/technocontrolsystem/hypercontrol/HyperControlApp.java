package it.technocontrolsystem.hypercontrol;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.communication.Connection;

/**
 *
 */
public class HyperControlApp extends Application {

    // Attenzione alle variabili statiche!
    // In Android, le variabili statiche possono essere ritenute
    // tra una sessione e l'altra della applicazione perché
    // il sistema non necessariamente distrugge il processo e ferma la JVM
    private static HyperControlApp instance;
    private static Connection conn;
    private static String lastConnectionError;  // ultima eccezione nella fase di connection
    private static String lastSyncDBError;// ultima eccezione nella fase di sync db
    private static ArrayList<OnConnectionStatusChangedListener> connectionStatusChangedListeners;

    public HyperControlApp() {
        instance = this;
        connectionStatusChangedListeners=new ArrayList<>();
    }

    public static Context getContext() {
        return instance;
    }

    /**
     * Distrugge le variabili statiche che devono essere distrutte.
     * In Android, le variabili statiche possono essere ritenute
     * tra una sessione e l'altra della applicazione perché
     * ilsistema non necessariamente distrugge il processo e ferma la JVM
     */
    public static void destroyStaticVariables() {
        conn=null;
    }


    public static void setConnection(Connection newConn) {
        Connection oldConn = HyperControlApp.conn;
        HyperControlApp.conn = newConn;

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
            for (OnConnectionStatusChangedListener l : connectionStatusChangedListeners) {
                l.connectionStatusChanged(conn);
            }
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static String getLastConnectionError() {
        return lastConnectionError;
    }

    public static void setLastConnectionError(String lastConnectionError) {
        HyperControlApp.lastConnectionError = lastConnectionError;
    }

    public static String getLastSyncDBError() {
        return lastSyncDBError;
    }

    public static void setLastSyncDBError(String lastSyncDBError) {
        HyperControlApp.lastSyncDBError = lastSyncDBError;
    }


    public static void addOnConnectionStatusChangedListener(OnConnectionStatusChangedListener l) {
        connectionStatusChangedListeners.add(l);
    }

    /**
     * Created by alex on 10-07-2015.
     */
    public static interface OnConnectionStatusChangedListener {
        public void connectionStatusChanged(Connection conn);
    }
}
