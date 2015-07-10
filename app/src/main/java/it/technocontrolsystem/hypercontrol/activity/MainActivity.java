package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);


        autoselect();
/*
        Button bnessuno = (Button) findViewById(R.id.btn_nessuno);
        bnessuno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               noSite();
            }
        });

        Button buno = (Button) findViewById(R.id.btn_uno);
        buno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              singleSite();
            }
        });

        Button bmolti = (Button) findViewById(R.id.btn_molti);
        bmolti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               manySites();
            }
        });*/
    }




    private void noSite(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, EditSiteActivity.class);
        intent.putExtra("destinationactivity", SiteActivity.class);
        startActivity(intent);
    }
    private void singleSite(){
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, SiteActivity.class);
//        Site site= DB.getSites() [0];
//        intent.putExtra("siteid",site.getId());
//        intent.putExtra("siteversion",site.getVersion());//federico
//        startActivity(intent);


        Intent intent = new Intent();
        intent.setClass(this, StartSiteActivity.class);
        Site site= DB.getSites() [0];
        intent.putExtra("siteid",site.getId());
        startActivity(intent);

    }
    private void manySites(){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SelectSiteActivity.class);
        startActivity(intent);

    }

    private void autoselect(){
        Results r=getSelect();
        switch (r){
            case NONE:
                noSite();
                break;
            case ONE:
                singleSite();
                break;
            case MANY:
                manySites();
                break;
        }
        finish();
    }

    private Results getSelect(){
        Results r=null;
       int numSites=DB.getSiteCount();
        if(numSites==0){
            r=Results.NONE;
        }else{
            if(numSites==1){
                r=Results.ONE;
            }else{
                r=Results.MANY;
            }

        }

        return r;
    }

    private enum Results{NONE, ONE, MANY}


}
