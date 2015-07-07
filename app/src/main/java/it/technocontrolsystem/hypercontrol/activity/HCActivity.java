package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import it.technocontrolsystem.hypercontrol.communication.LiveRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;

/**
 *
 */
public abstract class HCActivity extends Activity {

    public static final int MENU_SETTINGS = 1;
    public static final int MENU_BOARDS = 2;
    public static final int MENU_EVENTI = 3;

    protected HCListAdapter listAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            listAdapter.attachLiveListener();
            startLive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *attiva la trasmissione aggiornamenti live
     */
    private void startLive() throws Exception{
        LiveRequest request=new LiveRequest(getLiveCode(), getParamPlantNumCode(),getParamAreaNumCode());

        Response resp = SiteActivity.getConnection().sendRequest(request);
        if (resp != null) {
            if (!resp.isSuccess()) {
                throw new Exception(resp.getText());
            }
        } else {//comunication failed
            throw new Exception("Attivazione live fallita");
        }
    }

    /**
     * Ritorna il codice di attivazione live (Set) per questa activity
     */
   public abstract int getLiveCode();
   public abstract int getParamPlantNumCode();
   public abstract int getParamAreaNumCode();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
        menu.add(Menu.NONE, MENU_BOARDS, Menu.NONE, "Boards");
        menu.add(Menu.NONE, MENU_EVENTI, Menu.NONE, "Eventi");
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

            case MENU_BOARDS: {
                Intent intent = new Intent();
                intent.setClass(this, BoardActivity.class);
                intent.putExtra("siteid",getSite().getId());
                startActivity(intent);
                break;
            }

            case MENU_EVENTI: {
                Intent intent = new Intent();
                intent.setClass(this, EventiActivity.class);
                intent.putExtra("siteid",getSite().getId());
                startActivity(intent);
                break;
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public abstract Site getSite();

}
