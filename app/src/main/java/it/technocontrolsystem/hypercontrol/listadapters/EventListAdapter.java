package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import it.technocontrolsystem.hypercontrol.activity.EventiActivity;
import it.technocontrolsystem.hypercontrol.display.EventDisplay;
import it.technocontrolsystem.hypercontrol.domain.Event;

/**
 * Adapter per la lista degli eventi.
 * Created by Alex on 06/07/15.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    private Button bAltri;

    public EventListAdapter(Context context) {
        super(context, 0);

        // bottone Altri...
        // visualizzato sempre in fondo alla lista
        // quando premuto aggiunge all'adapter un altro blocco di eventi
        bAltri = new Button(getContext());
        bAltri.setText("Altri...");
        bAltri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventiActivity().loadEventi();
            }
        });

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View displayView;
        if(position<super.getCount()){ // righe normali
            Event e=getItem(position);
            displayView=new EventDisplay(getContext(),e);
        }else{  // ultima riga, mostro il bottone
            displayView=bAltri;
        }
        return displayView;
    }

    @Override
    /**
     * Inganno la lista facendole credere che ci sia una riga in più.
     * Quando visualizza l'ultima riga mostra il bottone Altri.
     */
    public int getCount() {
        int count = super.getCount();
        return count+1;
    }

    private EventiActivity getEventiActivity(){
        return (EventiActivity)getContext();
    }

}


