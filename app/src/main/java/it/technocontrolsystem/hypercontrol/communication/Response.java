package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * A Response received from the unit.
 */
public class Response {
    private String string;
    private XmlPullParser xpp;

    private int numero;
    private int risultato;
    private String comando;
    private String testo;

    public Response(String string) {
        this.string = string;

        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            setInput();
            try {
                xpp.nextTag();
            } catch (IOException e) {
                e.printStackTrace();
            }

            parseXML();

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    private void setInput()throws XmlPullParserException {
        StringReader reader = new StringReader(this.string);
        xpp.setInput(reader);
    }




    private void parseXML() throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;

        while (!stop) {

            parser.next();

            String name = parser.getName();
            if (name!=null) {

                if (name.equals("Numero")) {
                    int num=Integer.parseInt(parser.nextText());
                    setNumero(num);
                } else if (name.equals("Comando")) {
                    readComando();
                    stop=true;
                }

            }else{
                stop = true;
            }

        }


    }


    protected void readComando() throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {

            parser.next();

            String name = parser.getName();

            if (name!=null){
                if (name.equals("Id")) {
                    setComando(parser.nextText());
                } else if (name.equals("Risultato")) {
                    int num = Integer.parseInt(parser.nextText());
                    setRisultato(num);
                } else if (name.equals("Testo")) {
                    setText(parser.nextText());
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    /**
     * Va al primo tag con un dato nome
     */
    protected boolean gotoFirstTag(String tagName) throws XmlPullParserException, IOException{
        setInput();
        return gotoNextTag(tagName);
    }

    /**
     * Va al prossimo tag con un dato nome
     */
    protected boolean gotoNextTag(String tagName) throws XmlPullParserException, IOException{
        boolean found=false;
        boolean stopSearch = false;
        while (!stopSearch) {
            getParser().next();
            if (getParser().getEventType()==XmlPullParser.END_DOCUMENT){
                stopSearch=true ;
            }
            else {
                try {
                    getParser().require(XmlPullParser.START_TAG, null, tagName);
                    stopSearch = true;
                    found=true;
                } catch (Exception e) {
                }
            }
        }
        return found;
    }


    public boolean isSuccess() {
        return (getRisultato() == 0);
    }

    public String getString() {
        return string;
    }


    public XmlPullParser getParser() {
        return xpp;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getRisultato() {
        return risultato;
    }

    public void setRisultato(int risultato) {
        this.risultato = risultato;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getText() {
        return testo;
    }

    public void setText(String testo) {
        this.testo = testo;
    }
}
