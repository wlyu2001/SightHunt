package com.sighthunt.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.auth.AccountAuthenticatorActivity;
import com.sighthunt.auth.ServerAuthenticate;
import com.sighthunt.data.SightHuntContract;
import com.sighthunt.fragment.login.LoginFragment;
import com.sighthunt.fragment.login.SignUpFragment;
import com.sighthunt.fragment.login.WelcomeFragment;
import com.sighthunt.network.SightHuntService;
import com.sighthunt.network.model.User;
import com.sighthunt.util.AccountUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AccountAuthenticatorActivity {


	public static final String ARG_PASSWORD = "arg_password";
	public static final String ARG_NEW_ACCOUNT = "arg_new_account";
	//public static final String ARG_ACCOUNT_TYPE = "arg_account_type";
	//public static final String ARG_AUTH_TOKEN_TYPE = "arg_auth_token_type";

	private final int REQ_SIGNUP = 1;

	private AccountManager mAccountManager;
	private ServerAuthenticate mServerAuthenticate;

	//private String mAuthTokenType;
	private boolean mNewAccount;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("authenticator", "show LoginActivity");

		setContentView(R.layout.activity_login);
		setup(getIntent());
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

	@Override
	public void onNewIntent(Intent intent) {
		setup(intent);
	}

	private void setup(Intent intent) {
		Bundle args = intent.getExtras();
		//mAuthTokenType = args.getString(ARG_AUTH_TOKEN_TYPE);
		mNewAccount = args.getBoolean(ARG_NEW_ACCOUNT, false);
	}

	public void login(final String username, final String password) {

		mServerAuthenticate.loginAsync(username, password, AccountUtils.AUTH_TOKEN_TYPE, new Callback<User>() {
			@Override
			public void success(User user, Response response) {
				final Intent intent = new Intent();
				intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
				intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountUtils.ACCOUNT_TYPE);
				intent.putExtra(AccountManager.KEY_AUTHTOKEN, user.token);
				intent.putExtra(ARG_PASSWORD, password);

				Toast.makeText(LoginActivity.this, "Token " + user.token, Toast.LENGTH_LONG).show();
				finishLogin(intent);
			}

			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void finishLogin(Intent intent) {
		String username = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		String password = intent.getStringExtra(ARG_PASSWORD);
		final Account account = new Account(username, AccountUtils.ACCOUNT_TYPE);
		if (mNewAccount) {
			String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
			mAccountManager.addAccountExplicitly(account, password, null);
			mAccountManager.setAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, authToken);
		} else {
			mAccountManager.setPassword(account, password);
		}
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
