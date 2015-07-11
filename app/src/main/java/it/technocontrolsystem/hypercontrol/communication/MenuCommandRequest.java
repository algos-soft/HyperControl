package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request MenuCommand
 */
public class MenuCommandRequest extends Request{
    private int actionNumber;

    public MenuCommandRequest(int actionNumber) {
        this.actionNumber = actionNumber;
    }

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

    @Override
    public int getTimeout() {
        return 10;
    }

    @Override
    public String getMessage() {
        return "<IdMenu>"+actionNumber+"</IdMenu>";
    }

    @Override
    public String getCommandId() {
        return "MenuCommand";
    }


}
