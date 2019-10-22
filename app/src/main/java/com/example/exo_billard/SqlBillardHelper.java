package com.example.exo_billard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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
import android.util.Pair;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


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
	private static final String TABLE_SCORE = "EVALEXO";

	private static final String SCORE_IDEVAL = "idEval";
	private static final String SCORE_ID = "idExo";
	private static final String SCORE_INVERSE = "idInverse";
	private static final String SCORE_TERM = "exoTermine";
	private static final String SCORE_RES1 = "NbCoups1";
	private static final String SCORE_RES2 = "NbCoups2";
	private static final String SCORE_RES3 = "NbCoups3";
	private static final String SCORE_RGP1 = "Regroupement1";
	private static final String SCORE_RGP2 = "Regroupement2";
	private static final String SCORE_RGP3 = "Regroupement3";

    private static final String SCORE_CREATE = "create table " + TABLE_SCORE
			+ " ("
			+ SCORE_IDEVAL + " integer, "
			+ SCORE_ID + " integer, "
			+ SCORE_INVERSE + " integer, "
			+ SCORE_TERM + " integer, "
			+ SCORE_RES1 + " integer, "
			+ SCORE_RES2 + " integer, "
			+ SCORE_RES3 + " integer, "
			+ SCORE_RGP1 + " integer, "
			+ SCORE_RGP2 + " integer, "
			+ SCORE_RGP3 + " integer); ";

    //Table des SeanceExo
	private static final String TABLE_SEANCE = "EVAL";
	private static final String SEANCE_ID = "_idEval";
	private static final String SEANCE_IDLIVRET = "idLivret";
	private static final String SEANCE_ORIGINE = "Origine";
	private static final String SEANCE_NBEXO = "Nb_Exo";
	private static final String SEANCE_DATECREA = "DateCrea";

    private static final String SEANCE_CREATE = "create table " + TABLE_SEANCE
            + " (" + SEANCE_ID + " integer primary key autoincrement, "
			+ SEANCE_IDLIVRET + " integer, "
			+ SEANCE_ORIGINE + " long, "
			+ SEANCE_NBEXO + " integer, "
			+ SEANCE_DATECREA + " long ); ";

    //table match
	private static final String TABLE_MATCH = "ResultatMatch";
	private static final String MATCH_ADV = "Adversaire";
	private static final String MATCH_ORDRE = "Ordre";
	private static final String MATCH_REP = "NbCoups";
    private static final String MATCH_SCORE = "Score";
    private static final String MATCH_SCOREADV = "ScoreAdv";
    private static final String MATCH_DATE = "Date";
    private static final String MATCH_CREATE = "create table " + TABLE_MATCH
            + " (" + MATCH_ADV + " text, "
			+ MATCH_ORDRE + " integer, "
			+ MATCH_REP + " integer, "
			+ MATCH_SCORE + " integer, "
            + MATCH_SCOREADV + " integer, "
            + MATCH_DATE + " integer ); ";

	//CREATION DB
//	  public static final String DATABASE_CREATE = LIV_CREATE + " "+ EXO_CREATE + " " + EMPL_CREATE; 
//	  public static final String DATABASE_CREATE =EXO_CREATE +" "+ EMPL_CREATE; 
	private static final String DATABASE_NAME = "ExoBillard.db";
	private static final int DATABASE_VERSION = 26;

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
        database.execSQL(SEANCE_CREATE);
        database.execSQL(MATCH_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SqlBillardHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE " + TABLE_SEANCE);
		db.execSQL("DROP TABLE " + TABLE_SCORE);
		db.execSQL(SEANCE_CREATE);
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
		valexo.put(NOTE, exo.getFav());

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
		valexo.put(NOTE, exo.getFav());

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
		valexo.put(NOTE, exo.getFav());

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
			exo.setFav(cExo.getInt((cExo.getColumnIndex(NOTE))));

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

	public void saveMatch(String adv, int rep, int score, int scoreadv, int ordre) {
		SQLiteDatabase db = this.getWritableDatabase();

        ContentValues res = new ContentValues();
		res.put(MATCH_ORDRE, ordre);
		res.put(MATCH_ADV, adv);
		res.put(MATCH_REP, rep);
        res.put(MATCH_SCORE, score);
        res.put(MATCH_SCOREADV, scoreadv);
        res.put(MATCH_DATE, System.currentTimeMillis());
        // insert row
        db.insert(TABLE_MATCH, null, res);
    }

	public long addEval(Eval eval) {
		SQLiteDatabase db = this.getWritableDatabase();

        ContentValues res = new ContentValues();
		res.put(SEANCE_IDLIVRET, eval.getIdLivret());
		res.put(SEANCE_NBEXO, eval.getNbExo());
		res.put(SEANCE_DATECREA, eval.getDateCrea());
		Log.d("date", String.valueOf(eval.getDateCrea()));
		res.put(SEANCE_ORIGINE, eval.getOrigine());
		// insert row
		long seance_id = db.insert(TABLE_SEANCE, null, res);
		for (int i = 0; i < eval.getNbExo(); i++) {
			Eval.ExoEval exo = eval.getExo(i);
			addEvalExo(seance_id, exo);
		}
		return seance_id;
	}


	public Pair<List<Long>, List<String>> getListEvalNonFinie() {
		String SelectQuery = "SELECT DISTINCT " + SEANCE_ID + ", " + SEANCE_NBEXO + ", " + SEANCE_IDLIVRET + ", " + SEANCE_DATECREA + " FROM " + TABLE_SEANCE + " Order by " + SEANCE_DATECREA + " desc";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor clseance = db.rawQuery(SelectQuery, null);
		List<Long> lSeance = new ArrayList<Long>();
		List<String> lSeancedesc = new ArrayList<String>();
		if (clseance.moveToFirst()) {
			do {
				long id = clseance.getLong(clseance.getColumnIndex(SEANCE_ID));
				int idlivret = clseance.getInt(clseance.getColumnIndex(SEANCE_IDLIVRET));
				Date date = new Date(clseance.getLong(clseance.getColumnIndex(SEANCE_DATECREA)));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
				String datecrea = sdf.format(date);

				Log.d("date2", datecrea);
				int nbexo = clseance.getInt(clseance.getColumnIndex(SEANCE_NBEXO));
				String SelectQuery2 = "SELECT DISTINCT count(1) as Exo_fini FROM " + TABLE_SCORE + " where " + SCORE_TERM + "=1 and " + SCORE_IDEVAL + "=" + id;
				Cursor cls2 = db.rawQuery(SelectQuery2, null);
				int nbexoterm = 0;
				if (cls2.moveToFirst()) {
					nbexoterm = cls2.getInt(cls2.getColumnIndex("Exo_fini"));
				}
				if (nbexo > nbexoterm) {
					String SelectQuery3 = "SELECT " + LIV_TITRE + " FROM " + TABLE_LIV + " where " + LIV_ID + "= " + idlivret;
					Cursor cls3 = db.rawQuery(SelectQuery3, null);
					String titre = "";
					if (cls3.moveToFirst()) {
						titre = cls3.getString(cls3.getColumnIndex(LIV_TITRE));
					}
					lSeance.add(id);
					lSeancedesc.add(nbexoterm + "/" + nbexo + " exos de " + titre + " du " + datecrea);
				}
			} while (clseance.moveToNext());
		}
		return new Pair<List<Long>, List<String>>(lSeance, lSeancedesc);
	}

	public Pair<List<Long>, List<String>> getListEvalFinie() {
		String SelectQuery = "SELECT DISTINCT " + SEANCE_ID + ", " + SEANCE_NBEXO + ", " + SEANCE_IDLIVRET + ", " + SEANCE_DATECREA + " FROM " + TABLE_SEANCE + " Order by " + SEANCE_DATECREA + " desc";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor clseance = db.rawQuery(SelectQuery, null);
		List<Long> lSeance = new ArrayList<Long>();
		List<String> lSeancedesc = new ArrayList<String>();
		if (clseance.moveToFirst()) {
			do {
				long id = clseance.getLong(clseance.getColumnIndex(SEANCE_ID));
				int idlivret = clseance.getInt(clseance.getColumnIndex(SEANCE_IDLIVRET));
				Date date = new Date(clseance.getLong(clseance.getColumnIndex(SEANCE_DATECREA)));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
				String datecrea = sdf.format(date);
				int nbexo = clseance.getInt(clseance.getColumnIndex(SEANCE_NBEXO));
				String SelectQuery2 = "SELECT DISTINCT count(1) as Exo_fini FROM " + TABLE_SCORE + " where " + SCORE_TERM + "=1 and " + SCORE_IDEVAL + "=" + id;
				Cursor cls2 = db.rawQuery(SelectQuery2, null);
				int nbexoterm = 0;
				if (cls2.moveToFirst()) {
					nbexoterm = cls2.getInt(cls2.getColumnIndex("Exo_fini"));
				}
				if (nbexo == nbexoterm) {
					String SelectQuery3 = "SELECT " + LIV_TITRE + " FROM " + TABLE_LIV + " where " + LIV_ID + "= " + idlivret;
					Cursor cls3 = db.rawQuery(SelectQuery3, null);
					String titre = "";
					if (cls3.moveToFirst()) {
						titre = cls3.getString(cls3.getColumnIndex(LIV_TITRE));
					}
					lSeance.add(id);
					lSeancedesc.add(nbexoterm + "/" + nbexo + " exos de " + titre + " du " + datecrea);
				}
			} while (clseance.moveToNext());
		}
		return new Pair<List<Long>, List<String>>(lSeance, lSeancedesc);
	}

	public Eval getEval(long seance_id) {
		Eval eval = new Eval();

		// recup info exo
		String selectEval = "SELECT  * FROM " + TABLE_SEANCE
				+ " WHERE " + SEANCE_ID + " = " + seance_id + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cEval = db.rawQuery(selectEval, null);
		if (cEval.moveToFirst()) {
			eval.setIdEval(seance_id);
			eval.setDateCrea(cEval.getLong((cEval.getColumnIndex(SEANCE_DATECREA))));
			eval.setIdLivret(cEval.getInt((cEval.getColumnIndex(SEANCE_IDLIVRET))));
			eval.setNbExo(cEval.getInt((cEval.getColumnIndex(SEANCE_NBEXO))));
			eval.setOrigine(cEval.getInt((cEval.getColumnIndex(SEANCE_ORIGINE))));
		}
		cEval.close();
		// recup liste exo
		String selectExos = "SELECT  * FROM " + TABLE_SCORE
				+ " WHERE " + SCORE_IDEVAL + " = " + seance_id + ";";
		Cursor cExos = db.rawQuery(selectExos, null);
		if (cExos.moveToFirst()) {
			int i = 0;
			boolean test;
			do {
				i++;
				int exoId = cExos.getInt((cExos.getColumnIndex(SCORE_ID)));

				eval.addNewExo(exoId, cExos.getInt((cExos.getColumnIndex(SCORE_INVERSE))));

				eval.addNbPt(exoId, 1, cExos.getInt((cExos.getColumnIndex(SCORE_RES1))));
				eval.addNbPt(exoId, 2, cExos.getInt((cExos.getColumnIndex(SCORE_RES2))));
				eval.addNbPt(exoId, 3, cExos.getInt((cExos.getColumnIndex(SCORE_RES3))));
				Log.d("exo1", String.valueOf(cExos.getInt((cExos.getColumnIndex(SCORE_RES1)))));
				Log.d("exo3", String.valueOf(cExos.getInt((cExos.getColumnIndex(SCORE_RES3)))));
				if (cExos.getInt((cExos.getColumnIndex(SCORE_RGP1))) == 0)
					test = false;
				else
					test = true;
				eval.addRgpt(exoId, 1, test);
				if (cExos.getInt((cExos.getColumnIndex(SCORE_RGP2))) == 0)
					test = false;
				else
					test = true;
				eval.addRgpt(exoId, 2, test);
				if (cExos.getInt((cExos.getColumnIndex(SCORE_RGP3))) == 0)
					test = false;
				else
					test = true;
				eval.addRgpt(exoId, 3, test);
				if (cExos.getInt((cExos.getColumnIndex(SCORE_TERM))) == 0)
					test = false;
				else
					test = true;
				eval.setExoTermine(exoId, test);

			} while (cExos.moveToNext());
		}
		cExos.close();
		Log.d("eval finale", String.valueOf(eval.getlistIdExo()));
		return eval;

	}


	public void addEvalExo(long idEval, Eval.ExoEval exo) {
		SQLiteDatabase db2 = this.getWritableDatabase();
		ContentValues res2 = new ContentValues();
		res2.put(SCORE_IDEVAL, idEval);
		res2.put(SCORE_ID, exo.getIdExo());
		res2.put(SCORE_INVERSE, exo.getInverse());
		res2.put(SCORE_RES1, exo.getNbPt(1));
		res2.put(SCORE_RES2, exo.getNbPt(2));
		res2.put(SCORE_RES3, exo.getNbPt(3));
		res2.put(SCORE_RGP1, exo.getRgpt(1));
		res2.put(SCORE_RGP2, exo.getRgpt(2));
		res2.put(SCORE_RGP3, exo.getRgpt(3));
		res2.put(SCORE_TERM, exo.getExoTer());
		db2.insert(TABLE_SCORE, null, res2);
	}

	public void modEvalExo(long idEval, Eval.ExoEval exo) {
		SQLiteDatabase db2 = this.getWritableDatabase();
		ContentValues res2 = new ContentValues();
		res2.put(SCORE_INVERSE, exo.getInverse());
		res2.put(SCORE_RES1, exo.getNbPt(1));
		res2.put(SCORE_RES2, exo.getNbPt(2));
		res2.put(SCORE_RES3, exo.getNbPt(3));
		res2.put(SCORE_RGP1, exo.getRgpt(1));
		res2.put(SCORE_RGP2, exo.getRgpt(2));
		res2.put(SCORE_RGP3, exo.getRgpt(3));
		res2.put(SCORE_TERM, exo.getExoTer());
		db2.update(TABLE_SCORE, res2, SCORE_IDEVAL + " = ? AND " + SCORE_ID + " =?", new String[]{String.valueOf(idEval), String.valueOf(exo.getIdExo())});
	}

	public NoteEval getNoteEvalExo(long seance_id, int idExo) {
		// recup info exo
		NoteEval nE = new NoteEval();
		String selectEval = "SELECT " + SCORE_RES1 + "," + SCORE_RES2 + "," + SCORE_RES3 + "," + SCORE_RGP1 + "," + SCORE_RGP2 + "," + SCORE_RGP3 + " FROM " + TABLE_SCORE + " WHERE 1=1 ";
		if (idExo > 0) selectEval = selectEval + " and " + SCORE_ID + " = " + idExo;
		if (seance_id > 0) selectEval = selectEval + " and " + SCORE_IDEVAL + " = " + seance_id;
		selectEval = selectEval + ";";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cExos = db.rawQuery(selectEval, null);
		Boolean r1 = true;
		Boolean r2 = true;
		Boolean r3 = true;

		if (cExos.moveToFirst()) {
			do {
				r1 = true;
				r2 = true;
				r3 = true;
				if (cExos.getInt((cExos.getColumnIndex(SCORE_RGP1))) == 0) r1 = false;
				if (cExos.getInt((cExos.getColumnIndex(SCORE_RGP2))) == 0) r2 = false;
				if (cExos.getInt((cExos.getColumnIndex(SCORE_RGP3))) == 0) r3 = false;

				nE.addResEvalElt(seance_id, idExo, cExos.getInt((cExos.getColumnIndex(SCORE_RES1))), cExos.getInt((cExos.getColumnIndex(SCORE_RES2))), cExos.getInt((cExos.getColumnIndex(SCORE_RES3))), r1, r2, r3);
			} while (cExos.moveToNext());
		}
		nE.calculScore();
		return nE;
	}


	public String exportLivret(Context context, int liv_id) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {

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
			File myDir = new File(context.getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath());

			//File myDir = new File(Environment.getExternalStorageDirectory().getPath() + "/ExportExoBillard" );


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
		} else {
			return "Emplacement non disponible";
		}
	}

	public void importLivret(Context context, String Liv) {

		Livret livret = new Livret();

		//pour créer le repertoire dans lequel on va récupérer les fichiers
		File myDir = new File(context.getExternalFilesDir(DIRECTORY_DOWNLOADS).getPath());
		//File myDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Export ExoBillard" );


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


