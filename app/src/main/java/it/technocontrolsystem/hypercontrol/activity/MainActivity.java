package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // registra lo stato iniziale di connettività
        HyperControlApp.setHasConnectivity(Lib.isNetworkAvailable());

        /**
         * Registra un BroadcastReceiver per i cambi di connettività
         */
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean connected = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                HyperControlApp.setHasConnectivity(connected);
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));


        // seleziona il tipo di partenza dell'applicazione
        // in base al numero di siti presenti
        autoselect();

    }


    private void noSite() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, EditSiteActivity.class);
        intent.putExtra("destinationactivity", StartSiteActivity.class);
        startActivity(intent);
    }

    private void singleSite() {
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, SiteActivity.class);
//        Site site= DB.getSites() [0];
//        intent.putExtra("siteid",site.getId());
//        intent.putExtra("siteversion",site.getVersion());//federico
//        startActivity(intent);


        Intent intent = new Intent();
        intent.setClass(this, StartSiteActivity.class);
        Site site = DB.getSites()[0];
        intent.putExtra("siteid", site.getId());
        startActivity(intent);

    }

    private void manySites() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SelectSiteActivity.class);
        startActivity(intent);

    }

    private void autoselect() {
        Results r = getSelect();
        switch (r) {
            case NONE:
                noSite();
                break;
            case ONE:
                singleSite();
                break;
            case MANY:
                manySites();
                break;
        }
        finish();
    }

    private Results getSelect() {
        Results r = null;
        int numSites = DB.getSiteCount();
        if (numSites == 0) {
            r = Results.NONE;
        } else {
            if (numSites == 1) {
                r = Results.ONE;
            } else {
                r = Results.MANY;
            }

        }

        return r;
    }

    private enum Results {NONE, ONE, MANY}


}
