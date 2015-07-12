package it.technocontrolsystem.hypercontrol.listadapters;

import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import it.technocontrolsystem.hypercontrol.activity.MenuActivity;
import it.technocontrolsystem.hypercontrol.communication.LiveMessage;
import it.technocontrolsystem.hypercontrol.display.MenuDisplay;
import it.technocontrolsystem.hypercontrol.model.MenuModel;

/**
 * Created by Alex on 12/07/15.
 */
public class MenuListAdapter extends HCListAdapter<MenuModel> {

    private MenuActivity activity;

    public MenuListAdapter(MenuActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuModel model = getItem(position);
        MenuDisplay display;
        display = new MenuDisplay(activity, model.getMenu().getId());
        return display;
    }

    @Override
    public void updateAll() {

    }

    @Override
    public void update(Integer[] numbers) {

    }

    @Override
    public void updateByNumber(int number) {

    }

    @Override
    public void live(LiveMessage message) throws IOException, XmlPullParserException {

    }
}
