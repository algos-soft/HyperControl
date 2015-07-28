package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateAreaSensorsTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateAreaTask;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.SensorCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.model.SensorModel;


public class AreaSensorsActivity extends HCSiteActivity {
    private int idArea;
    private SensorModel sensMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        this.idArea = getIntent().getIntExtra("areaid", 0);

        // carica i dati
        new PopulateAreaSensorsTask(this).execute();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public AbsUpdateTask getUpdateTask() {
        return new UpdateAreaTask(this);
    }


    public String getHeadline2(){
        return getArea().getPlant().getName();
    }

    public String getHeadline3(){
        return getArea().getName();
    }

    public String getItemsType(){return "Sensori";}

    @Override
    public int getNumItemsInList() {
        return DB.getSensorCountByArea(getArea().getId());
    }



    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
            menu.setHeaderTitle(" " + sensMod.getSensor().getName());

            if (sensMod.getStatus() == 1) {
                menu.getItem(0).setTitle("Disabilita");
            }

            if (sensMod.isTest()) {
                menu.getItem(1).setTitle("Disattiva Test");
            }


        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int stato = sensMod.getStatus();
        int num = sensMod.getNumber();
        boolean test = sensMod.isTest();
        Request req;


        switch (item.getItemId()) {
            case R.id.Abilita:
                if (stato == 0) {
                    req = new SensorCommandRequest(num, 4, true);
                    Toast.makeText(this, "Abilitato", Toast.LENGTH_LONG).show();
                    HyperControlApp.sendRequest(req);
                } else {
                    req = new SensorCommandRequest(num, 4, false);
                    Toast.makeText(this, "Disabilitato", Toast.LENGTH_LONG).show();
                    HyperControlApp.sendRequest(req);
                }
                // add stuff here
                return true;

            case R.id.Test:
                if (!test) {
                    req = new SensorCommandRequest(num, 5, true);
                    Toast.makeText(this, "Test attivato", Toast.LENGTH_LONG).show();
                    HyperControlApp.sendRequest(req);
                } else {
                    req = new SensorCommandRequest(num, 5, false);
                    Toast.makeText(this, "Test disattivato", Toast.LENGTH_LONG).show();
                    HyperControlApp.sendRequest(req);
                }
                // edit stuff here
                return true;
            case R.id.Cancella:
                Toast.makeText(this, "Cancellato", Toast.LENGTH_LONG).show();
                // remove stuff here
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    public Site getSite() {
        return getArea().getPlant().getSite();
    }

    public Area getArea() {
        return DB.getArea(idArea);
    }


    public int getLiveCode() {
        return 3;
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
