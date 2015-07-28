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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.AbsPopulateTask;

/**
 * Activity di base con ActionBar che gestisce un header e una lista interna.
 * Usiamo ActionBarActivity per avere la garanzia che l'option menu sia sempre accessibile.
 * Senza la ActionBar dovremmo gestire l'option menu in modo custom
 * perché il pulsante menu non è più garantito sui dispositivi.
 * (nemmeno sui phones dopo una certa versione di Android)
 */
public abstract class HCActivity extends ActionBarActivity {

    public static final int MENU_SETTINGS = 190;
    public static final int MENU_CREDITS = 191;
    public static final String TAG = "HCActivity";

    private ArrayAdapter listAdapter;
    public ProgressDialog progress;
    private boolean newInstance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newInstance=(savedInstanceState==null);

        progress = new ProgressDialog(this);

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
        regolaHeader();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Ritorna il testo da visualizzare nel subtitle della ActionBar
     */
    public abstract String getActionBarSubtitle();


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


    public void regolaHeader() {
        TextView view;

        String line2 = getHeadline2();
        String line3 = getHeadline3();

        view = (TextView) findViewById(R.id.hdr_line2);
        if(view!=null){
            if (line2 != null && !line2.equals("")) {
                view.setText(line2);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        view = (TextView) findViewById(R.id.hdr_line3);
        if(view!=null){
            if (line3 != null && !line3.equals("")) {
                view.setText(line3);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        int number = getNumItemsInList();
        TextView numView = (TextView) findViewById(R.id.hdr_item_num);
        TextView typeView = (TextView) findViewById(R.id.hdr_item_type);
        if(number>-1){
            String type = getItemsType();
            if(numView!=null){
                numView.setText("" + number);
            }
            if(typeView!=null){
                typeView.setText(type);
            }
        }else{
            if(numView!=null){
                numView.setVisibility(View.GONE);
            }
            if(typeView!=null){
                typeView.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Ritorna il testo da visualizzare nella seconda riga dell'header
     */
    public abstract String getHeadline2();

    /**
     * Ritorna il testo da visualizzare nella terza riga dell'header
     */
    public abstract String getHeadline3();

    /**
     * Ritorna il numero di elementi contenuti nel database e visualizzati in lista
     * tornare -1 se l'informazione non è rilevante, in modo che non venga
     * visualizzata nell'header
     */
    public abstract int getNumItemsInList();

    /**
     * Ritorna il testo da visualizzare in corrispondenza del numero di elementi
     */
    public abstract String getItemsType();



    public ListView getListView() {
        return (ListView) findViewById(R.id.list);
    }


    public ArrayAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ArrayAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    /**
     * @return true if is a new instance, false if its being recreated after a config change
     */
    public boolean isNewInstance(){
        return newInstance;
    }
}
