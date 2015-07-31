package it.technocontrolsystem.hypercontrol.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateAreasInOutputDetailViewTask;
import it.technocontrolsystem.hypercontrol.asynctasks.PopulateOutputsTask;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Output;
import it.technocontrolsystem.hypercontrol.domain.Site;

public class OutputDetailActivity extends HCActivity {

    private Output output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_detail);

        int outputid = getIntent().getIntExtra("itemid", 0);
        this.output=DB.getOutput(outputid);

        TextView nameView = (TextView)findViewById(R.id.outputname);
        nameView.setText(output.getName());

        TextView numberView = (TextView)findViewById(R.id.outputnumber);
        numberView.setText(""+output.getNumber());

        // carica i dati
        new PopulateAreasInOutputDetailViewTask(this).execute();

    }


    @Override
    public String getActionBarSubtitle() {
        Site site = DB.getSite(output.getIdSite());
        return site.getName();
    }

    public ListView getListView(){
        ListView listView = (ListView)findViewById(R.id.list);
        return listView;
    }

    public Output getOutput() {
        return output;
    }
}
