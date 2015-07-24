package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.ListSensorsRequest;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.communication.StatusButtonListener;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.PlantDisplay;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.PlantListAdapter;
import it.technocontrolsystem.hypercontrol.model.PlantModel;

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

        if (idSite != 0) {

            setContentView(R.layout.activity_site);
            this.version = getIntent().getIntExtra("siteversion", 0);//federico

            // uso un inClickListener per poter gestire lo stato
            // senza invocare il checked change listener
            getConnectButton().setOnClickListener(new StatusButtonListener(this));

            // regola il bottone CONNECTED in base allo stato della connessione
            syncConnectButton();


            // attacca un listener al bottone di errore connessione
            getErrorButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorButtonClicked();
                }
            });

            // regola la visibilità del bottone di errore connessione
            // se c'è un errore di connessione lo visualizza se no lo rende invisibile
            if (HyperControlApp.getLastConnectionError()!=null) {
                getErrorButton().setVisibility(View.VISIBLE);
            }else {
                getErrorButton().setVisibility(View.GONE);
            }

            // crea l'adapter
            setListAdapter(new PlantListAdapter(SiteActivity.this));

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


            Log.d(TAG, "Connection is: " + HyperControlApp.getConnection());

            Log.d(TAG, "create + execute PopulateTask");

            // carica i dati
            populateTask = (AbsPopulateTask)new PopulateTask().execute();


            Button bTestReq=(Button)findViewById(R.id.bTestReq);
            bTestReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Request req = new ListSensorsRequest();
                    Response resp = HyperControlApp.sendRequest(req);
                    int a=87;
                }
            });



        } else {
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        // regola il bottone CONNECTED in base allo stato della connessione
        syncConnectButton();

        // aggiorna lo stato
        updateStatus();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // if not due to config change, close connection if present
        if (!isChangingConfigurations()){
            Connection conn=HyperControlApp.getConnection();
            if (conn!=null){
                conn.close();
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
    public void updateStatus() {
        Connection conn = HyperControlApp.getConnection();
        if ((conn != null) && (conn.isOpen())) {
            updateTask=(AbsUpdateTask)new UpdateTask().execute();
        }
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



    /**
     * AsyncTask per caricare i dati nell'adapter
     */
    class PopulateTask extends AbsPopulateTask {


        @Override
        public void populateAdapter() {
            Log.d(TAG, "Start populate adapter");
            Plant[] plants = DB.getPlants(idSite);
            publishProgress(-2, plants.length);
            PlantModel model;
            getListAdapter().clear();
            int i = 0;
            for (final Plant plant : plants) {
                model = new PlantModel(plant);
                getListAdapter().add(model);
                i++;
                publishProgress(-3, i);

                if (isCancelled()){
                    break;
                }

            }
            Log.d(TAG, "End populate adapter");
        }

        @Override
        public String getType() {
            return "impianti";
        }


    }


    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    class UpdateTask extends AbsUpdateTask {
    }


    /**
     * Sincronizza lo stato del bottone CONNECT in base allo stato della connessione
     */
    private void syncConnectButton(){
        Connection conn=HyperControlApp.getConnection();
        boolean open = (conn!=null) && (conn.isOpen());
        getConnectButton().setChecked(open);
    }


    private void errorButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore di connessione");
        builder.setMessage(Html.fromHtml(HyperControlApp.getLastConnectionError()));
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
