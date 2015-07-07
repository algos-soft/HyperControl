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

import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.R;


public class ConfigActivity extends Activity {
    private static final int DELETE_ACTION = 87;
    private static Activity activityA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AssetManager am = getAssets();
        Typeface type;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);

        activityA = this;

        TextView title=(TextView) findViewById(R.id.title);
        type = Typeface.createFromAsset(am, "GreatVibes-Regular.otf");
        title.setTypeface(type);

        Button bNewSite = (Button) findViewById(R.id.btn_new);
        type = Typeface.createFromAsset(am, "green_avocado.ttf");
        bNewSite.setTypeface(type);

        bNewSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSite();
            }
        });

        Button bEditSite = (Button) findViewById(R.id.btn_edit);
        type = Typeface.createFromAsset(am, "smart_watch.ttf");
        bEditSite.setTypeface(type);

        bEditSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSite();
            }
        });

        Button bDeleteSite = (Button) findViewById(R.id.btn_delete);
        type = Typeface.createFromAsset(am, "AlexBrush-Regular.ttf");
        bDeleteSite.setTypeface(type);

        bDeleteSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSite();
            }
        });

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

    private void newSite() {
        Intent intent = new Intent();
        intent.setClass(this, EditSiteActivity.class);
        startActivity(intent);
    }

    private void editSite() {
        Intent intent = new Intent();
        intent.setClass(this, RadioSelectSiteActivity.class);
        intent.putExtra("destinationactivity", EditSiteActivity.class);
        intent.putExtra("operation", "edit");
        startActivity(intent);

    }

    private void deleteSite() {
        Intent intent = new Intent();
        intent.setClass(this, RadioSelectSiteActivity.class);
        //    intent.putExtra("buttonname","Delete Site");
        intent.putExtra("operation", "delete");
        startActivityForResult(intent, DELETE_ACTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DELETE_ACTION:
                if (resultCode == Activity.RESULT_OK) {
                    final int id = data.getIntExtra("siteid", 0);
                    if (id > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getString(R.string.text_deletesite));
                        final String name = DB.getSite(id).getName();
                        builder.setMessage(getString(R.string.text_question) + "\n" + name);

                        builder.setPositiveButton(getString(R.string.text_btn_confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DB.deleteSite(id);
                                Toast.makeText(ConfigActivity.this, name + getString(R.string.text_btn_deleted), Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton(getString(R.string.text_btn_cancel), null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
                break;
        }
    }
    public static Activity getInstance() {
        return activityA;
    }
}
