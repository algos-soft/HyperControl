package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;


public class SplashActivity extends Activity {
    private static final int SPLASH_DISPLAY_TIME=2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // inizializza le variabili del Singleton (Application)
        HyperControlApp.init();

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



/*        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        final int SUCCESS=0;
                        final int SERVICE_MISSING=1;
                        final int SERVICE_VERSION_UPDATE_REQUIRED=2;
                        final int SERVICE_DISABLED=3;

                        int code =result.getErrorCode();
        //controlla che sia presente il servizio google play necessario per le notifiche push,qualora non lo fosse gestire i vari if
                        if(code==SUCCESS){
                            int a=0;
                        }else if(code==SERVICE_MISSING){
                            int a=1;
                        }else if(code==SERVICE_VERSION_UPDATE_REQUIRED){
                            int a=2;
                        }else if(code==SERVICE_DISABLED){
                            int a=3;
                        }else{
                            int a=9;
                        }


                    }
                })
                .build();

        mGoogleApiClient.connect();*/

        new Handler().postDelayed(new Runnable() {
            public void run() {

                Intent intent = new Intent();

                intent.setClass(SplashActivity.this, LoginActivity.class);
                //intent.setClass(SplashActivity.this, AuthActivityOld.class);

                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();

                // transition from splash to main menu
                // overridePendingTransition(R.animate.activityfadein,
                //       R.animate.splashfadeout);

            }
        }, SPLASH_DISPLAY_TIME);


    }



}
