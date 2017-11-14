package com.example.exo_billard;

/* Definition d un livret
   Identifiant du livret
   Nom du livret
   Auteur
   Commentaire
   Liste d exercice
*/

import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class Livret implements Serializable {

    private int livret_id;
    private String titre = "";
    private String commentaire = "";
	private String auteur = "";
	private List l_exoId = new LinkedList();

	// Creation d un livret vide
	public Livret() {
        livret_id = -1;
        commentaire = "";
        titre = "";
        auteur = "";
        l_exoId = new LinkedList();
    }

	// recuperation du numero de livret
	public int getId() {
		return livret_id;
	}

	//Modification du numero de livret
	public void setId(int id) {
        this.livret_id = id;
    }

	// Recuperation du commentaire
	public String getTitre() {
		return titre;
	}

	// Modification du commentaire
	public void setTitre(String t) {
        this.titre = t;
    }

	// Recuperation du commentaire
	public String getComment() {
		return commentaire;
	}

	// Modification du commentaire
	public void setComment(String c) {
        this.commentaire = c;
    }

	// Recuperation de l auteur
	public String getAuteur() {
		return auteur;
	}

	// Modification de l auteur
	public void setAuteur(String a) {
        this.auteur = a;
    }

	// recuperation du nombre d exercice
	public int getNbExo() {
		return this.l_exoId.size();
	}

	// recuperation du numero de l exo i
	public int getNumExo(int i) {
        return (int) this.l_exoId.get(i);
    }

	// ajout de l exo id
	public void addNumExo(int id) {
		this.l_exoId.add(id);
	}
}
