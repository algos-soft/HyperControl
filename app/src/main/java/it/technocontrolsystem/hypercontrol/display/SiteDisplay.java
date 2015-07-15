package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import it.technocontrolsystem.hypercontrol.activity.EditSiteActivity;
import it.technocontrolsystem.hypercontrol.activity.PlantActivity;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.activity.SitesListActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.model.SiteModel;

/**
 *
 */
public class SiteDisplay extends ItemDisplay {

    private SitesListActivity activity;

    public SiteDisplay(SitesListActivity activity, int itemId) {
        super(activity, itemId);
        this.activity=activity;
        testView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        getBswitch().setVisibility(View.GONE);

        Button bEdit = new Button(getContext());
        LinearLayout.LayoutParams params;
        params=createParams();
        params.gravity= Gravity.CENTER_VERTICAL;
        bEdit.setLayoutParams(params);

        final SitesListActivity activityFinal=activity;
        bEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), EditSiteActivity.class);
                intent.putExtra("siteid", getItemId());
                intent.putExtra("usesave", true);
                intent.putExtra("usedelete", true);
                activityFinal.startActivityForResult(intent,SitesListActivity.ACTIVITY_EDIT_SITE);
            }
        });
        bEdit.setText("Edit");
        addView(bEdit);
    }

    public void update(SiteModel siteModel){
        setNumber(siteModel.getSite().getId());
        setDescription(siteModel.getSite().getName());
    }
    @Override
    public void switchPressed(int itemId, CompoundButton button, boolean partial) {}

    @Override
    public void boxClicked(int itemId, CheckBox box, boolean buttonOn) {}

    public Site getSite(){
        return DB.getSite(getItemId());
    }

}
