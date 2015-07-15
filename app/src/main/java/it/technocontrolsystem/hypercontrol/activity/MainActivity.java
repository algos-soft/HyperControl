package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;


/**
 * Seleziona la modalità di partenza in base al numero di siti registrati.
 * Questa activity non ha interfaccia, lancia sempre un'altra
 * activity e poi si chiude automaticamente.
 */
public class MainActivity extends Activity {

    private static int ACTION_CREATE_NEW_SITE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // seleziona il tipo di partenza dell'applicazione
        // in base al numero di siti presenti
        Results r = getSelect();
        switch (r) {
            case NONE:
                noSite();
                break;
            case ONE:
                singleSite();
                break;
            case MANY:
                manySites();
                break;
        }
    }


    private void noSite() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, EditSiteActivity.class);
        intent.putExtra("title","Nessun sito registrato. " +
                "Devi accedere a un sito per scaricare la configurazione.");
        intent.putExtra("usedelete",false);
        intent.putExtra("usesave",true);
        startActivityForResult(intent, ACTION_CREATE_NEW_SITE);
    }

    private void singleSite() {
        Intent intent = new Intent();
        intent.setClass(this, StartSiteActivity.class);
        Site site = DB.getSites()[0];
        intent.putExtra("siteid", site.getId());
        startActivity(intent);
        finish();
    }

    private void manySites() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SitesListActivity.class);
        startActivity(intent);
        finish();
    }


    private Results getSelect() {
        Results r = null;
        int numSites = DB.getSitesCount();
        if (numSites == 0) {
            r = Results.NONE;
        } else {
            if (numSites == 1) {
                r = Results.ONE;
            } else {
                r = Results.MANY;
            }

        }

        return r;
    }

    private enum Results {NONE, ONE, MANY}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // ha creato un nuovo sito
        // lancio il nuovo sito e chiudo questa activity
        if(requestCode==ACTION_CREATE_NEW_SITE){
            if(resultCode==RESULT_OK){
                int siteid=data.getIntExtra("siteid",0);
                Intent intent = new Intent();
                intent.setClass(this, StartSiteActivity.class);
                intent.putExtra("siteid", siteid);
                startActivity(intent);
                finish();
            }
        }


    }
}
