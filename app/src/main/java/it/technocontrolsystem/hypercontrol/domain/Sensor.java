package it.technocontrolsystem.hypercontrol.domain;

/**
 * Domain Class for a single Sensor.
*/
public class Sensor extends AbsInOut {

    private  int tipo;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public SensorType getSensorType(){
        return SensorType.get(getTipo());
    }

}
