package it.technocontrolsystem.hypercontrol.communication;

import android.widget.CompoundButton;

import it.technocontrolsystem.hypercontrol.activity.SiteActivity;

/**
 * Created by alex on 5-07-2015.
 */
public class StatusButtonListener implements CompoundButton.OnCheckedChangeListener {

    SiteActivity activity;

    public StatusButtonListener(SiteActivity activity) {
        this.activity=activity;
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            buttonView.setOnCheckedChangeListener(null);
            buttonView.setChecked(false);
            new ConnectionTask(activity).execute();
        }else{
            activity.setConnection(null);
            activity.getListAdapter().clearStatus();
        }
    }

}
