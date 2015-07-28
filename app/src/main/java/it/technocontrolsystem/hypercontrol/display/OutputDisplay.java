package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.model.OutputModel;

/**
 *
 */
public class OutputDisplay extends ItemDisplay {

    public OutputDisplay(Context context,int outputId) {
        super(context, outputId);

        testView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        cyanText.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        bswitch.setVisibility(View.GONE);
        //  setNumberWidth(100);
    }

    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {

    }

    @Override
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {

    }

    public void update(OutputModel model){
        setNumber(model.getOutput().getNumber());
        setDescription(model.getOutput().getName());


        int statusCode = model.getStatus();

        int iconId;
      /*  switch (statusCode){
            case 0:
                redText.getBackground().setAlpha(100);
                getBswitch().setChecked(false);
                statusView.setText("Disabilitata");
                break;
            case 1:
                greenText.getBackground().setAlpha(100);
                getBswitch().setChecked(true);
                statusView.setText("Online");
                break;
            case 2:
                yellowText.getBackground().setAlpha(100);
                getBswitch().setChecked(true);
                //  statusView.setVisibility(VISIBLE);
                statusView.setText("TIMEOUT");
                break;
            default:
                getBswitch().setChecked(false);
                getBswitch().setEnabled(false);
                break;
        }*/


    }

/*
    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {
        int boardNumber= DB.getBoard(itemId).getNumber();

        Request req=new BoardsCommanRequest(boardNumber, button.isChecked(), false);

        Response resp= HyperControlApp.sendRequest(req);

        if(resp!=null){
            if (!resp.isSuccess()) {
                button.setChecked(!button.isChecked());
                Toast.makeText(getContext(), resp.getText(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {

    }*/
}
