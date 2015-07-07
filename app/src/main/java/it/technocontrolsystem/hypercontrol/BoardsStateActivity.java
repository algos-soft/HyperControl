/*
package it.technocontrolsystem.hypercontrol;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.domain.Site;

*/
/**
 * State of the boards
 *//*

public class BoardsStateActivity extends Activity {
    Connection conn = null;
    private int idSite = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AssetManager am = getAssets();
        Typeface type;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards_state);

        TextView title = (TextView) findViewById(R.id.stateboardtitle);
        type = Typeface.createFromAsset(am, "FFF_Tusj.ttf");
        title.setTypeface(type);

        try {
            this.idSite = getIntent().getIntExtra("siteid", 0);
            Site site = DB.getSite(idSite);
            conn = new Connection(site);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Request request;
//        Response resp;
//        TextView risposta;
//
//
//        LinearLayout stateBoardsLinear = (LinearLayout) findViewById(R.id.stateboardpanel);
//        stateBoardsLinear.setOrientation(LinearLayout.VERTICAL);
//        request = new BoardStatusRequest();
//
//        resp = conn.sendRequest(request);
//        BoardStatusResponse stateResp = (BoardStatusResponse) resp;
//
//        int numero;
//        int stato;
//
//        Board[] boards = stateResp.getBoards();
//        for (Board bo : boards) {
//
//            numero = bo.getNumber();
//            String num = String.valueOf(numero);
//
////            stato = bo.getStato();
////            String state = String.valueOf(stato);
//
////            risposta = new TextView(this);
////            risposta.setTextColor(Color.parseColor("#ff4ae02e"));
////            stateBoardsLinear.addView(risposta);
////            risposta.setText("Scheda numero: " + num + " Stato: " + state);
//
//
//        }
    }
}
*/
