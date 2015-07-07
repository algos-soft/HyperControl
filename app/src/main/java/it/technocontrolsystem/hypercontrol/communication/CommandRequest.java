package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request Command
 */
public abstract class CommandRequest extends Request {

    private int itemId;
    private boolean enable;

    public CommandRequest(int itemId, boolean enable) {
        this.itemId = itemId;
        this.enable=enable;
    }

    @Override
    public Class getResponseClass() {
        return Response.class;
    }

    @Override
    public int getTimeout() {
        return 60;
    }

    @Override
    public abstract String getMessage();

    @Override
    public String getCommandId() {
        return "Command";
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isEnable() {
        return enable;
    }
}
