package it.technocontrolsystem.hypercontrol.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.activity.MainActivity;

/**
 * Registers the application with GCM servers asynchronously.
 * <p>
 * Stores the registration ID in the application's shared preferences.
 */
public class GCMRegisterTask extends AsyncTask<Void, Void, String> {

    private static final String TAG="GCMREG";

    @Override
    protected String doInBackground(Void... params) {
        String msg = "";
        try {
            Context context = HyperControlApp.getContext();
            String senderId= context.getString(R.string.gcm_defaultSenderId);
            InstanceID instanceID = InstanceID.getInstance(context);
            String regToken = instanceID.getToken(senderId,  GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            // You should send the registration ID to your server over HTTP, so it
            // can use GCM/HTTP or CCS to send messages to your app.
            String err=sendRegistrationIdToBackend(regToken);
            if (err==null) {

                // Persist the regID - no need to register again.
                Prefs.getEditor().putString(Prefs.GCM_REGISTRATION_TOKEN, regToken).apply();

            } else {
                msg = "Error : " + err;
            }

        } catch (IOException ex) {
            msg = "Error : " + ex.getMessage();
        }
        return msg;
    }



    @Override
    protected void onPostExecute(String s) {
        Activity act = Lib.getForegroundActivity();
        if(act!=null){

            if(s.equals("")){
                String msg="Registrazione GCM effettuata.";
                Toast toast=Toast.makeText(act, msg,Toast.LENGTH_LONG);
                toast.show();
                Log.d(TAG, "Successfully registered to GCM");
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setPositiveButton("Continua", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setTitle("Registrazione GCM fallita");
                builder.setMessage("Non verranno ricevute notifiche.\nErrore: " + s);
                builder.show();
                Log.d(TAG, "GCM registration failed: "+s);

            }

        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app.
     * @return null if the backend acknowledged the registration, or the error message
     */
    private String sendRegistrationIdToBackend(String regToken) {
        // Your implementation here.
        return null;
    }


}
