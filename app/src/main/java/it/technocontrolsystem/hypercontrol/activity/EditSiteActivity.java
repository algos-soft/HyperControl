package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;

import it.technocontrolsystem.hypercontrol.R;
import it.technocontrolsystem.hypercontrol.database.DB;
import it.technocontrolsystem.hypercontrol.domain.Site;


public class EditSiteActivity extends Activity {
    private TextView mNameView;
    private TextView mAddressView;
    private TextView mPortView;
    private TextView mUsernameView;
    private TextView mPasswordView;
    private int idSite = 0;
    private int version=0;//federico


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_site);

        mNameView = (TextView) findViewById(R.id.name);
        mAddressView = (TextView) findViewById(R.id.address);
        mPortView = (TextView) findViewById(R.id.port);
        mUsernameView = (TextView) findViewById(R.id.username);
        mPasswordView = (TextView) findViewById(R.id.password);


        //if we have a site id
        //load data
        this.idSite=getIntent().getIntExtra("siteid",0);
        if(idSite!=0){
            loadSiteData();
        }else{
            mPortView.setText("9212");

            }

        Button bsave = (Button) findViewById(R.id.btn_save);
        bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();

            }
        });


    }

    private String getName() {
        return mNameView.getText().toString();
    }

    private String getAddress() {
        return mAddressView.getText().toString();
    }

    private int getPort() {
        int port=0;
        String text=mPortView.getText().toString();
        try{
            port=Integer.parseInt(text);
        }catch(Exception e){}
        return port;
    }

    private String getHttpAddress() {
        String address = getAddress();
        address = address.toLowerCase();
        if (!address.startsWith("http://")) {
            address = "http://" + address;
        }
        return address;
    }

    private String getUsername() {
        return mUsernameView.getText().toString();
    }

    private String getPassword() {
        return mPasswordView.getText().toString();
    }

    private void save() {
        if (checkValid()) {
            saveSite();
        }

    }

    private boolean checkValid() {
        String error = ""+getString(R.string.error_field_required);
        boolean valid = true;
        View focusView = null;

        //check name field isn't empty
        if (valid) {
            if (TextUtils.isEmpty(getName())) {
                mNameView.setError(error);
                focusView = mNameView;
                valid = false;
            }
        }


        //check address field isn't empty
        if (valid) {
            if (TextUtils.isEmpty(getAddress())) {
                mAddressView.setError(error);
                focusView = mAddressView;
                valid = false;
            }
        }
        //check port field is valid number//DA FAREEEEE!!!!!!!!!!
        if (valid) {
            if (TextUtils.isEmpty(getPort()+"")) {
                mPortView.setError(error);
                focusView = mPortView;
                valid = false;
            }
        }
        //check that the URL is valid
        if (valid) {
            if (!URLUtil.isHttpUrl(getHttpAddress())) {
                mAddressView.setError(getString(R.string.text_url_error));
                focusView = mAddressView;
                valid = false;
            }
        }

        //check username field isn't empty
        if (valid) {
            if (TextUtils.isEmpty(getUsername())) {
                mUsernameView.setError(error);
                focusView = mUsernameView;
                valid = false;
            }
        }
        //check password field isn't empty
        if (valid) {
            if (TextUtils.isEmpty(getPassword())) {
                mPasswordView.setError(error);
                focusView = mPasswordView;
                valid = false;
            }
        }
        if (!valid) {
            focusView.requestFocus();
        }


        return valid;
    }

    private void saveSite() {
        Site site = createSite();
        try {

            // save the new site in the database
            int id= DB.saveSite(site);

            //if a destination activity was specified, start the activity
            Serializable ser = getIntent().getSerializableExtra("destinationactivity");
            if (ser != null) {
                if (ser instanceof Class) {
                    Intent intent = new Intent();
                    intent.setClass(this, (Class) ser);
                    intent.putExtra("siteid",id);
                    startActivity(intent);
                }
            }
//            SiteActivity.getInstance().finish();
//            ConfigActivity.getInstance().finish();
//            Intent i = new Intent(EditSiteActivity.this, SiteActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            i.putExtra("restarta",1);
//            startActivity(i);


//            Intent intent=new Intent();
//            intent.putExtra("siteid",id);
//            intent.setClass(EditSiteActivity.this,SiteActivity.class);
//            startActivity(intent);


        } catch (Exception e) {
            e.printStackTrace();
        }

        finish();
    }

    /**
     * create a new site from the input fields
     *
     * @return new site
     */
    private Site createSite() {

        Site site = new Site(getName(), getAddress(),getPort(), getUsername(), getPassword(),version);//federico
        site.setId(this.idSite);
        return site;
    }
    //loads data from the site identified by idSite
    private void loadSiteData(){
        Site site=DB.getSite(idSite);
        mNameView.setText(site.getName());
        mAddressView.setText(site.getAddress());
        mPortView.setText(site.getPort()+"");
        mUsernameView.setText(site.getUsername());
        mPasswordView.setText(site.getPassword());
        version=site.getVersion();//federico
    }


}
