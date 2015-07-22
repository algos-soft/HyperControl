package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;


/**
 * Inizializza i servizi GCM (push).
 * Seleziona la modalità di partenza in base al numero di siti
 * registrati e lancia l'activity adeguata.
 * Questa activity non ha interfaccia, lancia sempre un'altra
 * activity e poi si chiude automaticamente.
 */
public class MainActivity extends Activity {

    private static int ACTION_CREATE_NEW_SITE=1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "HC-MAIN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inizializza i servizi GCM (se abilitati)
        if(Prefs.isRiceviNotifiche()){
            if (Prefs.getPrefs().getString(Prefs.GCM_REGISTRATION_TOKEN, null)==null){
                initGCM();
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
        intent.putExtra("title","Nessun sito registrato. " +
                "Devi accedere a un sito per scaricare la configurazione.");
        intent.putExtra("usedelete",false);
        intent.putExtra("usesave",true);
        startActivityForResult(intent, ACTION_CREATE_NEW_SITE);
    }

    private void singleSite() {
        Intent intent = new Intent();
        intent.setClass(this, StartSiteActivity.class);
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
        if(requestCode==ACTION_CREATE_NEW_SITE){
            if(resultCode==RESULT_OK) {
                int siteid = data.getIntExtra("siteid", 0);
                Intent intent = new Intent();
                intent.setClass(this, StartSiteActivity.class);
                intent.putExtra("siteid", siteid);
                startActivity(intent);
            }
            finish();
        }


    }


    // inizializza i servizi GCM
    private void initGCM(){
        if(checkPlayServices()) {
            RegisterTask task = new RegisterTask(this);
            task.execute();
        }else{
            Log.i(TAG, "No valid Google Play Services APK found.");
            Toast.makeText(this,"Questo dispositivo non supporta i Google Play Services\nImpossibile ricevere le notifiche.", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
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



    private String obtainRegistrationToken() throws IOException{
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId=getString(R.string.gcm_defaultSenderId);
        String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        return token;
    }


//    /**
//     * Registers the application with GCM servers asynchronously.
//     * <p>
//     * Stores the registration ID and the app versionCode in the application's
//     * shared preferences.
//     */
//    private void registerInBackground() {
//        RegisterTask task = new RegisterTask(this);
//        task.execute();
//
//
//    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    class RegisterTask extends AsyncTask<Void, Void, String>{

        private Context context;
        private String regToken;

        RegisterTask(Context context) {
            this.context=context;
        }

        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                String senderId=getString(R.string.gcm_defaultSenderId);
                regToken = gcm.register(senderId);
                msg = "Device registered, registration ID=" + regToken;

                // You should send the registration ID to your server over HTTP, so it
                // can use GCM/HTTP or CCS to send messages to your app.
                sendRegistrationIdToBackend();

                // Persist the regID - no need to register again.
                Prefs.getEditor().putString(Prefs.GCM_REGISTRATION_TOKEN, regToken).apply();

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
        }


        /**
         * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
         * messages to your app.
         */
        private void sendRegistrationIdToBackend() {
            // Your implementation here.
        }


    }






}
