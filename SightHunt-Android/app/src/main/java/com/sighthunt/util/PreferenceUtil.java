package com.sighthunt.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	public static final String HUNTS_LAST_UPDATE_PREFIX = "hunts_last_update_";
	public static final String HUNTS_LAST_DELETE = "hunts_last_delete";

	public static SharedPreferences getDataSharedPreferences(Context context) {
		return context.getSharedPreferences("DataPreferenceFile", Context.MODE_PRIVATE);
	}


	public static SharedPreferences getSettingSharedPreferences(Context context) {
		return context.getSharedPreferences("SettingPreferenceFile", Context.MODE_PRIVATE);
	}

	public static void saveHuntsLastUpdate(Context context, String user, long lastUpdate) {
		getDataSharedPreferences(context).edit().putLong(HUNTS_LAST_UPDATE_PREFIX + user, lastUpdate).commit();
	}

	public static long getHuntsLastUpdate(Context context, String user) {
		return getDataSharedPreferences(context).getLong(HUNTS_LAST_UPDATE_PREFIX + user, 0);
	}

	public static void saveSightsLastDelete(Context context, long lastDelete) {
		getDataSharedPreferences(context).edit().putLong(HUNTS_LAST_DELETE, lastDelete).commit();
	}

	public static long getSightsLastDelete(Context context) {
		return getDataSharedPreferences(context).getLong(HUNTS_LAST_DELETE, 0);
	}
}
