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

    private Connection conn;
    private String lastConnectionError;  // ultima eccezione nella fase di connection
    private String lastSyncDBError;// ultima eccezione nella fase di sync db
    private ArrayList<OnConnectionStatusChangedListener> connectionStatusChangedListeners;

    public HyperControlApp() {
        instance=this;
        connectionStatusChangedListeners=new ArrayList<>();
    }

    public static Context getContext() {
        return getInstance();
    }

    public static void setConnection(Connection newConn) {
        Connection oldConn = getInstance().conn;
        getInstance().conn=newConn;

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


    public static void addOnConnectionStatusChangedListener(OnConnectionStatusChangedListener l) {
        getInstance().connectionStatusChangedListeners.add(l);
    }

    /**
     * Created by alex on 10-07-2015.
     */
    public static interface OnConnectionStatusChangedListener {
        public void connectionStatusChanged(Connection conn);
    }

    public static HyperControlApp getInstance() {
        return instance;
    }


}
