package it.technocontrolsystem.hypercontrol.listadapters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

import it.technocontrolsystem.hypercontrol.activity.PlantActivity;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.communication.LiveRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.model.ModelIF;

/**
 *
 */
public abstract class HCListAdapter<T> extends ArrayAdapter<T> {
    private HashMap<Integer, ModelIF> modelMap;

    public HCListAdapter(Context context) {
        super(context, 0);
        modelMap = new HashMap<Integer, ModelIF>();
    }




    // assegno un LiveListener alla connessione
    public void attachLiveListener(){
        Connection conn = SiteActivity.getConnection();
        conn.setLiveListener(new Connection.LiveListener() {
            @Override
            public void liveReceived(LiveMessage message) {

                try {
                    live(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void add(T object) {
        super.add(object);
        ModelIF model = (ModelIF) object;
        modelMap.put(model.getNumber(), model);
    }

    /**
     * Aggiorna le informazioni dinamiche (stato) dalla centrale
     */
    public abstract void update();

    /**
     * Aggiorna lo stato di un elenco di oggetti
     *
     * @param numbers
     */
    public abstract void update(Integer[] numbers);

    public ModelIF getModel(int number) {
        return modelMap.get(number);
    }

    /**
     * Ritorna il Context come Activity
     */
    public Activity getActivity(){
        Activity a=null;
        Context c = getContext();
        if(c instanceof Activity){
            a=(Activity)c;
        }
        return a;
    }

    /**
     * Invocato quando si riceve un Live
     */
    public abstract void live(LiveMessage message) throws IOException, XmlPullParserException;

    /**
     * AsyncTask che attende che si liberi la Connection e quando è
     * libera fa partire la richiesta di update per gli oggetti variati
     */
    class UpdateTask extends AsyncTask<Integer[], Void, Integer[]> {
        @Override
        protected Integer[] doInBackground(Integer[]... params) {
            Connection conn = SiteActivity.getConnection();
            while(conn.isProcessingResponse()){
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return params[0];
        }

        protected void onPostExecute(Integer[] numbers) {
            update(numbers);
        }

    }
}
