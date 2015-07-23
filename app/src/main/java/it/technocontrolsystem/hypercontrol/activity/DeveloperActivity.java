package it.technocontrolsystem.hypercontrol.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.LibDev;

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


        Button bDeviceId = (Button) findViewById(R.id.showDeviceId);
        bDeviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeveloperActivity.this, Lib.getDeviceID(),Toast.LENGTH_LONG).show();
            }

        });

        Button bResetPassword = (Button) findViewById(R.id.resetPassword);
        bResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.remove(Prefs.PASSWORD);
                Toast.makeText(DeveloperActivity.this, "Password resettata.",Toast.LENGTH_LONG).show();
            }

        });

        Button bDelGCMToken = (Button) findViewById(R.id.delGCMtoken);
        bDelGCMToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.remove(Prefs.GCM_REGISTRATION_TOKEN);
                Toast.makeText(DeveloperActivity.this, "GCM Token eliminato.",Toast.LENGTH_LONG).show();
            }

        });

        Button bExpGCMToken = (Button) findViewById(R.id.exportGCMtoken);
        bExpGCMToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRegistration();
            }

        });


    }


    private void saveRegistration() {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/registration");
        myDir.mkdirs();
        String fname = "hcreg.txt";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            String str = Prefs.getRegistrationToken();
            byte[] bytes = str.getBytes();
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
            Toast.makeText(DeveloperActivity.this, "GCM Token exported in "+file,Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
