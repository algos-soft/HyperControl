package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.SiteDisplay;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.SiteListAdapter;
import it.technocontrolsystem.hypercontrol.model.SiteModel;

public class SitesListActivity extends HCActivity {

    public static  int ACTIVITY_EDIT_SITE=1;
    public static  int ACTIVITY_NEW_SITE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listsites);

        //crea l'adapter per la ListView
        setListAdapter(new SiteListAdapter(this));

        // attacca un click listener alla ListView
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteDisplay display = (SiteDisplay) view;
                Intent intent = new Intent();
                intent.setClass(SitesListActivity.this, StartSiteActivity.class);
                intent.putExtra("siteid", display.getSite().getId());
                SitesListActivity.this.startActivity(intent);
            }
        });


        // registra la listview per il clic contestuale
        registerForContextMenu(getListView());

        Button bNewSite=(Button)findViewById(R.id.bNewSite);
        bNewSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSite();
            }
        });

        //carica i dati
        populateTask = (AbsPopulateTask) new PopulateTask().execute();

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
        SiteDisplay view = (SiteDisplay)info.targetView;
        Site site = view.getSite();
        menu.setHeaderTitle(site.getName());

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sitelist_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SiteDisplay display = (SiteDisplay)info.targetView;
        final Site site = display.getSite();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.site_menu_apri:
                intent = new Intent();
                intent.setClass(SitesListActivity.this, StartSiteActivity.class);
                intent.putExtra("siteid", display.getSite().getId());
                SitesListActivity.this.startActivity(intent);
                return true;
            case R.id.site_menu_modifica:
                intent = new Intent();
                intent.setClass(this, EditSiteActivity.class);
                intent.putExtra("siteid", site.getId());
                intent.putExtra("usesave", true);
                intent.putExtra("usedelete", true);
                startActivityForResult(intent, SitesListActivity.ACTIVITY_EDIT_SITE);
                return true;
            case R.id.site_menu_elimina:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Attenzione!");
                builder.setMessage("Confermi l'eliminazione di questo sito?");
                builder.setNegativeButton("Annulla",null);
                builder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DB.deleteSite(site.getId());
                        updateAdapter(site.getId());
                        regolaHeader(); // aggiorno il numero di siti visualizzato
                    }
                });
                builder.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // edit site
        if (requestCode==ACTIVITY_EDIT_SITE){
            if (resultCode==RESULT_OK){
                boolean deleted =data.getBooleanExtra("deleted", false);
                if(deleted){
                    int idDeleted=data.getIntExtra("siteid",0);
                    updateAdapter(idDeleted);
                    regolaHeader(); // aggiorno il numero di siti visualizzato
                }else{
                    refreshAdapter();
                }
            }
        }

        // new site
        if (requestCode==ACTIVITY_NEW_SITE){
            if (resultCode==RESULT_OK){
                int idCreated=data.getIntExtra("siteid",0);
                final Site site = DB.getSite(idCreated);
                SiteModel model = new SiteModel(site);
                getListAdapter().add(model);
                getListAdapter().notifyDataSetChanged();
                regolaHeader(); // aggiorno il numero di siti visualizzato

                // scroll listview to bottom
                getListView().post(new Runnable() {
                    @Override
                    public void run() {
                        // Select the last row so it will scroll into view...
                        getListView().setSelection(getListAdapter().getCount() - 1);
                    }
                });


                // dopo aver creato il nuovo sito propone di connettersi
                if (Lib.isNetworkAvailable()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Nuovo sito creato");
                    builder.setMessage("Vuoi connetterti ora per scaricare la configurazione?");
                    builder.setNegativeButton("Dopo",null);
                    builder.setPositiveButton("Connetti", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClass(SitesListActivity.this, StartSiteActivity.class);
                            intent.putExtra("siteid", site.getId());
                            startActivity(intent);
                        }
                    });
                    builder.show();

                }

            }
        }



    }


    /**
     * AsyncTask per caricare i dati nell'adapter
     */
    class PopulateTask extends AbsPopulateTask {

        @Override
        public void populateAdapter() {

            Site[] sites = DB.getSites();
            publishProgress(-2, sites.length);

            SiteModel model;
            getListAdapter().clear();
            int i = 0;
            for (final Site site : sites) {
                model = new SiteModel(site);
                getListAdapter().add(model);
                i++;
                publishProgress(-3, i);

                if (isCancelled()) {
                    break;
                }

            }

        }

        @Override
        public String getType() {
            return "siti";
        }

    }

    /**
     * Aggiorna il contenuto dell'adapter rispetto al database.
     * Modifica gli item esistenti
     * Non aggiunge e non elimina mai.
     */
    private void refreshAdapter() {
        for (int pos = 0; pos < getListAdapter().getCount(); pos++) {
            SiteModel model = (SiteModel) getListAdapter().getItem(pos);
            Site site = model.getSite();
            Site dbSite = DB.getSite(site.getId());
            if (dbSite != null) {
                model.updateFromDB();
            }
        }
        getListAdapter().notifyDataSetChanged();
    }

    /**
     * Aggiorna l'adapter dopo una cancellazione
     */
    private void updateAdapter(int iddeleted){
        for (int pos = 0; pos < getListAdapter().getCount(); pos++) {
            SiteModel model = (SiteModel) getListAdapter().getItem(pos);
            Site site = model.getSite();
            if (site.getId() == iddeleted) {
                Object obj=getListAdapter().getItem(pos);
                getListAdapter().remove(obj);
                getListAdapter().notifyDataSetChanged();
                break;
            }
        }
    }

    private void newSite(){
        Intent intent = new Intent();
        intent.setClass(this, EditSiteActivity.class);
        intent.putExtra("usedelete", false);
        intent.putExtra("usesave",true);
        SitesListActivity.this.startActivityForResult(intent, ACTIVITY_NEW_SITE);
    }

    @Override
    public String getActionBarSubtitle() {
        return "Siti";
    }

    @Override
    public String getHeadline2() {
        return "Siti";
    }

    @Override
    public String getHeadline3() {
        return null;
    }

    @Override
    public int getNumItemsInList() {
        return DB.getSitesCount();
    }

    @Override
    public String getItemsType() {
        return "siti";
    }

}
