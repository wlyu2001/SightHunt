package com.sighthunt.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session;
import com.sighthunt.inject.Injectable;

public class AccountUtils implements Injectable {

	public static final String ACCOUNT_TYPE = "com.sighthunt";
	public static final String AUTH_TOKEN_TYPE = "default";

	private Context mContext;

	public void logoutAndClearAccounts(final ClearAccountCallback callback) {

		Session session = Session.getActiveSession();

		if (session == null) {
			session = Session.openActiveSessionFromCache(mContext);
		}

		if (session != null && session.isOpened()) {
			session.closeAndClearTokenInformation();
		}

		Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
		for (Account account : accounts) {
			mAccountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
				@Override
				public void run(AccountManagerFuture<Boolean> future) {
					callback.onClearAccount();
				}
			}, null);
		}

	}

	public interface TokenRequestCallback {
		public void onTokenRequestCompleted(String token);

		public void onTokenRequestFailed();
	}

	public interface ClearAccountCallback {
		public void onClearAccount();
	}

	private AccountManager mAccountManager;
	private String mUsername;
	private String mToken;
	private Activity mActivity;
	private TokenRequestCallback mTokenRequestCallback;

	public AccountUtils(Context context) {
		mContext = context;
		mAccountManager = AccountManager.get(context);
	}

	public boolean getToken(Activity activity, TokenRequestCallback callback) {
		mActivity = activity;
		mTokenRequestCallback = callback;

		Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
		if (accounts.length == 0) {
			addAccount(activity);
			return false;
		} else {
			mUsername = accounts[0].name;
			getTokenForAccount(activity, accounts[0]);
			return true;
		}
	}

	public String getUsername() {
		return mUsername;
	}

	public String getToken() {
		return mToken;
	}

	public void getTokenForAccount(Activity activity, Account account) {

		mAccountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, activity, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle result = future.getResult();
					mToken = result.getString(AccountManager.KEY_AUTHTOKEN);
					Log.i("Token fetched", mToken);
					if (mTokenRequestCallback != null) {
						mTokenRequestCallback.onTokenRequestCompleted(mToken);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (mTokenRequestCallback != null) {
						mTokenRequestCallback.onTokenRequestFailed();
					}
				}
			}
		}, null);
	}

	public void addAccount(final Activity activity) {
		AccountManagerFuture<Bundle> future = mAccountManager.addAccount(ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null, null, activity, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle result = future.getResult();
					String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
					getTokenForAccount(activity, new Account(name, ACCOUNT_TYPE));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, null);
	}

	public void invalidateToken() {
		mAccountManager.invalidateAuthToken(ACCOUNT_TYPE, mToken);
		getToken(mActivity, mTokenRequestCallback);
	}
}
