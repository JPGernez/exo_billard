package com.example.exo_billard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SqlBillardHelper extends SQLiteOpenHelper {

	//Table des livrets
    private static final String TABLE_LIV = "LIVRETS";
    private static final String LIV_ID = "_idLivret";
    private static final String LIV_TITRE = "Titre";
    private static final String LIV_COMMENT = "Commentaire";
    private static final String LIV_AUTEUR = "Auteur";

    private static final String LIV_CREATE = "create table " + TABLE_LIV
            + " (" + LIV_ID + " integer primary key autoincrement, "
			+ LIV_TITRE + " text, "
			+ LIV_COMMENT + " text, "
			+ LIV_AUTEUR + " text ); ";


	//Table des exos
    private static final String TABLE_EXO = "EXOS";
    private static final String EXO_ID = "_idExo";
    private static final String EXO_COMMENT = "Commentaire";
    private static final String EXOLIV_ID = "idLivret";
    private static final String COMM_POSX = "xComm";
    private static final String COMM_POSY = "yComm";
    private static final String NOTE = "exo_note";

    private static final String EXO_CREATE = "create table " + TABLE_EXO
            + " (" + EXO_ID + " integer primary key autoincrement, "
			+ EXO_COMMENT + " text, "
			+ EXOLIV_ID + " integer, "
			+ NOTE + " integer, "
			+ COMM_POSX + " real, "
			+ COMM_POSY + " real ); ";


	//Table des emplacements
    private static final String TABLE_EMPL = "EMPLACEMENT";
    private static final String EMPL_ID = "idExo";
    private static final String EMPL_BILLE = "NumBille";
    private static final String EMPL_NUMEMPL = "Numero";
    private static final String EMPL_X = "X";
    private static final String EMPL_Y = "Y";
    private static final String EMPL_TYPE = "Type";
    private static final String EMPL_CREATE = "create table " + TABLE_EMPL
            + " (" + EMPL_ID + " integer, "
			+ EMPL_BILLE + " integer, "
			+ EMPL_NUMEMPL + " integer, "
			+ EMPL_X + " real, "
			+ EMPL_Y + " real, "
			+ EMPL_TYPE + " text); ";

	//Table des tags
    private static final String TABLE_TAG = "TAGEXO";
    private static final String TAG_ID = "idExo";
    private static final String MOTCLE = "MotCle";

    private static final String TAG_CREATE = "create table " + TABLE_TAG
            + " (" + TAG_ID + " integer, "
			+ MOTCLE + " text); ";

    //Table des ScoreExo
    private static final String TABLE_SCORE = "SCORE";
    private static final String SCORE_ID = "idExo";
    private static final String SCORE_RES = "NbCoups";
    private static final String SCORE_RGP = "Regroupement";
    private static final String SCORE_DATE = "Date";

    private static final String SCORE_CREATE = "create table " + TABLE_SCORE
            + " (" + SCORE_ID + " integer, "
            + SCORE_RES + " integer, "
            + SCORE_RGP + " integer, "
            + SCORE_DATE + " integer ); ";



	//CREATION DB
//	  public static final String DATABASE_CREATE = LIV_CREATE + " "+ EXO_CREATE + " " + EMPL_CREATE; 
//	  public static final String DATABASE_CREATE =EXO_CREATE +" "+ EMPL_CREATE; 
	private static final String DATABASE_NAME = "ExoBillard.db";
    private static final int DATABASE_VERSION = 20;

	public SqlBillardHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

        File dossier =new File(Environment.getExternalStorageDirectory(), "ExoBillard");
        if (!dossier.exists() && dossier.isDirectory())
            dossier.mkdir();

        }

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(EXO_CREATE);
		database.execSQL(LIV_CREATE);
		database.execSQL(EMPL_CREATE);
		database.execSQL(TAG_CREATE);
        database.execSQL(SCORE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SqlBillardHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
        db.execSQL(SCORE_CREATE);
    }

	// ------------------------ "EXO" table methods ----------------//

	/**
	 * Creation EXO
	 */
	public long createExo(Exo exo) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues valexo = new ContentValues();
		valexo.put(EXO_COMMENT, exo.getComment());
		valexo.put(EXOLIV_ID, exo.getLivret());
		valexo.put(COMM_POSX , exo.getXComm());
		valexo.put(COMM_POSY , exo.getYComm());
		valexo.put(NOTE , exo.getNote());

		// insert row
		long exo_id = db.insert(TABLE_EXO, null, valexo);

		ContentValues valtag = new ContentValues();
		valtag.put(TAG_ID, exo_id);
		String liste_mot = " ";
		for (Map.Entry<String, Integer> entree : exo.motsCles.motscles.entrySet()) {
			if (exo.motsCles.getMotCle(entree.getKey()) == 1) {
				liste_mot = liste_mot + entree.getKey() + " ";
			}
		}
		liste_mot = liste_mot.trim();
		valtag.put(MOTCLE, liste_mot.trim());
		db.insert(TABLE_TAG, null, valtag);

		createEmpls(exo_id, 0, exo.getB(0));
		createEmpls(exo_id, 1, exo.getB(1));
		createEmpls(exo_id, 2, exo.getB(2));
		return exo_id;
	}

	/**
	 * Creation LIVRET
	 */
	public long createLivret(Livret livret) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues vallivret = new ContentValues();
		vallivret.put(LIV_COMMENT, livret.getComment());
		vallivret.put(LIV_AUTEUR, livret.getAuteur());
		vallivret.put(LIV_TITRE, livret.getTitre());
		// insert row
        return db.insert(TABLE_LIV, null, vallivret);
    }

	public long modLivret(Livret livret) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues vallivret = new ContentValues();
		vallivret.put(LIV_COMMENT, livret.getComment());
		vallivret.put(LIV_AUTEUR, livret.getAuteur());
		vallivret.put(LIV_TITRE, livret.getTitre());
		// insert row
		db.update(TABLE_LIV, vallivret, LIV_ID + " = ?", new String[]{String.valueOf(livret.getId())});
		return livret.getId();
	}

	public Livret getLivret(int liv_id) {
		Livret livret = new Livret();

		// recup info exo
		String selectLivret = "SELECT  * FROM " + TABLE_LIV
				+ " WHERE " + LIV_ID + " = " + liv_id + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cLiv = db.rawQuery(selectLivret, null);
		if (cLiv.moveToFirst()) {
			livret.setId(liv_id);
			livret.setTitre(cLiv.getString((cLiv.getColumnIndex(LIV_TITRE))));
			livret.setComment(cLiv.getString((cLiv.getColumnIndex(LIV_COMMENT))));
			livret.setAuteur(cLiv.getString((cLiv.getColumnIndex(LIV_AUTEUR))));
		}
        cLiv.close();
        // recup liste exo
		String selectExos = "SELECT  * FROM " + TABLE_EXO
				+ " WHERE " + EXOLIV_ID + " = " + liv_id + ";";
		Cursor cExos = db.rawQuery(selectExos, null);
		if (cExos.moveToFirst()) {
			do {
				livret.addNumExo(cExos.getInt(cExos.getColumnIndex(EXO_ID)));
			} while (cExos.moveToNext());
		}
        cExos.close();
        return livret;

	}

	public String getTitreLivret(int liv_id) {
		String titre ="";

		// recup info exo
		String selectLivret = "SELECT  * FROM " + TABLE_LIV
				+ " WHERE " + LIV_ID + " = " + liv_id + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cLiv = db.rawQuery(selectLivret, null);
		if (cLiv.moveToFirst()) {
			titre = cLiv.getString((cLiv.getColumnIndex(LIV_TITRE)));
		}
        cLiv.close();
        // recup liste exo

		return titre;

	}

	public List getListlivret() {
		// recup info exo
		List<Integer> lLivrets = new ArrayList<>();
		String selectListLivret = "SELECT " + LIV_ID + " FROM " + TABLE_LIV + " ORDER BY " + LIV_TITRE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cLLiv = db.rawQuery(selectListLivret, null);
		if (cLLiv.moveToFirst()) {
			do {
				lLivrets.add(cLLiv.getInt((cLLiv.getColumnIndex(LIV_ID))));
			} while (cLLiv.moveToNext());
		}
        cLLiv.close();
        return lLivrets;
	}

	/**
	 * modification EXO
	 */
	public long modExo(Exo exo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues valexo = new ContentValues();
		valexo.put(EXO_COMMENT, exo.getComment());
		valexo.put(EXOLIV_ID, exo.getLivret());
		valexo.put(COMM_POSX , exo.getXComm());
		valexo.put(COMM_POSY , exo.getYComm());
		valexo.put(NOTE , exo.getNote());

		// insert row
		db.update(TABLE_EXO, valexo, EXO_ID + " = ?", new String[]{String.valueOf(exo.getId())});

		db.delete(TABLE_EMPL, EMPL_ID + " = ?", new String[]{String.valueOf(exo.getId())});
		db.delete(TABLE_TAG, TAG_ID + " = ?", new String[]{String.valueOf(exo.getId())});

		ContentValues valtag = new ContentValues();
		valtag.put(TAG_ID, exo.getId());
        StringBuilder liste_mot = new StringBuilder();
        for (Map.Entry<String, Integer> entree : exo.motsCles.motscles.entrySet()) {
			if (exo.motsCles.getMotCle(entree.getKey()) == 1) {
                liste_mot.append(entree.getKey()).append(" ");
            }
		}
        liste_mot.trimToSize();
        valtag.put(MOTCLE, liste_mot.toString());
        db.insert(TABLE_TAG, null, valtag);

		createEmpls(exo.getId(), 0, exo.getB(0));
		createEmpls(exo.getId(), 1, exo.getB(1));
		createEmpls(exo.getId(), 2, exo.getB(2));
		return exo.getId();
	}

	public long setFav(Exo exo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues valexo = new ContentValues();
		valexo.put(NOTE , exo.getNote());

		// insert row
		db.update(TABLE_EXO, valexo, EXO_ID + " = ?", new String[]{String.valueOf(exo.getId())});
		return exo.getId();
    }



	public List<Integer> getListExo(MotsCles mc, int IdLivret) {
		String where = "1=1";
		if (IdLivret > -1) {
			where = where + " and " + EXOLIV_ID + " = " + IdLivret;
		}
		if (IdLivret == -1) {
			where = where + " and " + NOTE + " = 1 ";
		}
		for (Map.Entry<String, Integer> entree : mc.motscles.entrySet()) {
			if (mc.getMotCle(entree.getKey()) == 1) {
				where = where + " and " + MOTCLE + " like \"%" + entree.getKey() + "%\"";
			}
			if (mc.getMotCle(entree.getKey()) == -1) {
				where = where + " and " + MOTCLE + " not like \"%" + entree.getKey() + "%\"";
			}
		}

		String SelectQuery = "SELECT DISTINCT A." + EXO_ID + " FROM " + TABLE_EXO + " A left join " + TABLE_TAG + " B "
				+ " ON A." + EXO_ID + "=B." + TAG_ID
				+ " WHERE " + where;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor clexo = db.rawQuery(SelectQuery, null);
		List<Integer> lExo = new ArrayList<Integer>();

		if (clexo.moveToFirst()) {
			do {
				lExo.add(clexo.getInt((clexo.getColumnIndex(EXO_ID))));
			} while (clexo.moveToNext());
		}
		return lExo;
	}

	public Exo getExo(int exo_id) {
		Exo exo = new Exo();
		Bille b1 = new Bille();
		Bille b2 = new Bille();
		Bille b3 = new Bille();

		// recup info exo
		String selectExo = "SELECT  * FROM " + TABLE_EXO
				+ " WHERE " + EXO_ID + " = " + exo_id + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cExo = db.rawQuery(selectExo, null);
		if (cExo.moveToFirst()) {
			exo.setId(exo_id);
			exo.setComment(cExo.getString((cExo.getColumnIndex(EXO_COMMENT))));
			exo.setLivret(cExo.getInt((cExo.getColumnIndex(EXOLIV_ID))));
			exo.setXComm(cExo.getFloat((cExo.getColumnIndex(COMM_POSX))));
			exo.setYComm(cExo.getFloat((cExo.getColumnIndex(COMM_POSY))));
			exo.setNote(cExo.getInt((cExo.getColumnIndex(NOTE))));

		}

		b1 = getEmpls(exo_id, 0);
		b1.setCouleur(Color.WHITE);
		b2 = getEmpls(exo_id, 1);
		b2.setCouleur(Color.YELLOW);
		b3 = getEmpls(exo_id, 2);
		b3.setCouleur(Color.RED);
		exo.setB(0, b1);
		exo.setB(1, b2);
		exo.setB(2, b3);
		// recup info exo
		String selectTag = "SELECT  * FROM " + TABLE_TAG
				+ " WHERE " + TAG_ID + " = " + exo_id + ";";
		Cursor cTag = db.rawQuery(selectTag, null);
		if (cTag.moveToFirst()) {
			String[] result = cTag.getString((cTag.getColumnIndex(MOTCLE))).split(" ");
			for (int x = 0; x < result.length; x++) {
				if (result[x] != "")
					exo.motsCles.changeMotCle(result[x]);
			}
		}
		return exo;
	}

	public Bille getEmpls(int exo_id, int numb) {
		Bille b = new Bille();
		String selectEmpls = "SELECT  * FROM " + TABLE_EMPL
				+ " WHERE " + EMPL_ID + " = " + exo_id + " AND " + EMPL_BILLE + "=" + numb + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cEmpls = db.rawQuery(selectEmpls, null);
		int numEmpl = -1;
		if (cEmpls.moveToFirst()) {
			do {
				numEmpl++;
				b.creaEmplVide();
				b.setX(numEmpl, cEmpls.getFloat((cEmpls.getColumnIndex(EMPL_X))));
				b.setY(numEmpl, cEmpls.getFloat((cEmpls.getColumnIndex(EMPL_Y))));
				b.setType(numEmpl, cEmpls.getString((cEmpls.getColumnIndex(EMPL_TYPE))));
			} while (cEmpls.moveToNext());
		}
		return b;
	}

	public void deleteExo(long exo_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EXO, EXO_ID + " = ?", new String[]{String.valueOf(exo_id)});
		db.delete(TABLE_EMPL, EMPL_ID + " = ?", new String[]{String.valueOf(exo_id)});
		db.delete(TABLE_TAG, TAG_ID + " = ?", new String[]{String.valueOf(exo_id)});
	}

	public void deleteLivret(long IdLivret) {
		SQLiteDatabase db = this.getWritableDatabase();

		String SelectQuery = "SELECT DISTINCT " + EXO_ID + " FROM " + TABLE_EXO
				+ " WHERE " + EXOLIV_ID + " = " + IdLivret;
		;

		Cursor clexo = db.rawQuery(SelectQuery, null);
		List<Integer> lExo = new ArrayList<Integer>();
		if (clexo.moveToFirst()) {
			do {
				deleteExo(clexo.getColumnIndex(EXO_ID));
			} while (clexo.moveToNext());
		}
		SelectQuery = "SELECT DISTINCT A." + EXO_ID + " FROM " + TABLE_EXO
				+ " WHERE " + EXOLIV_ID + " = " + IdLivret;
		;
		db.delete(TABLE_LIV, LIV_ID + " = ?", new String[]{String.valueOf(IdLivret)});

	}

	public void deleteFav() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("UPDATE " + TABLE_EXO +
				" SET " + NOTE + " =0 ");

	}
// ------------------------ "EMPLACEMENTS" table methods ----------------//

	/**
	 * Creation des emplacements d un exo
	 */
	public long createEmpls(long exo_id, int numBille, Bille b) {
		long empl_id = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EMPL_ID, exo_id);
		values.put(EMPL_BILLE, numBille);
		int numEmpl = -1;
		for (Emplacement e : b.getEmpl()) {
			numEmpl++;
			values.put(EMPL_NUMEMPL, numEmpl);
			values.put(EMPL_X, e.getX());
			values.put(EMPL_Y, e.getY());

			values.put(EMPL_TYPE, e.getType());
			empl_id = db.insert(TABLE_EMPL, null, values);
		}
		return empl_id;
	}

    public void saveResult(int exoId, int score, int regroup) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues res = new ContentValues();
        res.put(SCORE_ID, exoId);
        res.put(SCORE_RES, score);
        res.put(SCORE_RGP, regroup);
        res.put(SCORE_DATE, System.currentTimeMillis());
        // insert row
        db.insert(TABLE_SCORE, null, res);
    }


	public String exportLivret  (int liv_id) {

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            Livret livret = getLivret(liv_id);

            List<Exo> Lexo = new CopyOnWriteArrayList<>();

            int numExo = 0;
            Exo exo = new Exo();
            do {
                exo = getExo(livret.getNumExo(numExo));
                Lexo.add(exo);
                numExo++;
            } while (numExo < livret.getNbExo());

            //pour créer le repertoire dans lequel on va mettre notre fichier

            File myDir = new File(Environment.getExternalStorageDirectory(), "ExoBillard");


            File myFile1 = new File(myDir, livret.getTitre() + ".eBi1"); //on déclare notre futur fichier
            File myFile2 = new File(myDir, livret.getTitre() + ".eBi2"); //on déclare notre futur fichier

            try {
                // ouverture d'un flux de sortie vers le fichier
                FileOutputStream fos1 = new FileOutputStream(myFile1);
                // création d'un "flux objet" avec le flux fichier
                ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
                try {
                    // sérialisation : écriture de l'objet dans le flux de sortie
                    oos1.writeObject(livret);
                    oos1.flush();
                } finally {
                    //fermeture des flux
                    try {
                        oos1.close();
                    } finally {
                        fos1.close();
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                // ouverture d'un flux de sortie vers le fichier
                FileOutputStream fos2 = new FileOutputStream(myFile2);
                // création d'un "flux objet" avec le flux fichier
                ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
                try {
                    // sérialisation : écriture de l'objet dans le flux de sortie
                    oos2.writeObject(Lexo);
                    oos2.flush();
                } finally {
                    //fermeture des flux
                    try {
                        oos2.close();
                    } finally {
                        fos2.close();
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return "2 Fichiers "+ livret.getTitre()+" écrit sous " + myDir;
        }
        else {
            return "Emplacement non disponible";
        }
	}

	public void importLivret( String Liv) {

		Livret livret = new Livret();

		//pour créer le repertoire dans lequel on va récupérer les fichiers
		File myDir = new File(Environment.getExternalStorageDirectory(), "ExoBillard");


		File myFile1 = new File(myDir, Liv+".eBi1"); //on déclare notre futur fichier
		File myFile2 = new File(myDir, Liv+".eBi2"); //on déclare notre futur fichier


		FileInputStream fis1 = null;
		try {
			fis1 = new FileInputStream(myFile1);
			ObjectInputStream ois1 = null;
			try {
				ois1 = new ObjectInputStream(fis1);
				try {
					// désérialisation : lecture de l'objet depuis le flux d'entrée
					livret = (Livret) ois1.readObject();
				} finally {
					// on ferme les flux
					try {
						ois1.close();
					} finally {
						fis1.close();
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
if (livret != null) {

	Long nv_livret_id = createLivret(livret);
	List<Exo> Lexo = new CopyOnWriteArrayList<>();

	FileInputStream fis2 = null;
	try {
		fis2 = new FileInputStream(myFile2);
		ObjectInputStream ois2 = null;
		try {
			ois2 = new ObjectInputStream(fis2);
			try {
				// désérialisation : lecture de l'objet depuis le flux d'entrée
				Lexo = (List<Exo>) ois2.readObject();
			} finally {
				// on ferme les flux
				try {
					ois2.close();
				} finally {
					fis2.close();
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
	if (Lexo != null) {
		Exo exo = new Exo();
		int numExo=0;
		do {
			exo = Lexo.get(numExo);
			exo.setLivret((int) nv_livret_id.intValue());
			Long exoId=createExo(exo);
			numExo++;
		} while (numExo < Lexo.size());

	}

	}
	}
	}


