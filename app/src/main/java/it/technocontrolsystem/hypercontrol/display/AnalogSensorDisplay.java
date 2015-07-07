package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;

/**
 *
 */
public class AnalogSensorDisplay extends SensorDisplay{
    public AnalogSensorDisplay(Context context, int sensorId) {
        super(context, sensorId);
    }

    @Override
    protected void setDetail(String detail) {
        detail="analog - "+detail;
        super.setDetail(detail);
    }
}
