package it.technocontrolsystem.hypercontrol.components;

import android.content.Context;
import android.widget.Spinner;

import it.technocontrolsystem.hypercontrol.R;

/**
 *
 */
public class HCSpinner extends Spinner {

    public HCSpinner(Context context) {
        super(context);
        setBackgroundResource(R.drawable.apptheme_spinner_background_holo_dark);
    }
}
