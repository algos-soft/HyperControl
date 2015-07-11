package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        this.idSite = getIntent().getIntExtra("siteid", 0);
        if (idSite != 0) {


            // crea l'adapter per la ListView
            listAdapter = new BoardListAdapter(BoardActivity.this);

            // carica i dati
            new PopulateTask().execute();

        } else {
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        // aggiorna i dati
        updateStatus();

    }


    public String getHeadline2(){
        return null;
    }

    public String getHeadline3(){
        return null;
    }

    public String getItemsType(){return "Schede";}

    @Override
    public int getNumItemsInList() {
        return DB.getBoardsCountBySite(getSite().getId());
    }


    @Override
    public void updateStatus(){
        if(HyperControlApp.getConnection()!=null){
            new UpdateTask().execute();
        }
    }


    /**
     * AsyncTask per caricare i dati nell'adapter
     */
    class PopulateTask extends AbsPopulateTask {

        @Override
        public void populateAdapter() {

            Board[] boards = DB.getBoards(idSite);
            publishProgress(-2, boards.length);

            BoardModel model;
            listAdapter.clear();
            int i = 0;
            for (final Board board : boards) {
                model = new BoardModel(board);
                listAdapter.add(model);
                i++;
                publishProgress(-3, i);
            }

        }

        @Override
        public String getType() {
            return "schede";
        }

    }

    /**
     * Task per aggiornare lo stato dalla centrale.
     */
    class UpdateTask extends AbsUpdateTask {
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
