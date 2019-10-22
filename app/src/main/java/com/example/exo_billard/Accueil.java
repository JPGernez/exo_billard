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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lae_JP on 14/11/2017.
 */

public class Accueil extends Activity implements PopupMenu.OnMenuItemClickListener {

    SharedPreferences sharedPreferences;
    public String Couleurs = "";
    SqlBillardHelper db;
    private boolean CoulAlea;
    ArrayList<String> titres = new ArrayList<>();
    List<Integer> idLivret = new ArrayList<>();
    List<Integer> nbExo = new ArrayList<>();
    int selectedLiv;
    int tailleLiv;

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

        final AlertDialog alert = new AlertDialog.Builder(this).create();
        //LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogview = inflater.inflate(R.layout.lanc_exo, null);
        //AlertDialog
        alert.setView(dialogview);
        titres.clear();
        idLivret.clear();
        titres.add("Parcourir tous les livrets");
        idLivret.add(-2);
        List livrets = db.getListlivret();
        for (int i = 0; i < livrets.size(); i++) {
            if (db.getListExo(new MotsCles(), (int) livrets.get(i)).size() > 0) {
                titres.add(db.getTitreLivret((int) livrets.get(i)));
                idLivret.add((int) livrets.get(i));
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titres);

        Spinner spinner = (Spinner) dialogview.findViewById(R.id.Lanc_listLivret);
        spinner.setAdapter(adapter);
        spinner.setEnabled(true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RadioButton rb1 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBNvLivret);
                rb1.setChecked(true);
                selectedLiv = idLivret.get(position);
                tailleLiv = db.getListExo(new MotsCles(), selectedLiv).size();
                SeekBar s = (SeekBar) dialogview.findViewById(R.id.Lanc_seekBar);
                SeekBar s2 = (SeekBar) dialogview.findViewById(R.id.Lanc_seekBarRgpt);

                TextView t = (TextView) dialogview.findViewById(R.id.Lanc_nb);
                s.setMax(tailleLiv - 1);
                if (tailleLiv - 1 > 19) {
                    s.setProgress(19);
                    t.setText("20 sur " + tailleLiv + " exos du livret");
                } else {
                    s.setProgress(tailleLiv - 1);
                    t.setText(tailleLiv + " sur " + tailleLiv + " exos du livret");
                }
                s2.setProgress(5);
                TextView t2 = (TextView) dialogview.findViewById(R.id.Lanc_nbSerie);
                t2.setText("Bonus si serie supperieur à : " + 5);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Pair<List<Long>, List<String>> pair = db.getListEvalNonFinie();
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pair.second);
        Spinner spinnerExist = (Spinner) dialogview.findViewById(R.id.Lanc_listSeance);
        spinnerExist.setAdapter(adapter2);
        spinnerExist.setEnabled(true);
        spinnerExist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RadioButton rb1 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBOldSeance);
                rb1.setChecked(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        final Pair<List<Long>, List<String>> pairTerm = db.getListEvalFinie();
        ArrayAdapter adapter3 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, pairTerm.second);
        final Spinner spinnerExistTerm = (Spinner) dialogview.findViewById(R.id.Lanc_listSeanceTerm);
        spinnerExistTerm.setAdapter(adapter3);
        if (pairTerm.first.size() > 0)
            spinnerExistTerm.setEnabled(true);
        else
            spinnerExistTerm.setEnabled(false);
        spinnerExistTerm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RadioButton rb2 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBOldSeanceTerm);
                rb2.setChecked(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SeekBar sb1 = (SeekBar) dialogview.findViewById(R.id.Lanc_seekBar);
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                TextView t = (TextView) dialogview.findViewById(R.id.Lanc_nb);

                t.setText(progressChanged + 1 + " sur " + tailleLiv + " exos du livret");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar sb2 = (SeekBar) dialogview.findViewById(R.id.Lanc_seekBarRgpt);
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged2 = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged2 = progress;
                TextView t2 = (TextView) dialogview.findViewById(R.id.Lanc_nbSerie);
                t2.setText("Bonus si série supérieure à : " + progressChanged2 + 1);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        RadioButton rb1 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBNvLivret);
        rb1.setChecked(true);
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
                                        RadioButton rb1 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBNvLivret);
                                        RadioButton rb2 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBOldSeance);
                                        RadioButton rb3 = (RadioButton) dialogview.findViewById(R.id.Lanc_RBOldSeanceTerm);
                                        long idSeance = -1;
                                        SeekBar sb1 = (SeekBar) dialogview.findViewById(R.id.Lanc_seekBar);
                                        SeekBar sb2 = (SeekBar) dialogview.findViewById(R.id.Lanc_seekBarRgpt);
                                        CheckBox cb1 = (CheckBox) dialogview.findViewById(R.id.Lanc_checkRgpt);
                                        CheckBox cb2 = (CheckBox) dialogview.findViewById(R.id.Lanc_checkSerie);
                                        Intent intent = new Intent(Accueil.this, ExoView.class);
                                        Spinner spinnerExist = (Spinner) dialogview.findViewById(R.id.Lanc_listSeance);
                                        if (rb1.isChecked()) {
                                            idSeance = Eval.creaEval(Accueil.this, selectedLiv, sb1.getProgress() + 1, cb1.isChecked(), cb2.isChecked(), sb2.getProgress() + 1);
                                        } else if (rb2.isChecked()) {
                                            idSeance = pair.first.get(spinnerExist.getSelectedItemPosition());
                                        } else if (rb3.isChecked()) {
                                            CheckBox cb3 = (CheckBox) dialogview.findViewById(R.id.Lanc_checkDup);
                                            if (cb3.isChecked()) {
                                                idSeance = Eval.copieEval(Accueil.this, pairTerm.first.get(spinnerExistTerm.getSelectedItemPosition()));
                                            } else {
                                                idSeance = pairTerm.first.get(spinnerExistTerm.getSelectedItemPosition());
                                            }
                                        }
                                        intent.putExtra("livret_sel", -1);
                                        intent.putExtra("seance", idSeance);
                                        startActivity(intent);
                                        alert.cancel();
                                    }
                                }
        );
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
        Button posA = (Button) dialogview.findViewById(R.id.score_switch);
        posA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tg = (EditText) dialogview.findViewById(R.id.score_nomgauche);
                EditText td = (EditText) dialogview.findViewById(R.id.score_nomdroit);
                EditText sg = (EditText) dialogview.findViewById(R.id.score_scoregauche);
                EditText sd = (EditText) dialogview.findViewById(R.id.score_scoredroit);

                String nomg = tg.getText().toString();
                String nomd = td.getText().toString();
                String scoreg = sg.getText().toString();
                String scored = sd.getText().toString();
                tg.setText(nomd);
                td.setText(nomg);
                sg.setText(scored);
                sd.setText(scoreg);
                if (tg.isEnabled()) {
                    tg.setEnabled(false);
                    td.setEnabled(true);
                } else {
                    tg.setEnabled(true);
                    td.setEnabled(false);
                }

            }
        });

        Button posB = (Button) alert.getButton(AlertDialog.BUTTON_POSITIVE);
        posB.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int rep = Integer.parseInt(((EditText) dialogview.findViewById(R.id.score_nbrep)).getText().toString());
                                        EditText tg = (EditText) dialogview.findViewById(R.id.score_nomgauche);
                                        EditText td = (EditText) dialogview.findViewById(R.id.score_nomdroit);
                                        EditText sg = (EditText) dialogview.findViewById(R.id.score_scoregauche);
                                        EditText sd = (EditText) dialogview.findViewById(R.id.score_scoredroit);
                                        String adv;
                                        int scoreadv;
                                        int score;
                                        int ordre;
                                        if (tg.isActivated()) {
                                            adv = tg.getText().toString();
                                            scoreadv = Integer.parseInt(sg.getText().toString());
                                            score = Integer.parseInt(sd.getText().toString());
                                            ordre = 2;
                                        } else {
                                            adv = td.getText().toString();
                                            scoreadv = Integer.parseInt(sd.getText().toString());
                                            score = Integer.parseInt(sg.getText().toString());
                                            ordre = 1;
                                        }
                                        if (rep < 1) {
                                            Toast.makeText(Accueil.this, "Le nombre de reprise ne peut pas etre nul", Toast.LENGTH_SHORT).show();
                                        } else {
                                            db.saveMatch(adv, rep, score, scoreadv, ordre);
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
