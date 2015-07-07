package it.technocontrolsystem.hypercontrol.communication;

/**
 * Request SensorsStatus
 */
public class SensorsStatusRequest extends Request {
   private int plant=-1;
    private int area=-1;
    private int sensor=-1;

    public SensorsStatusRequest(int sensor) {
        this.sensor=sensor;
    }

    public SensorsStatusRequest(int plant, int area) {
        this.plant = plant;
        this.area = area;
    }

    @Override
    public Class getResponseClass() {
        return SensorsStatusResponse.class;
    }

    @Override
    public int getTimeout() {
        return 600;
    }

    @Override
    public String getMessage() {
        String sensorParam="";

       if(plant!=-1){
           sensorParam="<Param1>"+plant+"</Param1>";

               if (area!=-1){
                sensorParam+= "<Param2>" +area+ "</Param2>";
            }
       }
        if(sensor!=-1){
            sensorParam+= "<Param3>" +sensor+ "</Param3>";
        }

       return sensorParam;
    }

    @Override
    public String getCommandId() {
        return "InputsStatus";
    }
}
