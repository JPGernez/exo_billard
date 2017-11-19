package com.example.exo_billard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lae_JP on 14/11/2017.
 */

public class Accueil extends Activity implements PopupMenu.OnMenuItemClickListener {

    SharedPreferences sharedPreferences;
    public String Couleurs = "";
    SqlBillardHelper db;
    private boolean CoulAlea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accueil);

        db = new SqlBillardHelper(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        readPref();
        if (CoulAlea) {
            Random randomGenerator = new Random();
            int coul = randomGenerator.nextInt(4);
            Log.d("Coul", String.valueOf(coul));
            Couleurs = (this.getResources().getStringArray(R.array.liste_couleurs))[coul];
            Log.d("Coul2", Couleurs);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("prefCouleur", Couleurs);
            editor.commit();
        }

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

        CoulAlea = sharedPreferences.getBoolean("tapisAlea", true);
        Couleurs = sharedPreferences.getString("prefCouleur", "blue");
    }

    private void drawWindow() {
        int couleurButton;
        int couleurText;
        int couleurBack;

        if ("green".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisG;
            couleurBack = Constantes.couleurBackG;
        } else if ("blue".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisB;
            couleurBack = Constantes.couleurBackB;
        } else if ("red".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisR;
            couleurBack = Constantes.couleurBackR;
        } else {
            couleurButton = Constantes.couleurTapisNB;
            couleurBack = Constantes.couleurBackNB;
        }

        Button b1 = (Button) findViewById(R.id.acc_Livret);
        b1.setBackgroundColor(couleurButton);
        //b1.setTextColor(couleurText);
        Button b2 = (Button) findViewById(R.id.acc_Test);
        b2.setBackgroundColor(couleurButton);
        //b2.setTextColor(couleurText);
        Button b3 = (Button) findViewById(R.id.acc_Score);
        b3.setBackgroundColor(couleurButton);
        //b3.setTextColor(couleurText);
        Button b4 = (Button) findViewById(R.id.acc_Stat);
        b4.setBackgroundColor(couleurButton);
        //b4.setTextColor(couleurText);

        LinearLayout t = (LinearLayout) findViewById(R.id.acc_Back);
        t.setBackgroundColor(couleurBack);

    }

    @Override
    protected void onResume() {
        super.onResume();
        readPref();
        drawWindow();
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
        recup_score();

    }

    public void recup_score() {

        final AlertDialog alert = new AlertDialog.Builder(this).create();
        //LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogview = inflater.inflate(R.layout.saisie_score, null);
        //AlertDialog
        alert.setView(dialogview);
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int idx) {
                //
                //
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int idx) {
                alert.cancel();
                //
            }
        });
        alert.show();
        Button posB = (Button) alert.getButton(AlertDialog.BUTTON_POSITIVE);
        posB.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String adv = ((EditText) dialogview.findViewById(R.id.score_adv)).getText().toString();
                                        int rep = Integer.parseInt(((EditText) dialogview.findViewById(R.id.score_nbrep)).getText().toString());
                                        int scoreadv = Integer.parseInt(((EditText) dialogview.findViewById(R.id.score_resAdv)).getText().toString());
                                        int score = Integer.parseInt(((EditText) dialogview.findViewById(R.id.score_res)).getText().toString());

                                        if (rep < 1) {
                                            Toast.makeText(Accueil.this, "Le nombre de reprise ne peut pas etre nul", Toast.LENGTH_SHORT).show();
                                        } else {
                                            db.saveMatch(adv, rep, score, scoreadv);
                                            alert.cancel();
                                        }
                                    }
                                }
        );
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
