package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.PlantListAdapter;
import it.technocontrolsystem.hypercontrol.model.PlantModel;

/**
 * AsyncTask per caricare i dati nell'adapter
 */
public class PopulateSiteTask extends AbsPopulateTask {


    public PopulateSiteTask(SiteActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateSiteTask(SiteActivity activity) {
        this(activity, null, null);
    }


    @Override
    public HCListAdapter<?> createAdapter() {
        return new PlantListAdapter(getSiteActivity());
    }

    @Override
    public void populateAdapter() {

        Plant[] plants = DB.getPlants(getSiteActivity().getSite().getId());
        publishProgress(-2, plants.length);

        PlantModel model;
        int i = 0;
        for (final Plant plant : plants) {
            model = new PlantModel(plant);
            activity.getListAdapter().add(model);
            i++;
            publishProgress(-3, i);

            if (isCancelled()){
                break;
            }

        }

    }



    private SiteActivity getSiteActivity(){
        return (SiteActivity)activity;
    }

    @Override
    public String getType() {
        return "impianti";
    }

}
