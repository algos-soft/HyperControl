package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.AreaOutputsActivity;
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
public class PopulateAreaOutputsTask extends AbsPopulateTask{

    public PopulateAreaOutputsTask(AreaOutputsActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateAreaOutputsTask(AreaOutputsActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new OutputListAdapter(activity, getSpecActivity().getArea());
    }


    @Override
    public void populateAdapter() {

        Output[] outputs = DB.getOutputsByArea(getSpecActivity().getArea().getId());
        publishProgress(-2, outputs.length);

        OutputModel aModel;
        int i = 0;
        for (final Output output : outputs) {
            aModel = new OutputModel(output);
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
        return "uscite";
    }

    private AreaOutputsActivity getSpecActivity() {
        return (AreaOutputsActivity) activity;
    }

}
