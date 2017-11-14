package com.example.exo_billard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Lae_JP on 14/11/2017.
 */

public class Accueil extends Activity implements PopupMenu.OnMenuItemClickListener {

    SharedPreferences sharedPreferences;
    public String Couleurs = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        readPref();
        drawWindow();

        // Passer la fen�tre en fullscreen == cacher la barre de notification
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView bOptions = (ImageView) findViewById(R.id.menuOptions);
        bOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(Accueil.this, v);
                popupMenu.setOnMenuItemClickListener(Accueil.this);
                popupMenu.inflate(R.menu.menu_accueil);
                popupMenu.show();
            }
        });


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
            couleurBack = Constantes.couleurBandeG;

        } else if ("blue".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisB;
            couleurText = Constantes.couleurLigneB;
            couleurBack = Constantes.couleurBandeB;
        } else if ("red".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisR;
            couleurText = Constantes.couleurLigneR;
            couleurBack = Constantes.couleurBandeR;
        } else {
            couleurButton = Constantes.couleurTapisNB;
            couleurText = Constantes.couleurMoucheNB;
            couleurBack = Constantes.couleurBandeNB;
        }
        Button b1 = (Button) findViewById(R.id.acc_Livret);
        b1.setBackgroundColor(couleurButton);
        b1.setTextColor(couleurText);
        Button b2 = (Button) findViewById(R.id.acc_Test);
        b2.setBackgroundColor(couleurButton);
        b2.setTextColor(couleurText);
        Button b3 = (Button) findViewById(R.id.acc_Score);
        b3.setBackgroundColor(couleurButton);
        b3.setTextColor(couleurText);
        Button b4 = (Button) findViewById(R.id.acc_Stat);
        b4.setBackgroundColor(couleurButton);
        b4.setTextColor(couleurText);

        TableLayout t = (TableLayout) findViewById(R.id.acc_Back);
        t.setBackgroundColor(couleurBack);

    }

    public void lancementLivret(View v) {
        Intent intent = new Intent(Accueil.this, Exo_entree.class);
        startActivity(intent);
    }

    public void lancementTest(View v) {
        // Intent intent = new Intent(Accueil.this, Exo_entree.class);
        // startActivity(intent);
    }

    public void lancementScore(View v) {
        // Intent intent = new Intent(Accueil.this, Exo_entree.class);
        // startActivity(intent);
    }

    public void lancementStat(View v) {
        // Intent intent = new Intent(Accueil.this, Exo_entree.class);
        // startActivity(intent);
    }

    // menu Option
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.options:
                Intent intent2 = new Intent(this, Preferences.class);
                int result4 = 0;
                startActivityForResult(intent2, result4);
                return true;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //on regarde quelle Activity a répondu
        readPref();
        drawWindow();
    }

}
