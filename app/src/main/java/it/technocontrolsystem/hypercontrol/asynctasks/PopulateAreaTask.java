package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.AreaActivity;
import it.technocontrolsystem.hypercontrol.activity.PlantActivity;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.SensorListAdapter;
import it.technocontrolsystem.hypercontrol.model.AreaModel;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulateAreaTask extends AbsPopulateTask {

    public PopulateAreaTask(AreaActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateAreaTask(AreaActivity activity) {
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

    private AreaActivity getSpecActivity() {
        return (AreaActivity) activity;
    }


}
