package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import it.technocontrolsystem.hypercontrol.model.SiteModel;

/**
 *
 */
public class SiteDisplay extends ItemDisplay {
    public SiteDisplay(Context context, int itemId) {
        super(context, itemId);
        testView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        getBswitch().setVisibility(View.GONE);
    }

    public void update(SiteModel siteModel){
        setNumber(siteModel.getSite().getId());
        setDescription(siteModel.getSite().getName());
    }
    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {}

    @Override
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {}

}
