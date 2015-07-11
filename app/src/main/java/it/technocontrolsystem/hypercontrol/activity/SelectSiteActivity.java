package it.technocontrolsystem.hypercontrol.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.display.SiteDisplay;
import it.technocontrolsystem.hypercontrol.domain.Site;
import it.technocontrolsystem.hypercontrol.listadapters.SiteListAdapter;
import it.technocontrolsystem.hypercontrol.model.SiteModel;


public class SelectSiteActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_site);

        AssetManager am = getAssets();
        Typeface type;

        //fillButtonPanel();

        TextView title=(TextView) findViewById(R.id.title);
        type = Typeface.createFromAsset(am, "FFF_Tusj.ttf");
        title.setTypeface(type);

        listAdapter = new SiteListAdapter(SelectSiteActivity.this);
        populateAdapter();
        // assegna l'adapter alla ListView
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SiteDisplay display = (SiteDisplay) view;
                Intent intent = new Intent();
                intent.setClass(SelectSiteActivity.this, StartSiteActivity.class);
                int siteId=display.getItemId();
                intent.putExtra("siteid", siteId);
                int version= DB.getSite(siteId).getVersion();
                intent.putExtra("siteversion",version);
                startActivity(intent);
            }
        });




        Button bconfig= (Button)findViewById(R.id.btn_config);
        type = Typeface.createFromAsset(am, "CaviarDreams.ttf");
        bconfig.setTypeface(type);
        bconfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(SelectSiteActivity.this,ConfigActivity.class);
                startActivity(intent);

            }
        });



    }

    //refresh SelectSiteActivity when i return from ConfigActivity
    protected void onResume(){
        super.onResume();
        this.onCreate(null);
    }
  /*//federico--->Inserito linear layout orizzontale al cui interno vengono creati bot e tog problema:arresto app quando ruoto schermo e text in tog all'inizio è diverso da quello impostato
    private void fillButtonPanel(){
        AssetManager am = getAssets();
        Typeface type;
        LinearLayout panel=(LinearLayout)findViewById(R.id.btnPanel);
        Site[] sites = DB.getSites();
        Button bot;

        for (final Site site:sites){
            LinearLayout hpanel=new LinearLayout(this);
            hpanel.setOrientation(LinearLayout.HORIZONTAL);
            bot=new Button(this);
            bot.setText(site.getName());
            type = Typeface.createFromAsset(am, "CaviarDreams_Italic.ttf");
            bot.setTypeface(type);
            bot.setTextColor(Color.parseColor("#ff4ae02e"));
            bot.setId(site.getId());
            bot.setWidth(200);
            //hpanel.addView(bot);
            bot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(SelectSiteActivity.this, SiteActivity.class);
                    intent.putExtra("siteid", site.getId());
                    intent.putExtra("siteversion",site.getVersion());//federico

                    startActivity(intent);
                }
            });

          panel.addView(bot);
        }

    }*/

    /**
     * Carica i siti dal DB nell'adapter
     */
    private void populateAdapter() {
        Site[] sites = DB.getSites();
        SiteModel model;
        for (final Site site : sites) {
            model = new SiteModel(site);
            listAdapter.add(model);
        }
    }




}
