package com.example.exo_billard;

import android.util.Log;
import java.util.Map;
import java.util.TreeMap;
import java.io.Serializable;
/* Liste des mots cles d un exercice  particulier ou servant a la selection */

public class MotsCles  implements Serializable  {

    //type=0 tag de l'exo
    //type=1 tag de recherche
    private int type=0;

    // MotsCles
    public Map<String , Integer> motscles =new TreeMap<>();
    public void creaListMotsCles(int t){
        type=t;
        motscles.put("CBase",0);
        //motscles.put("CFacile",0);
        //motscles.put("CDifficile",0);
        //motscles.put("CPuissant",0);
        motscles.put("CFolklo",0);
        motscles.put("CRetro",0);
        motscles.put("CEffet",0);
        motscles.put("CNat",0);
        motscles.put("CBande",0);
        motscles.put("CEchelle",0);
        motscles.put("CCoule",0);
        motscles.put("CRappel",0);
        motscles.put("CPlace",0);
        motscles.put("C3Bandes",0);
        motscles.put("CMasse",0);
        //motscles.put("CLent",0);
        motscles.put("CPlein",0);
        motscles.put("CFinesse",0);
        motscles.put("CPret",0);
        motscles.put("CBarrage",0);


    }

    // Modification de la selection d un mot cle
    // Si mots cles d un exercice alors vaut
        // 0: non
        // 1 : oui
    // Si mots cles de recherche alors  vaut
        // 0 : pas de selection
        // 1 : selection avec
        // -1 : seletion sans
    public void changeMotCle(String t){
       // Log.d("chgMtcle", "1 " + t);
        int v=motscles.get(t);
       // Log.d("chgMtcle", "2 " + String.valueOf("1"));
        if (type==0){
            if (v==1) v=0;
            else v=1;
        }
        else {
            if (v==1) v=-1;
            else if (v==-1) v=0;
            else v=1;
        }
        motscles.put(t,v);
    }

    // Recuperation de la selection ou non du mot cle
    public int getMotCle(String t){
        return motscles.get(t);
    }


  }
