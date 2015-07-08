package it.technocontrolsystem.hypercontrol.display;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.domain.Event;

/**
 * Created by alex on 8-07-2015.
 */
public class EventDisplay extends LinearLayout {

    private Event event;

    public EventDisplay(Context context, Event e) {
        super(context);
        this.event=e;
        setOrientation(HORIZONTAL);
        int m = Lib.px2sp(4);
        setPadding(m,m,m,m);
        setMinimumHeight(Lib.px2sp(60));

        init();

    }


    private void init() {

        String str;

        LinearLayout.LayoutParams params;

        TextView timeView=new TextView(getContext());
        params=createParams();
        timeView.setLayoutParams(params);
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        timeView.getLayoutParams().width= Lib.px2sp(90);
        str=event.getData();
        str=str.replace(" ", "\n");
        timeView.setText(str);

        TextView idView=new TextView(getContext());
        params=createParams();
        idView.setLayoutParams(params);
        idView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        idView.getLayoutParams().width= Lib.px2sp(50);
        idView.setText(""+event.getId());

        TextView typeView=new TextView(getContext());
        params=createParams();
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        typeView.setLayoutParams(params);
        typeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        idView.getLayoutParams().width= Lib.px2sp(25);
//        typeView.setTypeface(null, Typeface.BOLD);
        typeView.setText(""+event.getTipo());

        TextView detailView=new TextView(getContext());
        params=createParams();
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        detailView.setLayoutParams(params);
        detailView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        detailView.setText(event.getTesto());

        addView(timeView);
        addView(createGap());
        addView(idView);
        addView(createGap());
        addView(typeView);
        addView(createGap());
        addView(detailView);

    }

    protected LinearLayout.LayoutParams createParams(){
        return createParams(0);
    }

    private LinearLayout.LayoutParams createParams(float weight){
        LinearLayout.LayoutParams params;
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,weight);
        params.gravity= Gravity.TOP;
        return params;
    }

    private View createGap(){
        TextView spc=new TextView(getContext());
        LayoutParams params=createParams();
        spc.setLayoutParams(params);
        spc.getLayoutParams().width= Lib.px2sp(10);
        return spc;
    }



}
