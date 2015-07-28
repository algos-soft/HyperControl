package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.EventiActivity;
import it.technocontrolsystem.hypercontrol.communication.ListEventRequest;
import it.technocontrolsystem.hypercontrol.communication.ListEventResponse;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Event;
import it.technocontrolsystem.hypercontrol.listadapters.EventListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.model.EventModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulateEventTask extends AbsPopulateTask {

    public PopulateEventTask(EventiActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateEventTask(EventiActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new EventListAdapter(activity);
    }


    @Override
    public void populateAdapter() {
        try {

            Request request = new ListEventRequest(getSpecActivity().lastIdEvento, 10);
            Response resp = HyperControlApp.sendRequest(request);
            if (resp != null) {

                final ListEventResponse vResp = (ListEventResponse) resp;

                if (vResp != null) {

                    if (vResp.isSuccess()) {

                        // in questo caso l'adapter è già attaccato alla lista
                        // e il metodo listAdapter.add() deve eseguire nello UI Thread
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Event[] events = vResp.getEvents();
                                for (Event e : events) {
                                    EventModel model = new EventModel(e);
                                    activity.getListAdapter().add(model);
                                    getSpecActivity().lastIdEvento = e.getId();

                                    if (isCancelled()) {
                                        break;
                                    }

                                }
                            }
                        });

                    } else {  // list events request failed
                        throw new Exception(resp.getText());
                    }

                } else {  // list events response null
                    throw new Exception("List Events Request timeout");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return "eventi";
    }

    private EventiActivity getSpecActivity() {
        return (EventiActivity) activity;
    }


}
