package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;

/**
 *
 */
public class SiteModel implements ModelIF {
    private Site site;

    public SiteModel(Site site) {
        this.site = site;
    }

    public Site getSite() {
        return site;
    }

    @Override
    public int getNumber() {
        return getSite().getId();
    }

    @Override
    public void clearStatus() {}

    public void updateFromDB(){
        site = DB.getSite(site.getId());
    }
}
