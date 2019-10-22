package com.example.exo_billard;

import java.io.Serializable;

/* Definition d un exercice
   Numero d exercice
   3 billes
   Mots cles
   Commentaires
   NOTE
   Livret
*/

public class Exo implements Serializable {

	private int exo_id ;
 	private String commentaire = "";
    private float xComm = 25;
    private float yComm= 25;
	private int note = 0;

	private Bille[] B = new Bille[3];
    private int liv_id = -1;
	private int fav=0;
    public MotsCles motsCles= new MotsCles();

	// Creation d un exercice vide
	public Exo() {
    	exo_id=-1;
    	commentaire="" ;
    	note = 0;
		liv_id=-1;
		motsCles.creaListMotsCles(0);

        B[0]=new Bille();
		B[1]=new Bille();
		B[2]=new Bille();
		B[0].setCouleur(Constantes.couleurBBlanche);
		B[0].creaEmplVide();
		B[0].setType(0,"PLEIN");
		B[0].setX(0,25);
		B[0].setY(0,62.84f);
		B[1].setCouleur(Constantes.couleurBJaune);
		B[1].creaEmplVide();
		B[1].setType(0,"PLEIN");
		B[1].setX(0,25);
		B[1].setY(0,50);
		B[2].setCouleur(Constantes.couleurBRouge);
		B[2].creaEmplVide();
		B[2].setType(0,"PLEIN");
		B[2].setX(0,75);
		B[2].setY(0,50);
	}

	// Recuperation de la bille numBille
	public Bille getB(int numBille) {
		return B[numBille];
	}

	// Modification de la bille numBille
	public void setB(int numBille, Bille b) {
		this.B[numBille] =  b;
	}

	// recuperation du numero d exercice
	public int getId() {
		return exo_id;
	}

	//Modification du numero d exercice
	public void setId(int id) {
		this.exo_id =  id;
	}

	// Modification statut favoris
	public void setFav(int n) {
		this.note =  n;
	}

	// Recuperation du commentaire
	public String getComment() {
		return commentaire;
	}

	// Modification du commentaire
	public void setComment(String c) {
		this.commentaire =  c;
	}

	// Recuperation du statut favoris
	public int getFav() {
		return this.note;
	}

	// Recuperation du livret
	public int getLivret() {
		return liv_id;
	}
	// Modification du commentaire
	public void setLivret(int l) {
		this.liv_id =  l;
	}

	// recup emplacement X commentaire
	public float getXComm () {
		return this.xComm;
	}

	public void setXComm (float x) {
		x=Math.max(0, x);
		x=Math.min(95, x);
		this.xComm = x;
	}
	public float getYComm () {
		return this.yComm;
	}

	public void setYComm (float y) {
		y = Math.max(0, y);
		y = Math.min(95, y);
		this.yComm = y;
	}
}
