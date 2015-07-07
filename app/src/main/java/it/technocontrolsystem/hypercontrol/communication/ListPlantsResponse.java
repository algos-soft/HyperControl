package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Plant;

/**
 * Risposta ad una ListPlantsRequest
 */
public class ListPlantsResponse extends Response {

    private ArrayList<Plant> plants;

    public ListPlantsResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
        if (plants == null) {
            plants = new ArrayList<Plant>();
        }

        if (gotoFirstTag("Impianti")){
            readImpianti();
        }

    }


    /**
     * Legge tra i TAG "Impianti"
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readImpianti() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            boolean found = gotoNextTag("Impianto");
            if (found){
                Plant plant = new Plant();
                plants.add(plant);
                readImpianto(plant);
            }
            else{
                stop = true;
            }
        }
    }

    private void readImpianto(Plant plant) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;

        while (!stop) {

            parser.next();

            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    int numPlant = Integer.parseInt(parser.nextText());
                    plant.setNumber(numPlant);
                } else if (name.equals("Nome")) {
                    plant.setName(parser.nextText());
                } else if (name.equals("Area")) {
                    readAreas(plant);
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
     * Legge tutti i Tag "Area"
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readAreas(Plant plant) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            String name = getParser().getName();
            if (name.equals("Area")) {
                Area area = new Area();
                plant.addArea(area);
                readArea(area);
                getParser().next();
            }else{
                stop=true;
            }
        }
    }

    /**
     * Legge una singola area
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void readArea(Area area) throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            getParser().next();
            String name = getParser().getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    int num = Integer.parseInt(getParser().nextText());
                    area.setNumber(num);
                } else if (name.equals("Nome")) {
                    area.setName(getParser().nextText());
                } else{
                    stop = true;
                }
            }else{
                stop = true;
            }
        }
    }

    public Plant[] getPlants() {
        return plants.toArray(new Plant[0]);
    }


}
