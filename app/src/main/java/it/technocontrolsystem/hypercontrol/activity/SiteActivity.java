package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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
    private int idSite = 0;
    private int version = 0;//federico
    private static Activity activityA;
    private boolean loadingConfig;
    private boolean loadingSite;

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

            // regola il bottone CONNECTED in base allo stato della connessione
            getConnectButton().setChecked(getConnection()!=null);

            // attacca un listener e poi rende invisibile l'icona di errore connessione
            getErrorButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorButtonClicked();
                }
            });
            //getErrorButton().setVisibility(View.GONE);


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


            // carica i dati
            new PopulateTask().execute();

            // un listener notificato quando cambia lo stato della connessione
            HyperControlApp.addOnConnectionStatusChangedListener(new HyperControlApp.OnConnectionStatusChangedListener() {
                @Override
                public void connectionStatusChanged(Connection conn) {
                    getConnectButton().setChecked(conn!=null);
                }
            });

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

        // aggiorna lo stato
        update();
    }


    public void update(){
        new UpdateTask().execute();

    }


    /**
     * AsyncTask per caricare i dati nell'adapter
     */
    class PopulateTask extends AbsPopulateTask {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // devo aspettare che abbia finito di controllare / caricare la configurazione
            while (isLoadingConfig()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return super.doInBackground(params);
        }

        @Override
        public void populateAdapter() {
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
            int a = 87;
        }

        @Override
        public String getType() {
            return "impianti";
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setLoadingSite(false);
        }
    }


    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    class UpdateTask extends AbsUpdateTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            // se sta caricando il sito devo aspettare che abbia finito
            while (isLoadingSite()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return super.doInBackground(params);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


//    /**
//     * Task per aggiornare lo stato dalla centrale.
//     */
//    class UpdateTask extends AsyncTask<Void, Integer, Void> {
//
//        private PowerManager.WakeLock lock;
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            // attende che si liberi il semaforo
//            waitForSemaphore();
//            workingInBg = true;
//
//            // mostra il dialogo
//            publishProgress(-1);
//
//            try {
//
//                publishProgress(-2, listAdapter.getCount());
//
//                // aggiorna lo stato
//                if (SiteActivity.getConnection() != null) {
//                    for(int i=0;i<listAdapter.getCount();i++){
//                        PlantModel model = (PlantModel)listAdapter.getItem(i);
//                        listAdapter.updateByNumber(model.getNumber());
//                        publishProgress(-3,i+1);
//                    }
//                }
//
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//
//            // spegne il semaforo
//            workingInBg = false;
//
//            return null;
//        }
//
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            int param1=0, param2=0;
//            param1 = values[0];
//            if(values.length>1){
//                param2 = values[1];
//            }
//            switch (param1){
//                case -1:{
//                    Lib.lockOrientation(SiteActivity.this);
//                    lock = Lib.acquireWakeLock();
//                    progress.setMessage("aggiornamento stato...");
//                    progress.setProgress(0);
//                    progress.show();
//                    break;
//                }
//
//                case -2:{
//                    progress.setMax(param2);
//                    break;
//                }
//
//                case -3:{
//                    progress.setProgress(param2);
//                    break;
//                }
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            progress.dismiss();
//            listAdapter.notifyDataSetChanged();
//            Lib.unlockOrientation(SiteActivity.this);
//            Lib.releaseWakeLock(lock);
//        }
//    }
//





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

    public boolean isLoadingConfig() {
        return loadingConfig;
    }

    public void setLoadingConfig(boolean loadingConfig) {
        this.loadingConfig = loadingConfig;
    }


    public synchronized boolean isLoadingSite() {
        return loadingSite;
    }

    public synchronized void setLoadingSite(boolean loadingSite) {
        this.loadingSite = loadingSite;
    }

    public static Activity getInstance() {
        return activityA;
    }
}
