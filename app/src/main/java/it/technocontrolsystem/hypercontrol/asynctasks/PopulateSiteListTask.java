package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.SitesListActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.SiteListAdapter;
import it.technocontrolsystem.hypercontrol.model.SiteModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulateSiteListTask extends AbsPopulateTask{

    public PopulateSiteListTask(SitesListActivity sitelListActivity) {
        super(sitelListActivity);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new SiteListAdapter(activity);
    }


    @Override
    public void populateAdapter() {

        Site[] sites = DB.getSites();
        publishProgress(-2, sites.length);

        SiteModel model;
        int i = 0;
        for (final Site site : sites) {
            model = new SiteModel(site);
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
        return "siti";
    }



}
