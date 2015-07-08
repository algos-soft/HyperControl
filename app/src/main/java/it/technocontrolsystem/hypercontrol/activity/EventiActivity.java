package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.ListEventRequest;
import it.technocontrolsystem.hypercontrol.communication.ListEventResponse;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.communication.VersionRequest;
import it.technocontrolsystem.hypercontrol.communication.VersionResponse;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.domain.Event;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.BoardListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.EventListAdapter;
import it.technocontrolsystem.hypercontrol.model.BoardModel;

/**
 * Activity per la visualizzazione degli eventi direttamente dalla centrale.
 * Si può usare solo se il sito è connesso.
 * Se non connesso visualizza un messaggio ed esce.
 */
public class EventiActivity extends Activity {


    private int idSite;
    EventListAdapter listAdapter;
    private int lastIdEvento=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi);

        this.idSite = getIntent().getIntExtra("siteid", 0);
        if (idSite != 0) {
            if(SiteActivity.getConnection()!=null){

                // creo un adapter
                listAdapter = new EventListAdapter(this);

                // assegna l'adapter alla ListView
                ListView list = (ListView) findViewById(R.id.list);
                list.setAdapter(listAdapter);

                // carico gli eventi
                loadEventi();

            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sito non connesso");
                builder.setMessage("Per accedere agli eventi occorre essere connessi al sito");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }

        } else {
            finish();
        }

    }

    /**
     * Carica un blocco di eventi e li aggiunge all'adapter
     */
    public void loadEventi(){
        ActivityTask task = new ActivityTask();
        task.execute();
    }


    public Site getSite(){
        return DB.getSite(idSite);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



    class ActivityTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress=ProgressDialog.show(EventiActivity.this,null,"caricamento in corso...",true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // popola l'adapter per la ListView
                populateAdapter();

            }catch(Exception e1){
                e1.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            progress.dismiss();
            listAdapter.notifyDataSetChanged();
        }
    }


    /**
     * Carica gli impianti del sito dalla centrale nell'adapter.
     * Questo metodo viene invocato nel background task
     */
    private void populateAdapter() throws Exception {


        Request request = new ListEventRequest(lastIdEvento, 10);
        Response resp = SiteActivity.getConnection().sendRequest(request);
        final ListEventResponse vResp = (ListEventResponse) resp;

        if (vResp != null) {

            if (vResp.isSuccess()) {

                // in questo caso l'adapter è già attaccato alla lista
                // e il metodo listAdapter.add() deve eseguire nello UI Thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        Event[] events = vResp.getEvents();
                        for(Event e : events){
                            listAdapter.add(e);
                            lastIdEvento=e.getId();
                        }
                    }
                });

            } else {  // list events request failed
                throw new Exception(resp.getText());
            }

        } else {  // list events response null
            throw new Exception("List Events Request timeout");
        }

    }

}
