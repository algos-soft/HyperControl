package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.gcm.GCMRegisterTask;


/**
 * Inizializza i servizi GCM (push).
 * Seleziona la modalità di partenza in base al numero di siti
 * registrati e lancia l'activity adeguata.
 * Questa activity non ha interfaccia, lancia sempre un'altra
 * activity e poi si chiude automaticamente.
 */
public class MainActivity extends Activity {

    private static int ACTION_CREATE_NEW_SITE = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "HC-MAIN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inizializza i servizi GCM (se abilitati)
        if (Prefs.isRiceviNotifiche()) {
            if (Prefs.getPrefs().getString(Prefs.GCM_REGISTRATION_TOKEN, null) == null) {
                if (checkPlayServices()) {
                    GCMRegisterTask task = new GCMRegisterTask();
                    task.execute();
                } else {
                    Log.i(TAG, "No valid Google Play Services APK found.");
                    Toast.makeText(this, "Questo dispositivo non supporta i Google Play Services\nImpossibile ricevere le notifiche.", Toast.LENGTH_LONG).show();
                }
            }
        }

        // seleziona il tipo di partenza dell'applicazione
        // in base al numero di siti presenti
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
    }


    private void noSite() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, EditSiteActivity.class);
        intent.putExtra("title", "Nessun sito registrato. " +
                "Devi accedere a un sito per scaricare la configurazione.");
        intent.putExtra("usedelete", false);
        intent.putExtra("usesave", true);
        startActivityForResult(intent, ACTION_CREATE_NEW_SITE);
    }

    private void singleSite() {
        Intent intent = new Intent();
        intent.setClass(this, SiteActivity.class);
        Site site = DB.getSites()[0];
        intent.putExtra("siteid", site.getId());
        startActivity(intent);
        finish();
    }

    private void manySites() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SitesListActivity.class);
        startActivity(intent);
        finish();
    }


    private Results getSelect() {
        Results r;
        int numSites = DB.getSitesCount();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // ha creato un nuovo sito
        // lancio il nuovo sito e chiudo questa activity
        if (requestCode == ACTION_CREATE_NEW_SITE) {
            if (resultCode == RESULT_OK) {
                int siteid = data.getIntExtra("siteid", 0);
                Intent intent = new Intent();
                intent.setClass(this, SiteActivity.class);
                intent.putExtra("siteid", siteid);
                startActivity(intent);
            }
            finish();
        }

    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * @return true if Google Play Services is available
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device does not support GooglePlayServices (GCM push)");
                finish();
            }
            return false;
        }
        return true;
    }


}
