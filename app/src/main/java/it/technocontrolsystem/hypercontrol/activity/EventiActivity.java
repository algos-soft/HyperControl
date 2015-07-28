package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateEventTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdatePlantTask;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.EventListAdapter;

/**
 * Activity per la visualizzazione degli eventi direttamente dalla centrale.
 * Si può usare solo se il sito è connesso.
 * Se non connesso visualizza un messaggio ed esce.
 */
public class EventiActivity extends HCSiteActivity {


    private int idSite;
    public int lastIdEvento=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi);

        this.idSite = getIntent().getIntExtra("siteid", 0);

        if(HyperControlApp.isConnected()) {

            // creo un adapter
            setListAdapter(new EventListAdapter(this));

            // assegna l'adapter alla ListView
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(getListAdapter());

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

    }

    @Override
    public AbsUpdateTask getUpdateTask() {
        return null;
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

//    /**
//     * AsyncTask per caricare i dati nell'adapter
//     */
//    class PopulateTask extends AbsPopulateTask {
//
//        public PopulateTask() {
//            super(EventiActivity.this);
//        }
//
//        @Override
//        public void populateAdapter() {
//            try {
//
//
//                Request request = new ListEventRequest(lastIdEvento, 10);
//                Response resp = HyperControlApp.sendRequest(request);
//                if(resp!=null){
//
//                    final ListEventResponse vResp = (ListEventResponse) resp;
//
//                    if (vResp != null) {
//
//                        if (vResp.isSuccess()) {
//
//                            // in questo caso l'adapter è già attaccato alla lista
//                            // e il metodo listAdapter.add() deve eseguire nello UI Thread
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Event[] events = vResp.getEvents();
//                                    for(Event e : events){
//                                        EventModel model = new EventModel(e);
//                                        getListAdapter().add(model);
//                                        lastIdEvento=e.getId();
//
//                                        if (isCancelled()){
//                                            break;
//                                        }
//
//                                    }
//                                }
//                            });
//
//                        } else {  // list events request failed
//                            throw new Exception(resp.getText());
//                        }
//
//                    } else {  // list events response null
//                        throw new Exception("List Events Request timeout");
//                    }
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public String getType() {
//            return "eventi";
//        }
//
//        @Override
//        /**
//         * Overridden perché qui non devo riassegnare l'adapter
//         * alla listView, se no la lista mi ritorna all'inizio.
//         * In questa activity l'adapter viene creato e attaccato
//         * in onCreate() e i dati vengono aggiunti progressivamente.
//         */
//        protected void onPostExecute(Exception exception) {
//            super.onPostExecute(exception);
//            progress.dismiss();
//            Lib.unlockOrientation(EventiActivity.this);
////            Lib.releaseWakeLock(lock);
//        }
//
//        @Override
//        public AsyncTask getUpdateTask() {
//            return null;
//        }
//
//
//
//    }


    /**
     * Carica un blocco di eventi e li aggiunge all'adapter
     */
    public void loadEventi(){
        Connection conn= HyperControlApp.getConnection();
        if(conn!=null && conn.isOpen()){
            new PopulateEventTask(this).execute();
        }
    }


    public Site getSite(){
        return DB.getSite(idSite);
    }


    @Override
    public int getLiveCode() {
        return 0;
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
