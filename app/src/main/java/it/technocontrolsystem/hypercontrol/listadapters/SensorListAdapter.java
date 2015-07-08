package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.os.Debug;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.communication.SensorsStatusRequest;
import it.technocontrolsystem.hypercontrol.communication.SensorsStatusResponse;
import it.technocontrolsystem.hypercontrol.display.AnalogSensorDisplay;
import it.technocontrolsystem.hypercontrol.display.BalancedSensorDisplay;
import it.technocontrolsystem.hypercontrol.display.DigitalSensorDisplay;
import it.technocontrolsystem.hypercontrol.display.QuadriSensorDisplay;
import it.technocontrolsystem.hypercontrol.display.SensorDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.model.ModelIF;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Adapter per le liste di Sensor
 */
public class SensorListAdapter extends HCListAdapter<SensorModel>{
    Area area;

     public SensorListAdapter(Context context,Area area) {
        super(context);
        this.area=area;
    }

    @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        SensorModel model = getItem(position);
        SensorDisplay display;

         // Non usare la convertView perché c'è attaccato il listener con l'itemId
         // Creare sempre nuove view
        switch (model.getSensor().getSensorType()){
            case ANALOGIC:
                display = new AnalogSensorDisplay(getContext(), model.getSensor().getId());
                break;
            case BALANCED:
                display = new BalancedSensorDisplay(getContext(), model.getSensor().getId());
                break;
            case DIGITAL:
                display = new DigitalSensorDisplay(getContext(), model.getSensor().getId());
                break;
            case QUADRISTATE:
                display = new QuadriSensorDisplay(getContext(), model.getSensor().getId());
                break;
            default:
                display = new SensorDisplay(getContext(), model.getSensor().getId());
                break;
        }
        display.update(model);
        return display;
    }

    @Override
    public void update() {
        Connection conn = SiteActivity.getConnection();
        SensorModel model;
        SensorsStatusResponse resp;
        int numPlant=area.getPlant().getNumber();
        int numArea=area.getNumber();
        SensorsStatusRequest request = new SensorsStatusRequest(numPlant,numArea);
        resp = (SensorsStatusResponse) conn.sendRequest(request);
        HashMap<Integer,SensorModel> responseMap=resp.getResponseMap();
        SensorModel responseModel;

        for (int i = 0; i < getCount(); i++) {
            model = getItem(i);
            responseModel=responseMap.get(model.getSensor().getNumber());
            model.setStatus(responseModel.getStatus());
            model.setTamper(responseModel.isTamper());
            model.setValue(responseModel.getValue());
            model.setAlarm(responseModel.isAlarm());
            model.setTest(responseModel.isTest());
        }
    }

    @Override
    public void update(Integer[] numbers) {
        SensorModel model;
        SensorsStatusResponse resp;
        Connection conn = SiteActivity.getConnection();
        for (int number:numbers){
            model=(SensorModel) getModel(number);
            SensorsStatusRequest request = new SensorsStatusRequest(number);
            resp = (SensorsStatusResponse) conn.sendRequest(request);

            // capire se si può semplificare aggiungendo un metodo che mi permette di recuperare il singolo sensore
            HashMap<Integer,SensorModel> responseMap=resp.getResponseMap();
            SensorModel responseModel;

            responseModel=responseMap.get(model.getSensor().getNumber());
            model.setStatus(responseModel.getStatus());
            model.setTamper(responseModel.isTamper());
            model.setValue(responseModel.getValue());
            model.setAlarm(responseModel.isAlarm());
            model.setTest(responseModel.isTest());
        }

        //notifyDataSetChanged() va sempre invocato sullo UI Thread!
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {
        Integer[] numbers = message.getSensorNumbers();
        new UpdateTask().execute(numbers);
    }

}
