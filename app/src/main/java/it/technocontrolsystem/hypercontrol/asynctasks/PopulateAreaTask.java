package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.AreaActivity;
import it.technocontrolsystem.hypercontrol.activity.AreaSensorsActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Output;
import it.technocontrolsystem.hypercontrol.domain.Sensor;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.OutputListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.SensorListAdapter;
import it.technocontrolsystem.hypercontrol.model.OutputModel;
import it.technocontrolsystem.hypercontrol.model.SensorModel;

/**
 * Created by alex on 28-07-2015.
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
        HCListAdapter adapter=null;
        switch(getSpecActivity().mode){
            case AreaActivity.MODE_SENSORS:
                adapter=new SensorListAdapter(activity, getSpecActivity().getArea());
                break;

            case AreaActivity.MODE_OUTPUTS:
                adapter=new OutputListAdapter(activity, getSpecActivity().getArea());
                break;

        }

        return adapter;
    }


    @Override
    public void populateAdapter() {

        int i = 0;
        switch(getSpecActivity().mode){

            case AreaActivity.MODE_SENSORS:
                Sensor[] sensors = DB.getSensorsByArea(getSpecActivity().getArea().getId());
                publishProgress(-2, sensors.length);

                SensorModel sensorModel;
                for (final Sensor sensor : sensors) {
                    sensorModel = new SensorModel(sensor);
                    activity.getListAdapter().add(sensorModel);
                    i++;
                    publishProgress(-3, i);

                    if (isCancelled()) {
                        break;
                    }

                }
                break;

            case AreaActivity.MODE_OUTPUTS:
                Output[] outputs = DB.getOutputsByArea(getSpecActivity().getArea().getId());
                publishProgress(-2, outputs.length);

                OutputModel outputModel;
                for (final Output output : outputs) {
                    outputModel = new OutputModel(output);
                    activity.getListAdapter().add(outputModel);
                    i++;
                    publishProgress(-3, i);

                    if (isCancelled()) {
                        break;
                    }

                }

                break;

        }

    }


    @Override
    public String getType() {
        String type="";
        switch(getSpecActivity().mode){
            case AreaActivity.MODE_SENSORS:
                type="sensori";
                break;

            case AreaActivity.MODE_OUTPUTS:
                type="uscite";
                break;

        }

        return type;
    }

    private AreaActivity getSpecActivity() {
        return (AreaActivity) activity;
    }


}
