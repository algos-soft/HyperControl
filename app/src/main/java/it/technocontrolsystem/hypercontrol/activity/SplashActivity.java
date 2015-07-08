package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.service.DemoActivity;


public class SplashActivity extends Activity {
    private static final int SPLASH_DISPLAY_TIME=2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


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

                intent.setClass( SplashActivity.this,DemoActivity.class);
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
