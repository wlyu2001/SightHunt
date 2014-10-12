package com.sighthunt;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sighthunt.data.Contract;
import com.sighthunt.data.SightHuntDatabaseHelper;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.ApiManager;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.PreferenceUtil;
import com.sighthunt.util.SightsKeeper;

public class SightHuntApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		AccountUtils accountUtils = new AccountUtils(this);
		ApiManager apiManager = new ApiManager(this, accountUtils);


		Injector.inject(AccountUtils.class, accountUtils);
		Injector.inject(ApiManager.class, apiManager);
		Injector.inject(SightsKeeper.class, new SightsKeeper());

		initialize();
	}

	public void initialize() {
		SightHuntDatabaseHelper dbHelper = new SightHuntDatabaseHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SightsKeeper sk = Injector.get(SightsKeeper.class);
		Cursor cursor = db.rawQuery("SELECT " + Contract.Sight.KEY + ", " + Contract.Sight.UUID + " FROM " + Contract.Sight.TABLE_NAME, null);
		while (cursor.moveToNext()) {
			sk.addCachedSightKey(cursor.getLong(0), cursor.getLong(1));
		}
		cursor.close();
	}
}
