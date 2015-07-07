package it.technocontrolsystem.hypercontrol.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Switch;

import it.technocontrolsystem.hypercontrol.R;

/**
 * Created by Federico on 04/05/2015.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class HCSwitchButton extends Switch {
    public HCSwitchButton(Context context) {
        super(context);
        setButtonDrawable(R.drawable.apptheme_switch_inner_holo_dark);
    }
}
