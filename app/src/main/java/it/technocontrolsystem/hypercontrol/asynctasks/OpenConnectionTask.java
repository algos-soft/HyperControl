package it.technocontrolsystem.hypercontrol.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * AsyncTask to open an existing Connection.
 * Apre la connessione
 * lancia un SyncSiteTask per sincronizzare il database con la centrale
 *
 * Created by alex on 25-07-2015.
 */
public class OpenConnectionTask extends AsyncTask<Void, Void, Exception> {

    private SiteActivity activity;
    private Site site;
    private Runnable successRunnable;
    private Runnable failRunnable;
    private String uuid;
    private static String TAG = "OpenConnection";

    public OpenConnectionTask(SiteActivity activity, Site site, Runnable successRunnable, Runnable failRunnable) {
        this.activity=activity;
        this.site=site;
        this.successRunnable=successRunnable;
        this.failRunnable=failRunnable;
    }

    @Override
    protected Exception doInBackground(Void... params) {
        Exception exception=null;
        Connection conn = HyperControlApp.getConnection();
        if(conn!=null) {
            try {
                HyperControlApp.setLastConnectionError(null);
                conn.open();

                // recupera lo UUID della centrale dalla Connection e lo registra
                uuid = conn.getHpUUID();

            } catch (Exception e) {
                e.printStackTrace();
                exception=e;
            }
        }else{
            exception=new Exception("Connection is null");
        }
        return exception;
    }


    @Override
    protected void onPostExecute(Exception e) {
        if(e==null) {
            SyncSiteTask task = new SyncSiteTask(activity, site, uuid, successRunnable, failRunnable);
            task.execute();
        }else{

            // se c'è stata una eccezione registra l'errore
            if(e!=null){
                String err=e.getClass().getSimpleName()+"<p>"
                        +"<small>"+e.getMessage()+"</small>";
                HyperControlApp.setLastConnectionError(err);
                e.printStackTrace();
            }

            Log.e(TAG,e.getMessage());
            if(failRunnable!=null){
                failRunnable.run();
            }
        }
    }


}
