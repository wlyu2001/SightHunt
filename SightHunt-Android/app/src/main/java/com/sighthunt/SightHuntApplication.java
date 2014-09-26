package com.sighthunt;

import android.app.Application;

import com.sighthunt.inject.Injector;
import com.sighthunt.network.ApiManager;

public class SightHuntApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		ApiManager apiManager = new ApiManager(this);
		Injector.inject(ApiManager.class, apiManager);
	}
}
