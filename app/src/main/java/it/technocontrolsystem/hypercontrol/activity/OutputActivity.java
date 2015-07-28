package it.technocontrolsystem.hypercontrol.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Output;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.OutputListAdapter;
import it.technocontrolsystem.hypercontrol.model.OutputModel;

public class OutputActivity extends HCSiteActivity {
    //private int idArea;
    private OutputModel outsMod;
    private int idSite = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

//        this.idArea = getIntent().getIntExtra("areaid", 0);
//
//        if (idArea != 0) {
         this.idSite = getIntent().getIntExtra("siteid", 0);

        // crea l'adapter per la ListView
        setListAdapter(new OutputListAdapter(OutputActivity.this));

//        // carica i dati
//        populateTask = (AbsPopulateTask)new PopulateTask().execute();

    }

    @Override
    public AbsUpdateTask getUpdateTask() {
        return null;
    }

    @Override
    public void updateStatus(){
       /* Connection conn= HyperControlApp.getConnection();
        if(conn!=null && conn.isOpen()){
            updateTask=(AbsUpdateTask)new UpdateTask().execute();
        }*/
    }

    public String getHeadline2(){
        return "Lista uscite";
        //return getArea().getPlant().getName();
    }

    public String getHeadline3(){
        return null;
       // return getArea().getName();
    }

    public String getItemsType(){return "Uscite";}

    @Override
    public int getNumItemsInList() {
        return Integer.parseInt(null);
     //   return DB.getOutputCountBySite(getSite().getId());
    }

//    /**
//     * AsyncTask per caricare i dati nell'adapter
//     */
//    class PopulateTask extends AbsPopulateTask {
//
//        @Override
//        public void populateAdapter() {
//            Output[] outputs = DB.getOutputs(idSite);
//            publishProgress(-2, outputs.length);
//            OutputModel model;
//            getListAdapter().clear();
//            int i = 0;
//            for (final Output output : outputs) {
//                model = new OutputModel(output);
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
//            return "uscite";
//        }
//
//
//    }

//    /**
//     * Task per aggiornare lo stato dalla centrale.
//     */
//    class UpdateTask extends AbsUpdateTask {
//    }

    public int getLiveCode() {
        return 0;
    }
    public Site getSite() {
        return DB.getSite(idSite);
    }

//    public Area getArea() {
//        return DB.getArea(idArea);
//    }



    @Override
    public int getParamPlantNumCode() {
        return -1;
    }

    @Override
    public int getParamAreaNumCode() {
        return -1;
    }

}
