package com.example.exo_billard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class Nvlivret extends Activity {

    Livret livret=new Livret();
    SqlBillardHelper db;
    private int mod =0;
    private int liv_id=0;

    SharedPreferences sharedPreferences;
    public String Couleurs = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nvlivret);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        readPref();
        drawWindow();
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

    private void readPref() {

        Couleurs = sharedPreferences.getString("prefCouleur", "blue");
    }

    private void drawWindow() {
        int couleurButton;
        int couleurText;
        int couleurBack;

        if ("green".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisG;
            couleurText = Constantes.couleurMoucheG;
            couleurBack = Constantes.couleurBackG;
        } else if ("blue".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisB;
            couleurText = Constantes.couleurMoucheB;
            couleurBack = Constantes.couleurBackB;
        } else if ("red".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisR;
            couleurText = Constantes.couleurMoucheR;
            couleurBack = Constantes.couleurBackR;
        } else {
            couleurButton = Constantes.couleurTapisNB;
            couleurText = Constantes.couleurMoucheNB;
            couleurBack = Constantes.couleurBackNB;
        }

        Button b1 = (Button) findViewById(R.id.cancelNvLivret);
        b1.setBackgroundColor(couleurButton);
        //b1.setTextColor(couleurText);
        Button b2 = (Button) findViewById(R.id.validNvLivret);
        b2.setBackgroundColor(couleurButton);

        ScrollView t = (ScrollView) findViewById(R.id.nvl_Back);
        t.setBackgroundColor(couleurBack);

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



