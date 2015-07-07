package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.BoardListAdapter;
import it.technocontrolsystem.hypercontrol.model.BoardModel;

/**
 * List of boards
 */
public class BoardActivity extends HCActivity {

    private int idSite = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        this.idSite = getIntent().getIntExtra("siteid", 0);
        if (idSite != 0) {

            // crea l'adapter per la ListView
            listAdapter = new BoardListAdapter(BoardActivity.this);

            ActivityTask task = new ActivityTask();
            task.execute();

        } else {
            finish();
        }

    }

    class ActivityTask extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress=ProgressDialog.show(BoardActivity.this,"Caricamento schede","caricamento...",true);
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {

                // popola l'adapter per la ListView
                populateAdapter();

                if(SiteActivity.getConnection()!=null){
                    publishProgress(-1);
                    listAdapter.update();
                }

            }catch(Exception e1){
                e1.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            int num = values[0];
            if (num==-1){   // started update
                progress.setMessage("aggiornamento stato...");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid){
            // assegna l'adapter alla ListView
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(listAdapter);
            progress.dismiss();
        }
    }


    /**
     * Carica gli impianti del sito dal DB nell'adapter
     */
    private void populateAdapter() {
        Board[] boards = DB.getBoards(idSite);
        BoardModel model;
        for (final Board board : boards) {
            model=new BoardModel(board);
            listAdapter.add(model);
        }
    }


//    /**
//     *attiva la trasmissione aggiornamenti live
//     */
//    private void startLive() throws Exception{
//        LiveRequest request=new LiveRequest(4,-1,-1);
//
//        Response resp = SiteActivity.getConnection().sendRequest(request);
//        if (resp != null) {
//            if (!resp.isSuccess()) {
//                throw new Exception(resp.getText());
//            }
//        } else {//comunication failed
//            throw new Exception("Attivazione live boards fallita");
//        }
//    }

    public int getLiveCode() {
        return 4;
    }

    @Override
    public int getParamPlantNumCode() {
        return -1;
    }

    @Override
    public int getParamAreaNumCode() {
        return -1;
    }

    @Override
    public Site getSite() {
        return null;
    }


}
