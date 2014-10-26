package com.sighthunt.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sighthunt.BuildConfig;
import com.sighthunt.inject.Injectable;
import com.sighthunt.util.PreferenceUtil;

public class SightHuntDatabaseHelper extends SQLiteOpenHelper implements Injectable {

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String COMMA_SEP = ",";

	private static final String DATABASE_NAME = "sights.db";
	public static final int DATABASE_VERSION = 20;
	private static final String SQL_CREATE_SIGHTS =
			"CREATE TABLE " + Contract.Sight.TABLE_NAME + " (" +
					Contract.Sight.KEY + INTEGER_TYPE + COMMA_SEP +
					Contract.Sight.TITLE + TEXT_TYPE + COMMA_SEP +
					Contract.Sight.REGION + TEXT_TYPE + COMMA_SEP +
					Contract.Sight.CREATOR + TEXT_TYPE + COMMA_SEP +
					Contract.Sight.TIME_CREATED + INTEGER_TYPE + COMMA_SEP +
					Contract.Sight.VOTES + INTEGER_TYPE + COMMA_SEP +
					Contract.Sight.HUNTS + INTEGER_TYPE + COMMA_SEP +
					Contract.Sight.LAST_MODIFIED + INTEGER_TYPE + COMMA_SEP +
					Contract.Sight.IMAGE_KEY + TEXT_TYPE + COMMA_SEP +
					Contract.Sight.THUMB_KEY + TEXT_TYPE + COMMA_SEP +
					Contract.Sight.DESCRIPTION + TEXT_TYPE + COMMA_SEP +
					Contract.Sight.LON + REAL_TYPE + COMMA_SEP +
					Contract.Sight.LAT + REAL_TYPE + COMMA_SEP +
					Contract.Sight.UUID + INTEGER_TYPE + " PRIMARY KEY)";


	private static final String SQL_CREATE_HUNTS =
			"CREATE TABLE " + Contract.Hunt.TABLE_NAME + " (" +
					Contract.Hunt.USER + TEXT_TYPE + COMMA_SEP +
					Contract.Hunt.SIGHT_UUID + INTEGER_TYPE + COMMA_SEP +
					Contract.Hunt.VOTE + INTEGER_TYPE + COMMA_SEP +
					"PRIMARY KEY (" + Contract.Hunt.USER + COMMA_SEP +
					Contract.Hunt.SIGHT_UUID + "))";

	private static final String SQL_CREATE_REPORTS =
			"CREATE TABLE " + Contract.Report.TABLE_NAME + " (" +
					Contract.Report.REASON + INTEGER_TYPE + COMMA_SEP +
					Contract.Report.SIGHT_UUID + INTEGER_TYPE + COMMA_SEP +
					Contract.Report.REPORTER + TEXT_TYPE + COMMA_SEP +
					"PRIMARY KEY (" + Contract.Report.REPORTER + COMMA_SEP +
					Contract.Report.SIGHT_UUID + "))";

	private static final String SQL_DELETE_SIGHTS =
			"DROP TABLE IF EXISTS " + Contract.Sight.TABLE_NAME;


	private static final String SQL_DELETE_HUNTS =
			"DROP TABLE IF EXISTS " + Contract.Hunt.TABLE_NAME;


	private static final String SQL_DELETE_REPORTS =
			"DROP TABLE IF EXISTS " + Contract.Report.TABLE_NAME;

	private Context mContext;

	public SightHuntDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_SIGHTS);
		db.execSQL(SQL_CREATE_HUNTS);
		db.execSQL(SQL_CREATE_REPORTS);
		clearSharedPreference();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_SIGHTS);
		db.execSQL(SQL_DELETE_HUNTS);
		db.execSQL(SQL_DELETE_REPORTS);
		onCreate(db);
	}

	private void clearSharedPreference() {
		SharedPreferences prefs = PreferenceUtil.getDataSharedPreferences(mContext);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}

	@Override
	public void onOpen (SQLiteDatabase db) {

	}
}
