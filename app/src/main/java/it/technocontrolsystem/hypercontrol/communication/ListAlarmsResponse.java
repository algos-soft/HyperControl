package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Alarm;

/**
 * Risposta ad una ListAlarmsRequest
 */
public class ListAlarmsResponse extends Response {
    private ArrayList<Alarm> alarms;


    XmlPullParser parser = getParser();

    public ListAlarmsResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();


        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore

        if (alarms == null) {
            alarms = new ArrayList<Alarm>();
        }
        if (gotoFirstTag("Allarmi")) {
            readAlarm();
        }
    }

    /**
     * Legge tra i TAG "Area"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readAlarm() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            boolean found = gotoNextStart("Impianto");
            if (found) {
                Alarm alarm = new Alarm();
                alarms.add(alarm);
                readAllarmi(alarm);
                readArea(alarm);
                readSensor(alarm);
            } else {
                stop = true;
            }
        }
    }


    private void readAllarmi(Alarm alarm) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    alarm.setPlantNum(Integer.parseInt(parser.nextText()));
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }

    /**
     * Legge tra i TAG "Area"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readArea(Alarm alarm) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    alarm.setAreaNum(Integer.parseInt(parser.nextText()));

                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }


    /**
     * Legge tra i TAG "Sensore"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readSensor(Alarm alarm) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name != null) {
                if (name.equals("Sensore")) {
                } else if (name.equals("Numero")) {
                    alarm.setSensNum(Integer.parseInt(parser.nextText()));
                } else if (name.equals("Allarme")) {
                    alarm.setAlarm(Integer.valueOf(parser.nextText()));
                } else if (name.equals("Manomissione")) {
                    alarm.setManomissione(Integer.valueOf(parser.nextText()));
                } else {
                    stop = true;
                }
            }
        }
    }


    public Alarm[] getAlarms() {
        return alarms.toArray(new Alarm[0]);
    }
}
