package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request ListBoards
 */
public class ListBoardsRequest extends Request {
    @Override
    public Class getResponseClass() {
        return ListBoardsResponse.class;
    }

    @Override
    public int getTimeout() {
        return 60;
    }

    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getCommandId() {
        return "Boards";
    }
}
