package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.communication.PlantsStatusRequest;
import it.technocontrolsystem.hypercontrol.communication.PlantsStatusResponse;
import it.technocontrolsystem.hypercontrol.display.PlantDisplay;
import it.technocontrolsystem.hypercontrol.model.ModelIF;
import it.technocontrolsystem.hypercontrol.model.PlantModel;

/**
 * Adapter per le liste di Plants
 */
public class PlantListAdapter extends HCListAdapter<PlantModel> {

    public PlantListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlantModel model = getItem(position);
        PlantDisplay display;
        // Non usare la convertView perché c'è attaccato il listener con l'itemId
        // Creare sempre nuove view
//        if (convertView != null) {
//            display = (PlantDisplay) convertView;
//        } else {
//            display = new PlantDisplay(getContext(), model.getPlant().getId());
//        }
        display = new PlantDisplay(getContext(), model.getPlant().getId());
        display.update(model);
        return display;
    }


    @Override
    public void update() {
        ArrayList<Integer> numbers=new ArrayList<>();
        ModelIF model;
        for (int i = 0; i < getCount(); i++) {
            model = getItem(i);
            int num = model.getNumber();
            numbers.add(num);
        }
        update(numbers.toArray(new Integer[0]));
    }


    @Override
    public void update(Integer[] numbers) {
        PlantModel model;
        PlantsStatusResponse resp;
        Connection conn = SiteActivity.getConnection();

        for (int number:numbers){
            model=(PlantModel) getModel(number);
            PlantsStatusRequest request = new PlantsStatusRequest(number);
            resp = (PlantsStatusResponse) conn.sendRequest(request);
            int status = resp.getStatus();
            boolean alarm = resp.isAlarm();
            model.setStatus(status);
            model.setAlarm(alarm);
        }
        notifyDataSetChanged();
    }

    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {
        Integer[] numbers = message.getPlantNumbers();
        new UpdateTask().execute(numbers);
    }

}
