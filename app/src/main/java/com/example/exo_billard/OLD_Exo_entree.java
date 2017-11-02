package com.example.exo_billard;
/*  Ecran d'accueil de l application */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OLD_Exo_entree extends Activity implements PopupMenu.OnMenuItemClickListener {

    SqlBillardHelper db;

    private List<Integer> lLivret = new ArrayList<>();
    public int nbLiv = 0;
    private int premLiv = -1;
    public long livret_sel =0;
    int mod=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Passer la fen�tre en fullscreen == cacher la barre de notification
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.old_exo_entree);

        db = new SqlBillardHelper(this);

        majListLivret();
        majPage();

        ImageView bOptions = (ImageView) findViewById(R.id.menuOptions);

        bOptions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(OLD_Exo_entree.this, v);
                popupMenu.setOnMenuItemClickListener(OLD_Exo_entree.this);
                popupMenu.inflate(R.menu.menu_entree);
                popupMenu.show();
            }
        });

        ImageView bCopy = (ImageView) findViewById(R.id.me_copy);
        bCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyLivret(livret_sel);
            }
        });
        ImageView bSupp = (ImageView) findViewById(R.id.me_supp);
        bSupp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                suppLivret(livret_sel);
            }
        });
        ImageView bCancel = (ImageView) findViewById(R.id.me_cancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                menuMod(0,null);
            }
        });
        TextView b = null;
        b= (TextView) findViewById(R.id.titre1);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                menuMod(1,v);
                return true;
            }
        });
        b = (TextView) findViewById(R.id.titre2);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                menuMod(1,v);
                return true;
            }
        });
        b = (TextView) findViewById(R.id.titre3);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                menuMod(1,v);
                return true;
            }
        });
        b= (TextView) findViewById(R.id.titre6);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                menuMod(1,v);
                return true;
            }
        });
        b = (TextView) findViewById(R.id.titre5);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                menuMod(1,v);
                return true;
            }
        });
        b = (TextView) findViewById(R.id.titre4);
        b.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                menuMod(1,v);
                return true;
            }
        });
    }

    private void menuMod(int i,View v){
        ImageView bCopy = (ImageView) findViewById(R.id.me_copy);
        ImageView bCancel = (ImageView) findViewById(R.id.me_cancel);
        ImageView bSupp = (ImageView) findViewById(R.id.me_supp);
        if (i==0) {
                mod=0;
                bCopy.setVisibility(View.INVISIBLE);
                bCancel.setVisibility(View.INVISIBLE);
                bSupp.setVisibility(View.INVISIBLE);
                livret_sel=0;

        }
        else if (mod==0) {
            livret_sel= new Long((int)v.getTag());
            if ( livret_sel > 0) {
                mod = 1;
                bCopy.setVisibility(View.VISIBLE);
                bCancel.setVisibility(View.VISIBLE);
                bSupp.setVisibility(View.VISIBLE);
            }
        }
        Toast.makeText(OLD_Exo_entree.this,"sel " +mod +" - liv "+livret_sel,Toast.LENGTH_SHORT).show();
        majPage();
    }

    private void  copyLivret( long livret_sel) {

        menuMod(0,null);
        majListLivret();
        majPage();
    }

    private void  suppLivret( long livret_sel) {
        if (livret_sel>0) {
            db.deleteLivret(livret_sel);
        }

        menuMod(0,null);
        majListLivret();
        majPage();
    }


    private void majPage() {
        int numLivret = -1;
        Livret liv = null;
        TextView Empl = null;
        int numliv = 0;
        if (premLiv == -1) {
            TextView textViewToChange = (TextView) findViewById(R.id.titre1);
            textViewToChange.setText(" TOUS LES LIVRETS");
            textViewToChange.setTag(-1);
            textViewToChange = (TextView) findViewById(R.id.desc1);
            textViewToChange.setText(" Parcourir tous les exercices " + "\r\n"
                    + " sans selection de livret ");
            textViewToChange.setTag(-1);
        } else if (nbLiv >= premLiv + 2) {
            numliv = lLivret.get(premLiv + 1);
            liv = db.getLivret(numliv);
            Empl = (TextView) findViewById(R.id.titre1);
            Empl.setText(" "+liv.getTitre().toUpperCase());
            Empl.setTag(liv.getId());
            Empl = (TextView) findViewById(R.id.desc1);
            Empl.setText( " " + liv.getNbExo() + " exos "
                    + "\r\n"+ " Auteur: " + liv.getAuteur() + "\r\n"
                    + "  "+ liv.getComment());
            Empl.setTag("AutComm"+liv.getId());
        }  else { Empl = (TextView) findViewById(R.id.titre1);
            Empl.setText("-");
            Empl.setTag(-999);
            Empl = (TextView) findViewById(R.id.desc1);
            Empl.setText("-");
            Empl.setTag(-999);
        }

        if (nbLiv >= premLiv + 2) {
            numliv = lLivret.get(premLiv + 1);
            liv = db.getLivret(numliv);
            Log.d("entree", String.valueOf(liv.getAuteur()));
            Empl = (TextView) findViewById(R.id.titre2);
            Empl.setText(" "+liv.getTitre().toUpperCase());
            Empl.setTag(liv.getId());
            Empl = (TextView) findViewById(R.id.desc2);
            Empl.setText( " " + liv.getNbExo() + " exos "
                    + "\r\n"+ " Auteur: " + liv.getAuteur() + "\r\n"
                    + "  " + liv.getComment());
            Empl.setTag("AutComm"+liv.getId());
        }else { Empl = (TextView) findViewById(R.id.titre2);
            Empl.setText("-");
            Empl.setTag(-999);
            Empl = (TextView) findViewById(R.id.desc2);
            Empl.setText("-");
            Empl.setTag(-999);
        }
        if (nbLiv >= premLiv + 3) {
            numliv = lLivret.get(premLiv + 2);
            liv = db.getLivret(numliv);
            Log.d("entree", String.valueOf(liv.getAuteur()));
            Empl = (TextView) findViewById(R.id.titre3);
            Empl.setText(" "+liv.getTitre().toUpperCase());
            Empl.setTag(liv.getId());
            Empl = (TextView) findViewById(R.id.desc3);
            Empl.setText( " " + liv.getNbExo() + " exos "
                    + "\r\n"+ " Auteur: " + liv.getAuteur() + "\r\n"
                    + "  " + liv.getComment());
            Empl.setTag("AutComm"+liv.getId());
        }else { Empl = (TextView) findViewById(R.id.titre3);
            Empl.setText("-");
            Empl.setTag(-999);
            Empl = (TextView) findViewById(R.id.desc3);
            Empl.setText("-");
            Empl.setTag(-999);
        }

        if (nbLiv >= premLiv + 4) {
            numliv = lLivret.get(premLiv + 3);
            liv = db.getLivret(numliv);
            Log.d("entree", String.valueOf(liv.getAuteur()));
            Empl = (TextView) findViewById(R.id.titre4);
            Empl.setText(" "+liv.getTitre().toUpperCase());
            Empl.setTag(liv.getId());
            Empl = (TextView) findViewById(R.id.desc4);
            Empl.setText( " " + liv.getNbExo() + " exos "
                    + "\r\n"+ " Auteur: " + liv.getAuteur() + "\r\n"
                    + "  " + liv.getComment());
            Empl.setTag("AutComm"+liv.getId());
        }else { Empl = (TextView) findViewById(R.id.titre4);
            Empl.setText("-");
            Empl.setTag(-999);
            Empl = (TextView) findViewById(R.id.desc4);
            Empl.setText("-");
            Empl.setTag(-999);
        }
        if (nbLiv >= premLiv + 5) {
            numliv = lLivret.get(premLiv + 4);
            liv = db.getLivret(numliv);
            Log.d("entree", String.valueOf(liv.getAuteur()));
            Empl = (TextView) findViewById(R.id.titre5);
            Empl.setText(" "+liv.getTitre().toUpperCase());
            Empl.setTag(liv.getId());
            Empl = (TextView) findViewById(R.id.desc5);
            Empl.setText( " " + liv.getNbExo() + " exos "
                    + "\r\n"+ " Auteur: " + liv.getAuteur() + "\r\n"
                    + "  " + liv.getComment());
            Empl.setTag("AutComm"+liv.getId());
        }else { Empl = (TextView) findViewById(R.id.titre5);
            Empl.setText("-");
            Empl.setTag(-999);
            Empl = (TextView) findViewById(R.id.desc5);
            Empl.setText("-");
            Empl.setTag(-999);
        }
        if (nbLiv >= premLiv + 6) {
            numliv = lLivret.get(premLiv + 5);
            liv = db.getLivret(numliv);
            Log.d("entree", String.valueOf(liv.getAuteur()));
            Empl = (TextView) findViewById(R.id.titre6);
            Empl.setText(" "+liv.getTitre().toUpperCase());
            Empl.setTag(liv.getId());
            Empl = (TextView) findViewById(R.id.desc6);
            Empl.setText( " " + liv.getNbExo() + " exos "
                    + "\r\n"+ " Auteur: " + liv.getAuteur() + "\r\n"
                    + "  " + liv.getComment());
            Empl.setTag("AutComm" + liv.getId());
        }else { Empl = (TextView) findViewById(R.id.titre6);
            Empl.setText("-");
            Empl.setTag(-999);
            Empl = (TextView) findViewById(R.id.desc6);
            Empl.setText("-");
            Empl.setTag(-999);
        }
        long num=0;
        num= new Long((int) findViewById(R.id.titre1).getTag());
        if (mod==1 & num==livret_sel) {
            findViewById(R.id.titre1).setBackgroundColor(Constantes.couleurbtnBackgrndSel);
            findViewById(R.id.desc1).setBackgroundColor(Constantes.couleurbtnBackgrnd2Sel);
        }
        else {
            findViewById(R.id.titre1).setBackgroundColor(Constantes.couleurbtnBackgrnd);
            findViewById(R.id.desc1).setBackgroundColor(Constantes.couleurbtnBackgrnd2);
        }
        num= new Long((int) findViewById(R.id.titre2).getTag());
        if (mod==1 & num==livret_sel) {
            findViewById(R.id.titre2).setBackgroundColor(Constantes.couleurbtnBackgrndSel);
            findViewById(R.id.desc2).setBackgroundColor(Constantes.couleurbtnBackgrnd2Sel);
        }
        else {
            findViewById(R.id.titre2).setBackgroundColor(Constantes.couleurbtnBackgrnd);
            findViewById(R.id.desc2).setBackgroundColor(Constantes.couleurbtnBackgrnd2);
        }
        num= new Long((int) findViewById(R.id.titre3).getTag());
        if (mod==1 & num==livret_sel) {
            findViewById(R.id.titre3).setBackgroundColor(Constantes.couleurbtnBackgrndSel);
            findViewById(R.id.desc3).setBackgroundColor(Constantes.couleurbtnBackgrnd2Sel);
        }
        else {
            findViewById(R.id.titre3).setBackgroundColor(Constantes.couleurbtnBackgrnd);
            findViewById(R.id.desc3).setBackgroundColor(Constantes.couleurbtnBackgrnd2);
        }
        num= new Long((int) findViewById(R.id.titre4).getTag());
        if (mod==1 & num==livret_sel) {
            findViewById(R.id.titre4).setBackgroundColor(Constantes.couleurbtnBackgrndSel);
            findViewById(R.id.desc4).setBackgroundColor(Constantes.couleurbtnBackgrnd2Sel);
        }
        else {
            findViewById(R.id.titre4).setBackgroundColor(Constantes.couleurbtnBackgrnd);
            findViewById(R.id.desc4).setBackgroundColor(Constantes.couleurbtnBackgrnd2);
        }
        num= new Long((int) findViewById(R.id.titre5).getTag());
        if (mod==1 & num==livret_sel) {
            findViewById(R.id.titre5).setBackgroundColor(Constantes.couleurbtnBackgrndSel);
            findViewById(R.id.desc5).setBackgroundColor(Constantes.couleurbtnBackgrnd2Sel);
        }
        else {
            findViewById(R.id.titre5).setBackgroundColor(Constantes.couleurbtnBackgrnd);
            findViewById(R.id.desc5).setBackgroundColor(Constantes.couleurbtnBackgrnd2);
        }
        num= new Long((int) findViewById(R.id.titre6).getTag());
        if (mod==1 & num==livret_sel) {
            findViewById(R.id.titre6).setBackgroundColor(Constantes.couleurbtnBackgrndSel);
            findViewById(R.id.desc6).setBackgroundColor(Constantes.couleurbtnBackgrnd2Sel);
        }
        else {
            findViewById(R.id.titre6).setBackgroundColor(Constantes.couleurbtnBackgrnd);
            findViewById(R.id.desc6).setBackgroundColor(Constantes.couleurbtnBackgrnd2);
        }
    }

    // click sur un element du menu d entree
    public void choixLivret(View v) {
        int sel=(int) v.getTag();
        if (mod==0 & sel >= -1 ) {
            Intent intent = new Intent(OLD_Exo_entree.this, ExoView.class);
            intent.putExtra("livret_sel", (int) v.getTag());
            Log.d("entree", "lancement");
            startActivity(intent);
        }
    }

    private void majListLivret() {
        // Recup donn�es Exos.
        Log.d("liste_livret", "0 ");

        lLivret = db.getListlivret();
        nbLiv = lLivret.size();
        Log.d("liste_livret nb", String.valueOf(nbLiv));
        Log.d("liste_livret nb", String.valueOf(lLivret));
    }

    // menu Option
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.newLivret:
                Intent intent = new Intent(OLD_Exo_entree.this, Nvlivret.class);
                startActivityForResult(intent, 1);

                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //on regarde quelle Activity a répondu
        majListLivret();
        majPage();
    }

}
