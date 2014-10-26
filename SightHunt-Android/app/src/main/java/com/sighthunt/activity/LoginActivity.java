package com.sighthunt.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.sighthunt.BuildConfig;
import com.sighthunt.R;
import com.sighthunt.auth.AccountAuthenticatorActivity;
import com.sighthunt.auth.ServerAuthenticate;
import com.sighthunt.fragment.login.LoginFragment;
import com.sighthunt.fragment.login.SignUpFragment;
import com.sighthunt.fragment.login.WelcomeFragment;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.ApiManager;
import com.sighthunt.network.model.User;
import com.sighthunt.util.AccountUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AccountAuthenticatorActivity {


	public static final String ARG_PASSWORD = "arg_password";

	private final int REQ_SIGNUP = 1;

	private AccountManager mAccountManager;
	private ServerAuthenticate mServerAuthenticate;
	ApiManager mApiManager = Injector.get(ApiManager.class);


	//everytime user manually login, or sign up. clear all accounts first..
	public void clearAccounts() {

		Account[] accounts = mAccountManager.getAccountsByType(BuildConfig.PACKAGE_NAME);
		for (Account account : accounts) {
			mAccountManager.removeAccount(account, null, null);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("authenticator", "show LoginActivity");

		setContentView(R.layout.activity_login);
		mAccountManager = AccountManager.get(this);
		mServerAuthenticate = new ServerAuthenticate();

		getSupportFragmentManager().beginTransaction().replace(R.id.container, WelcomeFragment.createInstance(), null).commit();

	}

	public void showLogin() {
		getSupportFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.createInstance(), null).addToBackStack(null).commit();
	}

	public void showSignUp() {
		getSupportFragmentManager().beginTransaction().replace(R.id.container, SignUpFragment.createInstance(), null).addToBackStack(null).commit();
	}

	public void login(final String username, final String password) {
		clearAccounts();
		mServerAuthenticate.loginAsync(username, password, AccountUtils.AUTH_TOKEN_TYPE, new Callback<User>() {
			@Override
			public void success(User user, Response response) {
				if (user == null) {
					Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_LONG).show();
				} else {
					if (!TextUtils.isEmpty(user.token)) {
						final Intent intent = new Intent();
						intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
						intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, BuildConfig.PACKAGE_NAME);
						intent.putExtra(AccountManager.KEY_AUTHTOKEN, user.token);
						intent.putExtra(ARG_PASSWORD, password);

						finishLogin(intent);
					} else {
						Toast.makeText(LoginActivity.this, "Login failed: invalid token", Toast.LENGTH_LONG).show();
					}
				}
			}

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

	public void signup(final String username, final String password, final String email, final String nick) {
		clearAccounts();
		User user = new User();
		user.username = username;
		user.password = password;
		user.email = email;
		user.screen_name = nick;

		mApiManager.getUserService().signupUser(user, new Callback<User>() {
			@Override
			public void success(User user, Response response) {
				if (user == null) {
					Toast.makeText(LoginActivity.this, getString(R.string.username_taken), Toast.LENGTH_LONG).show();
				} else {
					if (!TextUtils.isEmpty(user.token)) {
						final Intent intent = new Intent();
						intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
						intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, BuildConfig.PACKAGE_NAME);
						intent.putExtra(AccountManager.KEY_AUTHTOKEN, user.token);
						intent.putExtra(ARG_PASSWORD, password);
						finishLogin(intent);
					} else {

						Toast.makeText(LoginActivity.this, "Login failed: invalid token", Toast.LENGTH_LONG).show();
					}
				}
			}

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void finishLogin(Intent intent) {
		String username = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		String password = intent.getStringExtra(ARG_PASSWORD);
		final Account account = new Account(username, BuildConfig.PACKAGE_NAME);

			String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
			mAccountManager.addAccountExplicitly(account, password, null);
			mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);

		setAccountAuthenticatorResult(intent.getExtras());
		setResult(Activity.RESULT_OK, intent);

		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// The sign up activity returned that the user has successfully created an account
		if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
			finishLogin(data);
		} else
			super.onActivityResult(requestCode, resultCode, data);
	}
}
