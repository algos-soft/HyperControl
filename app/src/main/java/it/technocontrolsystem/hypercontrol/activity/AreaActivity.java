package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.communication.SensorCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.display.SensorDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.SensorListAdapter;
import it.technocontrolsystem.hypercontrol.model.SensorModel;


public class AreaActivity extends HCActivity {
    private int idArea;
    private SensorModel sensMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        this.idArea = getIntent().getIntExtra("areaid", 0);

        if (idArea != 0) {

            loadAreaTitle();

            // crea l'adapter per la ListView
            listAdapter = new SensorListAdapter(AreaActivity.this, getArea());

            ActivityTask task = new ActivityTask();
            task.execute();

            ListView list = (ListView) findViewById(R.id.list);
            //  registerForContextMenu(list);

        } else {
            finish();
        }

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

            if(sensMod.isTest()){
                menu.getItem(1).setTitle("Disattiva Test");
            }


        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int stato = sensMod.getStatus();
        int num = sensMod.getNumber();
        boolean test=sensMod.isTest();
        Request req;


        switch (item.getItemId()) {
            case R.id.Abilita:
                if (stato == 0) {
                    req = new SensorCommandRequest(num, 4, true);
                    Toast.makeText(this, "Abilitato", Toast.LENGTH_LONG).show();
                    SiteActivity.getConnection().sendRequest(req);
                } else {
                    req = new SensorCommandRequest(num, 4, false);
                    Toast.makeText(this, "Disabilitato", Toast.LENGTH_LONG).show();
                    SiteActivity.getConnection().sendRequest(req);
                }
                // add stuff here
                return true;

            case R.id.Test:
                if(!test){
                    req = new SensorCommandRequest(num, 5, true);
                    Toast.makeText(this, "Test attivato", Toast.LENGTH_LONG).show();
                    SiteActivity.getConnection().sendRequest(req);
                }else{
                    req = new SensorCommandRequest(num, 5, false);
                    Toast.makeText(this, "Test disattivato", Toast.LENGTH_LONG).show();
                    SiteActivity.getConnection().sendRequest(req);
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

    class ActivityTask extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(AreaActivity.this, "Caricamento sensori", "caricamento...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // popola l'adapter per la ListView
                populateAdapter();

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
            final ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(listAdapter);


            //con questo listener  riesco a recuperare le info del sensore,ma non riesco a visualizzare il menu contestuale
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    sensMod = (SensorModel) list.getItemAtPosition(position);
                    registerForContextMenu(parent);
                    openContextMenu(parent);
                    return true;
                }


            });


            progress.dismiss();
        }
    }


    private void loadAreaTitle() {

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(getArea().getPlant().getName() + " - " + getArea().getName());
    }


    public Site getSite() {
        return getArea().getPlant().getSite();
    }

    public Area getArea() {
        return DB.getArea(idArea);
    }

    /**
     * Carica i sensori dell'area dal DB nell'adapter
     */
    private void populateAdapter() {
        Sensor[] sensors = DB.getSensorsByArea(idArea);
        SensorModel model;
        for (final Sensor sensor : sensors) {
            model = new SensorModel(sensor);
            listAdapter.add(model);
        }
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
