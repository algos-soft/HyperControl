package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.PlantCommandRequest;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Plant;
import it.technocontrolsystem.hypercontrol.model.PlantModel;

/**
 * Display class for plants
 */
public class PlantDisplay extends ItemDisplay {

    public PlantDisplay(Context context,int plantId) {
        super(context, plantId);

        greenText.setVisibility(View.GONE);
        redText.setVisibility(View.GONE);
        yellowText.setVisibility(View.GONE);
        cyanText.setVisibility(View.GONE);
        valueView.setVisibility(View.GONE);
        testView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        //  setNumberWidth(100);
    }

    public void update(PlantModel plantModel){
        setNumber(plantModel.getPlant().getNumber());
        setDescription(plantModel.getPlant().getName());

        int statusCode = plantModel.getStatus();


        switch (statusCode){
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

        setAlarm(plantModel.isAlarm());
    }

    public int getPlantId() {
        return getItemId();
    }

    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {
        Plant plant= DB.getPlant(itemId);
        int numPlant=plant.getNumber();

        Request req=new PlantCommandRequest(numPlant, button.isChecked(),partial);
        Response resp= HyperControlApp.getConnection().sendRequest(req);
        if(resp!=null){
            if (!resp.isSuccess()) {
                button.setChecked(!button.isChecked());
                Toast.makeText(getContext(), resp.getText(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {

    }
}


