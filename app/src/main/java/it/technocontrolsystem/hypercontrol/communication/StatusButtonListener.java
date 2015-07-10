package it.technocontrolsystem.hypercontrol.communication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.widget.CompoundButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;

/**
 * Created by alex on 5-07-2015.
 */
public class StatusButtonListener implements CompoundButton.OnCheckedChangeListener {

    SiteActivity activity;

    public StatusButtonListener(SiteActivity activity) {
        this.activity=activity;
    }

    public void onCheckedChanged(final CompoundButton button, boolean isChecked) {
        if(isChecked){
            button.setOnCheckedChangeListener(null);
            button.setChecked(false);
            button.setOnCheckedChangeListener(this);

            Runnable successRunnable = new Runnable() {
                @Override
                public void run() {
                    activity.update();
                }
            };

            Runnable failRunnable = new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Errore di connessione");
                    builder.setMessage(Html.fromHtml(HyperControlApp.getLastConnectionError()));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                    button.setOnCheckedChangeListener(null);
                    button.setChecked(false);
                    button.setOnCheckedChangeListener(StatusButtonListener.this);

                }
            };

            new ConnectionTask(activity, activity.getSite(), successRunnable, failRunnable).execute();

        }else{
            HyperControlApp.setConnection(null);
            activity.getListAdapter().clearStatus();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getListAdapter().notifyDataSetChanged();
                }
            });

        }
    }

}
