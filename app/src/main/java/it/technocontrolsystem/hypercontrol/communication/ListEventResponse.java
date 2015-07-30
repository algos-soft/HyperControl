package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.domain.Event;

/**
 * Risposta ad una ListEventRequest
 */
public class ListEventResponse extends Response {

    private ArrayList<Event> events;

    public ListEventResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore
        if (events == null) {
            events = new ArrayList<Event>();
        }

        if (gotoFirstTag("Eventi")){
            readEventi();
        }

//    XmlPullParser parser = getParser();
//    boolean stop = false;
//         while(!stop){
//
//            String name = parser.getName();
//
//             if (name!=null){
//            if (name.equals("Eventi")) {
//               readEventi();
//            }else{
//                stop = true;
//            }
//             }else{
//            stop = true;
//              }
//
//             parser.next();
//
//
//        }
    }

    /**
     * Legge tra i TAG "Eventi"
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected void readEventi() throws XmlPullParserException, IOException {

        boolean stop = false;
        while (!stop) {
            boolean found = gotoNextStart("Evento");
            if (found){
                Event event = new Event();
                events.add(event);
                readEvento(event);
             }
            else{
                stop = true;
            }

        }
    }

    protected void readEvento(Event event) throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Id")) {
                    event.setId(Integer.parseInt(parser.nextText()));
                   // setNumber(Integer.parseInt(parser.nextText()));
                } else if (name.equals("Data")) {
                    event.setData(parser.nextText());
                   // setDate(parser.nextText());
                }else if (name.equals("Tipo")) {
                    event.setTipo(Integer.parseInt(parser.nextText()));
                    //setNumber(Integer.parseInt(parser.nextText()));
                }else if (name.equals("Testo")) {
                    event.setTesto(parser.nextText());
                    //setText(parser.nextText());
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    public Event[] getEvents() {
        return events.toArray(new Event [0]);
    }

}
