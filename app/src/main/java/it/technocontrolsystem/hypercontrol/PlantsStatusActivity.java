/*
package it.technocontrolsystem.hypercontrol;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.PlantsStatusRequest;
import it.technocontrolsystem.hypercontrol.communication.PlantsStatusResponse;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.domain.Site;

*/
/**
 * Plants Status
 *//*

public class PlantsStatusActivity extends Activity {
    int numpl;
    int numar;
    Connection conn = null;
    private int idSite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plants_status);


        Button richiedi = (Button) findViewById(R.id.btn_plantstate);
        richiedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reqState();
            }

        });
    }


    private void reqState() {
        int numeropl;
        int statopl;
        int allarmepl;
        int numeroar;
        int statoar;
        int allarmear;
        String statear = null;
        String statepl=null;
        final EditText plantnum = (EditText) findViewById(R.id.edit_plantnumber);
        final EditText areanum = (EditText) findViewById(R.id.edit_areanumber);

        Request request;
        Response resp;
        TextView numeroimpianto;
        TextView statoimpianto;
        TextView numeroarea;
        TextView statoarea;
        LinearLayout statePlantsHorizontal;
        LinearLayout stateAreasHorizontal;

        String plantstr = String.valueOf(plantnum.getText());
        if (!plantstr.equals("")) {
            numpl = Integer.valueOf(plantstr);
        } else {
            numpl = -1;
        }

        String areastr = String.valueOf(areanum.getText());
        if (!areastr.equals("")) {
            numar = Integer.valueOf(areastr);
        } else {
            numar = -1;
        }


        try {
            idSite = getIntent().getIntExtra("siteid", 0);
            Site site = DB.getSite(idSite);
            conn = new Connection(site);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LinearLayout statePlantsLinear = (LinearLayout) findViewById(R.id.stateplantspanel);
        LinearLayout stateAreasLinear = (LinearLayout) findViewById(R.id.stateareaspanel);

        request = new PlantsStatusRequest();
        ((PlantsStatusRequest) request).setPlantNumber(numpl);
        ((PlantsStatusRequest) request).setArea(numar);
        resp = conn.sendRequest(request);
        PlantsStatusResponse planResp = (PlantsStatusResponse) resp;

       Plant[] plants = planResp.getPlants();

        for (Plant pl : plants) {

            numeropl = pl.getNumber();
            Plant plant=DB.getPlantBySiteAndNumber(idSite, numeropl);
            String num=plant.getName();

            statopl = pl.getState();

            if(statopl==0){
                statepl="Disinserito";
            }
            if(statopl==1){
                statepl="Parziale";
            }
            if(statopl==2){
                statepl="Totale";
            }

            allarmepl = pl.getAlarm();

            Area[] areas= pl.getAreas();
            for (Area ar : areas) {

                numeroar = ar.getNumber();
                Area area=DB.getAreaByIdPlantAndAreaNumber(plant.getId(),numeroar);
                String numear = area.getName();

                statoar = ar.getState();
                if(statoar==0){
                statear="Disinserita";
                }
                if(statoar==1){
                statear="Parziale";
                }
                if(statoar==2){
                statear="Totale";
                }


                allarmear = ar.getAlarm();

                statePlantsHorizontal=new LinearLayout(this);
                stateAreasHorizontal=new LinearLayout(this);

                statePlantsLinear.addView(statePlantsHorizontal);
                stateAreasLinear.addView(stateAreasHorizontal);

                statePlantsHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
                stateAreasHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

                numeroimpianto = new TextView(this);
                statoimpianto = new TextView(this);
                numeroarea = new TextView(this);
                statoarea = new TextView(this);


                numeroimpianto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                numeroimpianto.setGravity(Gravity.CENTER);
                statoimpianto.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                statoimpianto.setGravity(Gravity.CENTER);
                numeroarea.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                numeroarea.setGravity(Gravity.CENTER);
                statoarea.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                statoarea.setGravity(Gravity.CENTER);

                numeroimpianto.setSingleLine(true);
                numeroarea.setSingleLine(true);

                statoimpianto.setTextColor(Color.parseColor("#ff4ae02e"));
                numeroimpianto.setTextColor(Color.parseColor("#ff4ae02e"));
                statoarea.setTextColor(Color.parseColor("#ff4ae02e"));
                numeroarea.setTextColor(Color.parseColor("#ff4ae02e"));

                if(allarmepl==1) {
                numeroimpianto.setBackgroundColor(Color.RED);
                numeroimpianto.getBackground().setAlpha(25);
                statoimpianto.setBackgroundColor(Color.RED);
                statoimpianto.getBackground().setAlpha(25);
                    if(allarmear==1){
                        numeroimpianto.setBackgroundColor(Color.RED);
                        statoimpianto.setBackgroundColor(Color.RED);
                        numeroarea.setBackgroundColor(Color.RED);
                        statoarea.setBackgroundColor(Color.RED);
                    }
                }



                LinearLayout scrolltitle=(LinearLayout)findViewById(R.id.titleofscroll);
                LinearLayout tabletitle=(LinearLayout)findViewById(R.id.tabletitle);
                scrolltitle.setVisibility(View.VISIBLE);
                tabletitle.setVisibility(View.VISIBLE);

                statePlantsHorizontal.addView(numeroimpianto);
                statePlantsHorizontal.addView(statoimpianto);

                stateAreasHorizontal.addView(numeroarea);
                stateAreasHorizontal.addView(statoarea);



                numeroimpianto.setText(num);
                statoimpianto.setText(statepl);
                numeroarea.setText(numear);
                statoarea.setText(statear);

            }
        }
    }
}
*/
