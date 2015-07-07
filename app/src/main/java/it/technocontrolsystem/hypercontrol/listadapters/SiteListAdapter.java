package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.display.SiteDisplay;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.model.ModelIF;
import it.technocontrolsystem.hypercontrol.model.SiteModel;

/**
 *
 */
public class SiteListAdapter extends SimpleListAdapter<SiteModel> {
    Site site;

    public SiteListAdapter(Context context) {
        super(context);
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        SiteModel model = getItem(position);
        SiteDisplay display;
        display = new SiteDisplay(getContext(), model.getSite().getId());
        display.update(model);
        return display;
    }

}
