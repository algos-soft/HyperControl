package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.OpenConnectionTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateSiteTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateSiteTask;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.StatusButtonListener;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.PlantDisplay;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * Activity to display a single site
 */

public class SiteActivity extends HCSiteActivity {
    private static final String TAG = "SiteActivity";
    private int idSite = 0;
    private int version = 0;//federico
    //    private static Activity activityA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "SiteActivity - onCreate()");

        this.idSite = getIntent().getIntExtra("siteid", 0);

        setContentView(R.layout.activity_site);
        this.version = getIntent().getIntExtra("siteversion", 0);//federico


        // se nuova istanza, crea una nuova Connection e la registra (non la apre per ora)
        if(isNewInstance()){
            HyperControlApp.setConnection(new Connection(getSite()));
        }

        // uso un inClickListener per poter gestire lo stato
        // senza invocare il checked change listener
        getConnectButton().setOnClickListener(new StatusButtonListener(this));

        // regola il bottone CONNECTED in base allo stato della connessione
        syncConnectButton();

        // listener sul bottone di errore connessione
        getErrorButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorButtonClicked();
            }
        });

        // regola la visibilità del bottone di errore connessione
        // se c'è un errore di connessione registrato, lo visualizza se no lo rende invisibile
        if (HyperControlApp.getLastConnectionError()!=null) {
            getErrorButton().setVisibility(View.VISIBLE);
        }else {
            getErrorButton().setVisibility(View.GONE);
        }

        // listener per il clic sugli item della ListView
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlantDisplay display = (PlantDisplay) view;
                Intent intent = new Intent();
                intent.setClass(SiteActivity.this, PlantActivity.class);
                intent.putExtra("plantid", display.getPlantId());
                startActivity(intent);
            }
        });


//        Log.d(TAG, "Connection is: " + HyperControlApp.getConnection());

//        Log.d(TAG, "create + execute PopulateTask");

//            // aggiunge un listener alla connessione
//            Connection conn = HyperControlApp.getConnection();
//            if (conn!=null){
//                conn.addOnConnectionStatusChangedListener(new Connection.OnConnectionStatusChangedListener() {
//                    @Override
//                    public void connectionOpened(Connection conn) {
//                        connectivityHasChanged(true);
//                    }
//
//                    @Override
//                    public void connectionClosed(Connection conn) {
//                        connectivityHasChanged(false);
//                    }
//                });
//            }


        if(isNewInstance()) {

            if(Lib.isNetworkAvailable()) {
                try{

                    Runnable successRunnable = new Runnable() {
                        @Override
                        public void run() {
                            syncConnectButton();
                        }
                    };

                    // apre la connessione, sincronizza il db, popola l'adapter, aggiorna lo stato
                    OpenConnectionTask task = new OpenConnectionTask(this, getSite(), successRunnable, null);
                    task.execute();

                }catch(Exception e){

                    // dialogo errore connessione
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Errore di connessione");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", null);
                    builder.show();

                    // popola comunque l'adapter
                    new PopulateSiteTask(this).execute();
                }
            }else{  // new instance, network unavailable
                // popola comunque l'adapter
                new PopulateSiteTask(this).execute();
            }

        }else{  // config change
            new PopulateSiteTask(this).execute();
        }



    }


    @Override
    protected void onResume() {
        super.onResume();

        // regola il bottone CONNECTED in base allo stato della connessione
        syncConnectButton();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // if not due to config change, close connection if present
        if (!isChangingConfigurations()){
            Connection conn=HyperControlApp.getConnection();
            if (conn!=null){
                conn.close();
                HyperControlApp.setConnection(null);
            }
        }

    }

    public String getHeadline2(){
        return "Lista impianti";
    }

    public String getHeadline3(){
        return null;
    }

    public String getItemsType(){return "Impianti";}

    @Override
    public int getNumItemsInList() {
        return DB.getPlantsCountBySite(getSite().getId());
    }

    @Override
    public AbsUpdateTask getUpdateTask() {
        return new UpdateSiteTask(this);
    }

    /**
     * Invocato quando cambia lo stato della connettività del device
     */
    public void connectivityHasChanged(boolean newStatus){
        super.connectivityHasChanged(newStatus);

        // se spento, qui in più sincronizza il pulsante
        if(!newStatus){
            syncConnectButton();
        }
    }



//    /**
//     * Esegue populate e quando ha finito esegue update
//     */
//    class PopulateAndUpdateTask extends PopulateSiteTask {
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            UpdateTask uTask = new UpdateTask();
//            uTask.execute();
//        }
//    }


//    public void populateAndUpdate(){
//        PopulateAndUpdateTask task = new PopulateAndUpdateTask();
//        task.execute();
//    }


    /**
     * Sincronizza lo stato del bottone CONNECT in base allo stato della connessione
     */
    public void syncConnectButton(){
        Connection conn=HyperControlApp.getConnection();
        boolean open = (conn!=null) && (conn.isOpen());
        getConnectButton().setChecked(open);
    }


    private void errorButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore di connessione");
        String err=HyperControlApp.getLastConnectionError();
        if(err!=null){
            builder.setMessage(Html.fromHtml(err));
        }else{
            builder.setMessage("Errore di connessione");
        }
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("errbuttonvisibility", getErrorButton().getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean("errbuttonvisibility")) {
            getErrorButton().setVisibility(View.VISIBLE);
        } else {
            getErrorButton().setVisibility(View.GONE);
        }
    }



    public Site getSite() {
        return DB.getSite(idSite);
    }


    public ToggleButton getConnectButton() {
        return (ToggleButton) findViewById(R.id.connect_button);
    }

    public ImageButton getErrorButton() {
        return (ImageButton) findViewById(R.id.statusAlertButton);

    }

    public int getLiveCode() {
        return 1;
    }

    @Override
    public int getParamPlantNumCode() {
        return -1;
    }

    @Override
    public int getParamAreaNumCode() {
        return -1;
    }

}
