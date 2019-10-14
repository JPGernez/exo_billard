package com.example.exo_billard;
/* Evaluation */

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Eval implements Serializable {

    private long idEval;
    private int occurence;
    private long datecrea;
    private int idLivret;
    private int nbExo;
    private boolean bonusRgpt;
    private boolean bonusSerie;
    private int nbPourSerie;
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

        public ExoEval(int idExo, int inverse, boolean exoTermine, int nbPt1, int nbPt2, int nbPt3, Boolean Rgpt1, Boolean Rgpt2, Boolean Rgpt3) {
            this.idExo = idExo;
            this.inverse = inverse;
            this.exoTermine = exoTermine;
            this.nbPt1 = nbPt1;
            this.nbPt2 = nbPt2;
            this.nbPt3 = nbPt3;
            this.rgpt1 = rgpt1;
            this.rgpt2 = rgpt2;
            this.rgpt3 = rgpt3;
        }

        public int getIdExo() {
            return this.idExo;
        }

        public int getInverse() {
            return this.inverse;
        }

        public boolean getExoTer() {
            return this.exoTermine;
        }

        public int getNbPt(int i) {
            if (i == 1) return this.nbPt1;
            if (i == 2) return this.nbPt2;
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

        public void setIdExo(int i) {
            this.idExo = i;
        }

        public void setInverse(int i) {
            this.inverse = i;
        }

        public void setNbPt(int s, int i) {
            if (i == 1) this.nbPt1 = s;
            if (i == 2) this.nbPt2 = s;
            else this.nbPt3 = s;
        }

        public void setRgpt(boolean r, int i) {
            if (i == 1) this.rgpt1 = r;
            if (i == 2) this.rgpt2 = r;
            else this.rgpt3 = r;
        }

    }


    public int getIndice(int idExo) {
        int sortie = 0;
        for (int i = 0; i < listExo.size(); i++)
            if (listExo.get(i).getIdExo() == idExo) sortie = i;
        return sortie;
    }

    // ajout d un exo a une Eval
    public void addNewExo(int idExo, int inv) {
        this.listExo.add(new ExoEval(idExo, inv, false, -1, -1, -1, false, false, false));
    }

    public ExoEval getExo(int i) {
        return this.listExo.get(i);
    }

    //renseignement score
    public void addNbPt(int idExo, int i, int s) {
        listExo.get(this.getIndice(idExo)).setNbPt(s, i);
    }

    //renseignement regroupement
    public void addRgpt(int idExo, int i, boolean r) {
        listExo.get(this.getIndice(idExo)).setRgpt(r, i);
    }

    public void setExoTermine(int idExo, boolean r) {
        listExo.get(this.getIndice(idExo)).setExoTer(r);
    }

    // recuperation des scores
    public int getNbPt(int idExo, int i) {
        return listExo.get(this.getIndice(idExo)).getNbPt(i);
    }

    // recuperation des regroupements
    public boolean getRgpt(int idExo, int i) {
        return listExo.get(this.getIndice(idExo)).getRgpt(i);
    }

    // recuperation de l'identifiant de l'exo
    public int getIdExo(int i) {
        return listExo.get(i).getIdExo();
    }

    //Recuperation de la liste de tous les exos
    public List getlistIdExo() {
        return this.listExo;
    }

    // recuperation du nb d'exo dans l'eval
    public int getNbExo() {
        return this.nbExo;
    }

    // recuperation du nb d'exo effectué dans l'eval
    public int getNbExoEffectue() {
        int sortie = 0;
        for (int i = 0; i < listExo.size(); i++)
            if (listExo.get(i).getExoTer() == true) sortie = sortie + 1;
        return sortie;
    }

    public int getNbEssaiI(int idExo) {
        if (listExo.get(this.getIndice(idExo)).nbPt3 > -1) return 3;
        else if (listExo.get(this.getIndice(idExo)).nbPt2 > -1) return 2;
        else if (listExo.get(this.getIndice(idExo)).nbPt1 > -1) return 1;
        else return 0;

    }

    public void setOccurence(int i) {
        this.occurence = i;
    }

    public int getOccurence() {
        return this.occurence;
    }

    public void setBonusRgpt(boolean i) {
        this.bonusRgpt = i;
    }

    public boolean getBonusRgpt() {
        return this.bonusRgpt;
    }

    public void setBonusSerie(boolean i) {
        this.bonusSerie = i;
    }

    public boolean getBonusSerie() {
        return this.bonusSerie;
    }

    public void setNbPourSerie(int i) {
        this.nbPourSerie = i;
    }

    public int getNbPourSerie() {
        return this.nbPourSerie;
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

    public void setDateCrea(long i) {
        this.datecrea = i;
    }

    public long getDateCrea() {
        return this.datecrea;
    }
    //creation d'une eval


    public static long creaEval(Context c, int idLivret, int nb_exo, boolean bonusRgpt, boolean bonusSerie, int nbPourSerie) {
        Eval eval = new Eval();

        SqlBillardHelper db;
        db = new SqlBillardHelper(c);
        List<Integer> lExo = new ArrayList<>();
        lExo = db.getListExo(null, idLivret);
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
            eval.addNewExo(lExo.get(exo), inverse);
            lExoRestant.remove(exo);
        }
        eval.setOccurence(1);
        eval.setBonusRgpt(bonusRgpt);
        eval.setBonusSerie(bonusSerie);
        eval.setNbPourSerie(nbPourSerie);
        eval.setDateCrea(System.currentTimeMillis());
        long idEval = db.addEval(eval);
        eval.setIdEval(idEval);
        return idEval;
    }


}
