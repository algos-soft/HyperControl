package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request AreaStatus
 */
public class AreaStatusRequest extends Request {
    private int plantNumber = -1;
    private int areaNumber = -1;


    public AreaStatusRequest(int plantNumber, int areaNumber) {
        this.plantNumber = plantNumber;
        this.areaNumber = areaNumber;
    }

    @Override
    public Class getResponseClass() {
        return AreaStatusResponse.class;
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public String getMessage() {
        String plantParam = "";


        plantParam = "<Param1>" + plantNumber + "</Param1>";
        plantParam += "<Param2>" + areaNumber + "</Param2>";


        return plantParam;
    }

    @Override
    public String getCommandId() {
        return "PartitionsStatus";
    }

}
