package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Plant;

/**
 *
 */
public class PlantModel implements ModelIF {
    private Plant plant;
    private int status;
    private boolean alarm;

    public PlantModel(Plant plant) {
        this.plant = plant;
        this.status=-1; // valore iniziale: unknown
    }

    public Plant getPlant() {
        return plant;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    @Override
    public int getNumber() {
        return getPlant().getNumber();
    }

    /**
     * Elimina le informazioni di stato
     */
    public void clearStatus(){
        this.status=-1; // valore iniziale: unknown
        this.alarm=false;
    }

}
