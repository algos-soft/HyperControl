package it.technocontrolsystem.hypercontrol.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.database.LibDev;
import it.technocontrolsystem.hypercontrol.R;

public class DeveloperActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        Button bexport = (Button) findViewById(R.id.exportDB);//federico
        bexport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int result= LibDev.copyDb();
                if(result!=0){
                    Toast.makeText(DeveloperActivity.this,"Esportazione database eseguita",Toast.LENGTH_LONG).show();

                }
            }

        });

        Button bdeleteall = (Button) findViewById(R.id.deleteAllRecords);//federico
        bdeleteall.setText("delete all records");

        bdeleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LibDev.deleteAllRecords();
            }
        });

    }

}
