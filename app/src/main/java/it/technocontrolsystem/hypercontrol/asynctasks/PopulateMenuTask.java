package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.MenuActivity;
import it.technocontrolsystem.hypercontrol.activity.PlantActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Menu;
import it.technocontrolsystem.hypercontrol.listadapters.AreaListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.MenuListAdapter;
import it.technocontrolsystem.hypercontrol.model.MenuModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulateMenuTask extends AbsPopulateTask {

    public PopulateMenuTask(MenuActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateMenuTask(MenuActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new MenuListAdapter(getSpecActivity());
    }


    @Override
    public void populateAdapter() {

        int idSite = getSpecActivity().getSite().getId();
        int idPage = getSpecActivity().getIdPage();
        Menu[] menus = DB.getMenusBySiteAndPage(idSite, idPage);

        //Menu[] menus = DB.getMenus(idSite);
        publishProgress(-2, menus.length);

        MenuModel model;
        activity.getListAdapter().clear();
        int i = 0;
        for (final Menu menu : menus) {
            model = new MenuModel(menu);
            activity.getListAdapter().add(model);
            i++;
            publishProgress(-3, i);

            if (isCancelled()) {
                break;
            }

        }

    }


    @Override
    public String getType() {
        return "menu";
    }

    private MenuActivity getSpecActivity() {
        return (MenuActivity) activity;
    }


}
