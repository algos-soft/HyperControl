package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.LanguageRequest;

public class SettingActivity extends Activity {
    private String[] language;
    private Spinner languageChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        languageChoice = (Spinner) findViewById(R.id.spinner);
        language = new String[]{"Select Language", "Italian", "English"};
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, language);
        // spinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        languageChoice.setAdapter(spinAdapter);

        languageChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view1, int pos, long id) {
                int loc;
                SharedPreferences.Editor editor = Prefs.getEditor();
                SharedPreferences prefs = Prefs.getPrefs();
                String restoredText = prefs.getString("language", null);
                if (restoredText != null) {
                    String language = restoredText;//"No name defined" is the default value.
                    int a = 1;
                }
                loc = pos;
                LanguageRequest request;
                switch (loc) {
                    case 1:
                        request = new LanguageRequest();
                        request.setLan("IT");
                        HyperControlApp.getConnection().sendRequest(request);
                        editor.putString("language", "IT");
                        break;

                    case 2:
                        request = new LanguageRequest();
                        request.setLan("EN");
                        HyperControlApp.getConnection().sendRequest(request);
                        editor.putString("language", "EN");
                        break;

                    default:

                        break;
                }
                editor.commit();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg1) {
            }
        });
    }

}
