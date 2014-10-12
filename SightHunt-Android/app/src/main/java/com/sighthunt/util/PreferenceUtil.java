package com.sighthunt.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	public static final String HUNTS_LAST_UPDATE_PREFIX = "hunts_last_update_";

	public static SharedPreferences getDataSharedPreferences(Context context) {
		return context.getSharedPreferences("DataPreferenceFile", Context.MODE_PRIVATE);
	}


	public static SharedPreferences getSettingSharedPreferences(Context context) {
		return context.getSharedPreferences("SettingPreferenceFile", Context.MODE_PRIVATE);
	}

	public static void saveHuntsLastUpdate(Context context, String user, long lastUpdate) {
		getSettingSharedPreferences(context).edit().putLong(HUNTS_LAST_UPDATE_PREFIX + user, lastUpdate).commit();
	}

	public static long getHuntsLastUpdate(Context context, String user) {
		return getSettingSharedPreferences(context).getLong(HUNTS_LAST_UPDATE_PREFIX + user, 0);
	}
}
