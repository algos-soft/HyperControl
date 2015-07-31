package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.display.AreaDisplayInOutput;
import it.technocontrolsystem.hypercontrol.domain.Area;

/**
 * Created by alex on 31-07-2015.
 */
public class AreaInOutputListAdapter extends ArrayAdapter<Area> {

    public AreaInOutputListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Area area=getItem(position);
        AreaDisplayInOutput display=new AreaDisplayInOutput(getContext(),area);
        return display;
    }


}
