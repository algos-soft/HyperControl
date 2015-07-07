package it.technocontrolsystem.hypercontrol.components;

import android.content.Context;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.R;

/**
 * Created by Federico on 04/05/2015.
 */
public class HCToggleButton extends ToggleButton {
    public HCToggleButton(Context context) {
        super(context);
        setButtonDrawable(R.drawable.apptheme_btn_toggle_holo_dark);
    }
}
