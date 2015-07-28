package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.display.OutputDisplay;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.model.ModelIF;
import it.technocontrolsystem.hypercontrol.model.OutputModel;

/**
 * Adapter per le liste di Outputs
 */
public class OutputListAdapter extends HCListAdapter<OutputModel>{
    Area area;

    public OutputListAdapter(Context context, Area area) {
        super(context);
        this.area = area;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OutputModel model=getItem(position);
        OutputDisplay display;
        display=new OutputDisplay(getContext(),model.getOutput().getId());
        display.update(model);
        return display;
    }


    @Override
    public void updateAll() {
//        ArrayList<Integer> numbers=new ArrayList<>();
//        ModelIF model;
//
//        for (int i = 0; i < getCount(); i++) {
//            model = getItem(i);
//            int num = (model.getNumber());
//            numbers.add(num);
//        }
//        update(numbers.toArray(new Integer[0]));
    }

    @Override
    public void update(Integer[] numbers) {
//        for (int number:numbers){
//            updateByNumber(number);
//        }
//
//        //notifyDataSetChanged() va sempre invocato sullo UI Thread!
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                notifyDataSetChanged();
//            }
//        });
//
    }

    /**
     * Update one single item
     */
    public void updateByNumber(int number) {
    }


    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {
    }
}
