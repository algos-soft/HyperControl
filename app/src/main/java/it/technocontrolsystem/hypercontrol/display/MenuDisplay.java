package it.technocontrolsystem.hypercontrol.display;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.activity.MenuActivity;
import it.technocontrolsystem.hypercontrol.activity.SiteActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.MenuCommandRequest;
import it.technocontrolsystem.hypercontrol.communication.Response;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Menu;

/**
 * Display class for menus
 * Created by Alex on 12/07/15.
 */
public class MenuDisplay extends Button {

    MenuActivity activity;

    public MenuDisplay(MenuActivity activity,int menuId) {
        super(activity);

        this.activity=activity;

        final Menu menu = DB.getMenu(menuId);

        setText(menu.getName());
        setId(menu.getNumber());

        final MenuActivity act = activity;
        if (menu.getAction() == 0) {

            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();

                    intent.putExtra("siteid", act.getSite().getId());
                    intent.putExtra("title", getText());
                    intent.putExtra("pageid", menu.getNumber());
                    intent.setClass(getContext(), MenuActivity.class);
                    getContext().startActivity(intent);
                }
            });
        } else {
            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuCommandRequest request = new MenuCommandRequest(menu.getNumber());
                    HyperControlApp.sendRequest(request);
                }
            });


        }



    }



}

