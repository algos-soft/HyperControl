package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Plant;

/**
 *
 */
public class LiveMessage extends Response {

    public LiveMessage(String string) {
        super(string);
    }

    public Integer[] getPlantNumbers() throws XmlPullParserException, IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        boolean stop = false;
        boolean found;
        while (!stop) {
            found = gotoNextTag("Impianto");
            if (found) {
                found = gotoNextTag("Numero");
                if (found) {
                    int num = Integer.parseInt(getParser().nextText());
                    numbers.add(num);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
        return numbers.toArray(new Integer[0]);
    }

    public Integer[] getAreaNumbers() throws XmlPullParserException, IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        boolean stop = false;
        boolean found;
        while (!stop) {
            found = gotoNextTag("Area");
            if (found) {
                found = gotoNextTag("NumeroArea");
                if (found) {
                    int num = Integer.parseInt(getParser().nextText());
                    numbers.add(num);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
        return numbers.toArray(new Integer[0]);
    }

    public Integer[] getSensorNumbers() throws XmlPullParserException, IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        boolean stop = false;
        boolean found;
        while (!stop) {
            found = gotoNextTag("Sensore");
            if (found) {
                found = gotoNextTag("Numero");
                if (found) {
                    int num = Integer.parseInt(getParser().nextText());
                    numbers.add(num);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
        return numbers.toArray(new Integer[0]);
    }

    public Integer[] getBoardNumbers() throws XmlPullParserException, IOException {
        ArrayList<Integer> numbers = new ArrayList<>();
        boolean stop = false;
        boolean found;
        while (!stop) {
            found = gotoNextTag("Scheda");
            if (found) {
                found = gotoNextTag("Numero");
                if (found) {
                    int num = Integer.parseInt(getParser().nextText());
                    numbers.add(num);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
        return numbers.toArray(new Integer[0]);
    }
}
