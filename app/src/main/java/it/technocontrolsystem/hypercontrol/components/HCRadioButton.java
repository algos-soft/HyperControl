package it.technocontrolsystem.hypercontrol.components;

import android.content.Context;
import android.graphics.Color;
import android.widget.RadioButton;

import it.technocontrolsystem.hypercontrol.R;

/**
 * Created by Federico on 29/04/2015.
 */
public class HCRadioButton extends RadioButton {
    public HCRadioButton(Context context) {
        super(context);
        setButtonDrawable(R.drawable.custom_radiobtn);
        setTextColor(Color.parseColor("#ff4ae02e"));
    }
}
