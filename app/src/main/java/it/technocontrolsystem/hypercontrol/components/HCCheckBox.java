package it.technocontrolsystem.hypercontrol.components;

import android.content.Context;
import android.widget.CheckBox;

import it.technocontrolsystem.hypercontrol.R;

/**
 *
 */
public class HCCheckBox extends CheckBox{
    public HCCheckBox(Context context) {
        super(context);
        setButtonDrawable(R.drawable.apptheme_btn_check_holo_dark);
    }
}
