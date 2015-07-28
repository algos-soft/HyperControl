package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.PlantActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.model.AreaModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulatePlantTask extends AbsPopulateTask{

    public PopulatePlantTask(PlantActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulatePlantTask(PlantActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new AreaListAdapter(activity);
    }

    @Override
    public void populateAdapter() {

        Area[] areas = DB.getAreasByPlant(getSpecActivity().getPlant().getId());
        publishProgress(-2, areas.length);

        AreaModel model;
        int i = 0;
        for (final Area area : areas) {
            model = new AreaModel(area);
            activity.getListAdapter().add(model);
            i++;
            publishProgress(-3, i);

            if (isCancelled()) {
                break;
            }

        }

    }

    @Override
    public String getType() {
        return "aree";
    }


    private PlantActivity getSpecActivity(){
        return (PlantActivity)activity;
    }

}
