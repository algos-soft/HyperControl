package it.technocontrolsystem.hypercontrol.asynctasks;

import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.activity.HCActivity;
import it.technocontrolsystem.hypercontrol.activity.HCSiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;

/**
 * Task per caricare i dati dal db nell'adapter e aggiornare lo stato.
 * Assegna l'adapter alla ListView.
 * Popola l'adapter.
 * Al termina lancia un UpdateTask per aggiornare lo stato.
 */
public abstract class AbsPopulateTask extends AsyncTask<Void, Integer, Exception> {

    protected HCActivity activity;
    PowerManager.WakeLock lock;
    private Runnable successRunnable;
    private Runnable failRunnable;
    protected static String TAG = "PopulateTask";
    private HCListAdapter adapter;


    public AbsPopulateTask(HCActivity hcActivity,Runnable successRunnable,Runnable failRunnable) {
        this.activity = hcActivity;
        this.successRunnable=successRunnable;
        this.failRunnable=failRunnable;
    }

    public AbsPopulateTask(HCActivity activity) {
        this(activity, null, null);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // blocca orientamento e wake
        Lib.lockOrientation(activity);
        lock = Lib.acquireWakeLock();

        // mostra il dialogo
        activity.progress.setMessage("caricamento " + getType() + "...");
        activity.progress.setProgress(0);
        activity.progress.show();

    }

    @Override
    protected Exception doInBackground(Void... params) {

        Exception exception = null;
        Log.d(HCActivity.TAG, "PopulateTask - startBackground");

        try {

            // crea e assegna l'adapter
            adapter = createAdapter();
            activity.setListAdapter(adapter);

            // popola l'adapter
            populateAdapter();
            activity.getListAdapter().notifyDataSetChanged();

        } catch (Exception e1) {
            e1.printStackTrace();
            exception = e1;
        }

        Log.d(HCActivity.TAG, "PopulateTask - endBackground");

        return exception;
    }

    // crea e ritorna l'adapter
    public abstract HCListAdapter<?> createAdapter();


    // popola l'adapter della lista
    // da database o altra sorgente
    public abstract void populateAdapter();


    @Override
    protected void onProgressUpdate(Integer... values) {
        int param1 = 0, param2 = 0;
        param1 = values[0];
        if (values.length > 1) {
            param2 = values[1];
        }
        switch (param1) {

            case -2: {
                activity.progress.setMax(param2);
                break;
            }

            case -3: {
                activity.progress.setProgress(param2);
                break;
            }

        }

    }


    @Override
    protected void onPostExecute(Exception exception) {

        activity.progress.hide();

        if(exception == null){

            // assegna l'adapter alla ListView
            ListView view = activity.getListView();
            view.setAdapter(activity.getListAdapter());


            // se connesso attacca un live listener e lancia un update task
            if(HyperControlApp.isConnected()){

                // l'adapter attacca un live listener alla connessione
                adapter.attachLiveListenerToConnection();

                // lancia un comando di start live alla centrale
                try {
                    getHCSiteActivity().startLive();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // lancia un update task
                AbsUpdateTask updateTask = getHCSiteActivity().getUpdateTask();
                if(updateTask!=null){
                    updateTask.setSuccessRunnable(successRunnable);
                    updateTask.setFailRunnable(failRunnable);
                    updateTask.execute();
                }
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

    @Override
    protected void onCancelled() {
        activity.progress.hide();
        Lib.unlockOrientation(activity);
        if (lock != null) {
            Lib.releaseWakeLock(lock);
        }
    }


    /**
     * Ritorna il nome degli oggetti trattati
     */
    public abstract String getType();

    private HCSiteActivity getHCSiteActivity(){
        HCSiteActivity hcsa=null;
        if ((activity!=null) && (activity instanceof HCSiteActivity)){
            hcsa=(HCSiteActivity)activity;
        }
        return hcsa;
    }



}
