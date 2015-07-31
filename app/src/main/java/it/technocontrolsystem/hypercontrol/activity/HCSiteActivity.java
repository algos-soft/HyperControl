package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;

/**
 * Superclasse di tutte le HCActivity che sono relative a un Site
 * Created by alex on 15-07-2015.
 */
public abstract class HCSiteActivity extends HCListActivity {

    public static final int MENU_BOARDS = 2;
    public static final int MENU_EVENTI = 3;
    public static final int MENU_MENU = 4;
    public static final int MENU_SITES = 5;
    public static final int MENU_OUTPUTS = 6;

    // per distinguere se onResume è chiamato dopo onCreate() o dopo onPause()
    private boolean paused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HyperControlApp.addOnConnectivityChangedListener(new HyperControlApp.OnConnectivityChangedListener() {
            @Override
            public void connectivityChanged(boolean newStatus) {
                connectivityHasChanged(newStatus);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        /**
         * Regola il visualizzatore di presenza connessione
         */
        View view = findViewById(R.id.connection_status_button);
        if((view!=null) && (view instanceof ToggleButton)){
            ToggleButton btn = (ToggleButton)view;
            Connection conn = HyperControlApp.getConnection();
            if((conn!=null) && (conn.isOpen())){
                btn.setChecked(true);
            }else{
                btn.setChecked(false);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // se non è nuova istanza aggiorna lo stato
        if(paused){
            updateStatus();
        }

        try {

            if(!HyperControlApp.isConnected()){
                clearStatus();
            }

//            Connection conn= HyperControlApp.getConnection();
//            if (Lib.isNetworkAvailable() && (conn != null) && (conn.isOpen())) {
//                startLive();
//            } else {
//                clearStatus();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        paused=false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        // hide dialogs when paused
        if (progress != null) {
            progress.hide();
        }
        paused=true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Ritorna l'update task
     */
    public abstract AbsUpdateTask getUpdateTask();

    /**
     * Aggiorna lo stato corrente di tutti gli elementi dalla centrale
     */
    public void updateStatus(){
        if(HyperControlApp.isConnected()){
            AbsUpdateTask task = getUpdateTask();
            if(task!=null){
                task.execute();
            }
        }
    }

    /**
     * Svuota lo stato corrente di tutti gli elementi
     */
    public void clearStatus() {
        HCListAdapter adapter=getListAdapter();
        if(adapter!=null){
            adapter.clearStatus();
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
     * attiva la trasmissione aggiornamenti live
     */
    public void startLive() throws Exception {

        if (Lib.isNetworkAvailable()) {
            LiveRequest request = new LiveRequest(getLiveCode(), getParamPlantNumCode(), getParamAreaNumCode());
            Response resp = HyperControlApp.sendRequest(request);
            if (resp != null) {
                if (!resp.isSuccess()) {
                    throw new Exception(resp.getText());
                }
            } else {//comunication failed
                throw new Exception("Attivazione live fallita");
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

        item = menu.add(Menu.NONE, MENU_OUTPUTS, Menu.NONE, getString(R.string.menu_outputs));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_MENU, Menu.NONE, getString(R.string.menu_commands));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_EVENTI, Menu.NONE, getString(R.string.menu_events));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_SITES, Menu.NONE, getString(R.string.menu_sites));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item = menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, getString(R.string.menu_settings));
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

            case MENU_BOARDS: {
                Intent intent = new Intent();
                intent.setClass(this, BoardActivity.class);
                intent.putExtra("siteid", getSite().getId());
                startActivity(intent);
                break;
            }
            case MENU_OUTPUTS: {
                Intent intent = new Intent();
                intent.setClass(this, OutputsActivity.class);
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
