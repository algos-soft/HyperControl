package it.technocontrolsystem.hypercontrol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;


public class ConfigActivity extends HCListActivity {

    private Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        languageSpinner = (Spinner) findViewById(R.id.spinner);
        final ArrayAdapter<String> spinAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Languages.getNames());
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(spinAdapter);

        String langCode = Prefs.getPrefs().getString("language",Languages.IT.langCode);// IT è il default
        String langName=Languages.getNameByCode(langCode);
        if(langName!=null){
            languageSpinner.setSelection(getIndex(languageSpinner, langName));
        }


        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view1, int pos, long id) {
                String langName=spinAdapter.getItem(pos);
                String langCode=Languages.getCodeByName(langName);
                SharedPreferences.Editor editor = Prefs.getEditor();
                editor.putString("language", langCode);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg1) {
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

        final ToggleButton bReceiveNotif = (ToggleButton)findViewById(R.id.btn_receive_notif);
        bReceiveNotif.setChecked(Prefs.getPrefs().getBoolean(Prefs.RECEIVE_NOTIFICATIONS, true));
        bReceiveNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bReceiveNotif.isChecked()){  // going ON
                    Prefs.getEditor().putBoolean(Prefs.RECEIVE_NOTIFICATIONS, true).apply();
                }else{
                    Prefs.getEditor().putBoolean(Prefs.RECEIVE_NOTIFICATIONS, false).apply();
                }
            }
        });

        TextView vRegStatus = (TextView)findViewById(R.id.registration_status);
        String token=Prefs.getRegistrationToken();
        if(token!=null){
            vRegStatus.setText("Registrazione GCM effettuata");
        }else{
            vRegStatus.setText("Dispositivo non registrato su GCM");
        }

        Button bDeveloper = (Button) findViewById(R.id.btn_developer);
        if(HyperControlApp.isDeveloper()){
            bDeveloper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(ConfigActivity.this, DeveloperActivity.class);
                    startActivity(intent);
                }
            });

        }else{
            bDeveloper.setVisibility(View.GONE);
        }

    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        item=menu.add(Menu.NONE, MENU_CREDITS, Menu.NONE, getString(R.string.menu_credits));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }


    @Override
    public String getActionBarSubtitle() {
        return "Impostazioni";
    }

    @Override
    public String getHeadline2() {
        return null;
    }

    @Override
    public String getHeadline3() {
        return null;
    }

    @Override
    public int getNumItemsInList() {
        return 0;
    }

    @Override
    public String getItemsType() {
        return null;
    }

    // Enum dei linguaggi supportati
    public enum Languages{
        IT("IT","Italiano"),
        EN("EN","English");

        String langCode;
        String langName;

        Languages(String langCode, String langName) {
            this.langCode = langCode;
            this.langName = langName;
        }

        public static String[] getNames(){
            ArrayList<String> names = new ArrayList<>();
            for (Languages lang : Languages.values()){
                names.add(lang.langName);
            }
            return names.toArray(new String[0]);
        }

        public static String getNameByCode(String code){
            String name=null;
            for (Languages lang : Languages.values()){
                if(lang.langCode.equals(code)){
                    name=lang.langName;
                    break;
                }
            }
            return name;
        }

        public static String getCodeByName(String name){
            String code=null;
            for (Languages lang : Languages.values()){
                if(lang.langName.equals(name)){
                    code=lang.langCode;
                    break;
                }
            }
            return code;
        }

        public static Languages getLanguageByCode(String code){
            Languages foundlang = null;
            for (Languages lang : Languages.values()){
                if(lang.langCode.equals(code)){
                    foundlang=lang;
                    break;
                }
            }
            return foundlang;
        }

        public String getLangCode() {
            return langCode;
        }

        public void setLangCode(String langCode) {
            this.langCode = langCode;
        }
    }


}
