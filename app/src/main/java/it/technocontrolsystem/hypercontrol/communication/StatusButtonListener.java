package it.technocontrolsystem.hypercontrol.communication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.asynctasks.OpenConnectionTask;

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

        final ToggleButton button = (ToggleButton)v;

        if(button.isChecked()){// going ON

            button.setChecked(false);
            button.setTextOff(activity.getString(R.string.btn_conn_connecting)); // cambia provvisoriamente

            Runnable successRunnable = new Runnable() {
                @Override
                public void run() {

//                    postConnection();
//                    activity.updateStatus();
//                    try {
//                        activity.startLive();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    button.setTextOff(activity.getString(R.string.btn_conn_offline));  // rimette a posto in ogni caso
                    activity.syncConnectButton();
                }
            };

            Runnable failRunnable = new Runnable() {
                @Override
                public void run() {
                    Connection conn=HyperControlApp.getConnection();
                    if(conn!=null){
                        conn.close();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Errore di connessione");
                    String error=HyperControlApp.getLastConnectionError();
                    if (error!=null) {
                        builder.setMessage(Html.fromHtml(error));
                    }else{
                        builder.setMessage("Errore di connessione");
                    }
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    button.setTextOff(activity.getString(R.string.btn_conn_offline));  // rimette a posto in ogni caso
                    activity.getErrorButton().setVisibility(View.VISIBLE);
                }
            };

            activity.getErrorButton().setVisibility(View.GONE);

            OpenConnectionTask task = new OpenConnectionTask(activity, activity.getSite(), successRunnable, failRunnable);
            task.execute();

        }else{ // going OFF

            button.setChecked(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("Vuoi chiudere la connessione e andare offline?");
            builder.setPositiveButton("Chiudi\nconnessione",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Connection conn=HyperControlApp.getConnection();
                    if((conn!=null) && (conn.isOpen())){
                        conn.close();
                    }

                    activity.getListAdapter().clearStatus();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.getListAdapter().notifyDataSetChanged();
                        }
                    });
                    button.setChecked(false);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("Annulla",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();


        }

    }

//    /**
//     * Eseguito dopo l'attivazione della connessione.
//     * Controlla che il database sia sincronizzato.
//     */
//    private void postConnection(){
//
//        Runnable successRunnable = new Runnable() {
//            @Override
//            public void run() {
//                activity.populateAndUpdate();
//            }
//        };
//
//        Runnable failRunnable = new Runnable() {
//            @Override
//            public void run() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setMessage("Impossibile sincronizzare il database");
//                builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//            }
//        };
//
//        SyncSiteTask task = new SyncSiteTask(activity, activity.getSite(), successRunnable, failRunnable);
//        task.execute();
//
//    }

}
