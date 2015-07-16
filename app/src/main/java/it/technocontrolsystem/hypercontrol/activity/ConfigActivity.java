package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;


public class ConfigActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AssetManager am = getAssets();
        Typeface type;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);

        TextView title=(TextView) findViewById(R.id.title);
        type = Typeface.createFromAsset(am, "GreatVibes-Regular.otf");
        title.setTypeface(type);

        Button bSetting = (Button) findViewById(R.id.btn_setting);
        type = Typeface.createFromAsset(am, "AlexBrush-Regular.ttf");
        bSetting.setTypeface(type);

        bSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ConfigActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        Button bSetPassword = (Button)findViewById(R.id.btn_password);
        bSetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ConfigActivity.this, PasswordActivity.class);
                startActivity(intent);
            }
        });

        Button bDeveloper = (Button) findViewById(R.id.btn_developer);
        bDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ConfigActivity.this, DeveloperActivity.class);
                startActivity(intent);

            }
        });

    }


}
