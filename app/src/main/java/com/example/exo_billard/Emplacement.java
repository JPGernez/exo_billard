package com.example.exo_billard;
/* Definition d un emplacement:
	coordonnees X,Y et type de bille */

import java.io.Serializable;

public class Emplacement implements Serializable {
	/** Coordonnee sur la longueur 0-100**/
	private float x;
	/** Coordonnee sur la largeur 0-100**/
	private float y;
	/** Type (1:point, 2:cercle plein, 3:contours **/
	private String Type = null;   


	public void setX(float rX) {
		if (rX<0)
			x=0;
		else if (rX>100) 
			x=100;
		else 
			x = rX;
	}

    public void setY(float rY) {
		if (rY<0)
			y=0;
		else if (rY>100) 
			y=100;
		else 
			y = rY;
	}
    
  public void setType(String t) {
		this.Type = t;
	}
		
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public String getType() {
		return Type;
	}

	public Emplacement(String pT,float pX, float pY) {
		this.Type = pT;
		this.setX(pX);
		this.setY(pY);
	}
	
}
