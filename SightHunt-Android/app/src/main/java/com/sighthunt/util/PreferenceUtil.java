package com.sighthunt.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	public static SharedPreferences getDataSharedPreferences(Context context) {
		SharedPreferences pref = context.getSharedPreferences("DataPreferenceFile", Context.MODE_PRIVATE);
		return pref;
	}


	public static SharedPreferences getSettingSharedPreferences(Context context) {
		SharedPreferences pref = context.getSharedPreferences("SettingPreferenceFile", Context.MODE_PRIVATE);
		return pref;
	}
}
