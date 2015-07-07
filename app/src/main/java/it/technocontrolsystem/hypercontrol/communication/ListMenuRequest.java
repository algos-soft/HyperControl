package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request ListMenu
 */
public class ListMenuRequest extends Request {
    @Override
    public Class getResponseClass() {
        return ListMenuResponse.class;
    }

    @Override
    public int getTimeout() {
        return 0;
    }//50

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getCommandId() {
        return "Menu";
    }
}
