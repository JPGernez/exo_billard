package com.example.exo_billard;
/*  Ecran d'accueil de l application */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Exo_entree extends Activity  implements PopupMenu.OnMenuItemClickListener , View.OnTouchListener {

    SqlBillardHelper db;

    private TapisView tapis = null;
    private TapisView tapis2 = null;
    private TapisView tapis3 = null;
    private Spinner spinner = null;
    // creattion d un nouvel exo
    private List<Integer> lLivret = new ArrayList<>();
    private List<Integer> lExo = new ArrayList<>();
    Exo exo1 = new Exo();
    Exo exo2 = new Exo();
    Exo exo3 = new Exo();
    Exo exovide = new Exo();
    public int nbLiv = 0;
    private int liv = -2;
    private int premExo = 0;
    int affich = 1;
    float x1 =0;
    float x2 =0;
    float tps1 =0;
    float tps2 =0;
    float MIN_DISTANCE = 50;
    float MIN_TEMPS = 150;
    int depl=0;
    float deltaX=0;
    public String LMouches = "";
    public String LCadres = "";
    public String Couleurs = "";
    public boolean Soluce=true;
    public boolean Symetrie=true;

    SharedPreferences  sharedPreferences ;
    MotsCles tags = new MotsCles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Passer la fen�tre en fullscreen == cacher la barre de notification
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.exo_entree);

           sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            readPref();
           db = new SqlBillardHelper(this);
        tags.creaListMotsCles(1);

        // Passer la fen�tre en fullscreen == cacher la barre de notification
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Recuperation de la taille de l'ecran

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int largEcran = metrics.heightPixels;
        int longEcran = metrics.widthPixels;


        int largTapis = (int) (Math.min(largEcran, longEcran * 52.60 / 100) / 3) - 20;
        int longTapis = (int) (largTapis * 190.1 / 100);

        LinearLayout lL2 = new LinearLayout(this);
        lL2.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams Ll2Params = new ViewGroup.LayoutParams(longTapis, largTapis);
        lL2.setLayoutParams(Ll2Params);

        // Creation du "tapis1"
        tapis = new TapisView(this,0);
        lL2.addView(tapis);

        tapis.setOnTouchListener(this);


        LinearLayout lL1 = (LinearLayout) this.findViewById(R.id.emplExo1);
        lL1.addView(lL2);

        LinearLayout lL3 = new LinearLayout(this);
        lL3.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams Ll3Params = new ViewGroup.LayoutParams(longTapis, largTapis);
        lL3.setLayoutParams(Ll3Params);

        // Creation du "tapis2"
        tapis2 = new TapisView(this,0);
        lL3.addView(tapis2);
        tapis2.setOnTouchListener(this);
        ((LinearLayout) findViewById(R.id.emplExo2)).addView(lL3);
        // Creation du "tapis2"
        LinearLayout lL4 = new LinearLayout(this);
        lL4.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams Ll4Params = new ViewGroup.LayoutParams(longTapis, largTapis);
        lL4.setLayoutParams(Ll4Params);

        tapis3 = new TapisView(this,0);
        lL4.addView(tapis3);
        tapis3.setOnTouchListener(this);
        ((LinearLayout) findViewById(R.id.emplExo3)).addView(lL4);
        //Lecture exo 0
        readLivret();
        TableRow tap=  (TableRow) findViewById(R.id.tapis3);
        tap.setOnTouchListener(this);

        spinner = (Spinner) findViewById(R.id.titre);
        addListenerOnSpinnerItemSelection();


        ImageView bOptions = (ImageView) findViewById(R.id.menuOptions);
        bOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(Exo_entree.this, v);
                popupMenu.setOnMenuItemClickListener(Exo_entree.this);
                popupMenu.inflate(R.menu.menu_entree);
                popupMenu.show();
                drawTapis();
            }
        });


           majListLivret();
           majListExo();
        readExo();
       }

    @Override
    protected void onResume() {
        super.onResume();
        readPref();
        drawTapis();
    }

    public void addListenerOnSpinnerItemSelection() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeLivret();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(0);
                changeLivret();
            }
        });
    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                depl=0;
                x1 = event.getX();
                tps1=event.getEventTime();
                return true;

            case MotionEvent.ACTION_MOVE:
                x2 = event.getX();
                tps2=event.getEventTime();
                deltaX = x2 - x1;
                if (depl==0 && Math.abs(deltaX) > MIN_DISTANCE) {
                    depl=1;
                    if (deltaX<0) {
                        premExo = premExo + 3;
                        if ((premExo + 1) > lExo.size()) premExo = 0;
                        readExo();
                    }
                    else {
                        premExo = premExo - 3;
                        if ((premExo < 0)) premExo = (Math.round((lExo.size() - 1) / 3) * 3);
                        readExo();
                    }
                }
                return false;

            case MotionEvent.ACTION_UP:
                tps2=event.getEventTime();
                x2 = event.getX();
                deltaX = x2 - x1;
                float deltaT = tps2 - tps1;
                if (depl==0 && deltaT<MIN_TEMPS && Math.abs(deltaX) <= MIN_DISTANCE) {
                        if (v==tapis) {
                           Intent intent = new Intent(Exo_entree.this, ExoView.class);
                           if (liv >= 0) intent.putExtra("livret_sel", lLivret.get(liv));
                           else intent.putExtra("livret_sel", liv);
                           if (lExo.size()>0 ) {
                               intent.putExtra("exo_sel", lExo.get(premExo));
                           }
                           else{
                               intent.putExtra("exo_sel",-1);
                           }
                            intent.putExtra("seance", -1);

                            startActivity(intent);

                        }
                        else if (v==tapis2) {
                            Intent intent = new Intent(Exo_entree.this, ExoView.class);
                            if (liv >= 0) intent.putExtra("livret_sel", lLivret.get(liv));
                            else intent.putExtra("livret_sel", liv);
                            intent.putExtra("exo_sel", lExo.get(premExo + 1));
                            intent.putExtra("seance", -1);
                            startActivity(intent);
                        }
                        else if (v==tapis3) {
                            Intent intent = new Intent(Exo_entree.this, ExoView.class);
                            if (liv >= 0) intent.putExtra("livret_sel", lLivret.get(liv));
                            else intent.putExtra("livret_sel", liv);
                            intent.putExtra("exo_sel", lExo.get(premExo + 2));
                            intent.putExtra("seance", -1);
                            startActivity(intent);
                        }
                    }
                    return false;
                }
        return false;
    }

  private void addItemsOnSpinner() {
      ArrayList<String> titres = new ArrayList<>();
    titres.add("Parcourir tous les livrets");
    titres.add("Parcourir les favoris");
     for (int i = 0; i < lLivret.size(); i++) {
         titres.add(db.getTitreLivret(lLivret.get(i)));
     }
    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titres);
    spinner.setAdapter(adapter);
      spinner.setSelection(liv + 2);
  }

    private void changeLivret() {
        liv = spinner.getSelectedItemPosition() - 2;
        premExo = 0;
        majListExo();
        readLivret();
        readExo();
    }

    private void drawTapis() {
        Canvas canvas;

        canvas = tapis.mSurfaceHolder.lockCanvas();
        tapis.setLMouches(LMouches);
        tapis.setLCadres(LCadres);
        tapis.setCouleurs(Couleurs);

        if (canvas != null) {
            tapis.Draw(canvas);
            tapis.mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

        canvas = tapis2.mSurfaceHolder.lockCanvas();
        tapis2.setLMouches(LMouches);
        tapis2.setLCadres(LCadres);
        tapis2.setCouleurs(Couleurs);

        if (canvas != null) {
            tapis2.Draw(canvas);
            tapis2.mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

        canvas = tapis3.mSurfaceHolder.lockCanvas();
        tapis3.setLMouches(LMouches);
        tapis3.setLCadres(LCadres);
        tapis3.setCouleurs(Couleurs);

        if (canvas != null) {
            tapis3.Draw(canvas);
            tapis3.mSurfaceHolder.unlockCanvasAndPost(canvas);
        }

        int couleurButton;
        int couleurText;
        int couleurBack;
        int couleurDesc;


        if ("green".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisG;
            couleurDesc = Constantes.couleurBackG;
            //couleurText = Constantes.couleurMoucheG;
            couleurBack = Constantes.couleurBackG;

        } else if ("blue".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisB;
            couleurDesc = Constantes.couleurBackB;
            //couleurText = Constantes.couleurMoucheB;
            couleurBack = Constantes.couleurBackB;
        } else if ("red".equals(Couleurs.intern())) {
            couleurButton = Constantes.couleurTapisR;
            couleurDesc = Constantes.couleurBackR;
            //couleurText = Constantes.couleurMoucheR;
            couleurBack = Constantes.couleurBackR;
        } else {
            couleurButton = Constantes.couleurTapisNB;
            couleurDesc = Constantes.couleurBackNB;
            //couleurText = Constantes.couleurMoucheNB;
            couleurBack = Constantes.couleurBackNB;
        }
        LinearLayout l = (LinearLayout) findViewById(R.id.LlayoutMenu);
        l.setBackgroundColor(couleurBack);

        Spinner s = (Spinner) findViewById(R.id.titre);
        s.setBackgroundColor(couleurButton);
        //   ((TextView) s.getChildAt(0)).setTextColor(couleurText);

        TextView t = (TextView) findViewById(R.id.desc);
        t.setBackgroundColor(couleurDesc);
        //       t.setTextColor(couleurText);

        TextView t1 = (TextView) findViewById(R.id.desc1);
        t1.setBackgroundColor(couleurDesc);
        //     t1.setTextColor(couleurText);

        TextView t2 = (TextView) findViewById(R.id.desc2);
        t2.setBackgroundColor(couleurDesc);
        //   t2.setTextColor(couleurText);

        TextView t3 = (TextView) findViewById(R.id.desc3);
        t3.setBackgroundColor(couleurDesc);
        // t3.setTextColor(couleurText);

    }

    private void readLivret() {
        if (liv > -1) {
            Livret Liv = db.getLivret(lLivret.get(liv));
           ((TextView) this.findViewById(R.id.desc)).setText(" Auteur: " + Liv.getAuteur() + "\r\n" + "  " + Liv.getComment());
        } else {
            if (liv== -2) {
                ((TextView) this.findViewById(R.id.desc)).setText("Tous les exercices mais vous pouvez choisir un livret ci-dessus");
             }
            else {
               ((TextView) this.findViewById(R.id.desc)).setText("Les exos marqués comme favoris quelquesoit le livret ");
            }
        }
     }

    private void majListExo() {
        // Recup donn�es Exos.
        int idliv = liv;
        if (liv > -1)
            idliv = db.getLivret(lLivret.get(liv)).getId();
        lExo = db.getListExo(tags, idliv);
    }

    private void readExo() {
        Log.d("livret", String.valueOf(liv));
        Log.d("taille livret", String.valueOf(lExo.size()));
       if (lExo.size() >= premExo + 1) {
           Log.d("lectureExo1", String.valueOf(premExo + 1));
            tapis.setVisibility(View.VISIBLE);
           this.findViewById(R.id.desc1).setVisibility(View.VISIBLE);
           readExo1(premExo);
        } else {
            if (liv > -1 ) {
                Log.d("lectureExo2", String.valueOf(premExo + 1));

                tapis.setVisibility(View.VISIBLE);
                tapis.setAffich(affich);
                tapis.setBille1(exovide.getB(0));
                tapis.setBille2(exovide.getB(1));
                tapis.setBille3(exovide.getB(2));
                tapis.setVisuComm(0);
                readExo1(-1);
                this.findViewById(R.id.desc1).setVisibility(View.VISIBLE);
                ((TextView) this.findViewById(R.id.desc1)).setText("Cliquer sur le tapis pour commencer");
            }
           if (liv == -2 ) {
               Log.d("lectureExo3", String.valueOf(premExo + 1));

               tapis.setVisibility(View.INVISIBLE);
               this.findViewById(R.id.desc1).setVisibility(View.VISIBLE);
               ((TextView) this.findViewById(R.id.desc1)).setText("Commencer par créer un livret");
           }
           if (liv == -1 ) {
               Log.d("lectureExo4", String.valueOf(premExo + 1));

               tapis.setVisibility(View.INVISIBLE);
               this.findViewById(R.id.desc1).setVisibility(View.VISIBLE);
               ((TextView) this.findViewById(R.id.desc1)).setText("Pas d'exercice favori");
           }
        }
        if (lExo.size() >= premExo + 2) {
            tapis2.setVisibility(View.VISIBLE);
            this.findViewById(R.id.desc2).setVisibility(View.VISIBLE);
            readExo2(premExo + 1);
        } else {
            tapis2.setVisibility(View.INVISIBLE);
            this.findViewById(R.id.desc2).setVisibility(View.INVISIBLE);
        }
        if (lExo.size() >= premExo + 3) {
            tapis3.setVisibility(View.VISIBLE);
            this.findViewById(R.id.desc3).setVisibility(View.VISIBLE);
            readExo3(premExo + 2);
        } else {
            tapis3.setVisibility(View.INVISIBLE);
            this.findViewById(R.id.desc3).setVisibility(View.INVISIBLE);
        }
        drawTapis();
    }
private void readPref() {

    Couleurs = sharedPreferences.getString("prefCouleur", "blue");
    LMouches = sharedPreferences.getString("prefMouches", "sans");
    LCadres = sharedPreferences.getString("prefCadres", "plein");
    Soluce = sharedPreferences.getBoolean("prefSoluce", true);
    Symetrie = sharedPreferences.getBoolean("prefSymetrie", true);
    }

    private void readExo1(int numExo) {
        if (numExo > -1) {
            exo1 = db.getExo(lExo.get(numExo));
            tapis.setAffich(affich);
            tapis.setBille1(exo1.getB(0));
            tapis.setBille2(exo1.getB(1));
            tapis.setBille3(exo1.getB(2));
            tapis.setVisuComm(0);
            ((TextView) this.findViewById(R.id.desc1)).setText("  Exo " + (numExo + 1) + "\\" + lExo.size() + "\r\n" + exo1.getComment());
        }
    }

    private void readExo2(int numExo) {

        exo2 = db.getExo(lExo.get(numExo));

        tapis2.setAffich(affich);
        tapis2.setBille1(exo2.getB(0));
        tapis2.setBille2(exo2.getB(1));
        tapis2.setBille3(exo2.getB(2));
        tapis2.setVisuComm(0);
        ((TextView) this.findViewById(R.id.desc2)).setText("  Exo " + (numExo + 1) + "\\" + lExo.size() + "\r\n" + exo2.getComment());
    }

    private void readExo3(int numExo) {

        exo3 = db.getExo(lExo.get(numExo));

        tapis3.setAffich(affich);
        tapis3.setBille1(exo3.getB(0));
        tapis3.setBille2(exo3.getB(1));
        tapis3.setBille3(exo3.getB(2));
        tapis3.setVisuComm(0);
        ((TextView) this.findViewById(R.id.desc3)).setText("  Exo " + (numExo + 1) + "\\" + lExo.size() + "\r\n" + exo3.getComment());
    }



    private void suppLivret() {

        if (liv >= 0) {
            if (lLivret.get(liv) > 0) {
                db.deleteLivret(lLivret.get(liv));
                majListLivret();
                liv = liv - 1;
                 premExo = 0;
                readLivret();
                majListExo();
                readExo();
                majListLivret();
              }
        }
        if (liv==-1) {
            db.deleteFav();
        }
        if (liv==-2) {
            Toast.makeText(this,"Sélectionner un livret",Toast.LENGTH_SHORT).show();
        }
        majListLivret();

    }


        private void majListLivret() {
        // Recup donn�es Exos.
        lLivret = db.getListlivret();
            nbLiv = lLivret.size();
        addItemsOnSpinner();
    }

    // menu Option
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newLivret:
                Intent intent = new Intent(Exo_entree.this, Nvlivret.class);
                intent.putExtra("titre", "");
                intent.putExtra("auteur", "");
                intent.putExtra("comm", "");
                int result = 1;
                startActivityForResult(intent,result);
                return true;
            case R.id.modLivret:
                if (liv >= 0) {
                    Livret Liv = db.getLivret(lLivret.get(liv));
                    Intent intent2 = new Intent(Exo_entree.this, Nvlivret.class);
                    intent2.putExtra("titre", Liv.getTitre());
                    intent2.putExtra("auteur", Liv.getAuteur());
                    intent2.putExtra("comm", Liv.getComment());
                    intent2.putExtra("mod", 1);
                    intent2.putExtra("liv_id",Liv.getId());
                    int result3 = 1;
                    startActivityForResult(intent2,result3);
                }

                return true;

            case R.id.supLivret:
                suppLivret();
                return true;
            case R.id.exportL:
                if (liv >= 0) {
                    String result2 = db.exportLivret(Exo_entree.this, lLivret.get(liv));
                    Toast.makeText(Exo_entree.this, result2, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.importL:

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                //LayoutInflater
                LayoutInflater inflater = LayoutInflater.from(this);
                final View dialogview = inflater.inflate(R.layout.importlivret,null);
                //AlertDialog
                alert.setView(dialogview);

                File myDir = new File(this.getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath());

                ArrayList<String> titres = new ArrayList<>();

                File[] fichiers = myDir.listFiles();
                TextView txt = (TextView) dialogview.findViewById(R.id.infoImport);
                txt.setText("Les fichiers eBi1 et eBi2 doivent se trouver sous " + myDir.toString());
                Spinner spinner = (Spinner) dialogview.findViewById(R.id.listImport);


                //Toast.makeText(Exo_entree.this, fichiers[0].toString() + " -" + fichiers[1].toString(), Toast.LENGTH_LONG).show();
                int i = 0;
                // Si le répertoire n'est pas vide...
                if(fichiers != null)
                    // On les ajoute à  l'adaptateur
                    for(File f : fichiers) {
                        if (f.toString().endsWith(".eBi1")) {
                            String nomCourt = (f.getName() != null) ? f.getName().substring(0,f.getName().indexOf('.')) : "";
                            titres.add(nomCourt);
                            i++;
                        }
                    }
                if (i > 0) {
                    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titres);
                    spinner.setAdapter(adapter);
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idx) {

                            Spinner spinner = (Spinner) dialogview.findViewById(R.id.listImport);
                            String ImportSel = spinner.getSelectedItem().toString();
                            db.importLivret(Exo_entree.this, ImportSel);
                            majListLivret();

                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idx) {
                        }
                    });
                    alert.show();
                } else
                    Toast.makeText(Exo_entree.this, "Ici Les fichiers eBi1 et eBi2 doivent se trouver sous " + myDir.toString(), Toast.LENGTH_LONG).show();
                return true;
            case R.id.options:
                Intent intent2 = new Intent(this, Preferences.class);
                int result4  = 0;
                startActivityForResult( intent2, result4);
                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //on regarde quelle Activity a répondu
        majListLivret();
        readPref();
        drawTapis();

    }

    }
