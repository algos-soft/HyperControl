package it.technocontrolsystem.hypercontrol.communication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;

/**
 * Created by alex on 5-07-2015.
 */
public class StatusButtonListener implements CompoundButton.OnClickListener {

    SiteActivity activity;

    public StatusButtonListener(SiteActivity activity) {
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {

        final CompoundButton button = (CompoundButton)v;

        if(button.isChecked()){// going ON


            Runnable successRunnable = new Runnable() {
                @Override
                public void run() {
                    activity.updateStatus();
                    button.setChecked(true);
                }
            };

            Runnable failRunnable = new Runnable() {
                @Override
                public void run() {
                    HyperControlApp.setConnection(null);
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
                    button.setChecked(false);
                    activity.getErrorButton().setVisibility(View.VISIBLE);
                }
            };

            activity.getErrorButton().setVisibility(View.GONE);
            new ConnectionTask(activity, activity.getSite(), successRunnable, failRunnable).execute();

        }else{ // going OFF

            HyperControlApp.setConnection(null);
            activity.getListAdapter().clearStatus();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getListAdapter().notifyDataSetChanged();
                }
            });
            button.setChecked(false);

        }

    }
}
