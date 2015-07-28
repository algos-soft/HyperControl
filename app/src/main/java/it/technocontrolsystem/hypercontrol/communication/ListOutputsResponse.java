package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Output;

/**
 *
 */
public class ListOutputsResponse extends Response {

    private ArrayList<Output> outputs;


    public ListOutputsResponse(String string) {
        super(string);
    }


    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
        if (outputs == null) {
            outputs = new ArrayList<Output>();
        }
        String name = getParser().getName();
        if (gotoFirstTag("Uscite")) {
            readOutputs();
        }

    }

    /**
     * Legge tra i TAG "Uscite"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readOutputs() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            // se è già uscita non avanza
            boolean found = true;
            if (!getParser().getName().equals("Uscita")) {
                found = gotoNextTag("Uscita");
            }

            // acquisisce il sensore
            if (found) {
                Output output = new Output();
                outputs.add(output);
                readOutput(output);
            } else {
                stop = true;
            }

        }
    }

    private void readOutput(Output output) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;

        while (!stop) {

            parser.next();

            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numSensor = Integer.parseInt(parser.nextText());
                    output.setNumber(numSensor);
                } else if (name.equals("Nome")) {
                    output.setName(parser.nextText());
                } else if (name.equals("Impianto")) {
                    readPlants(output);
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

    private void readPlants(Output output) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            String name = getParser().getName();
            if (name.equals("Impianto")) {
                readPlant(output);
                getParser().next();
            } else {
                stop = true;
            }
        }
    }


    private void readPlant(Output output) throws XmlPullParserException, IOException {
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
                    output.setNumPlant(numPlant);
                } else if (name.equals("Area")) {
                    readArea(output);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }


    private void readArea(Output output) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            getParser().next();

            String name = getParser().getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numArea = Integer.parseInt(getParser().nextText());
                    output.addAreaNumber(numArea);
                } else {
                    stop = true;
                }
            } else {
                stop = true;
            }
        }
    }


    public Output[] getOutputs() {
        return outputs.toArray(new Output[0]);
    }
}
