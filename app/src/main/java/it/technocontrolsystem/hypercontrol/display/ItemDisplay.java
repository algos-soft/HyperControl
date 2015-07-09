package it.technocontrolsystem.hypercontrol.display;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.components.CircleText;
import it.technocontrolsystem.hypercontrol.components.HCCheckBox;
import it.technocontrolsystem.hypercontrol.components.MultiStateToggleButton;

/**
 * Superclass for all display classes
 */
public abstract class ItemDisplay extends LinearLayout {
    private TextView numberView;
    private TextView descriptionView;
    protected TextView detailView;
    protected TextView statusView;
    protected TextView valueView;
    //protected ImageView statusView;
    protected ImageView testView;
   // private CompoundButton bswitch;
   private MultiStateToggleButton bswitch;
    protected CheckBox checkBox;
    private int itemId;
    protected TextView redText;
    protected TextView yellowText;
    protected TextView greenText;
    protected TextView cyanText;



    //private Runnable infoRunnable;

//    public Runnable getInfoRunnable() {
//        return infoRunnable;
//    }

    public ItemDisplay(Context context,int itemId) {
        super(context);
        this.itemId=itemId;
        setOrientation(HORIZONTAL);
        int m = Lib.px2sp(4);
        setPadding(m,m,m,m);
        setMinimumHeight(Lib.px2sp(60));

        // la riga resta selezionabile anche se contiene bottoni
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);


        //this.infoRunnable=infoRunnable;
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void init() {
        LinearLayout.LayoutParams params;

        numberView=new TextView(getContext());
        params=createParams();
        params.gravity=Gravity.CENTER_VERTICAL;
        numberView.setLayoutParams(params);

        numberView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        setNumberWidth(50);

        descriptionView=new TextView(getContext());
        params=createParams();
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        descriptionView.setLayoutParams(params);
        descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        descriptionView.setTypeface(null, Typeface.BOLD);


        detailView=new TextView(getContext());
        params=createParams();
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        detailView.setLayoutParams(params);
        detailView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        setDetail("");

       // statusView=new ImageView(getContext());
        statusView=new TextView(getContext());
//        params=createParams();
//        params.width=Lib.px2sp(30);
//        params.height=Lib.px2sp(30);
//        params.gravity=Gravity.CENTER_VERTICAL;
//        statusView.setLayoutParams(params);
        //statusView.setImageResource(R.drawable.accept);
        params=createParams();
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        statusView.setLayoutParams(params);
        statusView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        testView=new ImageView(getContext());
        params=createParams();
        params.width=Lib.px2sp(30);
        params.height=Lib.px2sp(30);
        params.gravity=Gravity.CENTER_VERTICAL;
        testView.setLayoutParams(params);
        testView.setImageResource(R.drawable.unknown);

//        // Switch o ToggleButton
//        params=createParams();
//        if(Lib.hasIceCreamSandwich()){
//            bswitch = createSwitch();
//            params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
//        }else{
//            bswitch=createToggle();
//            params.width=Lib.px2sp(50);
//        }
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.gravity=Gravity.CENTER_VERTICAL;
//        bswitch.setLayoutParams(params);
//
//        // registro un listener sul bottone switch con itemId
//        SwitchClickedListener l=new SwitchClickedListener(getItemId());
//        bswitch.setOnClickListener(l);

        LinearLayout layout=new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        params=createParams(1);
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity=Gravity.CENTER_VERTICAL;
        layout.setLayoutParams(params);

        layout.addView(descriptionView);
        //Federico
        LinearLayout orizLayout=new LinearLayout(getContext());
        params=createParams(1);
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        orizLayout.setOrientation(HORIZONTAL);

        orizLayout.addView(numberView);
        orizLayout.addView(createSpacer());
        orizLayout.addView(detailView);
        orizLayout.addView(createSpacer());
        orizLayout.addView(statusView);
        orizLayout.addView(createSpacer());
        orizLayout.addView(testView);
        layout.addView(orizLayout);
        addView(layout);

        redText = new CircleText(getContext()) {
            @Override
            protected int getColor() {
                return Color.RED;
            }
        };
        redText.getBackground().setAlpha(20);
        params=createParams();
        params.width=Lib.px2sp(15);
        params.height=Lib.px2sp(15);
        params.gravity=Gravity.CENTER_VERTICAL;
        redText.setLayoutParams(params);

        yellowText = new CircleText(getContext()) {
            @Override
            protected int getColor() {
                return Color.YELLOW;
            }
        };
        yellowText.getBackground().setAlpha(20);
        params=createParams();
        params.width=Lib.px2sp(15);
        params.height=Lib.px2sp(15);
        params.gravity=Gravity.CENTER_VERTICAL;
        yellowText.setLayoutParams(params);


        greenText = new CircleText(getContext()) {
            @Override
            protected int getColor() {
                return Color.GREEN;
            }
        };

        greenText.getBackground().setAlpha(20);
        params=createParams();
        params.width=Lib.px2sp(15);
        params.height=Lib.px2sp(15);
        params.gravity=Gravity.CENTER_VERTICAL;
        greenText.setLayoutParams(params);

        cyanText = new CircleText(getContext()) {
            @Override
            protected int getColor() {
                return Color.CYAN;
            }
        };
        cyanText.getBackground().setAlpha(20);
        params=createParams();
        params.width=Lib.px2sp(15);
        params.height=Lib.px2sp(15);
        params.gravity=Gravity.CENTER_VERTICAL;
        cyanText.setLayoutParams(params);


        valueView=new TextView(getContext());
        params=createParams();
        params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity=Gravity.CENTER_VERTICAL;
        valueView.setLayoutParams(params);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        valueView.setTypeface(null, Typeface.BOLD);



        addView(valueView);
        addView(createSpacer());
        addView(cyanText);
        addView(createSpacer());
        addView(redText);
        addView(createSpacer());
        addView(yellowText);
        addView(createSpacer());
        addView(greenText);
        addView(createSpacer());
//       addView(statusView);
      //  addView(createRadioButton());
        addView(createSwitchView());
//        addView(testView);
//        addView(createSpacer());
        checkBox=createCheckBox();
        addView(checkBox);

        // registro un listener sul bottone switch con itemId e checkbox
        SwitchClickedListener l=new SwitchClickedListener(getItemId(), checkBox);
        bswitch.setOnClickListener(l);

        // registro un listener sul checkbox con itemId e bottone
        BoxCheckListener bl=new BoxCheckListener(getItemId(), bswitch);
        checkBox.setOnClickListener(bl);

    }

    /**
     * Crea la view con il componente di switch (switch button, slider o altro)
     */
    protected View createSwitchView(){

     //   LinearLayout.LayoutParams params;

        // Switch o ToggleButton
     //   params=createParams();

        bswitch=new MultiStateToggleButton(getContext());

        // Position one is selected by default
        bswitch.setElements(R.array.choice_array, 1);

        // Multiple elements can be selected simultaneously
        bswitch.enableMultipleChoice(false);

//        if(Lib.hasIceCreamSandwich()){
//            bswitch = createSwitch();
//            params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
//        }else{
//            bswitch=createToggle();
//            params.width=Lib.px2sp(50);
//        }
//        params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.gravity=Gravity.CENTER_VERTICAL;
//        bswitch.setLayoutParams(params);


        return bswitch;
    }

//
//    private RadioGroup createRadioButton(){
//
//        RadioGroup radioBox=new RadioGroup(getContext());
//
//        radioBox.addView(createMarcoToggle("Totale","Totale"));
//        radioBox.addView(createMarcoToggle("Parziale", "Parziale"));
//        radioBox.addView(createMarcoToggle("Spento", "Spento"));
//
//        radioBox.setOrientation(HORIZONTAL);
//        LinearLayout.LayoutParams params;
//        params=createParams();
//        params.gravity=Gravity.CENTER_VERTICAL;
////        params.setMargins(-5,-5,-5,-5);
//
//        radioBox.setLayoutParams(params);
//
//        return radioBox;
//    }


    private CheckBox createCheckBox(){

        CheckBox checkBox=new HCCheckBox(getContext());

        LinearLayout.LayoutParams params;
        params=createParams();
        params.gravity=Gravity.CENTER_VERTICAL;
        checkBox.setLayoutParams(params);

        return checkBox;
    }

    protected LinearLayout createSpacer(){
        LinearLayout.LayoutParams params;
        LinearLayout spacer=new LinearLayout(getContext());
        params=createParams();
        params.width= Lib.px2sp(10);
        spacer.setLayoutParams(params);
        return spacer;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Switch createSwitch(){
        Switch button=new Switch(getContext());
        //Switch button=new HCSwitchButton(getContext());
        button.setTextOn("On");
        button.setTextOff("Off");
        return button;
    }

    private ToggleButton createToggle(){
        ToggleButton button=new ToggleButton(getContext());
        button.setTextOn("On");
        button.setTextOff("Off");
        return button;
    }
//
//    private ToggleButton createMarcoToggle(String tOn, String tOff){
//        ToggleButton button=new ToggleButton(getContext());
//        button.setTextOn(tOn);
//        button.setTextOff(tOff);
//        button.setChecked(false);
//        button.setText(tOff);
//        return button;
//    }
    protected LinearLayout.LayoutParams createParams(){
       return createParams(0);
    }

    private LinearLayout.LayoutParams createParams(float weight){
        LinearLayout.LayoutParams params;
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,weight);
        return params;
    }

    protected void setNumber(int number) {
        numberView.setText("" + number);

    }

    protected void setDescription(String description) {
        descriptionView.setText(description);

    }
    protected void setDetail(String detail) {
        detailView.setText(detail);
        if (detail.equals("")) {
            detailView.setVisibility(GONE);
        }else{
            detailView.setVisibility(VISIBLE);
        }
    }

    protected void setNumberWidth(int width){
        numberView.getLayoutParams().width= Lib.px2sp(width);
    }

    protected void setStatusIcon(String resId){
        statusView.setText(resId);
    }
//    protected void setStatusIcon(int resId){
//        statusView.setImageResource(resId);
//    }

    protected void setAlarm(boolean alarm){
        if(alarm){
            setBackgroundColor(Color.RED);
        }else{
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public CompoundButton getBswitch( ){
        return bswitch;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public int getItemId() {
        return itemId;
    }

    class SwitchClickedListener implements OnClickListener{
        private int id;
        private CheckBox checkBox;

        SwitchClickedListener(int id, CheckBox checkBox) {
            this.id=id;
            this.checkBox=checkBox;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CompoundButton){
                CompoundButton button=(CompoundButton) v;
                switchPressed(id,button, checkBox.isChecked());
            }
        }
    }
    /**
    *  Il colore che indica il valore
    */
  //  public abstract void setValueColor();
    /**
     * Il bottone switch è stato premuto
     */
    public abstract void switchPressed(int itemId, CompoundButton button, boolean partial);

    class BoxCheckListener implements OnClickListener{
        private int id;
        private CompoundButton button;

        BoxCheckListener(int id, CompoundButton button) {
            this.id = id;
            this.button = button;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof CheckBox){
                CheckBox check=(CheckBox) v;
                boxClicked(id, check, button.isChecked());
            }
        }
    }

    /**
     * Il checkbox è stato cliccato
     */
    public abstract void boxClicked(int itemId, CheckBox box, boolean buttonOn);



}
