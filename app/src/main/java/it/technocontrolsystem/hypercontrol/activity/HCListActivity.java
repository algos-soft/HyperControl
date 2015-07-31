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
 * Activity che gestisce un header informativo e una lista interna.
 */
public abstract class HCListActivity extends HCActivity {

    public static final String TAG = "HCListActivity";

    private ArrayAdapter listAdapter;
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        regolaHeader();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

}
