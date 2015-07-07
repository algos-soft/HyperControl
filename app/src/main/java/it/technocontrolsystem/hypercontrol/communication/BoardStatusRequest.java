package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request BoardsStatus for a single Board
*/
public class BoardStatusRequest extends Request {
    private int boardNumber;

    public BoardStatusRequest(int boardNumber) {
        this.boardNumber=boardNumber;
    }

    @Override
    public Class getResponseClass() {
        return BoardStatusResponse.class;
    }

    @Override
    public int getTimeout() {
        return 60;
    }

    @Override
    public String getMessage() {
        return "<Param1>" + boardNumber + "</Param1>";
    }

    @Override
    public String getCommandId() {
        return "BoardsStatus";
    }
}
