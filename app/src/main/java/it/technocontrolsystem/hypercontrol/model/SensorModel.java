package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Sensor;

/**
 *
 */
public class SensorModel implements ModelIF {
    private Sensor sensor;
    private boolean alarm;
    private boolean tamper;
    private boolean test;
    private String value;
    private int status;

    public SensorModel(Sensor sensor) {
        this.sensor = sensor;
        this.status=-1; // valore iniziale: unknown
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public boolean isTamper() {
        return tamper;
    }

    public void setTamper(boolean tamper) {
        this.tamper = tamper;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getNumber() {
        return getSensor().getNumber();
    }

    @Override
    public void clearStatus() {
        setAlarm(false);
        setTamper(false);
        setTest(false);
        setValue("");
        setStatus(0);
    }


    /**
     * Update status from another model
     */
    public void updateStatus(SensorModel other){
        setStatus(other.getStatus());
        setTamper(other.isTamper());
        setValue(other.getValue());
        setAlarm(other.isAlarm());
        setTest(other.isTest());
    }
}
