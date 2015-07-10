package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Area;

/**
 *
 */
public class AreaModel implements ModelIF {

    private Area area;
    private int status;
    private boolean alarm;

    public AreaModel(Area area) {
        this.area = area;
        this.status = -1; // valore iniziale: unknown
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

    public Area getArea() {
        return area;
    }

    @Override
    public int getNumber() {
        return getArea().getNumber();
    }

    @Override
    public void clearStatus() {
        setStatus(-1);
        setAlarm(false);
    }
}
