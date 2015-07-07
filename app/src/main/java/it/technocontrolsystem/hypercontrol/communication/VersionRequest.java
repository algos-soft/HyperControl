package it.technocontrolsystem.hypercontrol.communication;

/**
 *
 */
public class VersionRequest extends Request {
    public String getMessage() {
        return "";
    }

    @Override
    public String getCommandId() {
        return "Version";
    }

    @Override
    public int getTimeout() {
        return 5;
    }

    @Override
    public Class getResponseClass() {
        return VersionResponse.class;
    }

}

