package it.technocontrolsystem.hypercontrol.communication;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Output;
import it.technocontrolsystem.hypercontrol.domain.Sensor;

/**
 * Risposta ad una ListSensorsRequest
 */
public class ListSensorsResponse extends Response {

    private ArrayList<Sensor> sensors;
    private Output.PlantEntry currentPlantEntry;


    public ListSensorsResponse(String string) {
        super(string);
    }


    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
        if (sensors == null) {
            sensors = new ArrayList();
        }

        if (gotoFirstTag("Sensori")) {
            readSensors();
        }

    }


    /**
     * Legge tutti i tag <Sensore></Sensore>
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readSensors() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            // se è già sensore non avanza
            boolean found = true;
            String name = getParser().getName();
            if(name!=null){
                if (!name.equals("Sensore")) {
                    found = gotoNextStart("Sensore");
                }
            }else{
                found=false;
            }

            // acquisisce il sensore
            if (found) {
                Sensor sens = new Sensor();
                sensors.add(sens);
                readSensor(sens);
                Log.d("CREATE SENSOR",sens.getNumber()+" "+sens.getName());

             } else {
                stop = true;
            }

        }
    }

    /**
     * Legge un singolo tag <Sensore></Sensore>
     */
    private void readSensor(Sensor sensor) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;


        while (!stop) {

            gotoNextStart();

            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numSensor = Integer.parseInt(parser.nextText());
                    sensor.setNumber(numSensor);
                } else if (name.equals("Nome")) {
                    String s=parser.nextText();
                    sensor.setName(s);
                } else if (name.equals("Tipo")) {
                    int type=Integer.parseInt(parser.nextText());
                    sensor.setTipo(type);
                } else if (name.equals("Impianto")) {
                    readPlants(sensor);
                    stop = true;
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }


//        try {
//
//        }catch (Exception e){
//            System.out.println(e);
//        }


    }



    /**
     * Legge tutti i Tag <Impianto></Impianto>
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readPlants(Sensor sensor) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            String name = getParser().getName();
            if (name.equals("Impianto")) {
                readPlant(sensor);
                if (!getParser().getName().equals("Impianto")) {
                    getParser().next();
                }
            } else {
                stop = true;
            }
        }
    }


    /**
     * Legge un singolo tag <Impianto></Impianto>
     */
    private void readPlant(Sensor sensor) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            getParser().next();

            String name = getParser().getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    String text = getParser().nextText();
                    int numPlant = Integer.parseInt(text);
                    currentPlantEntry = sensor.addPlantEntry(numPlant);
                } else if (name.equals("Area")) {
                    readAreas();
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }

    /**
     * Legge tutti i Tag <Area></Area>
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readAreas() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            String name = getParser().getName();
            if (name.equals("Area")) {
                readArea();
                getParser().next();
            } else {
                stop = true;
            }
        }
    }


    /**
     * Legge un singolo Tag <Area></Area>
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readArea() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            getParser().next();

            String name = getParser().getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numArea = Integer.parseInt(getParser().nextText());
                    currentPlantEntry.addArea(numArea);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }


    public Sensor[] getSensors() {
        return sensors.toArray(new Sensor[0]);
    }
}
