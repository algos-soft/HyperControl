/*
package it.technocontrolsystem.hypercontrol;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.ListAlarmsRequest;
import it.technocontrolsystem.hypercontrol.communication.ListAlarmsResponse;
import it.technocontrolsystem.hypercontrol.communication.ListEventResponse;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Alarm;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Event;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;

*/
/**
 * List of alarm
 *//*

public class AlarmActivity extends Activity {


    Connection conn = null;
    private int idSite = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AssetManager am = getAssets();
        Typeface type;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TextView title=(TextView) findViewById(R.id.alarmtitle);
        type = Typeface.createFromAsset(am, "FFF_Tusj.ttf");
        title.setTypeface(type);

        try {
            this.idSite = getIntent().getIntExtra("siteid", 0);
            Site site = DB.getSite(idSite);
            conn = new Connection(site);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request;
        Response resp;
        TextView risposta;
        ScrollView scrollAlarms = (ScrollView) findViewById(R.id.scrollalarm);
        LinearLayout alarmsLinear = new LinearLayout(this);
        alarmsLinear.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        scrollAlarms.setLayoutParams(params);
        scrollAlarms.addView(alarmsLinear);
        request = new ListAlarmsRequest();

        resp = conn.sendRequest(request);
        ListAlarmsResponse aResp = (ListAlarmsResponse) resp;

        int alarm;
        int manomissione;
        int numeroSensore;
        int numeroArea;
        int numeroImpianto;

        Alarm[] alarms = aResp.getAlarms();
        for (Alarm al : alarms) {

            numeroImpianto = al.getPlantNum();
            String num = String.valueOf(numeroImpianto);
            numeroArea = al.getAreaNum();
            String numArea = String.valueOf(numeroArea);
            numeroSensore = al.getSensNum();
            String numSensore = String.valueOf(numeroSensore);
            manomissione = al.getManomissione();
            String mano = String.valueOf(manomissione);
            alarm = al.getAlarm();
            String valal = String.valueOf(alarm);

            risposta = new TextView(this);
            risposta.setTextColor(Color.parseColor("#ff4ae02e"));
            alarmsLinear.addView(risposta);
            risposta.setText("Impianto: " + num + " Area: " + numArea + " Sensore: " + numSensore + " Codice manomissione:  " + mano + " Codice allarme: " + valal);


        }


    }


}*/
