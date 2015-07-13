package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.ListEventRequest;
import it.technocontrolsystem.hypercontrol.communication.ListEventResponse;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Event;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.EventListAdapter;
import it.technocontrolsystem.hypercontrol.model.EventModel;

/**
 * Activity per la visualizzazione degli eventi direttamente dalla centrale.
 * Si può usare solo se il sito è connesso.
 * Se non connesso visualizza un messaggio ed esce.
 */
public class EventiActivity extends HCActivity {


    private int idSite;
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

    public String getHeadline2(){
        return "Lista eventi";
    }

    public String getHeadline3(){
        return null;
    }

    public String getItemsType(){return null;}

    @Override
    public int getNumItemsInList() {
        return -1;
    }

    /**
     * AsyncTask per caricare i dati nell'adapter
     */
    class PopulateTask extends AbsPopulateTask {

        @Override
        public void populateAdapter() {
            try {


                Request request = new ListEventRequest(lastIdEvento, 10);
                Response resp = SiteActivity.getConnection().sendRequest(request);
                if(resp!=null){

                    final ListEventResponse vResp = (ListEventResponse) resp;

                    if (vResp != null) {

                        if (vResp.isSuccess()) {

                            // in questo caso l'adapter è già attaccato alla lista
                            // e il metodo listAdapter.add() deve eseguire nello UI Thread
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Event[] events = vResp.getEvents();
                                    for(Event e : events){
                                        EventModel model = new EventModel(e);
                                        listAdapter.add(model);
                                        lastIdEvento=e.getId();

                                        if (isCancelled()){
                                            break;
                                        }

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


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getType() {
            return "eventi";
        }

        @Override
        /**
         * Overridden perché qui non devo riassegnare l'adapter
         * alla listView, se no la lista mi ritorna all'inizio.
         * In questa activity l'adapter viene creato e attaccato
         * in onCreate() e i dati vengono aggiunti progressivamente.
         */
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            Lib.unlockOrientation(EventiActivity.this);
            Lib.releaseWakeLock(lock);
        }


    }

    @Override
    public void updateStatus(){
    }



    /**
     * Carica un blocco di eventi e li aggiunge all'adapter
     */
    public void loadEventi(){
        if(HyperControlApp.getConnection()!=null){
            populateTask = (AbsPopulateTask)new PopulateTask().execute();
        }
    }


    public Site getSite(){
        return DB.getSite(idSite);
    }


    @Override
    public int getLiveCode() {
        return -1;
    }

    @Override
    public int getParamPlantNumCode() {
        return -1;
    }

    @Override
    public int getParamAreaNumCode() {
        return -1;
    }


}
