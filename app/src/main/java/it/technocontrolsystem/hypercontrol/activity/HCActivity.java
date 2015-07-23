package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;

/**
 * Activity di base con ActionBar che gestisce un header e una lista interna.
 * Usiamo ActionBarActivity per avere la garanzia che l'option menu sia sempre accessibile.
 * Senza la ActionBar dovremmo gestire l'option menu in modo custom
 * perché il pulsante menu non è più garantito sui dispositivi.
 * (nemmeno sui phones dopo una certa versione di Android)
 */
public abstract class HCActivity extends ActionBarActivity {

    public static final int MENU_SETTINGS = 190;
    public static final int MENU_CREDITS = 191;
    protected static final String TAG = "HCActivity";

    private ArrayAdapter listAdapter;
    protected ProgressDialog progress;
    protected AbsPopulateTask populateTask;


    protected boolean workingInBg = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_launcher);
        String name = getString(R.string.app_name);
        bar.setTitle(name);

    }


    @Override
    protected void onStart() {
        super.onStart();
        String subtitle = getActionBarSubtitle();
        getSupportActionBar().setSubtitle(subtitle);
        regolaHeader();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (populateTask != null) {
            populateTask.cancel(true);
        }
        // drop references from inner class to main Activity
        populateTask = null;
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

        // popola l'adapter della lista
        // da database o altra sorgente
        public abstract void populateAdapter();


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
            getListView().setAdapter(getListAdapter());
            Lib.unlockOrientation(HCActivity.this);
            if (lock != null) {
                Lib.releaseWakeLock(lock);
            }
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
            Lib.unlockOrientation(HCActivity.this);
            if (lock != null) {
                Lib.releaseWakeLock(lock);
            }
        }


        /**
         * Ritorna il nome degli oggetti trattati
         */
        public abstract String getType();


    }







    /**
     * Ritorna il testo da visualizzare nel subtitle della ActionBar
     */
    public abstract String getActionBarSubtitle();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
        item=menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, getString(R.string.menu_settings));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item=menu.add(Menu.NONE, MENU_CREDITS, Menu.NONE, getString(R.string.menu_credits));
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

            case MENU_CREDITS: {

                String app_version = "";
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    app_version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String msg=getString(R.string.app_name)+" "+app_version+"\n";
                msg+="©2015 Technocontrol System\n";
                msg+="info@technocontrolsystem.it";
                builder.setMessage(msg);
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



    public void regolaHeader() {
        TextView view;

        String line2 = getHeadline2();
        String line3 = getHeadline3();

        view = (TextView) findViewById(R.id.hdr_line2);
        if(view!=null){
            if (line2 != null && !line2.equals("")) {
                view.setText(line2);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        view = (TextView) findViewById(R.id.hdr_line3);
        if(view!=null){
            if (line3 != null && !line3.equals("")) {
                view.setText(line3);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        int number = getNumItemsInList();
        TextView numView = (TextView) findViewById(R.id.hdr_item_num);
        TextView typeView = (TextView) findViewById(R.id.hdr_item_type);
        if(number>-1){
            String type = getItemsType();
            if(numView!=null){
                numView.setText("" + number);
            }
            if(typeView!=null){
                typeView.setText(type);
            }
        }else{
            if(numView!=null){
                numView.setVisibility(View.GONE);
            }
            if(typeView!=null){
                typeView.setVisibility(View.GONE);
            }
        }

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
     * Ritorna il numero di elementi contenuti nel database e visualizzati in lista
     * tornare -1 se l'informazione non è rilevante, in modo che non venga
     * visualizzata nell'header
     */
    public abstract int getNumItemsInList();

    /**
     * Ritorna il testo da visualizzare in corrispondenza del numero di elementi
     */
    public abstract String getItemsType();



    protected ListView getListView() {
        return (ListView) findViewById(R.id.list);
    }


    public ArrayAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ArrayAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }
}
