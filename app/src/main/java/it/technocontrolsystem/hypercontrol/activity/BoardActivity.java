package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateBoardTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulatePlantTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdateBoardTask;
import it.technocontrolsystem.hypercontrol.asynctasks.UpdatePlantTask;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.BoardListAdapter;

/**
 * List of boards
 */
public class BoardActivity extends HCSiteActivity {

    private int idSite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        this.idSite = getIntent().getIntExtra("siteid", 0);

        // carica i dati
        new PopulateBoardTask(this).execute();

    }


    @Override
    protected void onResume() {
        super.onResume();

        // aggiorna i dati
        //updateStatus();

    }

    @Override
    public AbsUpdateTask getUpdateTask() {
        return new UpdateBoardTask(this);
    }

    public String getHeadline2(){
        return "Lista schede";
    }

    public String getHeadline3(){
        return null;
    }

    public String getItemsType(){return "Schede";}

    @Override
    public int getNumItemsInList() {
        return DB.getBoardsCountBySite(getSite().getId());
    }



//    /**
//     * AsyncTask per caricare i dati nell'adapter
//     */
//    class PopulateTask extends AbsPopulateTask {
//
//        public PopulateTask() {
//            super(BoardActivity.this);
//        }
//
//        @Override
//        public void populateAdapter() {
//
//            Board[] boards = DB.getBoards(idSite);
//            publishProgress(-2, boards.length);
//
//            BoardModel model;
//            getListAdapter().clear();
//            int i = 0;
//            for (final Board board : boards) {
//                model = new BoardModel(board);
//                getListAdapter().add(model);
//                i++;
//                publishProgress(-3, i);
//
//                if (isCancelled()){
//                    break;
//                }
//
//            }
//
//        }
//
//        @Override
//        public String getType() {
//            return "schede";
//        }
//
//        @Override
//        public AsyncTask getUpdateTask() {
//            return null;
//        }
//
//
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
        return DB.getSite(idSite);
    }


}
