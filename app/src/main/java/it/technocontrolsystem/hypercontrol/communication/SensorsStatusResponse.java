package it.technocontrolsystem.hypercontrol.communication;

import android.support.v4.util.SimpleArrayMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Risposta ad una SensorsStatusRequest
 */
public class SensorsStatusResponse extends Response{
    private HashMap<Integer,SensorModel> sensors;
    boolean bool;
    String string;

    public SensorsStatusResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();
        XmlPullParser parser = getParser();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
        if (sensors == null) {
            sensors = new HashMap<>();
        }

        if (gotoFirstTag("Sensori")) {
            readSensori();
        }
    }

    private void readSensori()throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            boolean found = gotoNextTag("Sensore");
            if (found) {
                SensorModel sensor = new SensorModel(null);
                int num=readSensore(sensor);
                sensors.put(num,sensor);
            } else {
                stop = true;
            }
        }
    }

    private int readSensore(SensorModel sensor)throws XmlPullParserException, IOException {
        int numSensor=0;
        int  num;


        boolean stop = false;
        while (!stop) {
            XmlPullParser parser = getParser();
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                   numSensor=Integer.parseInt(parser.nextText());
                } else if (name.equals("Valore")) {
                   string=parser.nextText();
                   sensor.setValue(string);
                }else if (name.equals("Allarme")) {
                    num = Integer.parseInt(parser.nextText());
                    bool=(num==1);
                    sensor.setAlarm(bool);
                }else if (name.equals("Manomissione")) {
                    num = Integer.parseInt(parser.nextText());
                    bool =(num==1);
                    sensor.setTamper(bool);
                }else if (name.equals("Abilitato")) {
                    num = Integer.parseInt(parser.nextText());
                    sensor.setStatus(num);
                }else if (name.equals("Test")) {
                    num = Integer.parseInt(parser.nextText());
                    bool =(num==1);
                    sensor.setTest(bool);
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    return numSensor;
    }
    public boolean isAlarm(){
        return bool;
    }

    public HashMap<Integer, SensorModel> getResponseMap() {
        return sensors;
    }
}
