package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Sensor;

/**
 * Risposta ad una ListSensorsRequest
 */
public class ListSensorsResponse extends Response {

    private ArrayList<Sensor> sensors;


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
            sensors = new ArrayList<Sensor>();
        }
        String name = getParser().getName();
        if (gotoFirstTag("Sensori")) {
            readSensors();
        }

    }


    /**
     * Legge tra i TAG "Sensori"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readSensors() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            // se è già sensore non avanza
            boolean found = true;
            if (!getParser().getName().equals("Sensore")) {
                found = gotoNextTag("Sensore");
            }

            // acquisisce il sensore
            if (found) {
                Sensor sens = new Sensor();
                sensors.add(sens);
                readSensor(sens);
            } else {
                stop = true;
            }

        }
    }


    private void readSensor(Sensor sensor) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;

        while (!stop) {

            parser.next();

            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numSensor = Integer.parseInt(parser.nextText());
                    sensor.setNumber(numSensor);
                } else if (name.equals("Nome")) {
                    sensor.setName(parser.nextText());
                } else if (name.equals("Tipo")) {
                   //String type=parser.nextText();
                   // int code=SensorType.getCode(type);
                   //sensor.setTipo(code);
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
    }



    /**
     * Legge tutti i Tag "Impianto"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readPlants(Sensor sensor) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            String name = getParser().getName();
            if (name.equals("Impianto")) {
                //Area area = new Area();
                //plantNumber.addArea(area);
                readPlant(sensor);
                getParser().next();
            } else {
                stop = true;
            }
        }
    }


    private void readPlant(Sensor sensor) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            // se è già area non avanza
            boolean found = true;
            if (!getParser().getName().equals("Area")) {
                getParser().next();
            }

            String name = getParser().getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numPlant = Integer.parseInt(getParser().nextText());
                    sensor.setNumPlant(numPlant);
                } else if (name.equals("Area")) {
                    readArea(sensor);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }


    private void readArea(Sensor sensor) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            getParser().next();

            String name = getParser().getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numArea = Integer.parseInt(getParser().nextText());
                    sensor.addAreaNumber(numArea);
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
