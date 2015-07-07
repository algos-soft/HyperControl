package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.model.BoardModel;

/**
 * Risposta ad una BoardsStatusRequest
 */
public class BoardStatusResponse extends Response {
    private int status;
    public BoardStatusResponse(String string) {
        super(string);
    }


    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        // se non esiste lo crea ora
        // nel costruttore è troppo tardi perché la superclasse
        // invoca questo metodo dal costruttore

        if (gotoFirstTag("Schede")) {
            readSchede();
        }
    }

    private void readSchede() throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            boolean found = gotoNextTag("Scheda");
            if (found) {
                 readScheda();
            } else {
                stop = true;
            }
        }
    }

    private void readScheda() throws XmlPullParserException, IOException {

       boolean stop = false;
        while (!stop) {
            XmlPullParser parser = getParser();
            parser.next();
            String name = parser.getName();
            if (name != null) {
                if (name.equals("Numero")) {
                    parser.nextText();
                } else if (name.equals("Stato")) {
                    status=Integer.parseInt(parser.nextText());
                }else{
                    stop = true;
                }
            } else {
                stop = true;
            }

        }

    }

    public int getStatus() {
        return status;
    }
}