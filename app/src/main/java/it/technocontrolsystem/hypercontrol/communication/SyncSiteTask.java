package it.technocontrolsystem.hypercontrol.communication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.activity.ConfigActivity;
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
public class SyncSiteTask extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private Site site;
    private Runnable successRunnable;
    private Runnable failRunnable;
    ProgressDialog progress;
    Exception exception;
    boolean success = false;
    private PowerManager.WakeLock lock;
    private String logRow;

    private static final String TAG = "SyncDB";


    public SyncSiteTask(Context context, Site site, Runnable successRunnable, Runnable failRunnable) {
        this.context = context;
        this.site = site;
        this.successRunnable = successRunnable;
        this.failRunnable = failRunnable;

        progress = new ProgressDialog(context);
        progress.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Start sync database");
        lock = Lib.acquireWakeLock();
        progress.setTitle("Controllo configurazione");
        progress.setMessage("Verifica configurazione centrale...");
        progress.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {

            HyperControlApp.setLastSyncDBError(null);

            // Invia alla centrale una richiesta di impostazione lingua
            languageCheck();

            // sincronizza il db con la centrale
            syncDB();

            success = true;

        } catch (Exception e) {
            exception = e;
            success = false;
            HyperControlApp.setLastSyncDBError(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int updateCode = values[0];
        switch (updateCode) {
            case -1:
                progress.setTitle("Scaricamento configurazione in corso...");
                break;

            case -2:
                progress.setMessage(logRow);
                break;

        }
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(Void aVoid) {

        if (progress != null) {
            progress.dismiss();
        }

        if (success) {
            if (successRunnable != null) {
                successRunnable.run();
            }
            Log.d(TAG, "database sync successful");
        } else {
            if (failRunnable != null) {
                failRunnable.run();
            }
            Log.d(TAG, "database sync failed");
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
        request = new LanguageRequest();
        String langCode= Prefs.getPrefs().getString("language", ConfigActivity.Languages.IT.getLangCode());
        request.setLan(langCode);
//        lan = Locale.getDefault().getLanguage();
//        if (langCode.equals("IT") || lan.equals("it")) {
//            request.setLan("IT");
//        } else {
//            request.setLan("EN");
//        }
        HyperControlApp.sendRequest(request);
    }

    /**
     * Controlla la versione della configurazione sulla centrale
     * Se diverso riempie/sovrascrive il DB
     */
    private void syncDB() throws Exception {
        Request request;

        Log.d(TAG, "Sending version request");
        request = new VersionRequest();
        Response resp = HyperControlApp.sendRequest(request);
        if (resp != null) {
            VersionResponse vResp = (VersionResponse) resp;

            if (vResp != null) {

                Log.d(TAG, "Version response received");

                if (vResp.isSuccess()) {

                    int remoteConfig = vResp.getConfigurationNumber();
                    Log.d(TAG, "Remote config #" + vResp.getConfigurationNumber());
                    int localConfig = getSite().getVersion();
                    Log.d(TAG, "Local config #" + localConfig);


                    if (localConfig != remoteConfig) {

                        // tutta la creazione del sito e scaricamento dati è sotto transazione
                        // così la scrittura è molto più veloce e non rischiamo di lasciare
                        // il database scritto a metà.
                        DB.getWritableDb().beginTransaction();
                        Log.d(TAG, "Transaction started" + remoteConfig);
                        try{
                            Log.d(TAG, "Starting configuration download");
                            publishProgress(-1);    // scaricamento configurazione in corso
                            fillDB();
                            Log.d(TAG, "Set local site version number to: " + remoteConfig);
                            Site site = getSite();
                            site.setVersion(remoteConfig);
                            DB.saveSite(site);
                            DB.getWritableDb().setTransactionSuccessful();
                            Log.d(TAG, "Transaction completed successfully" + remoteConfig);
                        }finally {
                            DB.getWritableDb().endTransaction();
                        }


                    }


                } else {  // version request failed
                    throw new Exception(resp.getText());
                }

            } else {  // version response null
                throw new Exception("Version Request timeout");
            }
        }


    }


    /**
     * Svuota il DB e lo riempie recuperando i dati dalla centrale
     */
    private void fillDB() throws Exception {
        // Cancella tutti gli impianti e tutti i record collegati a cascata
        Log.d(TAG, "delete all records");
        deleteAllRecords();

        // Riempie il DB con impianti e aree
        Log.d(TAG, "start receive plants and areas");
        logRow = "trasferimento impianti ed aree...";
        publishProgress(-2);
        fillPlantsAndAreas();
        Log.d(TAG, "end receive plants and areas");

        // Riempie il DB con i sensori e la tabella di incrocio aree-sensori
        Log.d(TAG, "start receive sensors");
        logRow = "trasferimento sensori...";
        publishProgress(-2);
        fillSensors();
        Log.d(TAG, "end receive sensors");

        // Riempie il DB con le schede
        Log.d(TAG, "start receive boards");
        logRow = "trasferimento schede...";
        publishProgress(-2);
        fillBoards();
        Log.d(TAG, "end receive boards");

        // Riempie il DB con i menu
        Log.d(TAG, "start receive menus");
        logRow = "trasferimento menu...";
        publishProgress(-2);
        fillMenus();
        Log.d(TAG, "end receive menus");

    }


    public void deleteAllRecords() {
        // Cancella tutti gli impianti e tutti i record collegati a cascata
        Plant[] plants = getSite().getPlants();
        for (Plant plant : plants) {
            DB.deletePlant(plant.getId());
            Log.d(TAG, "Plant id " + plant.getId() + " " + plant.getName() + " deleted");
        }

        Log.d(TAG, plants.length + " plants deleted");

    }

    /**
     * Recupera impianti ed aree dalla centrale
     * e riempie le corrispondenti tabelle
     */
    private void fillPlantsAndAreas() throws Exception {
        String s;
        Request req = new ListPlantsRequest();
        Response resp = HyperControlApp.sendRequest(req);
        if (resp != null) {
            ListPlantsResponse pResp = (ListPlantsResponse) resp;
            for (Plant p : pResp.getPlants()) {
                p.setIdSite(getSite().getId());
                int idPlant = DB.savePlant(p);
                p.setId(idPlant);

                s = p.getId() + " " + p.getName();
                Log.d(TAG, "Plant " + s + " created");
                logRow = "Impianto id " + s + " creato";
                publishProgress(-2);

                for (Area area : p.getAreas()) {
                    area.setIdPlant(idPlant);
                    int idArea = DB.saveArea(area);
                    area.setId(idArea);

                    s = area.getId() + " " + area.getName();
                    Log.d(TAG, "Area " + s + " created");
                    logRow = "Area id " + s + " creata";
                    publishProgress(-2);

                }
            }

            Log.d(TAG, pResp.getPlants().length + " plants created");

        } else {
            throw new Exception("List Plants Request timeout");
        }
    }


    /**
     * Riempie la tabella sensori e la tabella di incrocio area-sensori
     */
    private void fillSensors() throws Exception {

        Request req = new ListSensorsRequest();
        Response resp = HyperControlApp.sendRequest(req);
        if (resp != null) {

            ListSensorsResponse sResp = (ListSensorsResponse) resp;

            int createdCount=0;

            for (Sensor sensor : sResp.getSensors()) {

                int idSite = getSite().getId();
                int numPlant = sensor.getNumPlant();
                Plant plant = DB.getPlantBySiteAndNumber(idSite, numPlant);

                if (plant != null) {

                    // recupera l'id plant e crea il sensore sul db
                    sensor.setIdPlant(plant.getId());
                    int id = DB.saveSensor(sensor);
                    sensor.setId(id);

                    String s = sensor.getId() + " " + sensor.getName();
                    Log.d(TAG, "Sensor " + s + " created");
                    logRow = "Sensore " + s + " creato";
                    publishProgress(-2);
                    createdCount++;

                    // crea i record nella tabella di incrocio
                    Integer[] areaNums = sensor.getAreaNums();
                    for (int areaNum : areaNums) {
                        Area area = DB.getAreaByIdPlantAndAreaNumber(plant.getId(), areaNum);
                        DB.saveAreaSensor(area.getId(), sensor.getId());
                    }

                }
            }

            Log.d(TAG, createdCount + " sensors created");

        }

    }

    /**
     * Riempie la tabella schede
     */
    private void fillBoards() {
        Request req = new ListBoardsRequest();
        Response resp = HyperControlApp.sendRequest(req);
        if (resp != null) {
            ListBoardsResponse sResp = (ListBoardsResponse) resp;
            for (Board board : sResp.getBoards()) {
                board.setIdSite(getSite().getId());
                DB.saveBoard(board);

                String s=board.getId() + " " + board.getName();
                Log.d(TAG, "Board "+s+" created");
                logRow = "Scheda " + s + " creata";
                publishProgress(-2);

            }
            Log.d(TAG, sResp.getBoards().length + " boards created");
        }
    }

    /**
     * Riempie la tabella menu
     */
    private void fillMenus() {
        Request req = new ListMenuRequest();
        Response resp = HyperControlApp.sendRequest(req);
        if (resp != null) {
            ListMenuResponse sResp = (ListMenuResponse) resp;
            for (Menu menu : sResp.getMenus()) {
                menu.setIdSite(getSite().getId());
                DB.saveMenu(menu);

                String s= menu.getId() + " " + menu.getName();
                Log.d(TAG, "Menu "+s+" created");
                logRow = "Menu " +s+ " creato";
                publishProgress(-2);

            }
            Log.d(TAG, sResp.getMenus().length + " menus created");
        }
    }

    private Site getSite() {
        return site;
    }

}
