package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.SensorCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.SensorListAdapter;
import it.technocontrolsystem.hypercontrol.model.SensorModel;


public class AreaActivity extends HCActivity {
    private int idArea;
    boolean workingInBg=false;
    private SensorModel sensMod;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        this.idArea = getIntent().getIntExtra("areaid", 0);

        if (idArea != 0) {

            progress = new ProgressDialog(this);

            loadAreaTitle();

            // crea l'adapter per la ListView
            listAdapter = new SensorListAdapter(AreaActivity.this, getArea());

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
                 * Carica i sensori dell'impianto dal DB nell'adapter
                 */
                Sensor[] sensors = DB.getSensorsByArea(idArea);
                SensorModel aModel;
                for (final Sensor sensor : sensors) {
                    aModel = new SensorModel(sensor);
                    listAdapter.add(aModel);
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
                Lib.lockOrientation(AreaActivity.this);
                lock = Lib.acquireWakeLock();
                progress.setMessage("caricamento sensori...");
                progress.show();
            }
        }



        @Override
        protected void onPostExecute(Void aVoid) {

            progress.dismiss();
            getListView().setAdapter(listAdapter);
            Lib.unlockOrientation(AreaActivity.this);
            Lib.releaseWakeLock(lock);


            //a cosa serve questo? - alex lug-2015
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
                Lib.lockOrientation(AreaActivity.this);
                lock = Lib.acquireWakeLock();
                progress.setMessage("aggiornamento stato...");
                progress.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            listAdapter.notifyDataSetChanged();
            Lib.unlockOrientation(AreaActivity.this);
            Lib.releaseWakeLock(lock);
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
