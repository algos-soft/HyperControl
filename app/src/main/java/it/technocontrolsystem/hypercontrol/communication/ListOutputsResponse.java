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
    private Output.PlantEntry currentPlantEntry;


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
            outputs = new ArrayList();
        }

        if (gotoFirstTag("Uscite")) {
            readOutputs();
        }

    }

    /**
     * Legge tutti i tag <Uscita>></Uscita>
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
                found = gotoNextStart("Uscita");
            }

            // acquisisce l'uscita
            if (found) {
                Output output = new Output();
                outputs.add(output);
                readOutput(output);
            } else {
                stop = true;
            }

        }
    }

    /**
     * Legge un singolo tag <Uscita></Uscita>
     */
    private void readOutput(Output output) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;

        while (!stop) {

            gotoNextStart();

            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numOutput = Integer.parseInt(parser.nextText());
                    output.setNumber(numOutput);
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
     * Legge tutti i Tag <Impianto></Impianto>
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
                if(!getParser().getName().equals("Impianto")){
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
    private void readPlant(Output output) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {

            getParser().next();

            String name = getParser().getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    String text = getParser().nextText();
                    int numPlant = Integer.parseInt(text);
                    currentPlantEntry=output.addPlantEntry(numPlant);
                } else if (name.equals("Area")) {
                    readAreas(output);
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
    private void readAreas(Output output) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            String name = getParser().getName();
            if (name.equals("Area")) {
                readArea(output);
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
    private void readArea(Output output) throws XmlPullParserException, IOException {
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


    public Output[] getOutputs() {
        return outputs.toArray(new Output[0]);
    }



}
