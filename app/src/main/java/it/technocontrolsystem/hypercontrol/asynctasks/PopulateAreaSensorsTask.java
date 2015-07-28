package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.AreaSensorsActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.SensorListAdapter;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulateAreaSensorsTask extends AbsPopulateTask {

    public PopulateAreaSensorsTask(AreaSensorsActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateAreaSensorsTask(AreaSensorsActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new SensorListAdapter(activity, getSpecActivity().getArea());
    }


    @Override
    public void populateAdapter() {

        Sensor[] sensors = DB.getSensorsByArea(getSpecActivity().getArea().getId());
        publishProgress(-2, sensors.length);

        SensorModel aModel;
        int i = 0;
        for (final Sensor sensor : sensors) {
            aModel = new SensorModel(sensor);
            activity.getListAdapter().add(aModel);
            i++;
            publishProgress(-3, i);

            if (isCancelled()) {
                break;
            }

        }

    }


    @Override
    public String getType() {
        return "sensori";
    }

    private AreaSensorsActivity getSpecActivity() {
        return (AreaSensorsActivity) activity;
    }


}
