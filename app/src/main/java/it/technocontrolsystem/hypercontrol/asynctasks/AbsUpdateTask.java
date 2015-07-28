package it.technocontrolsystem.hypercontrol.asynctasks;

import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.activity.HCSiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.model.ModelIF;

/**
 * Task per aggiornare lo stato dei dati visualizzati nell'adapter
 * Created by alex on 27-07-2015.
 */
public class AbsUpdateTask extends AsyncTask<Void, Integer, Exception> {

    private HCSiteActivity activity;
    private static String TAG="UpdateTask";
    private PowerManager.WakeLock lock;
    private Runnable successRunnable;
    private Runnable failRunnable;



    public AbsUpdateTask(HCSiteActivity activity, Runnable successRunnable, Runnable failRunnable) {
        this.activity=activity;
        this.successRunnable=successRunnable;
        this.failRunnable=failRunnable;
    }

    public AbsUpdateTask(HCSiteActivity activity) {
        this(activity, null, null);
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // blocca orientamento e wake
        Lib.lockOrientation(activity);
        lock = Lib.acquireWakeLock();

        // mostra il dialogo
        activity.progress.setMessage("aggiornamento stato...");
        activity.progress.setProgress(0);
        activity.progress.show();

    }


    @Override
    protected Exception doInBackground(Void... params) {
        Exception exception=null;

        Log.d(TAG, "start background process");

        try {

            //publishProgress(-2, activity.getListAdapter().getCount());

            // aggiorna lo stato
            if(Lib.isNetworkAvailable()){
                Connection conn = HyperControlApp.getConnection();
                boolean open=((conn!=null)&&(conn.isOpen()));
                if (open) {
                    for (int i = 0; i < activity.getListAdapter().getCount(); i++) {

                        if (!(isCancelled() | Thread.interrupted())) {
                            ModelIF model = (ModelIF) activity.getListAdapter().getItem(i);
                            activity.getListAdapter().updateByNumber(model.getNumber());
                            //publishProgress(-3, i + 1);
                        } else {
                            break;
                        }
                    }
                } else {
                    activity.getListAdapter().clearStatus();
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Log.d(TAG, "end background process");


        return exception;
    }



    @Override
    protected void onCancelled() {
        activity.progress.hide();
        Lib.unlockOrientation(activity);
        if (lock != null) {
            Lib.releaseWakeLock(lock);
        }
    }

    @Override
    protected void onPostExecute(Exception exception) {
        activity.progress.hide();

        if(exception==null){
            activity.getListAdapter().notifyDataSetChanged();
            if(successRunnable!=null){
                successRunnable.run();
            }
        }else{
            if(failRunnable!=null){
                failRunnable.run();
            }
        }

        Lib.unlockOrientation(activity);
        if (lock != null) {
            Lib.releaseWakeLock(lock);
        }
    }


    public void setSuccessRunnable(Runnable successRunnable) {
        this.successRunnable = successRunnable;
    }

    public void setFailRunnable(Runnable failRunnable) {
        this.failRunnable = failRunnable;
    }
}
