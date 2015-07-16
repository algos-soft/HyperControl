package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.communication.ConnectionTask;
import it.technocontrolsystem.hypercontrol.communication.SyncSiteTask;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * Activity di lancio della navigazione in un Site.
 * Gestisce la connessione iniziale e l'aggiornamento del database.
 * Questa activity non ha interfaccia e si chiude
 * automaticamente dopo che ha lanciato il sito.
 */
public class StartSiteActivity extends Activity {
    private static final String TAG="STARTSITE";

    private int idSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "StartSite - onCreate()");

        idSite = getIntent().getIntExtra("siteid", 0);


        if (Lib.isNetworkAvailable()) {
            Log.d(TAG, "Network is available");

            Lib.lockOrientation(this);

            Runnable successRunnable = new Runnable() {
                @Override
                public void run() {
                    // lancia la sincronizzazione DB
                    syncDB();
                }
            };

            Runnable failRunnable = new Runnable() {
                @Override
                public void run() {
                    HyperControlApp.setConnection(null);
                    startSite();
                }
            };

            ConnectionTask task = new ConnectionTask(this, getSite(), successRunnable, failRunnable);
            task.execute();

        } else {
            Log.d(TAG, "Network unavailable");
            startSite();
        }
    }


    /**
     * Esegue la sincronizzazione del database
     */
    private void syncDB(){

        Runnable successRunnable = new Runnable() {
            @Override
            public void run() {
                startSite();
            }
        };

        Runnable failRunnable = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };

        SyncSiteTask task = new SyncSiteTask(this, getSite(), successRunnable, failRunnable);
        task.execute();

    }


    /**
     * termina questa activity e Lancia la SiteActivity
     */
    public void startSite() {
        finish();
        Lib.unlockOrientation(this);
        Intent intent = new Intent();
        intent.setClass(this, SiteActivity.class);
        intent.putExtra("siteid", idSite);
        startActivity(intent);
    }

    public Site getSite() {
        return DB.getSite(idSite);
    }


}
