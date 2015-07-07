package it.technocontrolsystem.hypercontrol.components;

import android.content.Context;
import android.widget.EditText;

import it.technocontrolsystem.hypercontrol.R;

/**
 * Created by Federico on 04/05/2015.
 */
public class HCEditText extends EditText {
    public HCEditText(Context context) {
        super(context);
        setBackgroundResource(R.drawable.apptheme_edit_text_holo_dark);
    }
}
