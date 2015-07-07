package it.technocontrolsystem.hypercontrol.domain;

/**
 * Domain Class for a single alarm
 */
public class Alarm {
    private int plantNum;
    private int areaNum;
    private int sensNum;
    private int alarm;
    private int manomissione;

    public int getAreaNum() {
        return areaNum;
    }

    public void setAreaNum(int areaNum) {
        this.areaNum = areaNum;
    }

    public int getSensNum() {
        return sensNum;
    }

    public void setSensNum(int sensNum) {
        this.sensNum = sensNum;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getManomissione() {
        return manomissione;
    }

    public void setManomissione(int manomissione) {
        this.manomissione = manomissione;
    }

    public int getPlantNum() {

        return plantNum;
    }

    public void setPlantNum(int plantNum) {
        this.plantNum = plantNum;
    }
}
