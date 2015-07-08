package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.model.AreaModel;
import it.technocontrolsystem.hypercontrol.model.ModelIF;


public class PlantActivity extends HCActivity {
    private int idPlant;
    boolean workingInBg=false;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        this.idPlant = getIntent().getIntExtra("plantid", 0);


        if (idPlant != 0) {

            progress = new ProgressDialog(this);

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
            publishProgress(1);

            try {

                /**
                 * Carica le aree dell'impianto dal DB nell'adapter
                 */
                Area[] areas = DB.getAreasByPlant(idPlant);
                AreaModel model;
                listAdapter.clear();
                for (final Area area : areas) {
                    model = new AreaModel(area);
                    listAdapter.add(model);
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            workingInBg = false;

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int code = values[0];
            if(code==1){    // background started
                Lib.lockOrientation(PlantActivity.this);
                lock = Lib.acquireWakeLock();
                progress.setMessage("caricamento aree...");
                progress.show();
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
            publishProgress(1);

            try {

                // aggiorna lo stato
                if (SiteActivity.getConnection() != null) {
                    listAdapter.update();
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
            int code = values[0];
            if(code==1){    // background started
                Lib.lockOrientation(PlantActivity.this);
                lock = Lib.acquireWakeLock();
                progress.setMessage("aggiornamento stato...");
                progress.show();
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

    private ListView getListView() {
        return (ListView) findViewById(R.id.list);
    }

    /**
     * Attende che si liberi il semaforo
     */
    private void waitForSemaphore() {
        while (workingInBg) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
