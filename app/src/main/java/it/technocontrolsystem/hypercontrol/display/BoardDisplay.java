package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.BoardsCommanRequest;
import it.technocontrolsystem.hypercontrol.communication.Request;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.model.BoardModel;

/**
 * Display class for Boards
 */
public class BoardDisplay extends ItemDisplay {

    public BoardDisplay(Context context,int boardId) {
        super(context, boardId);

        testView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        //  setNumberWidth(100);
    }

    public void update(BoardModel model){
        setNumber(model.getBoard().getNumber());
        setDescription(model.getBoard().getName());

        int statusCode = model.getStatus();

        int iconId;
        switch (statusCode){
            case 0:
                getBswitch().setChecked(false);
                break;
            case 1:
                getBswitch().setChecked(true);
                break;
            case 2:
                getBswitch().setChecked(true);
                statusView.setVisibility(VISIBLE);
                break;
            default:
                getBswitch().setChecked(false);
                getBswitch().setEnabled(false);
                break;
            }


    }


    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {
        int boardNumber= DB.getBoard(itemId).getNumber();

        Request req=new BoardsCommanRequest(boardNumber, button.isChecked(), false);
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
