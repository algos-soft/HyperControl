package it.technocontrolsystem.hypercontrol;

import android.content.Context;
import android.content.SharedPreferences;

import it.technocontrolsystem.hypercontrol.activity.SettingActivity;

/**
 * Libreria per l'accesso alle preferenze della applicazione.
 */
public class Prefs {

    public static final String MY_PREFS_NAME = "UserPrefs";

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
        return getPrefs().getString("password",null);
    }

    public static void setPassword(String newPassword) {
        SharedPreferences.Editor editor=getEditor();
        editor.putString("password",newPassword);
        editor.commit();
    }


}
