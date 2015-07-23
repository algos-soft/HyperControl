package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
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
    private int version = 0;//federico


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_site2);

        boolean visible;

        Button bsave = (Button) findViewById(R.id.btn_save);
        Button bdelete = (Button) findViewById(R.id.btn_delete);

        mNameView = (TextView) findViewById(R.id.name);
        mAddressView = (TextView) findViewById(R.id.address);
        mPortView = (TextView) findViewById(R.id.port);
        mUsernameView = (TextView) findViewById(R.id.username);
        mPasswordView = (TextView) findViewById(R.id.password);

        // visibilità del bottone delete in base al parametro
        visible = getIntent().getBooleanExtra("usedelete", true);
        if (visible) {
            bdelete.setVisibility(View.VISIBLE);
            bdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete();
                }
            });
        } else {
            bdelete.setVisibility(View.GONE);
        }

        // visibilità del bottone save in base al parametro
        visible = getIntent().getBooleanExtra("usesave", true);
        if (visible) {
            bsave.setVisibility(View.VISIBLE);
            bsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                }
            });
        } else {
            bsave.setVisibility(View.GONE);
        }

        // testo del titolo della schermata in base al parametro
        TextView vTitle = (TextView) findViewById(R.id.title);
        String sTitle = getIntent().getStringExtra("title");
        vTitle.setText(sTitle);


        //if we have a site id, load data
        this.idSite = getIntent().getIntExtra("siteid", 0);


        if (idSite != 0) {
            loadSiteData();
        } else {
            mPortView.setText("9212");
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Lib.hideKeyboard(this);
    }


    private String getName() {
        return mNameView.getText().toString();
    }

    private String getAddress() {
        return mAddressView.getText().toString();
    }

    private int getPort() {
        int port = 0;
        String text = mPortView.getText().toString();
        try {
            port = Integer.parseInt(text);
        } catch (Exception e) {
        }
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
        String error = "" + getString(R.string.error_field_required);
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
            if (TextUtils.isEmpty(getPort() + "")) {
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


    /**
     * Registra o crea il sito
     */
    private void saveSite() {

        Site site = createSite();
        try {

            // save the new site in the database
            int id = DB.saveSite(site);

            Intent data = new Intent();
            data.putExtra("siteid",id);
            setResult(RESULT_OK, data);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Chiede conferma ed elimina il sito
     */
    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione!");
        builder.setMessage("Confermi l'eliminazione di questo sito?");
        builder.setNegativeButton("Annulla", null);
        builder.setPositiveButton("Elimina", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DB.deleteSite(getSiteId());
                Intent data = new Intent();
                data.putExtra("deleted",true);
                data.putExtra("siteid",getSiteId());
                setResult(RESULT_OK, data);
                finish();
            }
        });
        builder.show();
    }

    /**
     * create a new site from the input fields
     *
     * @return new site
     */
    private Site createSite() {

        Site site = new Site(getName(), getAddress(), getPort(), getUsername(), getPassword(), version);//federico
        site.setId(this.idSite);
        return site;
    }

    private int getSiteId() {
        return idSite;
    }

    //loads data from the site identified by idSite
    private void loadSiteData() {
        Site site = DB.getSite(idSite);
        mNameView.setText(site.getName());
        mAddressView.setText(site.getAddress());
        mPortView.setText(site.getPort() + "");
        mUsernameView.setText(site.getUsername());
        mPasswordView.setText(site.getPassword());
        version = site.getVersion();//federico
    }


}
