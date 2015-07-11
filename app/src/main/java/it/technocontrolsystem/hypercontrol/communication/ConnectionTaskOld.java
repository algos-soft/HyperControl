package it.technocontrolsystem.hypercontrol.communication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.PowerManager;

import it.technocontrolsystem.hypercontrol.activity.SiteActivity;

/**
 * AsyncTask per effettuare una nuova connessione.<br>
 * All'inizio mette subito la Connection a null nella activity.<br>
 * Se termina con successo, registra la nuova Connection nella activity.<br>
 * Se la Connection viene creata con successo, aggiorna l'indicatore di
 * stato della connessione nella Activity e lancia una SyncSiteTask (aggiornamento database)<br>
 * Se fallisce, registra l'eccezione nella activity e invoca updateSiteUI().
 * Created by alex on 5-07-2015.
 */
public class ConnectionTaskOld extends AsyncTask<Void, Void, Connection > {

    private  SiteActivity activity;
    private boolean success;
    private ProgressDialog progress;
    private PowerManager.WakeLock lock;


    public ConnectionTaskOld(SiteActivity activity) {
        this.activity=activity;
    }

//    @Override
//    protected void onPreExecute() {
//
//        // protegge il workflow seguente da cambio orientamento,
//        // verrà riattivato al temine dei background tasks
//        Lib.lockOrientation(activity);
//        lock=Lib.acquireWakeLock();
//
//
//        activity.setConnection(null);
//        activity.setLastConnectionError(null);
//        activity.getErrorButton().setVisibility(View.GONE);
//
//        progress = ProgressDialog.show(activity, null, "connessione in corso...", true);
//    }


    @Override
    protected Connection doInBackground(Void... params) {
        Connection conn=null;
//        success=false;
//
//        Exception exc=null;
//        String error="";
//
//        try{
//
//            conn = new Connection(activity.getSite());
//            success=true;
//
//        }catch(NetworkUnavailableException e){
//            exc=e;
//            error="Il dispositivo non è connesso";
//        }catch(SocketTimeoutException e){
//            exc=e;
//            error="Timeout nella creazione del socket TCP/IP";
//        }catch(SocketException e){
//            exc=e;
//            error="Errore nella creazione del socket TCP/IP";
//        }catch(Exception e){
//            exc=e;
//            error="Errore di connessione";
//        }
//
//        // se c'è stata una eccezione registra l'errore nella activity
//        if(exc!=null){
//            String err="<b>"+error+"</b><p>"
//                    +exc.getClass().getSimpleName()+"<p>"
//                    +"<small>"+exc.getMessage()+"</small>";
//            activity.setLastConnectionError(err);
//            success=false;
//            exc.printStackTrace();
//        }

        return conn;
    }


//    @Override
//    protected void onPostExecute(Connection conn) {
//        progress.dismiss();
//        if(success) {
//
//            // registra la Connection
//            activity.setConnection(conn);
//
//            // accendi il bottone CONNECTED senza lanciare un evento
//            // rimuove il listener e dopo lo rimette
//            activity.getConnectButton().setOnCheckedChangeListener(null);
//            activity.getConnectButton().setChecked(true);
//
//
//            // lancia la sincronizzazione DB
//            new SyncSiteTask(activity).execute();
//
//
//        }else{
//
//            // spegne semaforo
//            activity.setLoadingConfig(false);
//
//            // torna alla UI
//            activity.updateSiteUI();
//        }
//
//        activity.getConnectButton().setOnCheckedChangeListener(new StatusButtonListener(activity));
//
//        Lib.releaseWakeLock(lock);
//
//
//
//    }
//
}
