package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Risposta ad una AreaStatusRequest
 */
public class AreaStatusResponse extends Response{

    private int status;
    private boolean alarm;

    public AreaStatusResponse(String string) {
        super(string);
    }

    @Override
    // attenzione, questo metodo è invocato dal costruttore
    protected void readComando() throws XmlPullParserException, IOException {
        if (gotoFirstTag("Area")) {
            readArea();
        }
    }


    private void readArea()throws XmlPullParserException, IOException {
        boolean stop = false;
        while (!stop) {
            XmlPullParser parser = getParser();
            parser.next();
            String name = parser.getName();
            if (name!=null){
                if (name.equals("Numero")) {
                    parser.nextText();
                } else if (name.equals("Stato")) {
                    status=Integer.parseInt(parser.nextText());
                }else if (name.equals("Allarme")) {
                    int num = Integer.parseInt(parser.nextText());
                    alarm=(num==1);
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

        }
    }

    public int getStatus(){
        return status;
    }

    public boolean isAlarm(){
        return alarm;
    }

}
