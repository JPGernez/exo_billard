package com.example.exo_billard;
/*  Ecran d'accueil de l application */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Entree extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Passer la fen�tre en fullscreen == cacher la barre de notification
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.old_entree);
    }

    // click sur un element du menu d entree
    public void clickEntree(View v) {
        String mot=(String) v.getTag() ;
        //Log.d("entree",mot);
        if (mot.equals("LanceExo")) {
           Intent intent = new Intent(Entree.this, ExoView.class);
           //Log.d("entree","lancement");
           startActivity(intent);
        }
    }


}
