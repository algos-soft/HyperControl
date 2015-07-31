package it.technocontrolsystem.hypercontrol.asynctasks;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import it.technocontrolsystem.hypercontrol.activity.OutputDetailActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.domain.Output;
import it.technocontrolsystem.hypercontrol.listadapters.AreaInOutputListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.model.AreaModel;
import it.technocontrolsystem.hypercontrol.model.OutputModel;

/**
 * AsyncTask per caricare le aree all'interno della scheda di dettaglio di una uscita.
 * Crea un adapter, lo popola e lo assegna alla ListView
 */
public class PopulateAreasInOutputDetailViewTask extends AsyncTask<Void, Integer, Exception> {
    OutputDetailActivity activity;
    private AreaInOutputListAdapter adapter;

    public PopulateAreasInOutputDetailViewTask(OutputDetailActivity activity) {
        super();
        this.activity=activity;
    }

    @Override
    protected Exception doInBackground(Void... params) {
        Exception exception = null;

        try {

            // crea e popola l'adapter
            adapter = new AreaInOutputListAdapter(activity);
            populateAdapter();

        } catch (Exception e1) {
            e1.printStackTrace();
            exception = e1;
        }

        return exception;
    }




    @Override
    protected void onPostExecute(Exception exception) {

        // assegna l'adapter alla ListView
        if(exception == null){
            ListView view = activity.getListView();
            view.setAdapter(adapter);
        }

    }


    public void populateAdapter() {

        Area[] areas = DB.getAreasByOutputId(activity.getOutput().getId());
        for (final Area area : areas) {
            adapter.add(area);
            if (isCancelled()) {
                break;
            }
        }
    }



}
