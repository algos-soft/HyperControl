/*
package it.technocontrolsystem.hypercontrol;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;



*/
/**
*     Build ProgressDialog: federico
*//*

public class ProgressBarDialog extends AsyncTask<Void, Void, Void> {
            //ProgressDialog dialog;
            Boolean finished;

    public ProgressBarDialog() {//Context PDContext) {
        //dialog=new ProgressDialog(PDContext);
    }

    @Override
        protected void onPreExecute() {
            //set message of the dialog
            //dialog.setCanceledOnTouchOutside(false);
            //dialog.setMessage("Loading...");
            //show dialog
            //dialog.show();
            super.onPreExecute();
        }

    @Override
    protected Void doInBackground(Void... arg0) {
            // do background work here
        while(!finished){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;

    }


    protected void onPostExecute(Void result) {
            // do UI work here
            //if (dialog != null && dialog.isShowing()) {
                //dialog.dismiss();
            //}

        }

}
*/
