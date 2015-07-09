package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.components.HCRadioButton;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;


public class RadioSelectSiteActivity extends Activity {
    private RadioGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_select_site);

        String operation = getIntent().getStringExtra("operation");
        String buttonText=null;
        String title=null;
        if(operation.equals("edit")){
            buttonText= (String) getResources().getText(R.string.text_btn_edit);
            title=(String) getResources().getText(R.string.text_editesite);
        }
        if(operation.equals("delete")){
            buttonText= (String) getResources().getText(R.string.text_btn_delete);
            title=(String) getResources().getText(R.string.text_deletesite);
        }

        fillRadioPanel();
        TextView txt=(TextView) findViewById(R.id.title);
        Button bOk = (Button) findViewById(R.id.btn_ok);
        String name = buttonText;
        if (name != null) {
            bOk.setText(name);
            txt.setText(title);
        }
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSelectedSiteId()>0){
                    ok();
                }else{
                    Toast t=Toast.makeText(RadioSelectSiteActivity.this,getString(R.string.text_toast_error),Toast.LENGTH_SHORT);
                    t.show();
                }

            }
        });


    }

    private void ok(){
        //if a destination activity was specified
        //start the activity
        Serializable ser = getIntent().getSerializableExtra("destinationactivity");
        if (ser != null) {
            if (ser instanceof Class) {
                Intent intent = new Intent();
                intent.setClass(this, (Class) ser);
                intent.putExtra("siteid",getSelectedSiteId());
                startActivity(intent);
            }
        }
        Intent outIntent=new Intent();
        outIntent.putExtra("siteid",getSelectedSiteId());
        setResult(Activity.RESULT_OK,outIntent);
        finish();
    }


    private void fillRadioPanel(){
        ScrollView panel=(ScrollView)findViewById(R.id.radioPanel);
        Site[] sites = DB.getSites();
       this.group=new RadioGroup(this);
        RadioButton rdbot;
        for (Site site:sites){
            rdbot=new HCRadioButton(this);
            rdbot.setText(site.getName());
            rdbot.setId(site.getId());
            group.addView(rdbot);

        }
        panel.addView(group);
    }

    private int getSelectedSiteId(){
        return group.getCheckedRadioButtonId();
         }


}
