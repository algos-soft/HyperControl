package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateAreaSensorsTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateAreaTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateAreaTask;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.SensorCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.SensorDisplay;
import it.technocontrolsystem.hypercontrol.display.SiteDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Created by alex on 28-07-2015.
 */
public class AreaActivity extends HCSiteActivity {
    private int idArea;
    public int mode;

    public static final int MODE_SENSORS = 1;
    public static final int MODE_OUTPUTS = 2;

    private SensorModel sensMod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mode = MODE_SENSORS;

        setContentView(R.layout.activity_area);

        SwitchCompat switchItem = (SwitchCompat) findViewById(R.id.contentSwitch);
//        switchItem.setTextOn("Sensori");
//        switchItem.setTextOn("Uscite");
        switchItem.setShowText(true);
        switchItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {// going ON (Outputs)
                    mode = AreaActivity.MODE_OUTPUTS;
                } else { // going OFF (Sensors)
                    mode = AreaActivity.MODE_SENSORS;
                }


                new PopulateAreaTask(AreaActivity.this).execute();
                regolaHeader();

            }
        });

        this.idArea = getIntent().getIntExtra("areaid", 0);

        // registra la listview per il clic contestuale
        registerForContextMenu(getListView());

        // carica i dati
        new PopulateAreaTask(this).execute();

    }


    @Override
    public AbsUpdateTask getUpdateTask() {
        return new UpdateAreaTask(this);
    }


    public String getHeadline2() {
        return getArea().getPlant().getName();
    }

    public String getHeadline3() {
        return getArea().getName();
    }

    public String getItemsType() {
        String type = "";
        switch (mode) {
            case MODE_SENSORS:
                type = "sensori";
                break;
            case MODE_OUTPUTS:
                type = "uscite";
                break;
        }
        return type;
    }

    @Override
    public int getNumItemsInList() {
        int num = 0;
        switch (mode) {
            case MODE_SENSORS:
                num = DB.getSensorCountByArea(getArea().getId());
                break;

            case MODE_OUTPUTS:
                num = DB.getOutputCountByArea(getArea().getId());
                break;
        }
        return num;
    }


    /**
     * MENU
     */

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.list) {

            switch (mode) {
                case MODE_SENSORS:
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                    sensMod = (SensorModel) getListAdapter().getItem(info.position);

                    MenuInflater inflater = getMenuInflater();
                    inflater.inflate(R.menu.menu_list, menu);
                    menu.setHeaderTitle(" " + sensMod.getSensor().getName());

                    if (sensMod.getStatus() == 1) {
                        menu.getItem(0).setTitle("Disabilita");
                    }

                    if (sensMod.isTest()) {
                        menu.getItem(1).setTitle("Disattiva Test");
                    }
                    break;

                case MODE_OUTPUTS:
                    break;
            }


        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean retvalue = true;
        int stato = sensMod.getStatus();
        int num = sensMod.getNumber();
        boolean test = sensMod.isTest();
        Request req;


        switch (mode) {
            case MODE_SENSORS:
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
                        retvalue = true;
                        break;

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
                        retvalue = true;
                        break;

                    case R.id.Cancella:
                        Toast.makeText(this, "Cancellato", Toast.LENGTH_LONG).show();
                        // remove stuff here
                        retvalue = true;
                        break;

                    default:
                        retvalue = super.onContextItemSelected(item);
                }
                break;

            case MODE_OUTPUTS:
                break;
        }

        return retvalue;

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
