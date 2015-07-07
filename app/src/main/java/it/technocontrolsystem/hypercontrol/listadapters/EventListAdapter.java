package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import it.technocontrolsystem.hypercontrol.domain.Event;

/**
 * Adapter per la lista degli eventi.
 * Created by Alex on 06/07/15.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    public EventListAdapter(Context context) {
        super(context, 0);
    }

}


