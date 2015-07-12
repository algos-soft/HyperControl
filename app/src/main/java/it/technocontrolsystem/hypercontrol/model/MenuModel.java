package it.technocontrolsystem.hypercontrol.model;

import it.technocontrolsystem.hypercontrol.domain.Menu;

/**
 * Modello dati Menu.
 * Questo modello non ha informazioni di stato
 *
 * Created by alex on 10-07-2015.
 */
public class MenuModel implements ModelIF {
    private Menu menu;

    public MenuModel(Menu menu) {
        this.menu = menu;
    }

    @Override
    public int getNumber() {
        return 0;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void clearStatus() { }

}
