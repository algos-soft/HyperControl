package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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
        // Non usare la convertView perché c'è attaccato il listener con l'itemId
        // Creare sempre nuove view
//        if (convertView != null) {
//            display = (PlantDisplay) convertView;
//        } else {
//            display = new PlantDisplay(getContext(), model.getPlant().getId());
//        }

        PlantDisplay display = new PlantDisplay(getContext(), model.getPlant().getId());
        display.update(model);
        return display;
    }


    @Override
    public void updateAll() {
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
        for (int number:numbers){
            updateByNumber(number);
        }

        //notifyDataSetChanged() va sempre invocato sullo UI Thread!
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    /**
     * Update one single item
     */
    public void updateByNumber(int number){
        PlantModel model;
        PlantsStatusResponse resp;
        Connection conn = SiteActivity.getConnection();

        model=(PlantModel) getModel(number);
        PlantsStatusRequest request = new PlantsStatusRequest(number);
        resp = (PlantsStatusResponse) conn.sendRequest(request);
        if(resp!=null){
            int status = resp.getStatus();
            boolean alarm = resp.isAlarm();
            model.setStatus(status);
            model.setAlarm(alarm);
        }

    }

    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {
        Integer[] numbers = message.getPlantNumbers();
        new UpdateTask().execute(numbers);
    }

//    /**
//     * Elimina le informazioni di stato da tutti gli elementi
//     */
//    public void clearStatus(){
//
//        PlantModel model;
//        for(int i = 0; i<getCount();i++){
//            model = getItem(i);
//            model.clearStatus();
//        }
//
//        // da eseguire sempre nello UI thread
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                notifyDataSetChanged();
//            }
//        });
//    }
}
