package it.technocontrolsystem.hypercontrol;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.communication.Connection;

/**
 *
 */
public class HyperControlApp extends Application {
    private static HyperControlApp instance;
    private static Connection conn;
    private static String lastConnectionError;  // ultima eccezione nella fase di connection
    private static String lastSyncDBError;// ultima eccezione nella fase di sync db
    private static ArrayList<OnConnectionStatusChangedListener> connectionStatusChangedListeners = new ArrayList();

    public HyperControlApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    public static void setConnection(Connection newConn) {
        Connection oldConn=HyperControlApp.conn;
        HyperControlApp.conn = newConn;

        // check if changed and fire
        boolean fire=false;
        if ((oldConn==null) && (newConn!=null)){
            fire=true;
        }else{
            if((oldConn!=null) && (newConn==null)){
                fire=true;
            }else{
                if((oldConn!=null) && (newConn!=null)){
                    if(oldConn!=newConn){
                        fire=true;
                    }
                }
            }
        }

        if(fire){
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
