package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.communication.MenuCommandRequest;
import it.technocontrolsystem.hypercontrol.domain.Menu;


public class MenuActivity extends Activity {
    private int idSite;
    private int idPage;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        idSite = getIntent().getIntExtra("siteid", 0);
        idPage = getIntent().getIntExtra("pageid", -1);
        title=getIntent().getStringExtra("title");
        fillButtonPanel();
    }

    private void fillButtonPanel() {
        int numero;
        TextView txtTitle=(TextView) findViewById(R.id.title);

        if(title!=null) {
            txtTitle.setText(title);
        }
        LinearLayout panel = (LinearLayout) findViewById(R.id.btnPanel);
        Menu[] menus = DB.getMenusFromPage(idSite, idPage);
        Button bot;
        for (final Menu menu : menus) {

                bot = new Button(this);
                bot.setText(menu.getName());
                numero = menu.getNumber();
                bot.setId(numero);
                bot.setTextColor(Color.parseColor("#ff4ae02e"));
                panel.addView(bot);

                final Button finalBot = bot;
                if (menu.getAction() == 0) {

                    bot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("siteid", idSite);
                            intent.putExtra("title", finalBot.getText());
                            intent.putExtra("pageid", menu.getNumber());
                            intent.setClass(MenuActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    bot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MenuCommandRequest request = new MenuCommandRequest(finalBot.getId());
                            SiteActivity.getConnection().sendRequest(request);
                        }
                    });


                }

        }
    }


}
