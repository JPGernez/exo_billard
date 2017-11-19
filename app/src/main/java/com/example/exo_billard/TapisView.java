package com.example.exo_billard;

// representation du billard et de l exo

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.DashPathEffect;


@SuppressLint("ClickableViewAccessibility")
public class TapisView extends SurfaceView implements SurfaceHolder.Callback {
    // Les 3 billes
	Bille[] B = new Bille[3];

	// initialisation des dimensions de la table qui seront calcules ensuite
	int largeur=0;
	int longueur=0;
	int largBande=0;
	int largSurf=0;
	int longSurf=0;
	int lock=1;
	int affich=0;
	int visuSupp=0;
	int xCentreRadar=15;
	String comment="-";
	int visucomm=1;
    float xComm=25;
	float yComm=25;
    int commSelect=0;
	int inverse=0;
    int enableclick=0;

	public String LMouches = "point";
	public String LCadres = "sans";
	public String Couleurs = "blue";



	// dimension bille
	float rayon = Constantes.rayon;


	// bille selectionnee a l'ecran
	int billeSelect = 9;
	int emplSelect= 0;

	SurfaceHolder mSurfaceHolder;
	//DrawingThread mThread;
	Paint mPaint;

	// Modification des billes en parametres
	public void setBille1(Bille bille) {
		B[0] = bille;
	}
	public void setBille2(Bille bille) {
		B[1] = bille;
	}
	public void setBille3(Bille bille) {
		B[2] = bille;
	}
	public void setComment(String c) {
		comment = c;
	}

	public void setXComm(float x) {
		xComm = x;
	}

	public void setYComm(float y) {
		yComm = y;
	}
	public float getXComm() { return xComm ; }
	public float getYComm() { return yComm ; }

	public int couleurTapis = Constantes.couleurTapisB;
	public int couleurRadar = Constantes.couleurRadarB;
	public int couleurMouche = Constantes.couleurMoucheB;
	public int couleurBande = Constantes.couleurBandeB;
	public int couleurLigne = Constantes.couleurLigneB;
	public int couleurSel = Constantes.couleurSelB;

	public void setLMouches(String lm) {
		LMouches = lm;
	}

	public void setLCadres(String lc) {
		LCadres = lc;
	}

	public void setCouleurs(String c) {
		Couleurs = c;
		if ("green".equals(Couleurs.intern())) {
			couleurBande = Constantes.couleurBandeG;
			couleurRadar = Constantes.couleurRadarG;
			couleurLigne = Constantes.couleurLigneG;
			couleurTapis = Constantes.couleurTapisG;
			couleurMouche = Constantes.couleurMoucheG;
			couleurSel = Constantes.couleurSelG;
		} else if ("blue".equals(Couleurs.intern())) {
			couleurBande = Constantes.couleurBandeB;
			couleurRadar = Constantes.couleurRadarB;
			couleurLigne = Constantes.couleurLigneB;
			couleurTapis = Constantes.couleurTapisB;
			couleurMouche = Constantes.couleurMoucheB;
			couleurSel = Constantes.couleurSelB;
		} else if ("red".equals(Couleurs.intern())) {
			couleurBande = Constantes.couleurBandeR;
			couleurRadar = Constantes.couleurRadarR;
			couleurLigne = Constantes.couleurLigneR;
			couleurTapis = Constantes.couleurTapisR;
			couleurMouche = Constantes.couleurMoucheR;
			couleurSel = Constantes.couleurSelR;
		} else {
			couleurBande = Constantes.couleurBandeNB;
			couleurRadar = Constantes.couleurRadarNB;
			couleurLigne = Constantes.couleurLigneNB;
			couleurTapis = Constantes.couleurTapisNB;
			couleurMouche = Constantes.couleurMoucheNB;
			couleurSel = Constantes.couleurSelNB;
		}
	}

	public void setInverse(int inv) { inverse=inv; }

	public void setVisuComm(int v) {
		visucomm = v;
	}

	// lock de l exo
	public void setLock(int l) {
		lock=l;
	}

	// recuperation de la bille selectionné a l ecran
	public int getBilleSelect() {
		return billeSelect;
		}

	// recuperation de l emplacement de la bille selectionné a l ecran
	public int getEmplSelect() {
		return emplSelect;
		}

	// Modifictaion de l empalcement selectionne
	public void setEmplSelect(int e) {
		emplSelect=e;
		}

	// Modification de l affichage de la solution
	public void setAffich(int a) {
		affich=a;
		}


	public TapisView(Context pContext, int ena) {
		super(pContext);
        enableclick=ena;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
	//	mThread = new DrawingThread();

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
	  	this.setFocusable( true );
        this.requestFocus();
		// Activation du touch mode
        this.setFocusableInTouchMode( true );
		}

	

	public void Draw(Canvas pCanvas) {

		//rayon du dessin pour l emplacement (rayon de la bille ou du point)
		float r;
		// coordonnees de l'emplacement precedent
		float x_prec;
		float y_prec;
		//numero de bille et d'emplacement � d�ssiner
		int numBille;
		int numEmpl;

		// Dessiner le fond de l'�cran en premier
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(couleurBande);
		mPaint.setStyle(Paint.Style.FILL);
		pCanvas.drawPaint(mPaint);
		//dessin tapis
		mPaint.setColor(couleurTapis);
		pCanvas.drawRect(largBande, largBande, longSurf + largBande, largSurf + largBande, mPaint);
		
		//dessin mouches
		mPaint.setColor(couleurMouche);
		// petites bandes
		pCanvas.drawRect(largBande / 3, largBande - largBande / 12, largBande * 2 / 3, largBande + largBande / 12, mPaint);
		pCanvas.drawRect(largBande/3,largBande+largSurf/4-largBande/12,largBande*2/3,largBande+largSurf/4+largBande/12,mPaint);
		pCanvas.drawRect(largBande / 3, largBande + 2 * largSurf / 4 - largBande / 12, largBande * 2 / 3, largBande + 2 * largSurf / 4 + largBande / 12, mPaint);
		pCanvas.drawRect(largBande/3,largBande+3*largSurf/4-largBande/12,largBande*2/3,largBande+3*largSurf/4+largBande/12,mPaint);
		pCanvas.drawRect(largBande/3,largBande+largSurf-largBande/12,largBande*2/3,largBande+largSurf+largBande/12,mPaint);
		pCanvas.drawRect(longueur - largBande / 3, largBande - largBande / 12, longueur - largBande * 2 / 3, largBande + largBande / 12, mPaint);
		pCanvas.drawRect(longueur - largBande / 3, largBande + largSurf / 4 - largBande / 12, longueur - largBande * 2 / 3, largBande + largSurf / 4 + largBande / 12, mPaint);
		pCanvas.drawRect(longueur - largBande / 3, largBande + 2 * largSurf / 4 - largBande / 12, longueur - largBande * 2 / 3, largBande + 2 * largSurf / 4 + largBande / 12, mPaint);
		pCanvas.drawRect(longueur - largBande / 3, largBande + 3 * largSurf / 4 - largBande / 12, longueur - largBande * 2 / 3, largBande + 3 * largSurf / 4 + largBande / 12, mPaint);
		pCanvas.drawRect(longueur - largBande / 3, largBande + largSurf - largBande / 12, longueur - largBande * 2 / 3, largBande + largSurf + largBande / 12, mPaint);

		// grandes bandes
		pCanvas.drawRect(largBande - largBande / 12, largBande / 3, largBande + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + longSurf / 8 - largBande / 12, largBande / 3, largBande + longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + 2 * longSurf / 8 - largBande / 12, largBande / 3, largBande + 2 * longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + 3 * longSurf / 8 - largBande / 12, largBande / 3, largBande + 3 * longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + 4 * longSurf / 8 - largBande / 12, largBande / 3, largBande + 4 * longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + 5 * longSurf / 8 - largBande / 12, largBande / 3, largBande + 5 * longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + 6 * longSurf / 8 - largBande / 12, largBande / 3, largBande + 6 * longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + 7 * longSurf / 8 - largBande / 12, largBande / 3, largBande + 7 * longSurf / 8 + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande + longSurf - largBande / 12, largBande / 3, largBande + longSurf + largBande / 12, largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande - largBande / 12, largeur - largBande / 3, largBande + largBande / 12, largeur - largBande * 2 / 3, mPaint);
		pCanvas.drawRect(largBande+longSurf/8-largBande/12,largeur-largBande/3,largBande+longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+2*longSurf/8-largBande/12,largeur-largBande/3,largBande+2*longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+3*longSurf/8-largBande/12,largeur-largBande/3,largBande+3*longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+4*longSurf/8-largBande/12,largeur-largBande/3,largBande+4*longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+5*longSurf/8-largBande/12,largeur-largBande/3,largBande+5*longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+6*longSurf/8-largBande/12,largeur-largBande/3,largBande+6*longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+7*longSurf/8-largBande/12,largeur-largBande/3,largBande+7*longSurf/8+largBande/12,largeur-largBande*2/3,mPaint);
		pCanvas.drawRect(largBande+longSurf-largBande/12,largeur-largBande/3,largBande+longSurf+largBande/12,largeur-largBande*2/3,mPaint);

		//tapis
		pCanvas.drawRect(largBande+longSurf/4-largBande/6,largBande+largSurf/2-largBande/6,largBande+longSurf/4+largBande/6,largBande+largSurf/2+largBande/6,mPaint);
		pCanvas.drawRect(largBande+longSurf/4-largBande/6,(int) (largBande+largSurf/2-largSurf*12.84/100-largBande/6),largBande+longSurf/4+largBande/6,(int) (largBande+largSurf/2-largSurf*12.84/100+largBande/6),mPaint);
		pCanvas.drawRect(largBande+longSurf/4-largBande/6,(int) (largBande+largSurf/2+largSurf*12.84/100-largBande/6),largBande+longSurf/4+largBande/6,(int) (largBande+largSurf/2+largSurf*12.84/100+largBande/6),mPaint);
		pCanvas.drawRect(largBande+longSurf/2-largBande/6,largBande+largSurf/2-largBande/6,largBande+longSurf/2+largBande/6,largBande+largSurf/2+largBande/6,mPaint);
		pCanvas.drawRect(largBande + longSurf * 3 / 4 - largBande / 6, largBande + largSurf / 2 - largBande / 6, largBande + longSurf * 3 / 4 + largBande / 6, largBande + largSurf / 2 + largBande / 6, mPaint);

        //ligne de cadre
		mPaint.setColor(couleurLigne);
		mPaint.setPathEffect(null);
		if ("point".equals(LCadres.intern())) {
			mPaint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0));
		}
		if (!"sans".equals(LCadres.intern())) {
			pCanvas.drawLine(largSurf / 3 + largBande, largBande, largSurf / 3 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(longSurf - largSurf / 3 + largBande, largBande, longSurf - largSurf / 3 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(largBande, largSurf / 3 + largBande, longSurf + largBande, largSurf / 3 + largBande, mPaint);
			pCanvas.drawLine(largBande, 2 * largSurf / 3 + largBande, longSurf + largBande, 2 * largSurf / 3 + largBande, mPaint);
		}
		mPaint.setPathEffect(null);

        //ligne de mouche
		if ("point".equals(LMouches.intern())) {
			mPaint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0));
		}
		if (!"sans".equals(LMouches.intern())) {
			pCanvas.drawLine(largSurf / 4 + largBande, largBande, largSurf / 4 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(2 * largSurf / 4 + largBande, largBande, 2 * largSurf / 4 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(3 * largSurf / 4 + largBande, largBande, 3 * largSurf / 4 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(4 * largSurf / 4 + largBande, largBande, 4 * largSurf / 4 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(5*largSurf/4+largBande, largBande, 5*largSurf/4+largBande, largSurf+largBande, mPaint);
			pCanvas.drawLine(6*largSurf/4+largBande, largBande, 6*largSurf/4+largBande, largSurf+largBande, mPaint);
			pCanvas.drawLine(7 * largSurf / 4 + largBande, largBande, 7 * largSurf / 4 + largBande, largSurf + largBande, mPaint);
			pCanvas.drawLine(largBande, largSurf / 4 + largBande, longSurf + largBande, largSurf / 4 + largBande, mPaint);
			pCanvas.drawLine(largBande, 2*largSurf / 4 + largBande, longSurf + largBande, 2*largSurf / 4 + largBande, mPaint);
			pCanvas.drawLine(largBande, 3*largSurf / 4 + largBande, longSurf + largBande, 3*largSurf / 4 + largBande, mPaint);
		}

		// Dessiner chaque emplacement de chaque bille
		numBille=-1;
		for( Bille bi : B) {
			numBille ++;
			if(bi != null) {
				x_prec = 0;
				y_prec = 0;
				numEmpl=-1;
				for(Emplacement e : bi.getEmpl()) {
					numEmpl ++;
					float x;
					if (inverse==0 || inverse==1) {
						x = e.getX();
					}
					else {
						x = 100 - e.getX();
					}
					float y;
					if (inverse==0 || inverse==3) {
						y = e.getY();
					}
					else {
						y = 100 - e.getY();
					}
					if ((affich==1) || (numEmpl==0)) {
					// recuperation du type de dessin pour l'emplacement 
			     	if (e.getType().matches("POINT")) {
						mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
                        r=rayon;
			     	}
			     	else if (e.getType().matches("PLEIN")) {
						mPaint.setStyle(Paint.Style.FILL);
						r=rayon;
			     	}
			     	else {
			     		mPaint.setStyle(Paint.Style.STROKE);
						r=rayon;
					}
			     	//dessin de l'empacement 
					mPaint.setColor(bi.getCouleur());
						mPaint.setShadowLayer(3, 3, 3, Color.BLACK);
						pCanvas.drawCircle((x * longSurf / 100) + largBande, (y * largSurf / 100) + largBande, r * largSurf / 100, mPaint);
						mPaint.setShadowLayer(0, 0, 0, Color.GRAY);
                    mPaint.setPathEffect(null);
			    	// Dessin de la trajectoire
				    if (numEmpl !=0) {
                        if  (numBille != billeSelect) {
                            mPaint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
                        }
						mPaint.setShadowLayer(3, 3, 3, Color.BLACK);
						pCanvas.drawLine(x_prec * longSurf / 100 + largBande, y_prec * largSurf / 100 + largBande, x * longSurf / 100 + largBande, y * largSurf / 100 + largBande, mPaint);
                        mPaint.setPathEffect(null);
						mPaint.setShadowLayer(0, 0, 0, Color.GRAY);
					}
				    // si la bille est selectionnee affichage des marqueurs de placement
				    if  (numBille == billeSelect  && numEmpl==emplSelect) {
						mPaint.setStrokeWidth(largeur/400);
						mPaint.setColor(couleurSel);
						mPaint.setShadowLayer(3, 3, 3, Color.BLACK);

						mPaint.setAlpha(200);
						pCanvas.drawLine(x*longSurf/100+largBande, largBande, x*longSurf/100+largBande, largSurf+largBande, mPaint);
				    	pCanvas.drawLine(largBande,y*largSurf/100+largBande, longSurf+largBande, y*largSurf/100+largBande, mPaint);
						mPaint.setStyle(Paint.Style.STROKE);
						pCanvas.drawCircle((x*longSurf/100)+largBande, (y*largSurf/100)+largBande, rayon*3*largSurf/100, mPaint);
						pCanvas.drawCircle((x*longSurf/100)+largBande, (y*largSurf/100)+largBande, rayon*5*largSurf/100, mPaint);
						pCanvas.drawCircle((x*longSurf/100)+largBande, (y*largSurf/100)+largBande, rayon*7*largSurf/100, mPaint);
						mPaint.setAlpha(0);
						mPaint.setStrokeWidth(largeur/200);
						mPaint.setShadowLayer(0, 0, 0, Color.GRAY);

				    }
				    // Stockage des coordonees de l'emplacement pour dessin de la prochaine trajectoire
					x_prec=x;
					y_prec=y;
					}
				}
			}
		}

		// si bille en deplacement affichage d'un ecran de visu supp
		if (visuSupp==1) {
			// dessin d'un cercle bleu a l'oppose de l'emplacement de la bille
			float xSelect=B[billeSelect].getX(emplSelect);
			float ySelect=B[billeSelect].getY(emplSelect);

			int yCercle=30;
			// Dessin du radar
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(couleurRadar);
			mPaint.setShadowLayer(3, 3, 3, Color.BLACK);
			pCanvas.drawRect((xCentreRadar-rayon*5)*longSurf/100+largBande,(yCercle-rayon*10)*largSurf/100+largBande,(xCentreRadar+rayon*5)*longSurf/100+largBande,(yCercle+rayon*10)*largSurf/100+largBande,mPaint);
	     	mPaint.setShadowLayer(0, 0, 0, Color.GRAY);
	     	mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.WHITE);
			mPaint.setStrokeWidth(largeur/400);
			mPaint.setAlpha(200);
			mPaint.setColor(couleurLigne);
			pCanvas.drawCircle((xCentreRadar*longSurf/100)+largBande, (yCercle*largSurf/100)+largBande, rayon*3*largSurf/100, mPaint);
	     	pCanvas.drawCircle((xCentreRadar*longSurf/100)+largBande, (yCercle*largSurf/100)+largBande, rayon*5*largSurf/100, mPaint);
	     	pCanvas.drawCircle((xCentreRadar*longSurf/100)+largBande, (yCercle*largSurf/100)+largBande, rayon*7*largSurf/100, mPaint);
	     	
			//dessin des mouches
			mPaint.setColor(couleurLigne);
			pCanvas.drawLine(4 * largSurf / 4 + largBande, largBande, 4 * largSurf / 4 + largBande, largSurf + largBande, mPaint);

	     	if (Math.abs(xSelect-100/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+100/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+100/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-200/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+200/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+200/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-300/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+300/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+300/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-400/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+400/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+400/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-500/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+500/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+500/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-600/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+600/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+600/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-700/8)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+700/8)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+700/8)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(ySelect-100/4)<rayon*10) {
				pCanvas.drawLine(((xCentreRadar-rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+100/4)*largSurf/100+largBande, ((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+100/4)*largSurf/100+largBande,  mPaint);
			}
	     	if (Math.abs(ySelect-200/4)<rayon*10) {
				pCanvas.drawLine(((xCentreRadar-rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+200/4)*largSurf/100+largBande, ((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+200/4)*largSurf/100+largBande,  mPaint);
				}
	     	if (Math.abs(ySelect-300/4)<rayon*10) {
				pCanvas.drawLine(((xCentreRadar-rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+300/4)*largSurf/100+largBande, ((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+300/4)*largSurf/100+largBande,  mPaint);
				}

	     	//dessin du cadre
            mPaint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0));

            if (Math.abs(xSelect-100/6)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+100/6)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+100/6)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(xSelect-500/6)<rayon*5) {
				pCanvas.drawLine((xCentreRadar-xSelect+500/6)*longSurf/100+largBande, ((yCercle-rayon*10)*largSurf/100)+largBande, (xCentreRadar-xSelect+500/6)*longSurf/100+largBande, ((yCercle+rayon*10)*largSurf/100)+largBande, mPaint);
			}
	     	if (Math.abs(ySelect-100/3)<rayon*10) {
				pCanvas.drawLine(((xCentreRadar-rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+100/3)*largSurf/100+largBande, ((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+100/3)*largSurf/100+largBande,  mPaint);
			}
	     	if (Math.abs(ySelect-200/3)<rayon*10) {
				pCanvas.drawLine(((xCentreRadar-rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+200/3)*largSurf/100+largBande, ((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle-ySelect+200/3)*largSurf/100+largBande,  mPaint);

                mPaint.setPathEffect(null);
            }

	     	//dessin des bandes
			mPaint.setColor(couleurBande);
			mPaint.setStyle(Paint.Style.FILL);
	     	if (xSelect<rayon*5) {
	     		pCanvas.drawRect((xCentreRadar-rayon*5)*longSurf/100+largBande,(yCercle-rayon*10)*largSurf/100+largBande,(xCentreRadar-xSelect)*longSurf/100+largBande,(yCercle+rayon*10)*largSurf/100+largBande,mPaint);
		     	}
	     	else if (xSelect>(100-rayon*5)) {
	     		pCanvas.drawRect((xCentreRadar+(100-xSelect))*longSurf/100+largBande,(yCercle-rayon*10)*largSurf/100+largBande,(xCentreRadar+rayon*5)*longSurf/100+largBande,(yCercle+rayon*10)*largSurf/100+largBande,mPaint);
			}
			if (ySelect<rayon*10) {
	     		pCanvas.drawRect((xCentreRadar-rayon*5)*longSurf/100+largBande,(yCercle-rayon*10)*largSurf/100+largBande,((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle-ySelect)*largSurf/100+largBande,mPaint);
			}
			else if (ySelect>(100-rayon*10)) {
	     		pCanvas.drawRect((xCentreRadar-rayon*5)*longSurf/100+largBande,(yCercle+(100-ySelect))*largSurf/100+largBande,((xCentreRadar+rayon*5)*longSurf/100)+largBande,(yCercle+rayon*10)*largSurf/100+largBande,mPaint);
			}
		
			// dessin des billes
			mPaint.setStrokeWidth(largeur/400);
			numBille=-1;
			for( Bille bi : B) {
				numBille ++;
				if (bi != null) {
					numEmpl=-1;
					for(Emplacement e : bi.getEmpl()) {
						numEmpl ++;
						float x=e.getX();
						float y = e.getY();

						if ( (Math.abs(x-xSelect)<rayon*10/2) && (Math.abs(y-ySelect)<rayon*10) ) {
							// recuperation du type de dessin pour l'emplacement 
                          	if (e.getType().matches("POINT")) {
								mPaint.setStyle(Paint.Style.STROKE);
                                mPaint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
                                r=rayon;
					     	}
					     	else if (e.getType().matches("PLEIN")) {
					     		mPaint.setStyle(Paint.Style.FILL);
								r=rayon;
					     	}
					     	else {
					     		mPaint.setStyle(Paint.Style.STROKE);
								r=rayon;
							}
					     	//dessin de l'empacement 
							mPaint.setColor(bi.getCouleur());
							pCanvas.drawCircle(((x - xSelect + xCentreRadar) * longSurf / 100) + largBande, ((y - ySelect + yCercle) * largSurf / 100) + largBande, r * largSurf / 100, mPaint);
                            mPaint.setPathEffect(null);
						}
					}
				}
			}

		 }

        if (visucomm==1) {

			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setTextSize(largSurf / 20 );
			mPaint.setColor(Color.WHITE);
			mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			pCanvas.drawCircle((xComm * longSurf / 100) + largBande, (yComm * largSurf / 100) + largBande, largBande / 4, mPaint);
			if (comment.contains("\n")) {
				String[] parts = comment.split("\n");
				for(int i=0;i<parts.length;i++) {
					pCanvas.drawText(parts[i],  (xComm * longSurf / 100) + largBande + largBande+largBande * 3 / 4, (float) ((yComm * largSurf / 100) + largBande * (i*1.5 + 1)), mPaint);
				}
			}
				else pCanvas.drawText(comment,(xComm * longSurf / 100) + largBande + largBande+largBande * 3 / 4, (yComm * largSurf / 100) + largBande, mPaint);
		}
		mPaint.setColor(Color.BLACK);
	}

	@Override
	public void surfaceChanged(SurfaceHolder pHolder, int pFormat, int pWidth, int pHeight) {
		//
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder pHolder) {

	//	mThread.keepDrawing = true;
//		if(!mThread.isAlive()){
//			mThread.start();
//		}

		// recuperation des dimensions de la vue et calcul des dimensions du tapis et des bandes
		largeur=this.getHeight();
		longueur=this.getWidth();
		largBande=(int) (largeur*4.96/100);
		largSurf=largeur-2*largBande;
		longSurf=2*largSurf;

		//largeur du pinceau
		mPaint.setStrokeWidth(largeur/200);

        //Gestion des selections a l'ecran
		if (enableclick==1) {
			this.setLongClickable(true);
			this.setLongClickable(true);
			this.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View myView, MotionEvent event) {
					float x = 0;
				float y=0;
				 int action = event.getActionMasked();
			     //Si touche ecran alors on regarde si une bille est selectionnee
			     if (action==MotionEvent.ACTION_DOWN)                {
			    	 int ancBilleSelect=billeSelect;
		    		 // init de la bille selectionne a 9: pas de bille selectionne
                     billeSelect=9;
		     	     emplSelect=0;
					 commSelect=0;

                     // Si une bille selectionne avant on regarde en priorite cette bille
		     	     if (ancBilleSelect !=9) {
		     	    		 int numEmpl=-1;
		     	    		 for(Emplacement e : B[ancBilleSelect].getEmpl()) {
		     	    			 numEmpl ++;
								 if (inverse==0 || inverse==1) {
									 x = e.getX();
								 }
								 else {
									 x = 100 - e.getX();
								 }
								 if (inverse==0 || inverse==3) {
									 y = e.getY();
								 }
								 else {
									 y = 100 - e.getY();
								 }
								 x=e.getX();
								 if ((Math.abs(x*longSurf/100+largBande-event.getX())< Constantes.rayonSelect*largSurf/100) && (Math.abs(y*largSurf/100+largBande-event.getY())<Constantes.rayonSelect*largSurf/100)) {
		     	    				 billeSelect=ancBilleSelect;
		     	    				 emplSelect=numEmpl;
						    	 }
							 }
		     	     }
                     // Si on a pas trouve de bille a selectionne on regarde toutes les billes une par une
		     	     if (billeSelect==9){
		     	    	 int numBille=-1;
		     	    	 for( Bille bi : B) {
		     	    		 numBille ++;
		     	    		 if(bi != null) {
		     	    			 int numEmpl=-1;
		     	    			 for(Emplacement e : bi.getEmpl()) {
		     	    				 numEmpl ++;
									 if (inverse==0 || inverse==1) {
										 x = e.getX();
									 }
									 else {
										 x = 100 - e.getX();
									 }
									 if (inverse==0 || inverse==3) {
										 y = e.getY();
									 }
									 else {
										 y = 100 - e.getY();
									 }
									 if ((Math.abs(x*longSurf/100+largBande-event.getX())< Constantes.rayonSelect*largSurf/100) && (Math.abs(y*largSurf/100+largBande-event.getY())<Constantes.rayonSelect*largSurf/100)) {
		     	    					 billeSelect=numBille;
		     	    					 emplSelect=numEmpl;
		     	    				 }
		     	    			 }
		     	    		 }
		     	    	 }
		     	     }
					 if (billeSelect==9){
						 if ((Math.abs(xComm*longSurf/100+largBande-event.getX())< Constantes.rayonSelect*largSurf/100) && (Math.abs(yComm*largSurf/100+largBande-event.getY())<Constantes.rayonSelect*largSurf/100)) {
										 commSelect=1;
						 }
					 }
				 }





			     // si l'utilisateur bouge la bille on lui assoice les nouvelles coordonn�es
			     if (lock !=1 && action==MotionEvent.ACTION_MOVE && (event.getEventTime()-event.getDownTime())>200  && (billeSelect !=9) )                {

                     // si pas encore  d affichage de zoom on regarde si on le palce a droite ou a gauche
			    	if (visuSupp==0) {
			    		xCentreRadar=15;
			    		if (B[billeSelect].getX(emplSelect)<50) {
			    			xCentreRadar=85;
			    		}
			    	}
			    	visuSupp=1;
					B[billeSelect].setX(emplSelect,(event.getX()-largBande)*100/longSurf);
  	    			B[billeSelect].setY(emplSelect,(event.getY()-largBande)*100/largSurf);

			      }

			      if (action==MotionEvent.ACTION_MOVE && (event.getEventTime()-event.getDownTime())>200  && ( commSelect==1) )                {

					 // si pas encore  d affichage de zoom on regarde si on le palce a droite ou a gauche
					 xComm=(event.getX()-largBande)*100/longSurf;
					 yComm=(event.getY()-largBande)*100/largSurf;
				 }

				 // Si arret de toucher l ecran on enleve le zoom
				 if (action==MotionEvent.ACTION_UP) {
					 visuSupp=0;
					 commSelect=0;
				 }

				Canvas canvas=mSurfaceHolder.lockCanvas();
					 Draw(canvas);
				 if (canvas != null)
					 mSurfaceHolder.unlockCanvasAndPost(canvas);
				     return true;
			     }        

		        }) ;

			this.performClick();
		}



		Canvas canvas=mSurfaceHolder.lockCanvas();
		Draw(canvas);
		if (canvas != null)
			mSurfaceHolder.unlockCanvasAndPost(canvas);
	}

	
	
	@Override
public void surfaceDestroyed(SurfaceHolder pHolder) {

	}

}
