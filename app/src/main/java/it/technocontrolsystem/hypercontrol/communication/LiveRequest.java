package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request Live
 */
public class LiveRequest extends Request {
    int setNumber;
    int paramPlantNumber;
    int paramAreaNumber;

    public LiveRequest(int setNumber,int paramPlantNumber,int paramAreaNumber) {
        this.setNumber=setNumber;
        this.paramPlantNumber =paramPlantNumber;
        this.paramAreaNumber=paramAreaNumber;
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
    public String getMessage() {
        String message="<Set>" + setNumber + "</Set>";
        if(paramPlantNumber !=-1) {
           message+="<Param1>"+ paramPlantNumber +"</Param1>";
        }
        if(paramAreaNumber !=-1) {
            message+="<Param2>"+ paramAreaNumber +"</Param2>";
        }
        return message;
    }

    @Override
    public String getCommandId() {
        return "Live";
    }
}
