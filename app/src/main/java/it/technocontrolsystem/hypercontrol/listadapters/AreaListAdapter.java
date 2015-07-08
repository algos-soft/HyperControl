package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.activity.AreaActivity;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.AreaStatusRequest;
import it.technocontrolsystem.hypercontrol.communication.AreaStatusResponse;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.communication.PlantsStatusRequest;
import it.technocontrolsystem.hypercontrol.communication.PlantsStatusResponse;
import it.technocontrolsystem.hypercontrol.display.AreaDisplay;
import it.technocontrolsystem.hypercontrol.model.AreaModel;
import it.technocontrolsystem.hypercontrol.model.ModelIF;
import it.technocontrolsystem.hypercontrol.model.PlantModel;

/**
 * Adapter per le liste di Area
 */
public class AreaListAdapter extends HCListAdapter<AreaModel>{

    public AreaListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AreaModel model=getItem(position);
        AreaDisplay display;

        // Non usare la convertView perché c'è attaccato il listener con l'itemId
        // Creare sempre nuove view
//        if(convertView!=null) {
//            display=(AreaDisplay)convertView;
//        }else{
//            display=new AreaDisplay(getContext(),model.getArea().getId());
//        }
        display=new AreaDisplay(getContext(),model.getArea().getId());
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
        for (int number:numbers){
            update(number);
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
     * @param number the area number (not the position)
     */
    public void update(int number){
        AreaModel model;
        AreaStatusResponse resp;
        Connection conn = SiteActivity.getConnection();

        model=(AreaModel) getModel(number);
        int plantNum= model.getArea().getPlant().getNumber();
        AreaStatusRequest request = new AreaStatusRequest(plantNum,number);
        resp = (AreaStatusResponse) conn.sendRequest(request);
        int status = resp.getStatus();
        boolean alarm = resp.isAlarm();
        model.setStatus(status);
        model.setAlarm(alarm);

    }


    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {
        Integer[] numbers = message.getAreaNumbers();
        new UpdateTask().execute(numbers);
    }


}
