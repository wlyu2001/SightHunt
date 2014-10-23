package com.sighthunt.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sighthunt.inject.Injector;

public class NetworkStateChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		AccountUtils accountUtils = Injector.get(AccountUtils.class);

		if (accountUtils.getUer() == null) {
			accountUtils.fetchUser();
		}
	}
}
