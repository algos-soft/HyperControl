package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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

    private int idSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idSite = getIntent().getIntExtra("siteid", 0);

        if (Lib.isNetworkAvailable()) {
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
                    startSite();
                }
            };

            ConnectionTask task = new ConnectionTask(this, getSite(), successRunnable, failRunnable);
            task.execute();

        } else {
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
     * Lancia la SiteActivity e termina questa activity
     */
    public void startSite() {
        Lib.unlockOrientation(this);
        Intent intent = new Intent();
        intent.setClass(this, SiteActivity.class);
        intent.putExtra("siteid", idSite);
        startActivity(intent);
        finish();
    }

    public Site getSite() {
        return DB.getSite(idSite);
    }


}
