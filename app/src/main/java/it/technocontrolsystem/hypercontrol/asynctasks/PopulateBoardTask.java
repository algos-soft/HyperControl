package it.technocontrolsystem.hypercontrol.asynctasks;

import it.technocontrolsystem.hypercontrol.activity.BoardActivity;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Board;
import it.technocontrolsystem.hypercontrol.listadapters.BoardListAdapter;
import it.technocontrolsystem.hypercontrol.listadapters.HCListAdapter;
import it.technocontrolsystem.hypercontrol.model.BoardModel;

/**
 * Created by alex on 27-07-2015.
 */
public class PopulateBoardTask extends AbsPopulateTask {

    public PopulateBoardTask(BoardActivity activity, Runnable successRunnable, Runnable failRunnable) {
        super(activity, successRunnable, failRunnable);
    }

    public PopulateBoardTask(BoardActivity activity) {
        this(activity, null, null);
    }

    @Override
    public HCListAdapter<?> createAdapter() {
        return new BoardListAdapter(activity);
    }


    @Override
    public void populateAdapter() {

        int idSite = getSpecActivity().getSite().getId();
        Board[] boards = DB.getBoards(idSite);
        publishProgress(-2, boards.length);

        BoardModel model;
        activity.getListAdapter().clear();
        int i = 0;
        for (final Board board : boards) {
            model = new BoardModel(board);
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
        return "schede";
    }


    private BoardActivity getSpecActivity() {
        return (BoardActivity) activity;
    }


}
