package it.technocontrolsystem.hypercontrol.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.BoardListAdapter;
import it.technocontrolsystem.hypercontrol.model.BoardModel;

/**
 * List of boards
 */
public class BoardActivity extends HCActivity {

    private int idSite = 0;
    boolean workingInBg=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        this.idSite = getIntent().getIntExtra("siteid", 0);
        if (idSite != 0) {



            // crea l'adapter per la ListView
            listAdapter = new BoardListAdapter(BoardActivity.this);

            // carica i dati
            PopulateTask task = new PopulateTask();
            task.execute();

//            // riempio l'adapter
//            ActivityTask task = new ActivityTask();
//            task.execute();

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
            publishProgress(-1);

            try {

                /**
                 * Carica gli elementi dal DB nell'adapter
                 */
                Board[] boards = DB.getBoards(idSite);
                publishProgress(-2, boards.length);

                BoardModel model;
                listAdapter.clear();
                int i=0;
                for (final Board board : boards) {
                    model=new BoardModel(board);
                    listAdapter.add(model);
                    i++;
                    publishProgress(-3,i);
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            workingInBg = false;

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int param1=0, param2=0;
            param1 = values[0];
            if(values.length>1){
                param2 = values[1];
            }
            switch (param1){
                case -1:{
                    Lib.lockOrientation(BoardActivity.this);
                    lock = Lib.acquireWakeLock();
                    progress.setMessage("caricamento schede...");
                    progress.setProgress(0);
                    progress.show();
                    break;
                }

                case -2:{
                    progress.setMax(param2);
                    break;
                }

                case -3:{
                    progress.setProgress(param2);
                    break;
                }

            }

        }



        @Override
        protected void onPostExecute(Void aVoid) {

            progress.dismiss();
            getListView().setAdapter(listAdapter);
            Lib.unlockOrientation(BoardActivity.this);
            Lib.releaseWakeLock(lock);
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
            publishProgress(-1);

            try {

                publishProgress(-2, listAdapter.getCount());

                // aggiorna lo stato
                if (SiteActivity.getConnection() != null) {
                    for(int i=0;i<listAdapter.getCount();i++){
                        BoardModel model = (BoardModel)listAdapter.getItem(i);
                        ((BoardListAdapter)listAdapter).update(model.getNumber());
                        publishProgress(-3,i+1);
                    }
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
            int param1=0, param2=0;
            param1 = values[0];
            if(values.length>1){
                param2 = values[1];
            }
            switch (param1){
                case -1:{
                    Lib.lockOrientation(BoardActivity.this);
                    lock = Lib.acquireWakeLock();
                    progress.setMessage("aggiornamento stato...");
                    progress.setProgress(0);
                    progress.show();
                    break;
                }

                case -2:{
                    progress.setMax(param2);
                    break;
                }

                case -3:{
                    progress.setProgress(param2);
                    break;
                }

            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            listAdapter.notifyDataSetChanged();
            Lib.unlockOrientation(BoardActivity.this);
            Lib.releaseWakeLock(lock);
        }
    }




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
        return DB.getSite(idSite);
    }


}
