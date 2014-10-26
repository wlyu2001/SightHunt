package com.sighthunt.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sighthunt.inject.Injector;
import com.sighthunt.location.LocationHelper;
import com.sighthunt.util.AccountUtils;

public abstract class LocationConnectivityAwareActivity extends FragmentActivity implements LocationHelper.OnLocationObserver {

	private LocationHelper mLocationHelper;

	private BroadcastReceiver mNetworkStateChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			AccountUtils accountUtils = Injector.get(AccountUtils.class);

			if (accountUtils.getUsername() != null && accountUtils.getUer() == null) {
				accountUtils.fetchUser();
			}
			if (mLocationHelper.isConnected()) {
				mLocationHelper.requestRegion();
			} else {
				mLocationHelper.connect();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLocationHelper = new LocationHelper(this);
	}

	@Override
	public abstract void onLocationConnected();

	@Override
	public abstract void onRegionUpdated(String region, boolean changed);

	@Override
	public abstract void onLocationDisconnected();

	protected void onStart() {
		super.onStart();
		mLocationHelper.onStart();
		IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mNetworkStateChangeReceiver, filter);
	}

	@Override
	protected void onStop() {
		mLocationHelper.onStop();
		super.onStop();
		unregisterReceiver(mNetworkStateChangeReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLocationHelper.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationHelper.onPause();
	}

	public LocationHelper getLocationHelper() {
		return mLocationHelper;
	}

}
