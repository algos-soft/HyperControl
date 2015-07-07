package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request SetNews
 */
public class SetNewsRequest extends Request {

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

    @Override
    public int getTimeout() {
        return 60;
    }

    @Override
    public String getMessage() {
        return "<Stato>1</Stato>";
    }

    @Override
    public String getCommandId() {
        return "NewsStatus";
    }
}
