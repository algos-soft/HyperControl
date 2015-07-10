package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
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
            new PopulateTask().execute();

        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // aggiorna i dati
        if(HyperControlApp.getConnection()!=null){
            new UpdateTask().execute();
        }

    }

    /**
     * AsyncTask per caricare i dati nell'adapter
     */
    class PopulateTask extends AbsPopulateTask {

        @Override
        public void populateAdapter() {
            Area[] areas = DB.getAreasByPlant(idPlant);
            publishProgress(-2, areas.length);

            AreaModel model;
            listAdapter.clear();
            int i = 0;
            for (final Area area : areas) {
                model = new AreaModel(area);
                listAdapter.add(model);
                i++;
                publishProgress(-3, i);
            }

        }

        @Override
        public String getType() {
            return "aree";
        }

    }

    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    class UpdateTask extends AbsUpdateTask {
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
