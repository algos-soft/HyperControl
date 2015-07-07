package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.AreaCommandRequest;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Area;
import it.technocontrolsystem.hypercontrol.model.AreaModel;

/**
 * Display class for areas
 */
public class AreaDisplay extends ItemDisplay {

    public AreaDisplay(Context context, int areaId) {
        super(context, areaId);
        greenText.setVisibility(View.GONE);
        redText.setVisibility(View.GONE);
        yellowText.setVisibility(View.GONE);
        cyanText.setVisibility(View.GONE);
        valueView.setVisibility(View.GONE);
        testView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        //  setNumberWidth(100);
    }




    public void update(AreaModel model) {
        setNumber(model.getArea().getNumber());
        setDescription(model.getArea().getName());

        int statusCode = model.getStatus();

        switch (statusCode) {
            case 0://OFF
                getBswitch().setChecked(false);
                getCheckBox().setChecked(false);
                break;
            case 1://PARTIAL
                getBswitch().setChecked(true);
                getCheckBox().setChecked(true);
                break;
            case 2://TOTAL
                getBswitch().setChecked(true);
                getCheckBox().setChecked(false);
                break;
            default:
                getBswitch().setChecked(false);
                getCheckBox().setChecked(false);
                getBswitch().setEnabled(false);
                getCheckBox().setEnabled(false);
                break;
        }

        setAlarm(model.isAlarm());
    }

    public int getAreaId() {
        return getItemId();
    }


    @Override
    /**
     * Il bottone switch è stato premuto
     */
    public void switchPressed(int itemId, CompoundButton button,boolean partial) {
        Area area= DB.getArea(itemId);
        int numPlant=area.getPlant().getNumber();
        int areaNumber=area.getNumber();

        Request req=new AreaCommandRequest(numPlant,areaNumber, button.isChecked(),partial);
        Response resp= SiteActivity.getConnection().sendRequest(req);

        if (!resp.isSuccess()) {
            button.setChecked(!button.isChecked());
            Toast.makeText(getContext(), resp.getText(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    /**
     * Il checkbox è stato cliccato
     */
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {
        if(buttonOn){
            Area area= DB.getArea(itemId);
            int numPlant=area.getPlant().getNumber();
            int areaNumber=area.getNumber();

            Request req=new AreaCommandRequest(numPlant,areaNumber, true, box.isChecked());
            Response resp= SiteActivity.getConnection().sendRequest(req);

            if (!resp.isSuccess()) {
                box.setChecked(!box.isChecked());
                Toast.makeText(getContext(), resp.getText(), Toast.LENGTH_LONG).show();
            }
        }
    }

}


