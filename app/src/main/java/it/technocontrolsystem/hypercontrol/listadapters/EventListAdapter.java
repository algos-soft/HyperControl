package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import it.technocontrolsystem.hypercontrol.activity.EventiActivity;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.display.EventDisplay;
import it.technocontrolsystem.hypercontrol.domain.Event;
import it.technocontrolsystem.hypercontrol.model.EventModel;

/**
 * Adapter per la lista degli eventi.
 * Created by Alex on 06/07/15.
 */
public class EventListAdapter extends HCListAdapter<EventModel> {

    private Button bAltri;

    public EventListAdapter(Context context) {
        super(context);

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View displayView;
        if(position<super.getCount()){ // righe normali
            EventModel model=getItem(position);
            Event e = model.getEvent();
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


