package it.technocontrolsystem.hypercontrol.listadapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.model.ModelIF;

/**
 * Adapter semplice per le liste che non richiedono Live,ma solo la lettura da DB
 */
public abstract class SimpleListAdapter<T> extends ArrayAdapter<T> {
    private HashMap<Integer, ModelIF> modelMap;

    public SimpleListAdapter(Context context) {
        super(context,0);
        modelMap = new HashMap<Integer, ModelIF>();
    }

    @Override
    public void add(T object) {
        super.add(object);
        ModelIF model = (ModelIF) object;
        modelMap.put(model.getNumber(), model);
    }


}
