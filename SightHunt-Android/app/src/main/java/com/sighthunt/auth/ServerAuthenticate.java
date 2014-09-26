package com.sighthunt.auth;

import com.sighthunt.inject.Injector;
import com.sighthunt.network.ApiManager;
import com.sighthunt.network.model.User;

import retrofit.Callback;

public class ServerAuthenticate {

	private ApiManager mApiManager = Injector.get(ApiManager.class);

	public void loginAsync(String username, String password, String authTokenType, Callback<User> callback) {
		mApiManager.getUserService().loginAsync(username, password, callback);
	}

	public User loginSync(String username, String password, String authTokenType) {
		return mApiManager.getUserService().loginSync(username, password);
	}
}
