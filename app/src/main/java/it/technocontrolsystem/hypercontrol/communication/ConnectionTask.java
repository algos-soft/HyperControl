package it.technocontrolsystem.hypercontrol.communication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.view.View;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.activity.StartSiteActivity;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * AsyncTask per effettuare una nuova connessione e registrarla nella App.<br>
 * All'inizio mette subito la Connection a null.<br>
 * Se termina con successo, registra la nuova Connection e lancia
 * una SyncSiteTask (aggiornamento database).<br>
 * Se fallisce, registra l'errore e lancia la SiteActivity().
 * Created by alex on 5-07-2015.
 */
public class ConnectionTask extends AsyncTask<Void, Void, Connection > {

    private Context context;
    private Site site;
    private boolean success;
    private ProgressDialog progress;
    private PowerManager.WakeLock lock;
    private Runnable successRunnable;
    private Runnable failRunnable;

    public ConnectionTask(Context context, Site site, Runnable successRunnable, Runnable failRunnable) {
        this.context=context;
        this.site=site;
        this.successRunnable=successRunnable;
        this.failRunnable=failRunnable;
    }

    @Override
    protected void onPreExecute() {
        lock=Lib.acquireWakeLock();
        HyperControlApp.setLastConnectionError(null);
        progress = ProgressDialog.show(context, null, "connessione in corso...", true);
    }


    @Override
    protected Connection doInBackground(Void... params) {
        Connection conn=null;
        success=false;

        Exception exc=null;
        String error="";

        try{

            HyperControlApp.setLastConnectionError(null);

            conn = new Connection(site);
            success=true;

        }catch(NetworkUnavailableException e){
            exc=e;
            error="Il dispositivo non è connesso";
        }catch(SocketTimeoutException e){
            exc=e;
            error="Timeout nella creazione del socket TCP/IP";
        }catch(SocketException e){
            exc=e;
            error="Errore nella creazione del socket TCP/IP";
        }catch(Exception e){
            exc=e;
            error="Errore di connessione";
        }

        // se c'è stata una eccezione registra l'errore
        if(exc!=null){
            String err="<b>"+error+"</b><p>"
                    +exc.getClass().getSimpleName()+"<p>"
                    +"<small>"+exc.getMessage()+"</small>";
            HyperControlApp.setLastConnectionError(err);
            success=false;
            exc.printStackTrace();
        }

        return conn;
    }


    @Override
    protected void onPostExecute(Connection conn) {
        progress.dismiss();

        if(success) {
            HyperControlApp.setConnection(conn);
            if(successRunnable!=null){
                successRunnable.run();
            }
        }else{
            HyperControlApp.setConnection(null);
            if(failRunnable!=null){
                failRunnable.run();
            }
        }

        Lib.releaseWakeLock(lock);

    }

}
