package it.technocontrolsystem.hypercontrol.communication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import java.util.Locale;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.activity.StartSiteActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.domain.Menu;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 * Created by alex on 5-07-2015.
 */
public class SyncSiteTask  extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Site site;
    private Runnable successRunnable;
    private Runnable failRunnable;
    ProgressDialog progress;
    Exception exception;
    boolean success=false;
    private PowerManager.WakeLock lock;

    public SyncSiteTask(Context context, Site site, Runnable successRunnable, Runnable failRunnable) {
        this.context=context;
        this.site=site;
        this.successRunnable=successRunnable;
        this.failRunnable=failRunnable;
    }

    @Override
    protected void onPreExecute() {
        lock= Lib.acquireWakeLock();
        progress = ProgressDialog.show(context, null, "acquisizione dati in corso...", true);
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            HyperControlApp.setLastSyncDBError(null);

            // Invia alla centrale una richiesta di impostazione lingua
            languageCheck();

            // sincronizza il db con la centrale
            syncDB();

            success=true;

        } catch (Exception e) {
            exception=e;
            success=false;
            HyperControlApp.setLastSyncDBError(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progress.dismiss();

        if(success) {
            if(successRunnable!=null){
                successRunnable.run();
            }
        }else{
            if(failRunnable!=null){
                failRunnable.run();
            }
        }

        Lib.releaseWakeLock(lock);

    }


    /**
     * Invia alla centrale una richiesta di
     * impostazione lingua
     */
    private void languageCheck() {
        String lan;
        LanguageRequest request;


        Connection conn = getConnection();

        request = new LanguageRequest();
        lan = Locale.getDefault().getLanguage();
        if (lan.equals("IT") || lan.equals("it")) {
            request.setLan("IT");
        } else {
            request.setLan("EN");
        }
        conn.sendRequest(request);
    }

    /**
     * Controlla la versione della configurazione sulla centrale
     * Se diverso riempie/sovrascrive il DB
     */
    private void syncDB() throws Exception {
        Request request;
        Response resp;

        request = new VersionRequest();
        resp = getConnection().sendRequest(request);
        VersionResponse vResp = (VersionResponse) resp;

        if (vResp != null) {

            if (vResp.isSuccess()) {

                int remoteConfig = vResp.getConfigurationNumber();
                int localConfig = getSite().getVersion();

                if (localConfig != remoteConfig) {
                    fillDB();
                    Site site = getSite();
                    site.setVersion(remoteConfig);
                    DB.saveSite(site);
                }


            } else {  // version request failed
                throw new Exception(resp.getText());
            }

        } else {  // version response null
            throw new Exception("Version Request timeout");
        }

    }



    /**
     * Svuota il DB e lo riempie recuperando i dati dalla centrale
     */
    private void fillDB() throws Exception {
        // Cancella tutti gli impianti e tutti i record collegati a cascata
        deleteAllRecords();

        // Riempie il DB con impianti e aree
        fillPlantsAndAreas();

        // Riempie il DB con i sensori e la tabella di incrocio aree-sensori
        fillSensors();

        // Riempie il DB con le schede
        fillBoards();

        // Riempie il DB con i menu
        fillMenus();

    }


    public void deleteAllRecords() {
        // Cancella tutti gli impianti e tutti i record collegati a cascata
        Plant[] plants = getSite().getPlants();
        for (Plant plant : plants) {
            DB.deletePlant(plant.getId());
        }
    }

    /**
     * Recupera impianti ed aree dalla centrale
     * e riempie le corrispondenti tabelle
     */
    private void fillPlantsAndAreas() throws Exception {
        Request req = new ListPlantsRequest();
        Response resp = getConnection().sendRequest(req);
        if (resp != null) {
            ListPlantsResponse pResp = (ListPlantsResponse) resp;
            for (Plant p : pResp.getPlants()) {
                p.setIdSite(getSite().getId());
                int idPlant = DB.savePlant(p);
                p.setId(idPlant);
                for (Area area : p.getAreas()) {
                    area.setIdPlant(idPlant);
                    int idArea = DB.saveArea(area);
                    area.setId(idArea);
                }
            }
        } else {
            throw new Exception("List Plants Request timeout");
        }
    }


    /**
     * Riempie la tabella sensori e la tabella di incrocio area-sensori
     */
    private void fillSensors() throws Exception {
        Request req = new ListSensorsRequest();
        Response resp = getConnection().sendRequest(req);
        if (resp != null) {
            ListSensorsResponse sResp = (ListSensorsResponse) resp;
            for (Sensor sensor : sResp.getSensors()) {

                int idSite = getSite().getId();
                int numPlant = sensor.getNumPlant();
                Plant plant = DB.getPlantBySiteAndNumber(idSite, numPlant);

                if (plant != null) {

                    // recupera l'id plant e crea il sensore sul db
                    sensor.setIdPlant(plant.getId());
                    int id = DB.saveSensor(sensor);
                    sensor.setId(id);

                    // crea i record nella tabella di incrocio
                    Integer[] areaNums = sensor.getAreaNums();
                    for (int areaNum : areaNums) {
                        Area area = DB.getAreaByIdPlantAndAreaNumber(plant.getId(), areaNum);
                        DB.saveAreaSensor(area.getId(), sensor.getId());
                    }

                }
            }
        }

    }

    /**
     * Riempie la tabella schede
     */
    private void fillBoards() {
        Request req = new ListBoardsRequest();
        Response resp = getConnection().sendRequest(req);
        if (resp != null) {
            ListBoardsResponse sResp = (ListBoardsResponse) resp;
            for (Board board : sResp.getBoards()) {
                board.setIdSite(getSite().getId());
                DB.saveBoard(board);
            }
        }
    }

    /**
     * Riempie la tabella menu
     */
    private void fillMenus() {
        Request req = new ListMenuRequest();
        Response resp = getConnection().sendRequest(req);
        if (resp != null) {
            ListMenuResponse sResp = (ListMenuResponse) resp;
            for (Menu menu : sResp.getMenus()) {
                menu.setIdSite(getSite().getId());
                DB.saveMenu(menu);
            }
        }
    }


    private Connection getConnection(){
        return HyperControlApp.getConnection();
    }

    private Site getSite(){
        return site;
    }

}
