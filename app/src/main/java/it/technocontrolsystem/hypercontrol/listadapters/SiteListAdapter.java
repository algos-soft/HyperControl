package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import it.technocontrolsystem.hypercontrol.activity.SitesListActivity;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.display.SiteDisplay;
import it.technocontrolsystem.hypercontrol.model.SiteModel;

/**
 * Created by alex on 15-07-2015.
 */
public class SiteListAdapter extends HCListAdapter<SiteModel> {

    public SiteListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SiteModel model = getItem(position);
        SiteDisplay display;
        display = new SiteDisplay((SitesListActivity)getContext(), model.getSite().getId());
        display.update(model);
        return display;
    }

    @Override
    public void updateAll() {
    }

    @Override
    public void update(Integer[] numbers) {
    }

    @Override
    public void updateByNumber(int number) {
    }

    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {
    }


}
