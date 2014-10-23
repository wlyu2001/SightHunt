package com.sighthunt.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sighthunt.location.LocationHelper;

public abstract class LocationAwareActivity extends FragmentActivity implements LocationHelper.OnLocationObserver {

	private LocationHelper mLocationHelper;

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
	}

	@Override
	protected void onStop() {
		mLocationHelper.onStop();
		super.onStop();
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
	};

}
