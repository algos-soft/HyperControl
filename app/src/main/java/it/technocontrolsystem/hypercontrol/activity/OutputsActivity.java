package it.technocontrolsystem.hypercontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateOutputsTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateBoardTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateOutputsTask;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.display.OutputDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * List of all Outputs for one site
 */
public class OutputsActivity extends HCSiteActivity {

    private int idSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        this.idSite = getIntent().getIntExtra("siteid", 0);

        // attacca un click listener alla ListView
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OutputDisplay display = (OutputDisplay) view;
                Intent intent = new Intent();
                intent.setClass(OutputsActivity.this, OutputDetailActivity.class);
                intent.putExtra("itemid", display.getItemId());
                startActivity(intent);
            }
        });

        // carica i dati
        new PopulateOutputsTask(this).execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public AbsUpdateTask getUpdateTask() {
        return new UpdateOutputsTask(this);
    }


    public String getHeadline2(){
        return "Lista uscite";
    }

    public String getHeadline3(){
        return null;
    }

    public String getItemsType(){return "Uscite";}

    public int getNumItemsInList() {
        return DB.getOutputsCountBySite(getSite().getId());
    }

    public int getLiveCode() {
        return 0;
    }   // @todo da assegnare!!

    @Override
    public Site getSite() {
        return DB.getSite(idSite);
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
