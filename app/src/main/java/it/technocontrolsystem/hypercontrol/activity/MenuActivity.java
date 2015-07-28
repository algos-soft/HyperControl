package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsUpdateTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateAreaTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateMenuTask;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.MenuListAdapter;


public class MenuActivity extends HCSiteActivity {
    private int idSite;
    private int idPage;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        idSite = getIntent().getIntExtra("siteid", 0);
        idPage = getIntent().getIntExtra("pageid", -1);
        title=getIntent().getStringExtra("title");

        getListView().setDivider(null);

        // crea l'adapter per la ListView
        setListAdapter(new MenuListAdapter(MenuActivity.this));

        // carica i dati
        new PopulateMenuTask(this).execute();

    }



//    /**
//     * AsyncTask per caricare i dati nell'adapter
//     */
//    class PopulateTask extends AbsPopulateTask {
//
//        public PopulateTask() {
//            super(MenuActivity.this);
//        }
//
//        @Override
//        public void populateAdapter() {
//
//            Menu[] menus = DB.getMenusBySiteAndPage(idSite, idPage);
//
//            //Menu[] menus = DB.getMenus(idSite);
//            publishProgress(-2, menus.length);
//
//            MenuModel model;
//            getListAdapter().clear();
//            int i = 0;
//            for (final Menu menu : menus) {
//                model = new MenuModel(menu);
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
//            return "menu";
//        }
//
//        @Override
//        protected void onPostExecute(Exception exception) {
//            super.onPostExecute(exception);
//            regolaHeader();
//        }
//
//
//        @Override
//        public AsyncTask getUpdateTask() {
//            return null;
//        }
//
//    }


    @Override
    public AbsUpdateTask getUpdateTask() {
        return null;
    }


    @Override
    public String getHeadline2() {
        String testo;
        if(title!=null && !title.equals("")){
            testo=title;
        }else{
            testo="Comandi";
        }
        return testo;
    }

    @Override
    public String getHeadline3() {
        return null;
    }

    @Override
    public String getItemsType() {
        return "comandi";
    }

    @Override
    public int getNumItemsInList() {
        return DB.getMenusCountBySiteAndPage(idSite, idPage);
    }

    @Override
    public int getLiveCode() {
        return 0;
    }

    @Override
    public int getParamPlantNumCode() {
        return 0;
    }

    @Override
    public int getParamAreaNumCode() {
        return 0;
    }

    @Override
    public Site getSite() {
        return DB.getSite(idSite);
    }


    public int getIdPage() {
        return idPage;
    }
}
