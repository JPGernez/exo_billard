package com.example.exo_billard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class Nvlivret extends Activity {

    Livret livret=new Livret();
    SqlBillardHelper db;
    private int mod =0;
    private int liv_id=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Passer la fen�tre en fullscreen == cacher la barre de notification
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                setContentView(R.layout.nvlivret);

        db = new SqlBillardHelper(this);
        Bundle extras = getIntent().getExtras();
        EditText txt;
        if (extras != null) {

            txt= (EditText) findViewById(R.id.titreNvLivret);
            txt.setText(extras.getString("titre"));
            txt= (EditText) findViewById(R.id.auteurNvLivret);
            txt.setText(extras.getString("auteur"));
            txt= (EditText) findViewById(R.id.commNvLivret);
            txt.setText(extras.getString("comm"));
            liv_id=extras.getInt("liv_id");
            mod=extras.getInt("mod");
            }
        Log.d("Livret mod", String.valueOf(mod));
    }


    // click sur un element du menu d entree

    public void validNvLivret(View v) {

        Log.d("nvlivret", "1");
        EditText txt;
        txt= (EditText) findViewById(R.id.titreNvLivret);
        livret.setTitre(txt.getText().toString());
        txt = (EditText) findViewById(R.id.auteurNvLivret);
        livret.setAuteur(txt.getText().toString());
        txt = (EditText) findViewById(R.id.commNvLivret);
        livret.setComment(txt.getText().toString());
        Log.d("nvlivret", "2");
        Log.d("Livret mod", String.valueOf(mod));
        if (mod==0) {
            long livid;
            livid = db.createLivret(livret);
            Log.d("nvlivret num", String.valueOf(livid));
        }
        else {
            Log.d("Livret", String.valueOf(livret.getId()));
            livret.setId(liv_id);
            db.modLivret(livret);
        }
        setResult(1);
        finish();
        Log.d("nvlivret", "4");
    }

    public void cancelNvLivret(View v){
        setResult(0);
        finish();
    }
}



