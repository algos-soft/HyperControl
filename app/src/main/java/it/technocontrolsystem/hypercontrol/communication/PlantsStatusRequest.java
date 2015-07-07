package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request PlantStatus
 */
public class PlantsStatusRequest extends Request{
    private int plantNumber;


    public PlantsStatusRequest(int plantNumber) {
        this.plantNumber = plantNumber;
    }

    @Override
    public Class getResponseClass() {
        return PlantsStatusResponse.class;
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public String getMessage() {
        String plantParam= "<Param1>" + plantNumber + "</Param1>";
        return plantParam;
    }

    @Override
    public String getCommandId() {
        return "PartitionsStatus";
    }

}
