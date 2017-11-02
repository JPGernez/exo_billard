package com.example.exo_billard;
/* Definition d une bille:
    couleur puis serie d emplacement */

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import android.graphics.Color;
import java.io.Serializable;

public class Bille implements Serializable {

	// Couleur de la boule
	private int mCouleur= Color.WHITE;
	//liste emplacement 
	private List<Emplacement> Emplacements =new CopyOnWriteArrayList<>();

	// ajout d un nouvel emplacement
	public void creaEmplVide() {
        this.Emplacements.add(new Emplacement("PLEIN",0,0));
	}

    //Suppression du dernier emplacement
	public void enleveEmpl () {
		int nbElement=this.Emplacements.size();
		if (nbElement>1) { 
		    this.Emplacements.remove( nbElement-1);
            if (nbElement>2) {
			    this.setType( nbElement-2, "CONTOUR");
            }
        }
	}

    // Recuperation du nombre d emplacement
	public int getNbEmpl() {
        return this.Emplacements.size();
	}

    // Recuperation de la couleur de la bille
	public int getCouleur() {
        return mCouleur;
    }

    // Modification de la couleur de la bille
	public void setCouleur(int color) {
        this.mCouleur =  color;
		}
	
	// Recuperation de la liste des emplacements
	public List<Emplacement> getEmpl() {
        return Emplacements;
	}

//	public void setEmpl (List<Emplacement> e) {
//		this.Emplacements=e;
//	}

    // Recuperation de la position X du ieme emplacement
	public float getX (int i) {
        return this.Emplacements.get(i).getX();
	}

    // Modification de la position X du ieme emplacement (decalage de la position d un rayon de la bande)
    public void setX (int i, float x) {
    	x=Math.max(Constantes.rayon/2, x);
    	x=Math.min(100-(Constantes.rayon/2), x);
    	this.Emplacements.set(i, new Emplacement(this.getType(i),x,this.getY(i)));
    }

    // Recuperation de la position Y du ieme emplacement
    public float getY (int i) {
        return this.Emplacements.get(i).getY();
	}

    // Modification de la position Y du ieme emplacement (decalage de la position d un rayon de la bande)
    public void setY (int i, float y) {
    	y=Math.max(Constantes.rayon, y);
    	y=Math.min(100-Constantes.rayon, y);
    	this.Emplacements.set(i, new Emplacement(this.getType(i),this.getX(i),y));
    }

    // Recuperation du type d'emplacement au ieme emplacement (plein, point, contour)
	public String getType (int i) {
        return this.Emplacements.get(i).getType();
	}

    // Modification du type d'emplacement au ieme emplacement (plein, point, contour)
    public void setType (int i, String t) {
    	this.Emplacements.set(i, new Emplacement(t,this.getX(i),this.getY(i)));
    	this.Emplacements.get(i).setType(t);
    }
}
