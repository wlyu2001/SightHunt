package com.sighthunt.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;

import com.facebook.Session;

public class AccountUtils {

	public static final String ACCOUNT_TYPE = "com.sighthunt";
	public static final String AUTH_TOKEN_TYPE = "default";

	public void clearAccount(final ClearAccountCallback callback) {

		Session session = Session.getActiveSession();

		if (session == null) {
			session = Session.openActiveSessionFromCache(mActivity);
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

	Activity mActivity;
	AccountManager mAccountManager;

	public AccountUtils(Activity activity) {
		mActivity = activity;
		mAccountManager = AccountManager.get(activity);
	}

	public boolean getToken(final TokenRequestCallback callback) {
		Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
		if (accounts.length == 0) {
			addAccount(callback);
			return false;
		} else {
			getTokenForAccount(accounts[0], callback);
			return true;
		}
	}

	public void getTokenForAccount(Account account, final TokenRequestCallback callback) {
		mAccountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, mActivity, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle result = future.getResult();
					String token = result.getString(AccountManager.KEY_AUTHTOKEN);
					callback.onTokenRequestCompleted(token);
				} catch (Exception e) {
					e.printStackTrace();
					callback.onTokenRequestFailed();
				}
			}
		}, null);
	}

	public void addAccount(final TokenRequestCallback callback) {
		AccountManagerFuture<Bundle> future = mAccountManager.addAccount(ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null, null, mActivity, new AccountManagerCallback<Bundle>() {
			@Override
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					Bundle result = future.getResult();
					String name = result.getString(AccountManager.KEY_ACCOUNT_NAME);
					getTokenForAccount(new Account(name, ACCOUNT_TYPE), callback);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, null);
	}
}
