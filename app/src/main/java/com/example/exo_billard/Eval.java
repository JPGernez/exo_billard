package com.example.exo_billard;
/* Evaluation */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Eval implements Serializable {

    private long idEval;
    private long origine;
    private long datecrea;
    private int idLivret;
    private int nbExo;
    private List<ExoEval> listExo = new CopyOnWriteArrayList<>();

    //eClasse ExoEval
    public class ExoEval {
        private int idExo;
        private int inverse;
        private boolean exoTermine;
        private int nbPt1;
        private int nbPt2;
        private int nbPt3;
        private boolean rgpt1;
        private boolean rgpt2;
        private boolean rgpt3;
        private long datecrea;

        public ExoEval(int idExo, int inverse, boolean exoTermine, int nbPt1, int nbPt2, int nbPt3, Boolean rgpt1, Boolean rgpt2, Boolean rgpt3, Long dc) {
            this.idExo = idExo;
            this.inverse = inverse;
            this.exoTermine = exoTermine;
            this.nbPt1 = nbPt1;
            this.nbPt2 = nbPt2;
            this.nbPt3 = nbPt3;
            this.rgpt1 = rgpt1;
            this.rgpt2 = rgpt2;
            this.rgpt3 = rgpt3;
            this.datecrea = dc;
        }

        public int getIdExo() {
            return this.idExo;
        }

        public long getDatecrea() {
            return this.datecrea;
        }

        public int getInverse() {
            return this.inverse;
        }

        public boolean getExoTer() {
            return this.exoTermine;
        }

        public int getNbPt(int i) {
            if (i == 1) return this.nbPt1;
            else if (i == 2) return this.nbPt2;
            else return this.nbPt3;
        }

        public boolean getRgpt(int i) {
            if (i == 1) return this.rgpt1;
            if (i == 2) return this.rgpt2;
            else return this.rgpt3;
        }

        public void setExoTer(boolean t) {
            this.exoTermine = t;
        }

        public void setNbPt(int s, int i) {
            if (i == 1) this.nbPt1 = s;
            else if (i == 2) this.nbPt2 = s;
            else this.nbPt3 = s;
        }

        public void setRgpt(boolean r, int i) {
            if (i == 1) this.rgpt1 = r;
            else if (i == 2) this.rgpt2 = r;
            else this.rgpt3 = r;
        }
    }


    public int getIndice(int idExo) {
        int sortie = 0;
        for (int i = 0; i < listExo.size(); i++)
            if (this.listExo.get(i).getIdExo() == idExo) sortie = i;
        return sortie;
    }

    // ajout d un exo a une Eval
    public void addNewExo(int idExo, int inv, Long dc) {
        this.listExo.add(new ExoEval(idExo, inv, false, -1, -1, -1, false, false, false, dc));
    }

    public ExoEval getExo(int i) {
        return this.listExo.get(i);
    }

    //renseignement score
    public void addNbPt(int idExo, int i, int s) {
        this.listExo.get(this.getIndice(idExo)).setNbPt(s, i);
    }

    //renseignement regroupement
    public void addRgpt(int idExo, int i, boolean r) {
        this.listExo.get(this.getIndice(idExo)).setRgpt(r, i);
    }

    public void setExoTermine(int idExo, boolean r) {
        this.listExo.get(this.getIndice(idExo)).setExoTer(r);
    }


    //Recuperation de la liste de tous les exos
    public List<Integer> getlistIdExo() {
        List<Integer> lExo = new ArrayList<>();
        Log.d("id exo", String.valueOf(this.listExo));
        for (int i = 0; i < this.listExo.size(); i++) {
            lExo.add(this.listExo.get(i).getIdExo());
        }
        return lExo;
    }

    // recuperation du nb d'exo dans l'eval
    public int getNbExo() {
        return this.nbExo;
    }


    public void setOrigine(long i) {
        this.origine = i;
    }

    public long getOrigine() {
        return this.origine;
    }

    public void setIdLivret(int i) {
        this.idLivret = i;
    }

    public int getIdLivret() {
        return this.idLivret;
    }

    public void setNbExo(int i) {
        this.nbExo = i;
    }

    public void setIdEval(long i) {
        this.idEval = i;
    }

    public long getIdEval() {
        return this.idEval;
    }

    public int getExoNonTerm() {
        int sortie = -1;
        for (int i = 0; i < listExo.size(); i++)
            if (listExo.get(i).getExoTer() == false & sortie == -1)
                sortie = listExo.get(i).getIdExo();
        return sortie;
    }
    public void setDateCrea(long i) {
        this.datecrea = i;
    }

    public long getDateCrea() {
        return this.datecrea;
    }
    //creation d'une eval


    public static long creaEval(Context c, int idLivret, int nb_exo) {
        Eval eval = new Eval();

        SqlBillardHelper db;
        db = new SqlBillardHelper(c);
        List<Integer> lExo = new ArrayList<>();
        MotsCles mot = new MotsCles();
        lExo = db.getListExo(mot, idLivret);
        Random randomGenerator = new Random();
        eval.setIdLivret(idLivret);
        eval.setNbExo(nb_exo);
        List<Integer> lExoRestant = lExo;
        for (int i = 0; i < nb_exo; i++) {
            if (lExoRestant.size() <= 0) {
                lExoRestant = lExo;
            }
            int exo = randomGenerator.nextInt(lExo.size());
            int inverse = randomGenerator.nextInt(5) - 1;
            eval.addNewExo(lExo.get(exo), inverse, eval.getDateCrea());
            lExoRestant.remove(exo);
        }
        eval.setOrigine(-1);
        eval.setDateCrea(System.currentTimeMillis());
        long idEval = db.addEval(eval);
        eval.setIdEval(idEval);
        return idEval;
    }

    public static Eval videEval(Eval e) {
        for (int i = 0; i < e.getNbExo(); i++) {
            e.getExo(i).setExoTer(false);
            e.getExo(i).setRgpt(false, 1);
            e.getExo(i).setRgpt(false, 2);
            e.getExo(i).setRgpt(false, 3);
            e.getExo(i).setNbPt(-1, 1);
            e.getExo(i).setNbPt(-1, 2);
            e.getExo(i).setNbPt(-1, 3);
        }
        return e;
    }

    public static long copieEval(Context c, long id) {
        SqlBillardHelper db;
        db = new SqlBillardHelper(c);
        Eval eval = db.getEval(id);
        if (eval.getOrigine() <= 0) eval.setOrigine(id);
        eval = videEval(eval);
        eval.setDateCrea(System.currentTimeMillis());
        eval.setIdEval(-1);
        long idEval = db.addEval(eval);
        eval.setIdEval(idEval);
        return idEval;
    }


}
