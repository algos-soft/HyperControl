package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateAreaOutputsTask;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.OutputListAdapter;

public class AreaOutputsActivity extends HCSiteActivity {
    private int idArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        this.idArea = getIntent().getIntExtra("areaid", 0);

        // carica i dati
        new PopulateAreaOutputsTask(this).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public AbsUpdateTask getUpdateTask() {
        return null;
    }


    public String getHeadline2(){
        return getArea().getPlant().getName();
    }

    public String getHeadline3(){
        return getArea().getName();
    }

    public String getItemsType(){return "Uscite";}

    public int getNumItemsInList() {
        return DB.getOutputCountByArea(getArea().getId());
    }

    public Site getSite() {
        return getArea().getPlant().getSite();
    }

    public Area getArea() {
        return DB.getArea(idArea);
    }

    public int getLiveCode() {
        return 0;
    }

    @Override
    public int getParamPlantNumCode() {
        return getArea().getPlant().getNumber();
    }

    @Override
    public int getParamAreaNumCode() {
        return getArea().getNumber();
    }




}
