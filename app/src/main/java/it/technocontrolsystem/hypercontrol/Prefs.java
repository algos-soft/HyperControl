package it.technocontrolsystem.hypercontrol;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Libreria per l'accesso alle preferenze della applicazione.
 */
public class Prefs {

    public static final String MY_PREFS_NAME = "UserPrefs";

    public static final String PASSWORD = "password";
    public static final String RECEIVE_NOTIFICATIONS = "receiveNotifications";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_REGISTRATION_TOKEN = "gmcRegistrationToken";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";


    /**
     * Ritorna le preferenze della applicazione
     */
    public static SharedPreferences getPrefs(){
        return HyperControlApp.getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Ritorna un editor delle preferenze
     */
    public static SharedPreferences.Editor getEditor(){
        return getPrefs().edit();
    }

    public static void remove(String key){
        SharedPreferences.Editor editor=getEditor();
        editor.remove(key);
        editor.commit();
    }

    public static String getPassword() {
        return getPrefs().getString(PASSWORD,null);
    }

    public static void setPassword(String newPassword) {
        SharedPreferences.Editor editor=getEditor();
        editor.putString(PASSWORD,newPassword);
        editor.commit();
    }

    public static boolean isRiceviNotifiche() {
        return getPrefs().getBoolean(RECEIVE_NOTIFICATIONS,true);
    }

    public static void setPassword(boolean flag) {
        SharedPreferences.Editor editor=getEditor();
        editor.putBoolean(RECEIVE_NOTIFICATIONS,flag);
        editor.commit();
    }

}
