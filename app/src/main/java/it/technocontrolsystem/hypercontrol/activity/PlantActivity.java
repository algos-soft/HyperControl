package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.model.AreaModel;


public class PlantActivity extends HCActivity {
    private int idPlant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        this.idPlant = getIntent().getIntExtra("plantid", 0);


        if (idPlant != 0) {

            //progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);

            loadPlantTitle();

            // crea l'adapter per la ListView
            listAdapter = new AreaListAdapter(PlantActivity.this);

            // attacca un click listener alla ListView
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AreaDisplay display = (AreaDisplay) view;
                    Intent intent = new Intent();
                    intent.setClass(PlantActivity.this, AreaActivity.class);
                    intent.putExtra("areaid", display.getAreaId());
                    startActivity(intent);
                }
            });

            // carica i dati
            PopulateTask task = new PopulateTask();
            task.execute();


        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // aggiorna le aree (se sta ancora caricando, aspetta in background)
        UpdateTask task = new UpdateTask();
        task.execute();

    }


    /**
     * Task per caricare i dati dal db nell'adapter.
     * Dopo aver caricato i dati assegna l'adapter alla ListView.
     */
    class PopulateTask extends AsyncTask<Void, Integer, Void> {

        private PowerManager.WakeLock lock;

        @Override
        protected Void doInBackground(Void... params) {

            // attende che si liberi il semaforo
            waitForSemaphore();
            workingInBg = true;

            // mostra il dialogo
            publishProgress(-1);

            try {

                /**
                 * Carica gli elementi dal DB nell'adapter
                 */
                Area[] areas = DB.getAreasByPlant(idPlant);
                publishProgress(-2, areas.length);

                AreaModel model;
                listAdapter.clear();
                int i=0;
                for (final Area area : areas) {
                    model = new AreaModel(area);
                    listAdapter.add(model);
                    i++;
                    publishProgress(-3,i);
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            workingInBg = false;

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int param1=0, param2=0;
            param1 = values[0];
            if(values.length>1){
                param2 = values[1];
            }
            switch (param1){
                case -1:{
                    Lib.lockOrientation(PlantActivity.this);
                    lock = Lib.acquireWakeLock();
                    progress.setMessage("caricamento aree...");
                    progress.setProgress(0);
                    progress.show();
                    break;
                }

                case -2:{
                    progress.setMax(param2);
                    break;
                }

                case -3:{
                    progress.setProgress(param2);
                    break;
                }

            }

        }



        @Override
        protected void onPostExecute(Void aVoid) {

            progress.dismiss();
            getListView().setAdapter(listAdapter);
            Lib.unlockOrientation(PlantActivity.this);
            Lib.releaseWakeLock(lock);
        }
    }

    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    class UpdateTask extends AsyncTask<Void, Integer, Void> {

        private PowerManager.WakeLock lock;

        @Override
        protected Void doInBackground(Void... params) {

            // attende che si liberi il semaforo
            waitForSemaphore();
            workingInBg = true;

            // mostra il dialogo
            publishProgress(-1);

            try {

                publishProgress(-2, listAdapter.getCount());

                // aggiorna lo stato
                if (SiteActivity.getConnection() != null) {
                    for(int i=0;i<listAdapter.getCount();i++){
                        AreaModel model = (AreaModel)listAdapter.getItem(i);
                        ((AreaListAdapter)listAdapter).update(model.getNumber());
                        publishProgress(-3,i+1);
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // spegne il semaforo
            workingInBg = false;

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            int param1=0, param2=0;
            param1 = values[0];
            if(values.length>1){
                param2 = values[1];
            }
            switch (param1){
                case -1:{
                    Lib.lockOrientation(PlantActivity.this);
                    lock = Lib.acquireWakeLock();
                    progress.setMessage("aggiornamento stato...");
                    progress.setProgress(0);
                    progress.show();
                    break;
                }

                case -2:{
                    progress.setMax(param2);
                    break;
                }

                case -3:{
                    progress.setProgress(param2);
                    break;
                }

            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            listAdapter.notifyDataSetChanged();
            Lib.unlockOrientation(PlantActivity.this);
            Lib.releaseWakeLock(lock);
        }
    }


    private void loadPlantTitle() {
        TextView text = (TextView) findViewById(R.id.title);
        text.setText(getSite().getName() + " - " + getPlant().getName());
    }

    private Plant getPlant() {
        return DB.getPlant(idPlant);
    }

    public Site getSite() {
        return getPlant().getSite();
    }



    public int getLiveCode() {
        return 2;
    }

    @Override
    public int getParamPlantNumCode() {
        return getPlant().getNumber();
    }

    @Override
    public int getParamAreaNumCode() {
        return -1;
    }


}
