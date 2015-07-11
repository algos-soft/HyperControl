package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.LiveRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.model.ModelIF;

/**
 *
 */
public abstract class HCActivity extends ActionBarActivity {
    private static final String TAG = "HCActivity";

    public static final int MENU_SETTINGS = 1;
    public static final int MENU_BOARDS = 2;
    public static final int MENU_EVENTI = 3;
    public static final int MENU_MENU = 4;
    public static final int MENU_CREDITS = 5;

    protected HCListAdapter listAdapter;
    protected boolean workingInBg = false;
    protected ProgressDialog progress;

    private View actionBarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);

        HyperControlApp.addOnConnectivityChangedListener(new HyperControlApp.OnConnectivityChangedListener() {
            @Override
            public void connectivityChanged(boolean newStatus) {
                connectivityHasChanged(newStatus);
            }
        });

        // non uso la ActionBar ma una custom header view.
        // dichiaro l'activity come ActionBarActivity per avere un menu ben fatto
        // se no il menu rimane trasparente e non si legge
        //getSupportActionBar().hide();

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_launcher);
        String name = getString(R.string.app_name);
        bar.setTitle(name);

    }


    @Override
    protected void onStart() {
        super.onStart();
        regolaHeader();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Lib.isNetworkAvailable() && (HyperControlApp.getConnection() != null)) {
                listAdapter.attachLiveListener();
                startLive();
            } else {
                clearStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiorna lo stato corrente di tutti gli elementi dalla centrale
     */
    public abstract void updateStatus();

    /**
     * Svuota lo stato corrente di tutti gli elementi
     */
    public void clearStatus() {
        getListAdapter().clearStatus();
    }

    /**
     * Ritorna il testo da visualizzare nella seconda riga dell'header
     */
    public abstract String getHeadline2();

    /**
     * Ritorna il testo da visualizzare nella terza riga dell'header
     */
    public abstract String getHeadline3();

    /**
     * Ritorna il testo da visualizzare in corrispondenza del numero di elementi
     */
    public abstract String getItemsType();

    /**
     * Ritorna il numero di elementi contenuti nel database e visualizzati in lista
     * tornare -1 se l'informazione non è rilevante, in modo che non venga
     * visualizzata nell'header
     */
    public abstract int getNumItemsInList();

    private void regolaHeader(){
        TextView view;

        String line1=getSite().getName();
        String line2= getHeadline2();
        String line3= getHeadline3();

        view=(TextView)findViewById(R.id.hdr_line1);
        if(line1!=null && !line1.equals("")){
            view.setText(line1);
        }else{
            view.setVisibility(View.GONE);
        }

        view=(TextView)findViewById(R.id.hdr_line2);
        if(line2!=null && !line2.equals("")){
            view.setText(line2);
        }else{
            view.setVisibility(View.GONE);
        }

        view=(TextView)findViewById(R.id.hdr_line3);
        if(line3!=null && !line3.equals("")){
            view.setText(line3);
        }else{
            view.setVisibility(View.GONE);
        }

        int number = getNumItemsInList();
        String type = getItemsType();
        TextView numView=(TextView)findViewById(R.id.hdr_item_num);
        TextView typeView=(TextView)findViewById(R.id.hdr_item_type);
        if(number>-1){
            numView.setText(""+number);
            typeView.setText(type);
        }else{
            numView.setVisibility(View.GONE);
            typeView.setVisibility(View.GONE);
        }

    }


    /**
     * Invocato quando cambia lo stato della connettività del device
     */
    public void connectivityHasChanged(boolean newStatus) {
        if (newStatus) {
            updateStatus();
        } else {
            clearStatus();
        }
    }


    /**
     * Task per caricare i dati dal db nell'adapter.
     * Dopo aver caricato i dati assegna l'adapter alla ListView.
     */
    abstract class AbsPopulateTask extends AsyncTask<Void, Integer, Void> {

        PowerManager.WakeLock lock;

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "PopulateTask - startBackground");

            // attende che si liberi il semaforo
            waitForSemaphore();
            workingInBg = true;

            // mostra il dialogo
            publishProgress(-1);

            try {

                populateAdapter();

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            workingInBg = false;

            Log.d(TAG, "PopulateTask - endBackground");

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int param1 = 0, param2 = 0;
            param1 = values[0];
            if (values.length > 1) {
                param2 = values[1];
            }
            switch (param1) {
                case -1: {
                    Lib.lockOrientation(HCActivity.this);
                    lock = Lib.acquireWakeLock();
                    progress.setMessage("caricamento " + getType() + "...");
                    progress.setProgress(0);
                    progress.show();
                    break;
                }

                case -2: {
                    progress.setMax(param2);
                    break;
                }

                case -3: {
                    progress.setProgress(param2);
                    break;
                }

            }

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            getListView().setAdapter(listAdapter);
            Lib.unlockOrientation(HCActivity.this);
            Lib.releaseWakeLock(lock);
        }

        /**
         * Popola l'adapter dal database o da altra sorgente
         */
        public abstract void populateAdapter();

        /**
         * Ritorna il nome degli oggetti trattati
         */
        public abstract String getType();


    }


    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    abstract class AbsUpdateTask extends AsyncTask<Void, Integer, Void> {

        private PowerManager.WakeLock lock;

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "UpdateTask - startBackground");

            // attende che si liberi il semaforo
            waitForSemaphore();
            workingInBg = true;

            // mostra il dialogo
            publishProgress(-1);

            try {

                publishProgress(-2, listAdapter.getCount());

                // aggiorna lo stato
                if (SiteActivity.getConnection() != null) {
                    for (int i = 0; i < listAdapter.getCount(); i++) {
                        ModelIF model = (ModelIF) listAdapter.getItem(i);
                        listAdapter.updateByNumber(model.getNumber());
                        publishProgress(-3, i + 1);
                    }
                } else {
                    listAdapter.clearStatus();
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // spegne il semaforo
            workingInBg = false;

            Log.d(TAG, "UpdateTask - endBackground");


            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            int param1 = 0, param2 = 0;
            param1 = values[0];
            if (values.length > 1) {
                param2 = values[1];
            }
            switch (param1) {
                case -1: {
                    Lib.lockOrientation(HCActivity.this);
                    lock = Lib.acquireWakeLock();
                    progress.setMessage("aggiornamento stato...");
                    progress.setProgress(0);
                    progress.show();
                    break;
                }

                case -2: {
                    progress.setMax(param2);
                    break;
                }

                case -3: {
                    progress.setProgress(param2);
                    break;
                }

            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            listAdapter.notifyDataSetChanged();
            Lib.unlockOrientation(HCActivity.this);
            Lib.releaseWakeLock(lock);
        }
    }


    /**
     * attiva la trasmissione aggiornamenti live
     */
    private void startLive() throws Exception {
        LiveRequest request = new LiveRequest(getLiveCode(), getParamPlantNumCode(), getParamAreaNumCode());

        Response resp = SiteActivity.getConnection().sendRequest(request);
        if (resp != null) {
            if (!resp.isSuccess()) {
                throw new Exception(resp.getText());
            }
        } else {//comunication failed
            throw new Exception("Attivazione live fallita");
        }
    }

    /**
     * Ritorna il codice di attivazione live (Set) per questa activity
     */
    public abstract int getLiveCode();

    public abstract int getParamPlantNumCode();

    public abstract int getParamAreaNumCode();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
        item=menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item=menu.add(Menu.NONE, MENU_BOARDS, Menu.NONE, "Boards");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item=menu.add(Menu.NONE, MENU_MENU, Menu.NONE, "Comandi rapidi");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item=menu.add(Menu.NONE, MENU_EVENTI, Menu.NONE, "Eventi");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item=menu.add(Menu.NONE, MENU_CREDITS, Menu.NONE, "Credits");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case MENU_SETTINGS: {
                Intent intent = new Intent();
                intent.setClass(this, ConfigActivity.class);
                startActivity(intent);
                break;
            }

            case MENU_BOARDS: {
                Intent intent = new Intent();
                intent.setClass(this, BoardActivity.class);
                intent.putExtra("siteid", getSite().getId());
                startActivity(intent);
                break;
            }

            case MENU_MENU: {
                Intent intent = new Intent();
                intent.setClass(this, MenuActivity.class);
                intent.putExtra("siteid", getSite().getId());
                startActivity(intent);
                break;
            }

            case MENU_EVENTI: {
                Intent intent = new Intent();
                intent.setClass(this, EventiActivity.class);
                intent.putExtra("siteid", getSite().getId());
                startActivity(intent);
                break;
            }

            case MENU_CREDITS: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("HyperControl\n©2015 Technocontrol System\ninfo@technocontrolsystem.it");
                builder.setPositiveButton("continua", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public abstract Site getSite();

    public HCListAdapter getListAdapter() {
        return listAdapter;
    }

    /**
     * Attende che si liberi il semaforo
     */
    protected void waitForSemaphore() {
        while (workingInBg) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int a = 0;
    }

    protected ListView getListView() {
        return (ListView) findViewById(R.id.list);
    }


}
