package com.example.exo_billard;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class ExoView extends Activity implements PopupMenu.OnMenuItemClickListener {

	
	private TapisView tapis = null ;
    
    int lock=1;
    int nv_exo=1;
    int affich=1;
    int exoEnCours=0;
    int nbExo=0;
    int livret=-1;
	int inverse=0;
	int fav =0;

	public String LMouches = "";
	public String LCadres = "";
	public String Couleurs = "";
	public boolean Soluce=true;
	public boolean Symetrie=true;

	// creattion d un nouvel exo
	Exo exo = new Exo();
	private List<Integer> listExo = new ArrayList<>();

    Button bPlus ;
    Button bMoins ;
    Button bAffich ;
    Button bComm ;
    Button bTypo ;
    Button bPrev ;
    Button bNext ;
    Button bFiltre ;
	Button bOptions ;
	Button bVerso ;
	Button bAlea ;
	Button bFav ;

	SqlBillardHelper db;

    MotsCles tags=new MotsCles();
    private View vTypo=null;
    private int filtre=0;
	SharedPreferences  sharedPreferences ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//---------Récupère informations------------
			livret = extras.getInt("livret_sel");
		}
		else livret=-1;
	    tags.creaListMotsCles(1);

		db = new SqlBillardHelper(this);
        // mAj Liste exo

		sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		readPref();

		majListExo();
		if (extras != null) {
			if (extras.getInt("exo_sel")>=0) {
				if (listExo.contains(extras.getInt("exo_sel")))
						exoEnCours = listExo.indexOf(extras.getInt("exo_sel"));
			} else exoEnCours=0;
			//---------Récupère informations------------

		}
		else exoEnCours=0;
		Log.d("Affichage Exo", String.valueOf(exoEnCours));

		// Passer la fen�tre en fullscreen == cacher la barre de notification
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        // Recuperation de la taille de l'ecran
        setContentView(R.layout.exoview);
		DisplayMetrics metrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int largEcran = metrics.heightPixels;
        int taille_barre=0;  
		Resources resources = this.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
		   taille_barre= resources.getDimensionPixelSize(resourceId);
		}
		int longEcran = metrics.widthPixels;

		
		// Calcul de la taille des �l�ments
		int largButton=(largEcran)*10/100;
		int largTapis=(int) Math.min(largEcran-largButton, longEcran*52.60/100);
		int longTapis=(int) (largTapis*190.1/100);

		//Ajout Positionnement des boutons
	    RelativeLayout lL1 = new RelativeLayout(this);
        RelativeLayout.LayoutParams lL1Params = new RelativeLayout.LayoutParams(longEcran,largButton);
        lL1Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lL1.setLayoutParams(lL1Params);
		lL1.setBackgroundColor(R.color.textColor);

        //Bouton Menus
        bOptions = new Button(this);
        bOptions.setId(1001);
        bOptions.setBackgroundResource(R.drawable.ic_action_overflow);
        bOptions.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PopupMenu popupMenu = new PopupMenu(ExoView.this, v);
				popupMenu.setOnMenuItemClickListener(ExoView.this);
				if (lock == 1)
					popupMenu.inflate(R.menu.menu_detail);
				else
					popupMenu.inflate(R.menu.menu_detail_mod);

				popupMenu.show();
			}
		});
        RelativeLayout.LayoutParams pOptions = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
		pOptions.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		bOptions.setLayoutParams(pOptions);
		lL1.addView(bOptions);

		//Bouton Favoris
		bFav = new Button(this);
		bFav.setId(1013);
		bFav.setBackgroundResource(R.drawable.ic_action_fav_border);
		bFav.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
                fav=exo.getNote();
				if (fav == 0) {
					fav = 1;
					bFav.setBackgroundResource(R.drawable.ic_action_fav_full);
				}
				else {
					fav = 0;
					bFav.setBackgroundResource(R.drawable.ic_action_fav_border);
				}
				exo.setNote(fav);
				db.setFav(exo);

			}
		});
		RelativeLayout.LayoutParams pFav = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
		pFav.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

		// pFav.addRule(RelativeLayout.RIGHT_OF,bOptions.getId());
		bFav.setLayoutParams(pFav);
		lL1.addView(bFav);


		//Bouton Commentaire
        bComm = new Button(this);
        bComm.setId(1002);
		bComm.setVisibility(View.INVISIBLE);
        bComm.setBackgroundResource(R.drawable.ic_action_view_as_list);
        bComm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				affichComm();
			}
		});
        RelativeLayout.LayoutParams pComm = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pComm.addRule(RelativeLayout.RIGHT_OF,bFav.getId());
        pComm.leftMargin=largButton;
        bComm.setLayoutParams(pComm);
        lL1.addView(bComm);

		//Bouton verso
		bVerso = new Button(this);
		bVerso.setId(1010);
		bVerso.setBackgroundResource(R.drawable.ic_action_inverse);
		bVerso.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				inverse();
			}
		});
		bVerso.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams pVerso = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
		pVerso.leftMargin=largButton;
		pVerso.addRule(RelativeLayout.RIGHT_OF, bFav.getId());
		bVerso.setLayoutParams(pVerso);
		lL1.addView(bVerso);

		//Bouton Typologie
        bTypo = new Button(this);
        bTypo.setId(1003);
        bTypo.setBackgroundResource(R.drawable.ic_action_labels);
        bTypo.setOnClickListener(new View.OnClickListener() {
                                  public void onClick(View v)   {affichTypo(); } } );
        RelativeLayout.LayoutParams pTypo = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pTypo.leftMargin=largButton/2;
        pTypo.addRule(RelativeLayout.RIGHT_OF, bComm.getId());
        bTypo.setLayoutParams(pTypo);
        lL1.addView(bTypo);

        //Bouton Affich
        bAffich = new Button(this);
        bAffich.setId(1004);
        bAffich.setBackgroundResource(R.drawable.ic_action_visibility_off);
        bAffich.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) { affichTrajet();  } } );
        RelativeLayout.LayoutParams pAffich = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pAffich.leftMargin=largButton/3;
        pAffich.addRule(RelativeLayout.RIGHT_OF, bTypo.getId());
        bAffich.setLayoutParams(pAffich);
        lL1.addView(bAffich);


        //Bouton Filtre
        bFiltre = new Button(this);
        bFiltre.setId(1005);
        bFiltre.setBackgroundResource(R.drawable.ic_action_search);
        bFiltre.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				filtre();
			}
		});
        RelativeLayout.LayoutParams pFiltre = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pFiltre.addRule(RelativeLayout.RIGHT_OF,bAffich.getId());
        pFiltre.leftMargin=largButton*2;
        bFiltre.setLayoutParams(pFiltre);
        lL1.addView(bFiltre);

        //Bouton Prev
        bPrev = new Button(this);
        bPrev.setId(1006);
        bPrev.setBackgroundResource(R.drawable.ic_action_previous_item);
        bPrev.setOnClickListener(new View.OnClickListener() {
                                  public void onClick(View v) { actionPrev(); } } );
        RelativeLayout.LayoutParams pPrev = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pPrev.leftMargin=largButton;
        pPrev.addRule(RelativeLayout.RIGHT_OF, bFiltre.getId());
        bPrev.setLayoutParams(pPrev);
        lL1.addView(bPrev);

        //Bouton Next
        bNext = new Button(this);
        bNext.setId(1007);
        bNext.setBackgroundResource(R.drawable.ic_action_next_item);
        bNext.setOnClickListener(new View.OnClickListener() {
                                  public void onClick(View v) { actionNext(); } });
        RelativeLayout.LayoutParams pNext = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pNext.leftMargin=largButton/3;
        pNext.addRule(RelativeLayout.RIGHT_OF, bPrev.getId());
        bNext.setLayoutParams(pNext);
        lL1.addView(bNext);

		//Bouton Aleat
		bAlea = new Button(this);
		bAlea.setId(1011);
		bAlea.setBackgroundResource(R.drawable.ic_action_shuffle);
		bAlea.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) { actionShuff(); } });
		RelativeLayout.LayoutParams pAlea = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
		pAlea.leftMargin=largButton/3;
		pAlea.addRule(RelativeLayout.RIGHT_OF, bNext.getId());
		bAlea.setLayoutParams(pAlea);
		lL1.addView(bAlea);

		//Bouton Add
        bPlus = new Button(this);
        bPlus.setId(1008);
        bPlus.setBackgroundResource(R.drawable.ic_menu_add);
        bPlus.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addEmpl();
			}
		});
        bPlus.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams pPlus = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
         pPlus.addRule(RelativeLayout.RIGHT_OF,bAffich.getId());
        pPlus.leftMargin=largButton*3;
        bPlus.setLayoutParams(pPlus);
        lL1.addView(bPlus);

        //Bouton Supp
        bMoins = new Button(this);
        bMoins.setId(1009);
        bMoins.setBackgroundResource(R.drawable.ic_menu_remove);
        bMoins.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				enlevEmpl();
			}
		});
        bMoins.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams pMoins = new RelativeLayout.LayoutParams(largButton-5,largButton-5);
        pMoins.leftMargin=largButton/3;
        pMoins.addRule(RelativeLayout.RIGHT_OF, bPlus.getId());
        bMoins.setLayoutParams(pMoins);
        lL1.addView(bMoins);


 	    LinearLayout lL2 = new LinearLayout(this);
	    lL2.setOrientation(LinearLayout.VERTICAL);
	    LayoutParams Ll2Params = new LayoutParams( longTapis ,largTapis);
        lL2.setLayoutParams(Ll2Params);
        lL2.setY((longEcran-longTapis)/2);
        lL2.setY((largEcran - largTapis - largButton) / 2);

        // Creation du "tapis"
		tapis = new TapisView(this,1);
		tapis.setLMouches(LMouches);
		tapis.setLCadres(LCadres);
		tapis.setCouleurs(Couleurs);
		if (Soluce==true){
			bAffich.setBackgroundResource(R.drawable.ic_action_visibility_off);
			affich=1;
			tapis.setAffich(1);
			tapis.setVisuComm(1);
		}
		else {
			affich=0;
			bAffich.setBackgroundResource(R.drawable.ic_action_visibility_on);
			tapis.setEmplSelect(0);
			tapis.setAffich(0);
			tapis.setVisuComm(0);
		}

		lL2.addView(tapis);
		lL1.setBackgroundColor(R.color.btnBackgrnd);
		((LinearLayout) findViewById(R.id.Llayout01)).addView(lL1);
		((LinearLayout) findViewById(R.id.Llayout01)).addView(lL2);
		
		//Lecture exo 0 
		readExo(exoEnCours);


	}

	private void majListExo() {
		// Recup donn�es Exos.
		listExo=db.getListExo( tags ,livret);
		nbExo=listExo.size();
		Toast.makeText(ExoView.this,nbExo +" exos",Toast.LENGTH_SHORT).show();
	}

	private void readPref() {
		Couleurs = sharedPreferences.getString("prefCouleur", "blue");
		LMouches = sharedPreferences.getString("prefMouches", "sans");
		LCadres = sharedPreferences.getString("prefCadres", "plein");
		Soluce = sharedPreferences.getBoolean("prefSoluce", true);
		Symetrie = sharedPreferences.getBoolean("prefSymetrie", true);
	}
	private void readExo(int numExo) {

        if (nbExo==0) {
			exo = new Exo();
			nv_exo = 1;
			if (livret>-1) exo.setLivret(livret);
		}
        else {
			exo = db.getExo(listExo.get(numExo));
			nv_exo = 0;
		}
		fav=exo.getNote();
		if (fav == 0) {
			bFav.setBackgroundResource(R.drawable.ic_action_fav_border);
		}
		else {
			bFav.setBackgroundResource(R.drawable.ic_action_fav_full);
		}
		if (Soluce==true){
			bAffich.setBackgroundResource(R.drawable.ic_action_visibility_off);
			affich=1;
			tapis.setAffich(1);
			tapis.setVisuComm(1);
		}
		else {
			affich=0;
			bAffich.setBackgroundResource(R.drawable.ic_action_visibility_on);
			tapis.setEmplSelect(0);
			tapis.setAffich(0);
			tapis.setVisuComm(0);
		}
		tapis.setBille1(exo.getB(0));
		tapis.setBille2(exo.getB(1));
		tapis.setBille3(exo.getB(2));
		tapis.setComment(db.getTitreLivret(exo.getLivret()) + "\n" + exo.getComment());
		tapis.setXComm(exo.getXComm());
		tapis.setYComm(exo.getYComm());
		drawTapis();
	}
    
	////////////
	// ACTION //
	////////////

	//Lock Unlock ecran
	private  void actionUnLock() {
		lock=0;
		inverse=0;
		tapis.setInverse(inverse);
	    bPlus.setVisibility(View.VISIBLE);
	    bMoins.setVisibility(View.VISIBLE);
	    bFiltre.setVisibility(View.INVISIBLE);
	    bPrev.setVisibility(View.INVISIBLE);
	    bNext.setVisibility(View.INVISIBLE);
		bComm.setVisibility(View.VISIBLE);
		bAffich.setBackgroundResource(R.drawable.ic_action_visibility_off);
		bVerso.setVisibility(View.INVISIBLE);
		bAlea.setVisibility(View.INVISIBLE);
	   	affich=1;
    	tapis.setAffich(1);
        tapis.setLock(lock);
		tapis.setVisuComm(1);
		drawTapis();
		}

    private  void actionLock() {
		lock=1;
	    bPlus.setVisibility(View.INVISIBLE);
	    bMoins.setVisibility(View.INVISIBLE);
	    bFiltre.setVisibility(View.VISIBLE);
	    bPrev.setVisibility(View.VISIBLE);
	    bNext.setVisibility(View.VISIBLE);
		bComm.setVisibility(View.INVISIBLE);
		bVerso.setVisibility(View.VISIBLE);
		bAlea.setVisibility(View.VISIBLE);
		readExo(exoEnCours);
		tapis.setLock(lock);
		drawTapis();
	}

 	// Revenir exo pr�c�dent
	private  void actionPrev() {
	if (exoEnCours>0) {
		exoEnCours--;
	}
	else {
		exoEnCours = nbExo - 1;
	}
	tapis.setEmplSelect(0);
	readExo(exoEnCours);
	drawTapis();
	} 

	private void inverse() {
		if ( lock == 1 ) {
			inverse ++;
			if (inverse==4) inverse=0;
		}
		else {
			inverse=0;
		}
		tapis.setInverse(inverse);
		drawTapis();
	}
	// Aller exo suivant
	private  void actionNext() {
	if (exoEnCours<nbExo-1) {
		exoEnCours++;
	}
	else {
		exoEnCours=0;
	}
	tapis.setEmplSelect(0);
	readExo(exoEnCours);
	drawTapis();
	}

	// Aller exo au hasard
	private  void actionShuff() {
		Random randomGenerator = new Random();
		if (nbExo>0) {
			exoEnCours = randomGenerator.nextInt(nbExo);
		}
		inverse=randomGenerator.nextInt(5)-1;
		tapis.setInverse(inverse);
		tapis.setEmplSelect(0);
		readExo(exoEnCours);
		drawTapis();
	}

	// Ajout emplacement bille s�lectionn�e
	private void addEmpl() {
	if (lock!=1 && affich==1) { 
		int billeSelect=tapis.getBilleSelect();
		if (billeSelect !=9) {
			int dernElement=exo.getB(billeSelect).getNbEmpl();
			if (dernElement>1){
				exo.getB(billeSelect).setType(dernElement-1, "POINT");
    		}
			exo.getB(billeSelect).creaEmplVide();
			exo.getB(billeSelect).setType(dernElement,"CONTOUR");
			if (exo.getB(billeSelect).getX(dernElement-1)<50) {
				exo.getB(billeSelect).setX(dernElement,exo.getB(billeSelect).getX(dernElement-1)+(int)Constantes.rayonSelect);
			}
			else {
				exo.getB(billeSelect).setX(dernElement,exo.getB(billeSelect).getX(dernElement-1)-(int)Constantes.rayonSelect);
			}
			if (exo.getB(billeSelect).getY(dernElement-1)<50) {
				exo.getB(billeSelect).setY(dernElement,exo.getB(billeSelect).getY(dernElement-1)+(int)Constantes.rayonSelect);
			}
			else {
				exo.getB(billeSelect).setY(dernElement, exo.getB(billeSelect).getY(dernElement - 1) - (int) Constantes.rayonSelect);
			}
			tapis.setEmplSelect(dernElement);
			drawTapis();
   	    }
		else {
			Toast.makeText(ExoView.this,"S�lectionnez une bille",Toast.LENGTH_LONG).show();
		}
	}	               
	}    

	// Enlev emplacement bille s�lectionn�e
	private void enlevEmpl() {
	if (lock!=1 && affich==1) { 
		int billeSelect = tapis.getBilleSelect();
		if (billeSelect !=9) {
			int emplSelect=tapis.getEmplSelect();
			int dernElement=exo.getB(billeSelect).getNbEmpl();
			if (emplSelect==dernElement-1 && emplSelect!=0) {
				tapis.setEmplSelect(emplSelect - 1);
			}
			exo.getB(billeSelect).enleveEmpl();
			drawTapis();
   		}
		else {
			Toast.makeText(ExoView.this,"Sélectionnez une bille",Toast.LENGTH_LONG).show();
		}
	}               
	}	    
	
	public void setLivret(int l) {
		livret=l;
	}

	private void affichComm()
	{
		// Affichage commentaire
		if (lock != 1) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			//LayoutInflater
			LayoutInflater inflater = LayoutInflater.from(this);
			final View dialogview = inflater.inflate(R.layout.commentaire,null);
			//AlertDialog
			alert.setView(dialogview);
			EditText txt = (EditText) dialogview.findViewById(R.id.alertCommentaire);
			txt.setText(exo.getComment());
			txt.setTextColor(Color.rgb(180,180,180));
            txt.setBackgroundColor(Color.rgb(29,72,81));
            Spinner spinner = (Spinner) dialogview.findViewById(R.id.ListLivret);
			List livrets= db.getListlivret();
			ArrayList<String> titres= new ArrayList<String>();
			for(int i=0; i<livrets.size(); i++) {
				titres.add(db.getTitreLivret((int) livrets.get(i)));
			}
			String[] array = titres.toArray(new String[0]);
			ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, titres);
			spinner.setAdapter(adapter);
			spinner.setEnabled(false);
			spinner.setBackgroundColor(Color.rgb(29,72,81));
			int id=livrets.indexOf(exo.getLivret());

			if (id>=0) spinner.setSelection(id);
			spinner.setEnabled(true);
			txt.setEnabled(true);
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int idx) {
					EditText txt = (EditText) dialogview.findViewById(R.id.alertCommentaire);
					Spinner spinner = (Spinner) dialogview.findViewById(R.id.ListLivret);
					exo.setComment(txt.getText().toString());

					List livrets= db.getListlivret();
					exo.setLivret((int) livrets.get(spinner.getSelectedItemPosition()));
					tapis.setComment(db.getTitreLivret(exo.getLivret()) + "\n" + exo.getComment());
					drawTapis();
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int idx) {
				}
			});
			alert.show();
			drawTapis();
		}
	}

	private void affichTypo()
	{
	// Affichage Typologie
         
     	 AlertDialog.Builder alert = new AlertDialog.Builder(this);  
         if (lock!=1) alert.setTitle("Modifier la typo de l'exo");  
         else alert.setTitle("Typo de l'exo");  
         //LayoutInflater
         LayoutInflater inflater = LayoutInflater.from(this);  
    		// Il faudra donc un layout alert.xml sous res/layout avec un EditText ayant un id a EditText01
    		// Mais si vous etes ici, vous devez deja conna�tre tout cela
        vTypo = inflater.inflate(R.layout.typologie,null);
         //AlertDialog  
    	 alert.setView(vTypo); 
    	 if (lock!=1) {
    		 alert.setPositiveButton("OK", new  DialogInterface.OnClickListener(){  
    			 public void onClick(DialogInterface dialog, int idx) {  
    				 
    			 }});
    		 alert.setNegativeButton("Cancel",  new  DialogInterface.OnClickListener(){  
    			 public void onClick(DialogInterface dialog, int idx) {  
    			     		 }});
    	 }
    	 else {
    		 alert.setPositiveButton("OK", new  DialogInterface.OnClickListener(){  
    			 public void onClick(DialogInterface dialog, int idx) {  
    			 }});
    	 }
    	 alert.show();
    	 tagColor();
 	 }

	private void filtre()
	{
	// Affichage Typologie
         filtre=1;
     	 AlertDialog.Builder alert = new AlertDialog.Builder(this);  
         alert.setTitle("Filtrer les exercices");  
         //LayoutInflater
         LayoutInflater inflater = LayoutInflater.from(this);  
         vTypo = inflater.inflate(R.layout.typologie,null);    
         //AlertDialog 
    	 alert.setView(vTypo); 
    	 alert.setPositiveButton("OK", new  DialogInterface.OnClickListener(){  
    		public void onClick(DialogInterface dialog, int idx) {  
    			filtre=0;
				if ( listExo.contains(exo.getId() )) {
					exoEnCours=listExo.indexOf(exo.getId());
				}
				else exoEnCours=0;
				readExo(exoEnCours);
				drawTapis();
    			 }});
    	 alert.show();
    	 tagColorFiltre();
	 }

	private void drawTapis() {
		Canvas canvas=tapis.mSurfaceHolder.lockCanvas();
		if (canvas != null) {
			tapis.setLMouches(LMouches);
			tapis.setLCadres(LCadres);
			tapis.setCouleurs(Couleurs);
			tapis.Draw(canvas);
			tapis.mSurfaceHolder.unlockCanvasAndPost(canvas);
		}

	}

	// Affichage du trajet des billes (solution) ou seulement du premier emplacement
    private void affichTrajet() 
    {
    	if (affich==1) {
    		affich=0;
			bAffich.setBackgroundResource(R.drawable.ic_action_visibility_on);
    		tapis.setEmplSelect(0);
    		tapis.setAffich(0);
			tapis.setVisuComm(0);
    		}
    	else {
			bAffich.setBackgroundResource(R.drawable.ic_action_visibility_off);
    		affich=1;
    		tapis.setAffich(1);
			tapis.setVisuComm(1);
    		}
		drawTapis();
    }
    		
    //Enregistrement de l'exercice
    private void saveExo() {
    	if (lock!=1 && nv_exo==1) {
			exo.setXComm(tapis.getXComm());
			exo.setYComm(tapis.getYComm());

			db.createExo(exo);
        	majListExo();
        	exoEnCours=nbExo-1;
        	readExo(exoEnCours);
        	actionLock();
    	}
        if (lock!=1 && nv_exo==0) {
			exo.setXComm(tapis.getXComm());
			exo.setYComm(tapis.getYComm());
			db.modExo(exo);
            readExo(exoEnCours);
            actionLock();
        }
    	}
    // Suppression de l'exercice
   private void deleteExo() {
    	if (exo.getId()!=-1) {
    		db.deleteExo(exo.getId());
           	majListExo();
           	exoEnCours--;
    	   	readExo(exoEnCours);
    	   	actionLock();
    	}
    	}       

   // menu Typologie
   
   // gestion affichage en cas de filtre
   public void tagColorFiltre() {
		TextView t;
       for (Map.Entry<String, Integer> entree : tags.motscles.entrySet()) {
           t= (TextView) vTypo.findViewWithTag(entree.getKey());
           t.setTextColor(changeColorTag(tags.getMotCle(entree.getKey())));
       }
       	majListExo();
	   }


// gestion affichage en cas de tag de l'exo
   public void tagColor() {
		TextView t;
       for (Map.Entry<String, Integer> entree : exo.motsCles.motscles.entrySet()) {
           t= (TextView) vTypo.findViewWithTag(entree.getKey());
           t.setTextColor(changeColorTag(exo.motsCles.getMotCle(entree.getKey())));
       }
	   }
		
   // recup couleur du tag
   public int changeColorTag(int typ){
	   if (typ==-1) return Color.RED;
	   else if (typ==1) return Color.GREEN;
	   else return Color.LTGRAY;
   }
    // Click dur les tags en fonction de si c'est pour filtre ou pour chgt tag exo
    public void clickTypo(View v) {
        String mot=(String) v.getTag() ;

        if (filtre==1) {
            tags.changeMotCle(mot);
            tagColorFiltre();
        }
        else if (lock!=1) {
            exo.motsCles.changeMotCle(mot);
            tagColor();
        }
    }

// menu Option
public boolean onMenuItemClick(MenuItem item) {
    switch (item.getItemId()) {
        case R.id.newExo:
            exo= new Exo();
			tapis.setBille1(exo.getB(0));
            tapis.setBille2(exo.getB(1));
            tapis.setBille3(exo.getB(2));
			if (livret>-1) exo.setLivret(livret);
            nv_exo=1;
			actionUnLock();
			return true;
        case R.id.modExo:
			actionUnLock();
            return true;
        case R.id.copyExo:
            nv_exo=1;
            actionUnLock();
            return true;
        case R.id.delExo:
            deleteExo();
            return true;
        case R.id.undoExo:
            actionLock();
            return true;
        case R.id.saveExo:
            saveExo();
            return true;
		case R.id.retourMenu:
			this.finish();
			return true;
    }
    return true;
}

}

