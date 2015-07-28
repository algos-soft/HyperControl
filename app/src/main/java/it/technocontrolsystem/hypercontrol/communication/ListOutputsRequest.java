package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class ListOutputsRequest extends Request {
    @Override
    public Class getResponseClass() {
        return ListSensorsResponse.class;
    }

    public String getMessage() {
        return "";
    }

    @Override
    public String getCommandId() {
        return "Outputs";
    }

    @Override
    public int getTimeout() {
        return 120;
    }



}