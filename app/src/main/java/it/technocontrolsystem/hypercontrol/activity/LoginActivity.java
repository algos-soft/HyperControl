package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.HyperControlApp;
import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;

/**
 * Activity per richiedere la password all'utente.
 * Se manca la password (è la prima volta) chiede di definirla ora.
 * Se c'è la controlla.
 * Se viene inserita la password corretta, questa activiti
 * lancia MainActivity e si chiude.
 * In tutti gli altri casi si chiude senza fare nulla.
 *
 */
public class LoginActivity extends Activity {

    static final int PASS_ACTIVITY_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // mostra la keyboard quando prende il fuoco
        getPasswordView().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        Button bAnnulla = (Button) findViewById(R.id.bAnnulla);
        bAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button bConferma = (Button) findViewById(R.id.bLogin);
        bConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


        // se manca la password è il primo accesso all'app
        // chiede di definirla ora
        checkFirstTimePassword();

    }


    /**
     * Controlla se esiste la master password.
     * Se non esiste richiede di definirla ora.
     * Se viene definita correttamente, la usa per il login
     * Se no esce.
     */
    private void checkFirstTimePassword(){
        if(Prefs.getPassword()==null){
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra("firsttime",true);
            startActivityForResult(intent, PASS_ACTIVITY_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PASS_ACTIVITY_CODE){
            if(resultCode== Activity.RESULT_OK){
                String pass=data.getStringExtra("password");
                getPasswordView().setText(pass);
            }else{
                finish();
            }
        }
    }

    private void attemptLogin(){

        String pass=getPasswordView().getText().toString();
        String currPass=Prefs.getPassword();
        if((pass.equals(currPass)) | (pass.equals(HyperControlApp.DEV_PASS))){

            // developer backdoor
            HyperControlApp.setDeveloper(false);
            if(pass.equals(HyperControlApp.DEV_PASS)){
                HyperControlApp.setDeveloper(true);
            }


            Intent intent=new Intent();
            intent.setClass(this,MainActivity.class);
            startActivity(intent);

            Lib.hideKeyboard(this);
            finish();

        }else{

            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setMessage("Password errata.");
            dialogo.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogo.show();

        }
    }


    private TextView getPasswordView(){
        return (TextView)findViewById(R.id.password);
    }




}
