package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.model.AreaModel;


public class PlantActivity extends HCActivity {
    private int idPlant;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_plant);
//
//        this.idPlant = getIntent().getIntExtra("plantid", 0);
//
//
//        if (idPlant != 0) {
//
//
//            loadPlantTitle();
//            // crea l'adapter per la ListView
//            listAdapter = new AreaListAdapter(PlantActivity.this, getPlant().getNumber());
//            ActivityTask task = new ActivityTask();
//            task.execute();
//
//        } else {
//            finish();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_plant);
        this.idPlant = getIntent().getIntExtra("plantid", 0);


        if (idPlant != 0) {
            loadPlantTitle();

            // crea l'adapter per la ListView
            listAdapter = new AreaListAdapter(PlantActivity.this, getPlant().getNumber());
            ActivityTask task = new ActivityTask();
            task.execute();

        } else {
            finish();
        }
    }

    class ActivityTask extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(PlantActivity.this, "Caricamento aree", "caricamento...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {


                // popola l'adapter per la ListView
                populateAdapter();

                // aggiorna lo stato
                if(SiteActivity.getConnection()!=null){
                    publishProgress(-1);
                    listAdapter.update();
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            int num = values[0];
            if (num==-1){   // started update
                progress.setMessage("aggiornamento stato...");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // assegna l'adapter alla ListView
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(listAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AreaDisplay display = (AreaDisplay) view;
                    Intent intent = new Intent();
                    intent.setClass(PlantActivity.this, AreaActivity.class);
                    intent.putExtra("areaid", display.getAreaId());
                    startActivity(intent);
                }
            });

            progress.dismiss();
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

    /**
     * Carica le aree dell'impianto dal DB nell'adapter
     */
    private void populateAdapter() {
        Area[] areas = DB.getAreasByPlant(idPlant);
        AreaModel model;
        for (final Area area : areas) {
            model = new AreaModel(area);
            listAdapter.add(model);
        }
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
