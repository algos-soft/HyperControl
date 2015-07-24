package it.technocontrolsystem.hypercontrol.display;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.activity.MenuActivity;
import it.technocontrolsystem.hypercontrol.communication.Connection;
import it.technocontrolsystem.hypercontrol.communication.MenuCommandRequest;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Menu;

/**
 * Display class for menus
 * Created by Alex on 12/07/15.
 */
public class MenuDisplay extends Button {

    MenuActivity activity;

    public MenuDisplay(final MenuActivity activity,int menuId) {
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

                    String errMsg=null;
                    if(Lib.isNetworkAvailable()) {
                        Connection conn=HyperControlApp.getConnection();
                        if((conn!=null) && conn.isOpen()){
                            MenuCommandRequest request = new MenuCommandRequest(menu.getNumber());
                            HyperControlApp.sendRequest(request);
                        }else{
                            errMsg="Connessione chiusa";
                        }
                    }else{
                        errMsg="Network non disponibile";
                    }

                    if(errMsg!=null){
                        AlertDialog.Builder builder = new  AlertDialog.Builder(activity);
                        builder.setMessage("Impossibile inviare il comando\n"+errMsg);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }


                }
            });


        }



    }



}

