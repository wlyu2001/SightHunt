package com.sighthunt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SightHuntDatabase extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String COMMA_SEP = ",";

	private static final String DATABASE_NAME = "sight.db";
	public static final int DATABASE_VERSION = 4;
	private static final String SQL_CREATE_SIGHTS =
			"CREATE TABLE " + Contract.Sight.TABLE_NAME + " (" +
					Contract.Sight.KEY + " TEXT PRIMARY KEY," +
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
					Contract.Sight.LAT + REAL_TYPE + " )";

	private static final String SQL_DELETE_SIGHTS =
			"DROP TABLE IF EXISTS " + Contract.Sight.TABLE_NAME;


	public SightHuntDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_SIGHTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_SIGHTS);
		onCreate(db);
	}
}
