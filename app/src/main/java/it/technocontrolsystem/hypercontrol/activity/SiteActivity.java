package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.ConnectionTaskOld;
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

public class SiteActivity extends HCActivity {
    private static final String TAG="SiteActivity";
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
//            activityA = this;

            // titolo della schermata
            TextView text = (TextView) findViewById(R.id.title);
            text.setText(getSite().getName());

            // uso un inClickListener per poter gestire lo stato senza invocare il listener
            getConnectButton().setOnClickListener(new StatusButtonListener(this));

            // regola il bottone CONNECTED in base allo stato della connessione
            getConnectButton().setChecked(getConnection()!=null);


            // attacca un listener e poi rende invisibile l'icona di errore connessione
            getErrorButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorButtonClicked();
                }
            });
            getErrorButton().setVisibility(View.GONE);


            // crea l'adapter
            listAdapter = new PlantListAdapter(SiteActivity.this);

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

            Button menu = (Button) findViewById(R.id.menu);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("siteid", idSite);
                    intent.setClass(SiteActivity.this, MenuActivity.class);
                    startActivity(intent);

                }
            });

            Log.d(TAG, "Connection is: "+HyperControlApp.getConnection());

            Log.d(TAG, "create + execute PopulateTask");

            // carica i dati
            new PopulateTask().execute();

//            // un listener notificato quando cambia lo stato della connessione
//            HyperControlApp.addOnConnectionStatusChangedListener(new HyperControlApp.OnConnectionStatusChangedListener() {
//                @Override
//                public void connectionStatusChanged(Connection conn) {
//                    getConnectButton().setChecked(conn!=null);
//                }
//            });

        } else {
            finish();
        }

    }



    @Override
    protected void onResume() {
        super.onResume();

        // aggiorna lo stato
        update();
    }


    public void update(){
        if(getConnection()!=null){
            new UpdateTask().execute();
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
            listAdapter.clear();
            int i = 0;
            for (final Plant plant : plants) {
                model = new PlantModel(plant);
                listAdapter.add(model);
                i++;
                publishProgress(-3, i);
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



    private void errorButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore di connessione");
        builder.setMessage(Html.fromHtml(HyperControlApp.getLastConnectionError()));
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("errbuttonvisibility",getErrorButton().getVisibility()==View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.getBoolean("errbuttonvisibility")){
            getErrorButton().setVisibility(View.VISIBLE);
        }else{
            getErrorButton().setVisibility(View.GONE);
        }
    }


    /**
     * Carica gli impianti del sito dal DB nell'adapter
     */
    private void populateAdapter() {
        listAdapter.clear();
        Plant[] plants = DB.getPlants(idSite);
        PlantModel model;
        for (final Plant plant : plants) {
            model = new PlantModel(plant);
            listAdapter.add(model);
        }
    }


    public PlantListAdapter getListAdapter(){
        return (PlantListAdapter)listAdapter;
    }


    public Site getSite() {
        return DB.getSite(idSite);
    }


    public ToggleButton getConnectButton(){
        return (ToggleButton)findViewById(R.id.connect_button);
    }

    public ImageButton getErrorButton(){
        return (ImageButton)findViewById(R.id.statusAlertButton);

    }

    public static Connection getConnection() {
        return HyperControlApp.getConnection();
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

//    public static Activity getInstance() {
//        return activityA;
//    }
}
