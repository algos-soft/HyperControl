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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.model.ModelIF;

/**
 * Superclasse di tutte le HCActivity che sono relative a un Site
 * Created by alex on 15-07-2015.
 */
public abstract class HCSiteActivity extends HCActivity {

    public static final int MENU_BOARDS = 2;
    public static final int MENU_EVENTI = 3;
    public static final int MENU_MENU = 4;
    public static final int MENU_SITES = 5;

//    private static final int NEW_SITE_ACTION=1;

    protected AbsUpdateTask updateTask;


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

    }




    @Override
    protected void onResume() {
        super.onResume();
        try {
            Connection conn= HyperControlApp.getConnection();
            if (Lib.isNetworkAvailable() && (conn != null) && (conn.isOpen())) {
                getListAdapter().attachLiveListener();
                startLive();
            } else {
                clearStatus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // dismiss dialogs when paused
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel update task if running
        if (updateTask != null) {
            updateTask.cancel(true);
        }
        // drop references from inner class to main Activity
        updateTask = null;


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

                publishProgress(-2, getListAdapter().getCount());

                // aggiorna lo stato
                Connection conn = HyperControlApp.getConnection();
                boolean open=((conn!=null)&&(conn.isOpen()));
                if (open) {
                    for (int i = 0; i < getListAdapter().getCount(); i++) {

                        if (!(isCancelled() | Thread.interrupted())) {
                            ModelIF model = (ModelIF) getListAdapter().getItem(i);
                            getListAdapter().updateByNumber(model.getNumber());
                            publishProgress(-3, i + 1);
                        } else {
                            break;
                        }
                    }
                } else {
                    getListAdapter().clearStatus();
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
                    Lib.lockOrientation(HCSiteActivity.this);
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
        protected void onCancelled() {
            progress.dismiss();
            Lib.unlockOrientation(HCSiteActivity.this);
            if (lock != null) {
                Lib.releaseWakeLock(lock);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            getListAdapter().notifyDataSetChanged();
            Lib.unlockOrientation(HCSiteActivity.this);
            if (lock != null) {
                Lib.releaseWakeLock(lock);
            }
        }
    }


    /**
     * attiva la trasmissione aggiornamenti live
     */
    private void startLive() throws Exception {

        if (Lib.isNetworkAvailable()) {

            Connection conn = HyperControlApp.getConnection();
            if ((conn != null) && (conn.isOpen())) {

                LiveRequest request = new LiveRequest(getLiveCode(), getParamPlantNumCode(), getParamAreaNumCode());

                Response resp = conn.sendRequest(request);
                if (resp != null) {
                    if (!resp.isSuccess()) {
                        throw new Exception(resp.getText());
                    }
                } else {//comunication failed
                    throw new Exception("Attivazione live fallita");
                }

            }
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

        item = menu.add(Menu.NONE, MENU_BOARDS, Menu.NONE, getString(R.string.menu_boards));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_MENU, Menu.NONE, getString(R.string.menu_commands));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_EVENTI, Menu.NONE, getString(R.string.menu_events));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, getString(R.string.menu_settings));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_SITES, Menu.NONE, getString(R.string.menu_sites));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_CREDITS, Menu.NONE, getString(R.string.menu_credits));
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

                String app_version = "";
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    app_version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String msg = getString(R.string.app_name) + " " + app_version + "\n";
                msg += "©2015 Technocontrol System\n";
                msg += "info@technocontrolsystem.it";
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

            case MENU_SITES: {

                Connection conn = HyperControlApp.getConnection();
                if (conn!=null){
                    conn.close();
                }

                Intent intent = new Intent();
                intent.setClass(this, SitesListActivity.class);
                startActivity(intent);
                finish();
                break;
            }


        }

        return super.onOptionsItemSelected(item);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode==NEW_SITE_ACTION){
//            if(resultCode==RESULT_OK){
//                Intent intent = new Intent();
//                intent.setClass(this, SitesListActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }
//    }

    /**
     * @return il listAdapter come HCListAdapter
     */
    public HCListAdapter getListAdapter() {
        ListAdapter adatper = super.getListAdapter();
        return (HCListAdapter) adatper;
    }


    public abstract Site getSite();


    /**
     * Ritorna il testo da visualizzare nel subtitle della ActionBar
     */
    @Override
    public String getActionBarSubtitle() {
        return getSite().getName();
    }

}
