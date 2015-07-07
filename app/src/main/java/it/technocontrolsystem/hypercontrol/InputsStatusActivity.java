/*
package it.technocontrolsystem.hypercontrol;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.communication.SensorsStatusRequest;
import it.technocontrolsystem.hypercontrol.communication.SensorsStatusResponse;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.domain.Site;

*/
/**
 * Inputs Status
 *//*

public class InputsStatusActivity extends Activity {
    int numpl;
    int numar;
    Connection conn = null;
    private int idSite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputs_status);


        Button richiedi = (Button) findViewById(R.id.btn_inputstate);
        richiedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //reqState();
            }

        });
    }

//
//    private void reqState() {
//
//        Request request;
//        Response resp;
//
//        String valore;
//        String abi;
//
//        int numero;
//        int allarme;
//        int manomissione;
//        int abilitato;
//        int test;
//
//        //Sensor sensor;
//
//        LinearLayout stateInputsHorizontal;
//        ImageView image;
//        TextView numerosensore;
//        TextView valoresensore;
//        TextView statosensore;
//        final EditText plantnum = (EditText) findViewById(R.id.edit_plantnumber);
//        final EditText areanum = (EditText) findViewById(R.id.edit_areanumber);
//
//        String plantstr = String.valueOf(plantnum.getText());
//        if (!plantstr.equals("")) {
//            numpl = Integer.valueOf(plantstr);
//        } else {
//            numpl = -1;
//        }
//
//        String areastr = String.valueOf(areanum.getText());
//        if (!areastr.equals("")) {
//            numar = Integer.valueOf(areastr);
//        } else {
//            numar = -1;
//        }
//
//
//        try {
//            idSite = getIntent().getIntExtra("siteid", 0);
//            Site site = DB.getSite(idSite);
//            conn = new Connection(site);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//        LinearLayout stateInputsLinear = (LinearLayout) findViewById(R.id.stateinputspanel);
//
//        request = new SensorsStatusRequest();
//        ((SensorsStatusRequest) request).setPlant(numpl);
//        ((SensorsStatusRequest) request).setArea(numar);
//        resp = conn.sendRequest(request);
//        SensorsStatusResponse sensResp = (SensorsStatusResponse) resp;
//
//
//
//        Sensor[] sensors = sensResp.getSensors();
//        for (Sensor se : sensors) {
//
//            numero = se.getNumber();
//            String num=String.valueOf(numero);
//            //sensor=DB.getSensor(numero);
//            //String num = sensor.getName();
//
//            valore = se.getValue();
//
//            allarme = se.getAlarm();
//
//            manomissione = se.getManomissione();
//
//            abilitato = se.getAbilitato();
//            if(abilitato==1){
//                abi="Abilitato";
//            }else{
//                abi="Disabilitato";
//            }
//
//            test = se.getTest();
//
//            stateInputsHorizontal=new LinearLayout(this);
//            stateInputsHorizontal.setOrientation(LinearLayout.HORIZONTAL);
//
//            if (manomissione == 1) {
//                stateInputsHorizontal.setBackgroundColor(Color.YELLOW);
//            }
//            if (allarme == 1) {
//                stateInputsHorizontal.setBackgroundColor(Color.RED);
//
//            }
//
//
//            stateInputsLinear.addView(stateInputsHorizontal);
//
//            stateInputsHorizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
//
//            numerosensore = new TextView(this);
//            valoresensore = new TextView(this);
//            statosensore = new TextView(this);
//
//
//            numerosensore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            numerosensore.setGravity(Gravity.CENTER);
//            valoresensore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            valoresensore.setGravity(Gravity.CENTER);
//            statosensore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            statosensore.setGravity(Gravity.CENTER);
//
//            numerosensore.setTextColor(Color.parseColor("#ff4ae02e"));
//            valoresensore.setTextColor(Color.parseColor("#ff4ae02e"));
//            statosensore.setTextColor(Color.parseColor("#ff4ae02e"));
//
//            LinearLayout scrolltitle=(LinearLayout)findViewById(R.id.inputslinear);
//            LinearLayout tabletitle=(LinearLayout)findViewById(R.id.titletablelinear);
//            scrolltitle.setVisibility(View.VISIBLE);
//            tabletitle.setVisibility(View.VISIBLE);
//
//            LinearLayout imageLinear=new LinearLayout(this);
//            imageLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
//            imageLinear.setGravity(Gravity.CENTER);
//            if(test==0){
//                image=new ImageView(this);
//                image.setImageResource(R.drawable.accept);
//                imageLinear.addView(image);
//            }
//
//            stateInputsHorizontal.addView(imageLinear);
//            stateInputsHorizontal.addView(numerosensore);
//            stateInputsHorizontal.addView(valoresensore);
//            stateInputsHorizontal.addView(statosensore);
//
//
//
//           // numerosensore.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logo, 0, 0, 0);
//
//            numerosensore.setText(num);
//            valoresensore.setText(valore);
//            statosensore.setText(abi);
//         }
//    }
}


*/
