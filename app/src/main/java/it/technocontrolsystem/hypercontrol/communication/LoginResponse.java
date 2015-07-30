package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by alex on 29-07-2015.
 */
public class LoginResponse extends Response {

    private String hpUUID;

    public LoginResponse(String string) {
        super(string);
    }

    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        if (gotoFirstTag("GuidHP")) {
            XmlPullParser parser = getParser();
            String name=parser.nextText();
            hpUUID =name;
        }
    }

    public String getHpUUID() {
        return hpUUID;
    }
}
