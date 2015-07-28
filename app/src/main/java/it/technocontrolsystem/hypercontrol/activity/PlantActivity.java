package it.technocontrolsystem.hypercontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulatePlantTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdatePlantTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateSiteTask;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;


public class PlantActivity extends HCSiteActivity {
    private int idPlant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        this.idPlant = getIntent().getIntExtra("plantid", 0);

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
        new PopulatePlantTask(this).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        // aggiorna i dati
//        updateStatus();

    }

    @Override
    public AbsUpdateTask getUpdateTask() {
        return new UpdatePlantTask(this);
    }


    public String getHeadline2(){
        return getPlant().getName();
    }

    public String getHeadline3(){
        return null;
    }

    public String getItemsType(){return "Aree";}


    public int getNumItemsInList() {
        return DB.getAreasCountByPlant(getPlant().getId());
    }



    public Plant getPlant() {
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
