package it.technocontrolsystem.hypercontrol.communication;

/**
 * Language Request
 */
public class LanguageRequest extends Request {
    String lan;

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public String getMessage() {
        return "<Lingua>"+lan+"</Lingua>";
    }

    @Override
    public String getCommandId() {
        return "Language";
    }

    public String getLan() {
        return lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }
}
