package it.technocontrolsystem.hypercontrol;

import android.app.Application;
import android.content.Context;

/**
 *
 */
public class HyperControlApp extends Application {
    private static HyperControlApp instance;

    public HyperControlApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}
