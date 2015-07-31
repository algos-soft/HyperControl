package it.technocontrolsystem.hypercontrol.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import it.technocontrolsystem.hypercontrol.R;

/**
 * Activity di base con ActionBar che fornisce i menu di base e l'ActionBar dell'applicazione.
 * Usiamo ActionBarActivity per avere la garanzia che l'option menu sia sempre accessibile.
 * Senza la ActionBar dovremmo gestire l'option menu in modo custom
 * perché il pulsante menu non è più garantito sui dispositivi.
 * (nemmeno sui phones dopo una certa versione di Android)
 */
public abstract class HCActivity extends ActionBarActivity {

    public static final int MENU_SETTINGS = 190;
    public static final int MENU_CREDITS = 191;

    private boolean newInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newInstance=(savedInstanceState==null);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setIcon(R.drawable.ic_launcher);
        String name = getString(R.string.app_name);
        bar.setTitle(name);

    }


    @Override
    protected void onStart() {
        super.onStart();
        String subtitle = getActionBarSubtitle();
        getSupportActionBar().setSubtitle(subtitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
        item=menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, getString(R.string.menu_settings));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        item=menu.add(Menu.NONE, MENU_CREDITS, Menu.NONE, getString(R.string.menu_credits));
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case MENU_SETTINGS: {
                Intent intent = new Intent();
                intent.setClass(this, ConfigActivity.class);
                startActivity(intent);
                break;
            }

            case MENU_CREDITS: {

                String app_version = "";
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    app_version = pInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String msg=getString(R.string.app_name)+" "+app_version+"\n";
                msg+="©2015 Technocontrol System\n";
                msg+="info@technocontrolsystem.it";
                builder.setMessage(msg);
                builder.setPositiveButton("continua", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            }


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @return true if is a new instance, false if its being recreated after a config change
     */
    public boolean isNewInstance(){
        return newInstance;
    }

    /**
     * Ritorna il testo da visualizzare nel subtitle della ActionBar
     */
    public abstract String getActionBarSubtitle();


}
