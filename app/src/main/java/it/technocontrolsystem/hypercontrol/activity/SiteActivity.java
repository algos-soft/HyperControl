package it.technocontrolsystem.hypercontrol.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.ConnectionTask;
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
    private int idSite = 0;
    private int version = 0;//federico
    private static Connection conn = null;
    private static Activity activityA;

    private String lastConnectionError;  // ultima eccezione nella fase di connection
    private String lastSyncDBError;// ultima eccezione nella fase di sync db


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.idSite = getIntent().getIntExtra("siteid", 0);

        if (idSite != 0) {

            setContentView(R.layout.activity_site);
            this.version = getIntent().getIntExtra("siteversion", 0);//federico
            activityA = this;

            // titolo della schermata
            TextView text = (TextView) findViewById(R.id.title);
            text.setText(getSite().getName());

            // regola il bottone CONNECTED in base alla connessione
            getConnectButton().setChecked(conn!=null);

            // attacca un listener e poi rende invisibile l'icona di errore connessione
            getErrorButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorButtonClicked();
                }
            });
            getErrorButton().setVisibility(View.GONE);


            // crea l'adapter per la ListView e lo assegna
            listAdapter = new PlantListAdapter(SiteActivity.this);
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(listAdapter);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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


            /**
             * tenta la sincronizzazione, ma solo la prima volta
             * che l'activity viene creata, e non dopo una rotazione.
             */
            if (savedInstanceState == null) {   // nuova activity
                if (Lib.isNetworkAvailable()) {

                    // l'esecuzione di questo task determina sempre in un modo o nell'altro
                    // una chiamata a updateSiteUI() - vedi SiteActivity workflow
                    ConnectionTask task = (new ConnectionTask(this));
                    task.execute();

                } else {    // creazione post rotazione
                    updateSiteUI();
                }
            } else {    // niente connessione
                updateSiteUI();
            }


        } else {
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        // questo listener va aggiunto in onResume - in onCreate Android cerca di ripristinare lo stato
        // e se era ON invoca il listener. Ricordati che questo listener viene rimosso appena premi
        // il bottone e poi riattaccato alla fine di ConnectionTask
        getConnectButton().setOnCheckedChangeListener(new StatusButtonListener(this));
    }


    /**
     * Aggiorna la UI in base allo stato corrente
     */
    public void updateSiteUI() {

        Lib.unlockOrientation(this);    // da qui in poi sblocca l'orientamento

        // controlla l'errore connessione
        if(lastConnectionError!=null) {
            getErrorButton().setVisibility(View.VISIBLE);
        }else{
            getErrorButton().setVisibility(View.GONE);
        }

        // popola l'adapter per la ListView dal database
        populateAdapter();

        // Se la connessione è attiva, aggiorna lo stato dalla centrale
        if(conn!=null){
            listAdapter.update();
        }

    }


    private void errorButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore di connessione");
        builder.setMessage(Html.fromHtml(lastConnectionError));
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("errbuttonvisibility",getErrorButton().getVisibility()==View.VISIBLE);
        outState.putString("lastConnectionError",lastConnectionError);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.getBoolean("errbuttonvisibility")){
            getErrorButton().setVisibility(View.VISIBLE);
        }else{
            getErrorButton().setVisibility(View.GONE);
        }
        lastConnectionError=savedInstanceState.getString("lastConnectionError");
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
        return conn;
    }

    public static void setConnection(Connection conn) {
        SiteActivity.conn = conn;
    }


    public String getLastConnectionError() {
        return lastConnectionError;
    }

    public void setLastConnectionError(String lastConnectionError) {
        this.lastConnectionError = lastConnectionError;
    }

    public String getLastSyncDBError() {
        return lastSyncDBError;
    }

    public void setLastSyncDBError(String lastSyncDBError) {
        this.lastSyncDBError = lastSyncDBError;
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

    public static Activity getInstance() {
        return activityA;
    }
}
