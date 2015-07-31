package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.domain.Area;

/**
 * Created by alex on 31-07-2015.
 */
public class AreaDisplayInOutput extends LinearLayout {

    private Area area;
    private TextView areaNameView;
    private TextView plantNameView;

    public AreaDisplayInOutput(Context context, Area area) {
        super(context);
        this.area=area;

        setOrientation(HORIZONTAL);
        int m = Lib.px2sp(4);
        setPadding(m,m,m,m);
        setMinimumHeight(Lib.px2sp(60));

        // la riga resta selezionabile anche se contiene bottoni
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        init();
    }


    private void init() {
        LinearLayout.LayoutParams params;

        areaNameView=new TextView(getContext());
        params=createParams();
        params.gravity= Gravity.CENTER_VERTICAL;
        areaNameView.setLayoutParams(params);
        areaNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        areaNameView.setText(area.getName());


        plantNameView=new TextView(getContext());
        params=createParams();
        plantNameView.setTypeface(null, Typeface.BOLD);
        params.gravity= Gravity.CENTER_VERTICAL;
        plantNameView.setLayoutParams(params);
        plantNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        plantNameView.setText(area.getPlant().getName());

        LinearLayout layout=new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        params=createParams(1);
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity=Gravity.CENTER_VERTICAL;
        layout.setLayoutParams(params);

        layout.addView(plantNameView);
        layout.addView(areaNameView);

        addView(layout);

    }


    protected LinearLayout.LayoutParams createParams(){
        return createParams(0);
    }

    private LinearLayout.LayoutParams createParams(float weight){
        LinearLayout.LayoutParams params;
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,weight);
        return params;
    }



}
