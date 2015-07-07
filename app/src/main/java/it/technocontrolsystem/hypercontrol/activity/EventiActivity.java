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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi);

        this.idSite = getIntent().getIntExtra("siteid", 0);
        if (idSite != 0) {
            if(SiteActivity.getConnection()!=null){

                // creo un adapter e lo assegno alla ListView
                listAdapter = new EventListAdapter(this);
                ListView lv = (ListView)findViewById(R.id.list);
                lv.setAdapter(listAdapter);

                // riempio l'adapter
                ActivityTask task = new ActivityTask();
                task.execute();

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
            progress=ProgressDialog.show(EventiActivity.this,"Caricamento eventi","caricamento in corso...",true);
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
        }
    }


    /**
     * Carica gli impianti del sito dalla centrale nell'adapter.
     * Questo metodo viene invocato nel background task
     */
    private void populateAdapter() throws Exception {


        Request request = new ListEventRequest(0);
        Response resp = SiteActivity.getConnection().sendRequest(request);
        ListEventResponse vResp = (ListEventResponse) resp;

        if (vResp != null) {

            if (vResp.isSuccess()) {

                listAdapter.clear();

                Event[] events = vResp.getEvents();
                for(Event e : events){
                    listAdapter.add(e);
                }

            } else {  // list events request failed
                throw new Exception(resp.getText());
            }

        } else {  // list events response null
            throw new Exception("List Events Request timeout");
        }

    }

}
