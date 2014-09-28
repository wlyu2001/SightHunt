package com.sighthunt;

import android.app.Application;

import com.sighthunt.inject.Injector;
import com.sighthunt.network.ApiManager;
import com.sighthunt.util.AccountUtils;

public class SightHuntApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		AccountUtils accountUtils = new AccountUtils(this);
		ApiManager apiManager = new ApiManager(this, accountUtils);

		Injector.inject(AccountUtils.class, accountUtils);
		Injector.inject(ApiManager.class, apiManager);
	}
}
