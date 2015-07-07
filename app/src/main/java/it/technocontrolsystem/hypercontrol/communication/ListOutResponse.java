package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Risposta ad una ListEventRequest
 */
public class ListOutResponse extends Response {
    private int number;
  private String nameText;

    public ListOutResponse(String string) {
        super(string);
    }
    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        XmlPullParser parser = getParser();
        boolean stop = false;
        while(!stop){

            String name = parser.getName();

            if (name!=null){
                if (name.equals("Uscite")) {
                    readUscite();
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

            parser.next();


        }
    }

    /**
     * Legge tra i TAG "Uscite"
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected void readUscite() throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Uscita")){
                    readUscita();
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    /**
     * Legge tra i TAG "Uscita"
     * @throws XmlPullParserException
     * @throws IOException
     */

    protected void readUscita() throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    setNumber(Integer.parseInt(parser.nextText()));
                } else if (name.equals("Nome")) {
                    setNameText(parser.nextText());
                }else if (name.equals("Impianto")) {
                    readImpianto();
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    /**
     * Legge tra i TAG "Impianto"
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected void readImpianto() throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    number = Integer.parseInt(parser.nextText());
                    setNumber(number);
                } else if (name.equals("Area")) {
                    readArea();
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    /**
     * Legge tra i TAG "Area"
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected void readArea() throws XmlPullParserException, IOException {
        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    number = Integer.parseInt(parser.nextText());
                    setNumber(number);
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }



    public String getNameText() {
        return nameText;
    }

    public void setNameText(String nameText) {
        this.nameText = nameText;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }




}
