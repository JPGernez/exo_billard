package com.example.exo_billard;

import android.util.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Lae_JP on 21/10/2019.
 */

public class NoteEval implements Serializable {

    private List<NoteEval.ResEvalElt> listEval = new CopyOnWriteArrayList<>();
    private FeuilleScore fs;

    private class FeuilleScore {
        private int joue;
        private int reussi;
        private int rgpt;
        private int pt;
        private int score;

        public FeuilleScore() {
            this.joue = 0;
            this.reussi = 0;
            this.rgpt = 0;
            this.pt = 0;
            this.score = 0;
        }
    }

    ;

    public FeuilleScore addFeuilleScore(FeuilleScore fs1, FeuilleScore fs2) {
        FeuilleScore fs = new FeuilleScore();
        fs.joue = fs1.joue + fs2.joue;
        fs.reussi = fs1.reussi + fs2.reussi;
        fs.rgpt = fs1.rgpt + fs2.rgpt;
        fs.pt = fs1.pt + fs2.pt;
        fs.score = fs1.score + fs2.score;
        return fs;
    }

    public FeuilleScore calculScore(ResEvalElt nEE) {
        FeuilleScore fs = new FeuilleScore();
        if (nEE.nbPt1 >= 0) {
            fs.joue = fs.joue + 1;
            if (nEE.nbPt1 > 0) {
                fs.reussi = fs.reussi + 1;
                fs.pt = fs.pt + nEE.nbPt1;
                fs.score = fs.score + Constantes.ptExoReussi;
                if (nEE.nbPt1 >= Constantes.bonus1) {
                    fs.score = fs.score + Constantes.ptExoReussi_bonus1;
                    if (nEE.nbPt1 >= Constantes.bonus2) {
                        fs.score = fs.score + Constantes.ptExoReussi_bonus2;
                    }
                }
                if (nEE.rgpt1) {
                    fs.rgpt = fs.rgpt + 1;
                    fs.score = fs.score + Constantes.ptExoRgpt;
                }
            }
        }
        if (nEE.nbPt2 >= 0) {
            fs.joue = fs.joue + 1;
            if (nEE.nbPt2 > 0) {
                fs.reussi = fs.reussi + 1;
                fs.pt = fs.pt + nEE.nbPt2;
                fs.score = fs.score + Constantes.ptExoReussi;
                if (nEE.nbPt2 >= Constantes.bonus1) {
                    fs.score = fs.score + Constantes.ptExoReussi_bonus1;
                    if (nEE.nbPt2 >= Constantes.bonus2) {
                        fs.score = fs.score + Constantes.ptExoReussi_bonus2;
                    }
                }
                if (nEE.rgpt2) {
                    fs.rgpt = fs.rgpt + 1;
                    fs.score = fs.score + Constantes.ptExoRgpt;
                }
            }
        }
        if (nEE.nbPt3 >= 0) {
            fs.joue = fs.joue + 1;
            if (nEE.nbPt3 > 0) {
                fs.reussi = fs.reussi + 1;
                fs.pt = fs.pt + nEE.nbPt3;
                fs.score = fs.score + Constantes.ptExoReussi;
                if (nEE.nbPt3 >= Constantes.bonus1) {
                    fs.score = fs.score + Constantes.ptExoReussi_bonus1;
                    if (nEE.nbPt3 >= Constantes.bonus2) {
                        fs.score = fs.score + Constantes.ptExoReussi_bonus2;
                    }
                }
                if (nEE.rgpt3) {
                    fs.rgpt = fs.rgpt + 1;
                    fs.score = fs.score + Constantes.ptExoRgpt;
                }
            }
        }
        return fs;
    }

    public class ResEvalElt {
        private long idEval;
        private int idExo;
        private int nbPt1;
        private int nbPt2;
        private int nbPt3;
        private boolean rgpt1;
        private boolean rgpt2;
        private boolean rgpt3;

        public ResEvalElt(long idEval, int idExo, int nbPt1, int nbPt2, int nbPt3, Boolean rgpt1, Boolean rgpt2, Boolean rgpt3) {
            this.idEval = idEval;
            this.idExo = idExo;
            this.nbPt1 = nbPt1;
            this.nbPt2 = nbPt2;
            this.nbPt3 = nbPt3;
            this.rgpt1 = rgpt1;
            this.rgpt2 = rgpt2;
            this.rgpt3 = rgpt3;
        }
    }

    public void calculScore() {
        this.fs = new FeuilleScore();
        for (int i = 0; i < this.listEval.size(); i++) {
            this.fs = addFeuilleScore(this.fs, calculScore(this.listEval.get(i)));
        }
    }

    public void addResEvalElt(long idEval, int idExo, int nbPt1, int nbPt2, int nbPt3, Boolean rgpt1, Boolean rgpt2, Boolean rgpt3) {
        this.listEval.add(new ResEvalElt(idEval, idExo, nbPt1, nbPt2, nbPt3, rgpt1, rgpt2, rgpt3));
    }

    public int getScore() {
        return this.fs.score;
    }

    public int getJoue() {
        return this.fs.joue;
    }

    public int getPt() {
        return this.fs.pt;
    }

    public int getReussi() {
        return this.fs.reussi;
    }

    public int getRgpt() {
        return this.fs.rgpt;
    }

}

