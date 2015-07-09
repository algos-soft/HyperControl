package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.communication.SensorCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Display class for sensors
 */
public class SensorDisplay extends ItemDisplay {

  /*  int areaNum;
    Area area;
    private Sensor sensor;
    Button bot;
    private int allarmesen;
    Connection conn;

*/
  /*  public SensorDisplay(Context context,Area area, Sensor sensor, Runnable infoRunnable) {
        super(context,infoRunnable);
        this.area=area;
        this.sensor=sensor;
        if (sensor==null){
            int a = 87;
        }
       // init();
    }*/
    private String value;


    public SensorDisplay(Context context, int sensorId) {
        super(context, sensorId);

        testView.setVisibility(View.GONE);
        //statusView.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        getBswitch().setVisibility(View.GONE);

        //  setNumberWidth(100);
    }


    public void update(SensorModel model) {
        setNumber(model.getNumber());
        setDescription(model.getSensor().getName());

        int statusCode = model.getStatus();

       // int iconId;


        setValueColor(model);

        String iconId;
        switch (statusCode) {
            case 0:
                //getBswitch().setChecked(false);
                //stato="Disabilitato";
                //redText.getBackground().setAlpha(100);
               // setValueColor();
                statusView.setTextColor(Color.parseColor("#ffa500"));
                iconId = "Disabilitato";

                //R.drawable.disabled;
                break;
            case 1:
             //   getBswitch().setChecked(true);
             //   greenText.getBackground().setAlpha(100);
              //  setValueColor();
                iconId ="Abilitato";
                //iconId = R.drawable.total;
                break;

            default:
             //   getBswitch().setChecked(false);
             //   getBswitch().setEnabled(false);
                iconId ="Unknown";
                //iconId = R.drawable.unknown;
                break;
        }
        setStatusIcon(iconId);

        setAlarm(model.isAlarm());

        boolean test = model.isTest();
        if (test) {
            testView.setVisibility(VISIBLE);
        }

        value = model.getValue();
        String sensorType;
        int type=model.getSensor().getTipo();
        if(type==1){
            sensorType="Digitale";
            yellowText.setVisibility(View.INVISIBLE);
            cyanText.setVisibility(View.INVISIBLE);
            valueView.setVisibility(View.INVISIBLE);

        }else if(type==2){
            sensorType="Bilanciato";
            cyanText.setVisibility(View.INVISIBLE);
            valueView.setVisibility(View.INVISIBLE);
        }else if(type==3){
            sensorType="Analogico";
             valueView.setText("" + value);
            yellowText.setVisibility(View.GONE);
            cyanText.setVisibility(View.GONE);
            redText.setVisibility(View.GONE);
            greenText.setVisibility(View.GONE);
        }else if(type==4){
            sensorType="Quadristato";

        }else{
            sensorType="";
        }
        if(type!=3) {
            value = value + " " + sensorType;
            setDetail(value);
        }else{
            setDetail("");
           detailView.setVisibility(View.GONE);
        }
    }



    public void setValueColor(SensorModel sensModel) {

        value=sensModel.getValue();
        if(value!=null){
            if(value.equals("0")){
                greenText.getBackground().setAlpha(100);
            }else if(value.equals("1")){
                redText.getBackground().setAlpha(100);
            }else if(value.equals("2")){
                yellowText.getBackground().setAlpha(100);
            }else if(value.equals("3")){
                cyanText.getBackground().setAlpha(100);
            }
        }

    }


    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {
        int sensorNumber=DB.getSensor(itemId).getNumber();

        Request req=new SensorCommandRequest(sensorNumber,4,button.isChecked());
        Response resp=SiteActivity.getConnection().sendRequest(req);

        if (!resp.isSuccess()) {
            button.setChecked(!button.isChecked());
            Toast.makeText(getContext(),resp.getText(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {

    }
}


