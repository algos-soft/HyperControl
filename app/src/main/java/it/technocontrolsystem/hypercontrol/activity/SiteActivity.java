package it.technocontrolsystem.hypercontrol.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.ConnectionTask;
import it.technocontrolsystem.hypercontrol.communication.StatusButtonListener;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.PlantDisplay;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.PlantListAdapter;
import it.technocontrolsystem.hypercontrol.model.PlantModel;

/**
 * Activity to display a single site
 */

public class SiteActivity extends HCActivity {
    private int idSite = 0;
    private int version = 0;//federico
    private static Connection conn = null;
    private static Activity activityA;
    //private ToggleButton connectButton;

    private String lastConnectionError;  // ultima eccezione nella fase di connection
    private String lastSyncDBError;// ultima eccezione nella fase di sync db


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.idSite = getIntent().getIntExtra("siteid", 0);

        if (idSite != 0) {

            setContentView(R.layout.activity_site);
            this.version = getIntent().getIntExtra("siteversion", 0);//federico
            activityA = this;

            // titolo della schermata
            TextView text = (TextView) findViewById(R.id.title);
            text.setText(getSite().getName());

//            // crea lo status indicator e lo aggiunge al placeholder nella UI
//            connectButton = (ToggleButton)findViewById(R.id.connect_button);
//            connectButton.setTextOff("OFFLINE");
//            connectButton.setTextOn("CONNECTED");
//            // se no, non si sveglia e non cambia i testi
//            connectButton.setChecked(connectButton.isChecked());


            // attacca un listener e poi rende invisibile l'icona di errore connessione
            getErrorButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorButtonClicked();
                }
            });
            getErrorButton().setVisibility(View.GONE);


            // crea l'adapter per la ListView e lo assegna
            listAdapter = new PlantListAdapter(SiteActivity.this);
            ListView list = (ListView) findViewById(R.id.list);
            list.setAdapter(listAdapter);


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PlantDisplay display = (PlantDisplay) view;
                    Intent intent = new Intent();
                    intent.setClass(SiteActivity.this, PlantActivity.class);
                    intent.putExtra("plantid", display.getPlantId());
                    startActivity(intent);
                }
            });

            Button menu = (Button) findViewById(R.id.menu);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("siteid", idSite);
                    intent.setClass(SiteActivity.this, MenuActivity.class);
                    startActivity(intent);

                }
            });


            /**
             * tenta la sincronizzazione, ma solo la prima volta
             * che l'activity viene creata, e non dopo una rotazione.
             */
            if (savedInstanceState == null) {   // nuova activity
                if (Lib.isNetworkAvailable()) {

                    // l'esecuzione di questo task determina sempre in un modo o nell'altro
                    // una chiamata a updateSiteUI() - vedi SiteActivity workflow
                    ConnectionTask task = (new ConnectionTask(this));
                    task.execute();

                } else {    // creazione post rotazione
                    updateSiteUI();
                }
            } else {    // niente connessione
                updateSiteUI();
            }


        } else {
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        // questo listener va aggiunto in onResume - in onCreate Android cerca di ripristinare lo stato
        // e se era ON invoca il listener. Ricordati che questo listener viene rimosso appena premi
        // il bottone e poi riattaccato alla fine di ConnectionTask
        getConnectButton().setOnCheckedChangeListener(new StatusButtonListener(this));
    }


    /**
     * Aggiorna la UI in base allo stato corrente
     */
    public void updateSiteUI() {

        Lib.unlockOrientation(this);    // da qui in poi sblocca l'orientamento

        // controlla l'errore connessione
        if(lastConnectionError!=null) {
            getErrorButton().setVisibility(View.VISIBLE);
        }else{
            getErrorButton().setVisibility(View.GONE);
        }

        // popola l'adapter per la ListView dal database
        populateAdapter();

        // Se la connessione è attiva, aggiorna lo stato dalla centrale
        if(conn!=null){
            listAdapter.update();
        }

    }


    private void errorButtonClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore di connessione");
        builder.setMessage(Html.fromHtml(lastConnectionError));
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("errbuttonvisibility",getErrorButton().getVisibility()==View.VISIBLE);
        outState.putString("lastConnectionError",lastConnectionError);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.getBoolean("errbuttonvisibility")){
            getErrorButton().setVisibility(View.VISIBLE);
        }else{
            getErrorButton().setVisibility(View.GONE);
        }
        lastConnectionError=savedInstanceState.getString("lastConnectionError");
    }

    //    /**
//     * Crea la connessione.
//     * Sincronizza il db con la centrale.
//     * Registra la connessione nella Activity.
//     * Aggiorna lo stato della connessione nella UI.
//     */
//    private void syncFromNetwork() {
//        String connStatus = "";
//
//        if (Lib.isNetworkAvailable()) {
//
//            ProgressDialog connDialog = null;
//            try {
//
//                connDialog = ProgressDialog.show(this, "Connessione", "apertura connessione...", true);
//                conn = new Connection(this);
//                connDialog.dismiss();
//
//                ActivityTask task = new ActivityTask();
//                task.execute();
//                connStatus = "Connesso";
//
//            } catch (Exception e) {
//                if (connDialog != null) {
//                    connDialog.dismiss();
//                }
//                e.printStackTrace();
//                connStatus = "Connessione fallita";
//                connStatus += "\n" + e.getClass().getSimpleName() + "\n" + e.getMessage();
//            }
//
//        } else {
//            connStatus = "Connessione Internet non disponibile";
//        }
//
//
//        // qui ho tutte le informazioni sulla connessione e posso
//        // regolare un indicatore grafico nella UI
//        TextView connView = (TextView) findViewById(R.id.connectionStatus);
//        connView.setText(connStatus);
//
//    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//            siteActivityStart();
//    }


//    /**
//     * Sincronizzazione / scaricamento dati dalla centrale.<br>
//     * AsyncTask che confronta la versione della configurazione locale
//     * con quella della centrale e riscarica il database se sono diverse
//     */
//    class ActivityTask extends AsyncTask<Void, Void, Void> {
//
//        ProgressDialog progress;
//
//        @Override
//        protected void onPreExecute() {
//            progress = ProgressDialog.show(SiteActivity.this, "Caricamento centrale", "caricamento in corso...", true);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                //getConnection();
//                //conn = new Connection(getSite()); VECCHIO
//
//                // sincronizza il db con la centrale
//                syncDB();
//
//                languageCheck();
//
//                // popola l'adapter per la ListView
//                populateAdapter();
//
//                // assegna le info di stato dalla centrale
//                listAdapter.update();
//
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//
//            // assegna l'adapter alla ListView
//            ListView list = (ListView) findViewById(R.id.list);
//            list.setAdapter(listAdapter);
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    PlantDisplay display = (PlantDisplay) view;
//                    Intent intent = new Intent();
//                    intent.setClass(SiteActivity.this, PlantActivity.class);
//                    intent.putExtra("plantid", display.getPlantId());
//                    startActivity(intent);
//                }
//            });
//
//
//            Button menu = (Button) findViewById(R.id.menu);
//            menu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putExtra("siteid", idSite);
//                    intent.setClass(SiteActivity.this, MenuActivity.class);
//                    startActivity(intent);
//
//                }
//            });
//
//            progress.dismiss();
//        }
//
//    }


    //smanettare per tirare fuori il database in una cartella pubblica del tablet!!!!!!!!!!!!!!

//    /**
//     * Controlla la versione della configurazione sulla centrale
//     * Se diverso riempie/sovrascrive il DB
//     */
//    private void syncDB() throws Exception {
//        Request request;
//        Response resp;
//
//        request = new VersionRequest();
//        resp = getConnection().sendRequest(request);
//        VersionResponse vResp = (VersionResponse) resp;
//
//        if (vResp != null) {
//
//            if (vResp.isSuccess()) {
//
//                int remoteConfig = vResp.getConfigurationNumber();
//                int localConfig = getSite().getVersion();
//
//                if (localConfig != remoteConfig) {
//                    fillDB();
//                    Site site = getSite();
//                    site.setVersion(remoteConfig);
//                    DB.saveSite(site);
//                }
//
//
//            } else {  // version request failed
//                throw new Exception(resp.getText());
//            }
//
//        } else {  // version response null
//            throw new Exception("Version Request timeout");
//        }
//
//    }
//
//    /**
//     * Svuota il DB e lo riempie recuperando i dati dalla centrale
//     */
//    private void fillDB() throws Exception {
//        // Cancella tutti gli impianti e tutti i record collegati a cascata
//        deleteAllRecords();
//
//        // Riempie il DB con impianti e aree
//        fillPlantsAndAreas();
//
//        // Riempie il DB con i sensori e la tabella di incrocio aree-sensori
//        fillSensors();
//
//        // Riempie il DB con le schede
//        fillBoards();
//
//        // Riempie il DB con i menu
//        fillMenus();
//
//    }
//
//
//    public void deleteAllRecords() {
//        // Cancella tutti gli impianti e tutti i record collegati a cascata
//        Plant[] plants = getSite().getPlants();
//        for (Plant plant : plants) {
//            DB.deletePlant(plant.getId());
//        }
//    }
//
//    /**
//     * Recupera impianti ed aree dalla centrale
//     * e riempie le corrispondenti tabelle
//     */
//    private void fillPlantsAndAreas() throws Exception {
//        Request req = new ListPlantsRequest();
//        Response resp = getConnection().sendRequest(req);
//        if (resp != null) {
//            ListPlantsResponse pResp = (ListPlantsResponse) resp;
//            for (Plant p : pResp.getPlants()) {
//                p.setIdSite(getSite().getId());
//                int idPlant = DB.savePlant(p);
//                p.setId(idPlant);
//                for (Area area : p.getAreas()) {
//                    area.setIdPlant(idPlant);
//                    int idArea = DB.saveArea(area);
//                    area.setId(idArea);
//                }
//            }
//        } else {
//            throw new Exception("List Plants Request timeout");
//        }
//    }
//
//
//    /**
//     * Riempie la tabella sensori e la tabella di incrocio area-sensori
//     */
//    private void fillSensors() throws Exception {
//        Request req = new ListSensorsRequest();
//        Response resp = getConnection().sendRequest(req);
//        if (resp != null) {
//            ListSensorsResponse sResp = (ListSensorsResponse) resp;
//            for (Sensor sensor : sResp.getSensors()) {
//
//                int idSite = getSite().getId();
//                int numPlant = sensor.getNumPlant();
//                Plant plant = DB.getPlantBySiteAndNumber(idSite, numPlant);
//
//                if (plant != null) {
//
//                    // recupera l'id plant e crea il sensore sul db
//                    sensor.setIdPlant(plant.getId());
//                    int id = DB.saveSensor(sensor);
//                    sensor.setId(id);
//
//                    // crea i record nella tabella di incrocio
//                    Integer[] areaNums = sensor.getAreaNums();
//                    for (int areaNum : areaNums) {
//                        Area area = DB.getAreaByIdPlantAndAreaNumber(plant.getId(), areaNum);
//                        DB.saveAreaSensor(area.getId(), sensor.getId());
//                    }
//
//                }
//            }
//        }
//
//    }
//
//    /**
//     * Riempie la tabella schede
//     */
//    private void fillBoards() {
//        Request req = new ListBoardsRequest();
//        Response resp = getConnection().sendRequest(req);
//        if (resp != null) {
//            ListBoardsResponse sResp = (ListBoardsResponse) resp;
//            for (Board board : sResp.getBoards()) {
//                board.setIdSite(idSite);
//                DB.saveBoard(board);
//            }
//        }
//    }
//
//    /**
//     * Riempie la tabella menu
//     */
//    private void fillMenus() {
//        Request req = new ListMenuRequest();
//        Response resp = getConnection().sendRequest(req);
//        if (resp != null) {
//            ListMenuResponse sResp = (ListMenuResponse) resp;
//            for (Menu menu : sResp.getMenus()) {
//                menu.setIdSite(idSite);
//                DB.saveMenu(menu);
//            }
//        }
//    }


    /**
     * Carica gli impianti del sito dal DB nell'adapter
     */
    private void populateAdapter() {
        listAdapter.clear();
        Plant[] plants = DB.getPlants(idSite);
        PlantModel model;
        for (final Plant plant : plants) {
            model = new PlantModel(plant);
            listAdapter.add(model);
        }
    }

  /*  //federico
    private void loadEventText() {
        Request request;
        Response resp;
        TextView risposta;
        try {
            conn = new Connection(getSite());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ScrollView scrollEvents = (ScrollView) findViewById(R.id.text_scroll);
        LinearLayout eventsLinear = new LinearLayout(this);
        eventsLinear.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        scrollEvents.setLayoutParams(params);
        scrollEvents.addView(eventsLinear);


        request = new ListEventRequest(10);

        resp = conn.sendRequest(request);
        ListEventResponse eResp = (ListEventResponse) resp;
        String event;
        Event[] events = eResp.getEvents();
        for (Event ev : events) {
            risposta = new TextView(this);
            risposta.setTextColor(Color.parseColor("#ff4ae02e"));
            event = ev.getTesto();
            eventsLinear.addView(risposta);
            risposta.setText(event);

        }

    }*/

    public Site getSite() {
        return DB.getSite(idSite);
    }


    public ToggleButton getConnectButton(){
        return (ToggleButton)findViewById(R.id.connect_button);
    }

    public ImageButton getErrorButton(){
        return (ImageButton)findViewById(R.id.statusAlertButton);

    }


//    /**
//     * Invia alla centrale una richiesta di impostazione lingua
//     */
//    private void languageCheck() {
//        String lan;
//        LanguageRequest request;
//
//
//        Connection conn = getConnection();
//
//        request = new LanguageRequest();
//        lan = Locale.getDefault().getLanguage();
//        if (lan.equals("IT") || lan.equals("it")) {
//            request.setLan("IT");
//        } else {
//            request.setLan("EN");
//        }
//        conn.sendRequest(request);
//    }

    public static Connection getConnection() {
        return conn;
    }

    public static void setConnection(Connection conn) {
        SiteActivity.conn = conn;
    }


    public String getLastConnectionError() {
        return lastConnectionError;
    }

    public void setLastConnectionError(String lastConnectionError) {
        this.lastConnectionError = lastConnectionError;
    }

    public String getLastSyncDBError() {
        return lastSyncDBError;
    }

    public void setLastSyncDBError(String lastSyncDBError) {
        this.lastSyncDBError = lastSyncDBError;
    }

    public int getLiveCode() {
        return 1;
    }

    @Override
    public int getParamPlantNumCode() {
        return -1;
    }

    @Override
    public int getParamAreaNumCode() {
        return -1;
    }

    public static Activity getInstance() {
        return activityA;
    }
}
