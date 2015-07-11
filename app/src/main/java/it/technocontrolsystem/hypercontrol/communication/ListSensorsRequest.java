package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class ListSensorsRequest extends Request {
    public String getMessage() {

        return "";

    }

    @Override
    public String getCommandId() {
        return "Inputs";
    }

    @Override
    public int getTimeout() {
        return 600;
    }

    @Override
    public Class getResponseClass() {
        return ListSensorsResponse.class;
    }

}
