package it.technocontrolsystem.hypercontrol.communication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Risposta ad una versionRequest
 */
public class VersionResponse extends Response{

    private String softwareVersion;
    private int configurationNumber;

    public VersionResponse(String string) {
        super(string);
   }


    @Override
    protected void readComando() throws XmlPullParserException, IOException {
        super.readComando();

        XmlPullParser parser = getParser();
        boolean stop = false;
        while (!stop) {

            String name = parser.getName();

            if (name!=null){
                if (name.equals("Configurazione")) {
                    int num = Integer.parseInt(parser.nextText());
                    setConfigurationNumber(num);
                } else if (name.equals("Software")) {
                    setSoftwareVersion(parser.nextText());
                }else{
                    stop = true;
                }
            }else{
                stop = true;
            }

            parser.next();


        }

    }

    public String getSoftwareVersion(){
        return this.softwareVersion;
    }

    public  int getConfigurationNumber(){
        return this.configurationNumber;
    }

    private void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    private void setConfigurationNumber(int configurationNumber) {
        this.configurationNumber = configurationNumber;
    }
}
