package it.technocontrolsystem.hypercontrol.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.technocontrolsystem.hypercontrol.Lib;
import it.technocontrolsystem.hypercontrol.Prefs;
import it.technocontrolsystem.hypercontrol.R;


/**
 * Activity per inserire la password per la prima
 * volta o per cambiarla
 */
public class PasswordActivity extends HCListActivity {

    // se true sta registrando la password per la prima volta
    private boolean firstTimeMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent()!=null){
            firstTimeMode = getIntent().getBooleanExtra("firsttime", false);
        }

        setContentView(R.layout.activity_password);



        // se first time, cambia titolo e niente old password
        TextView titleView = (TextView) findViewById(R.id.passScreenTitle);
        if(firstTimeMode) {
            titleView.setText("Benvenuto in HyperControl!\nStai usando questa applicazione per la prima volta. Per la tua sicurezza, devi definire una password di accesso.");

            View v = findViewById(R.id.oldPasswordGroup);
            v.setVisibility(View.GONE);

        }else{
            titleView.setText("Cambio password");
        }

        Button bAnnulla = (Button) findViewById(R.id.bAnnulla);
        bAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        Button bConferma = (Button) findViewById(R.id.bConferma);
        bConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conferma();
            }
        });

    }

    @Override
    public String getActionBarSubtitle() {
        String title;
        if(firstTimeMode){
            title="Registrazione password";
        }else {
            title="Cambio password";
        }
        return title;
    }

    @Override
    public String getHeadline2() {
        return null;
    }

    @Override
    public String getHeadline3() {
        return null;
    }

    @Override
    public int getNumItemsInList() {
        return 0;
    }

    @Override
    public String getItemsType() {
        return null;
    }

    /**
     * Registra la nuova password e chiude
     */
    private void conferma() {

        String errText = checkValida();

        if (errText.equals("")) {

            final String newPass=getPassword(R.id.newPassword);
            Prefs.setPassword(newPass);

            if(!firstTimeMode){
                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setMessage("Password modificata.");
                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishOK();
                    }
                });
                dialogo.show();

            }else{
                finishOK();
            }

        }else{

            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("Errore");
            dialogo.setMessage(errText);
            dialogo.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogo.show();

        }
    }

    /**
     * Controlla se i dati sono validi
     * Ritorna stringa vuota se validi o il motivo se non validi.
     */
    private String checkValida() {
        String errText = "";
        String oldPass = getPassword(R.id.oldPassword);
        if (Lib.checkPassword(oldPass) | firstTimeMode) {
            String newPass = getPassword(R.id.newPassword);
            String passError = isGoodPassword(newPass);
            if (passError.equals("")) {
                String confirmPass = getPassword(R.id.confirmPassword);
                if (newPass.equals(confirmPass)) {
                    errText = "";
                } else {
                    errText = "La nuova password e la conferma non sono uguali.";
                }
            } else {
                errText = "La nuova password non è valida.\n" + passError;
            }

        } else {
            errText = "La vecchia passord non è valida.";
        }

        return errText;
    }

    /**
     * Controlla se una stringa è una password valida
     * Ritorna stringa vuota se valida, spiegazione se non valida.
     */
    private String isGoodPassword(String pass){
        boolean valida=false;
        String errText="";
        final int minLength=4;
        if (pass.length()>=minLength) {
            if(!pass.contains(" ")){
                valida=true;
            }else{
                errText="La password non deve contenere spazi.";
            }
        }else{
            errText="La lunghezza minima è "+minLength;
        }

        // valore di ritorno
        String ret="";
        if(!valida){
            ret=errText;
        }
        return ret;
    }

    /**
     * Ritorna la password da uno dei campi password
     *
     * @param id l'id del campo
     * @return la password contenuta
     */
    private String getPassword(int id) {
        TextView tv = (TextView) findViewById(id);
        return tv.getText().toString();
    }

    /**
     * Termina con result RESULT_OK e ritorna la password
     */
    private void finishOK(){
        final String newPass=getPassword(R.id.newPassword);
        Intent intent = new Intent();
        intent.putExtra("password",newPass);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }




}
