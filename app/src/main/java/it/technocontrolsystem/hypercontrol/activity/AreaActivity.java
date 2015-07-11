package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.SensorCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.PlantListAdapter;
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
        updateStatus();

    }

    @Override
    public void updateStatus(){
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
            Sensor[] sensors = DB.getSensorsByArea(idArea);
            publishProgress(-2, sensors.length);
            SensorModel aModel;
            listAdapter.clear();
            int i = 0;
            for (final Sensor sensor : sensors) {
                aModel = new SensorModel(sensor);
                listAdapter.add(aModel);
                i++;
                publishProgress(-3, i);
            }

        }

        @Override
        public String getType() {
            return "sensori";
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //== a cosa serve questo? - alex lug-2015 ==
            //con questo listener  riesco a recuperare le info del sensore,
            // ma non riesco a visualizzare il menu contestuale
            getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    sensMod = (SensorModel) getListView().getItemAtPosition(position);
                    registerForContextMenu(parent);
                    openContextMenu(parent);
                    return true;
                }


            });

        }
    }

    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    class UpdateTask extends AbsUpdateTask {
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
                    SiteActivity.getConnection().sendRequest(req);
                } else {
                    req = new SensorCommandRequest(num, 4, false);
                    Toast.makeText(this, "Disabilitato", Toast.LENGTH_LONG).show();
                    SiteActivity.getConnection().sendRequest(req);
                }
                // add stuff here
                return true;

            case R.id.Test:
                if (!test) {
                    req = new SensorCommandRequest(num, 5, true);
                    Toast.makeText(this, "Test attivato", Toast.LENGTH_LONG).show();
                    SiteActivity.getConnection().sendRequest(req);
                } else {
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
