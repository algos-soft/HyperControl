package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.OutputsActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Output;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.OutputListAdapter;
import it.technocontrolsystem.hypercontrol.model.OutputModel;

/**
 * Created by alex on 28-07-2015.
 */
public class PopulateOutputsTask extends AbsPopulateTask{

    public PopulateOutputsTask(OutputsActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateOutputsTask(OutputsActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new OutputListAdapter(activity, null);
    }


    @Override
    public void populateAdapter() {

        Output[] outputs = DB.getOutputsBySite(getSpecActivity().getSite().getId());
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

    private OutputsActivity getSpecActivity() {
        return (OutputsActivity) activity;
    }

}
