package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class ListOutRequest extends Request {
    @Override
    public String getMessage() {
       return "";
    }

    @Override
    public String getCommandId() {
        return "Outputs";
    }

    @Override
    public int getTimeout() {
        return 600;
    }

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

}
