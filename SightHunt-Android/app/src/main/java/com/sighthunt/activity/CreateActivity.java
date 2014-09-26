package com.sighthunt.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sighthunt.R;
import com.sighthunt.location.LocationHelper;

public class CreateActivity extends FragmentActivity {

	LocationHelper mLocationHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		mLocationHelper = new LocationHelper(this);
	}

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
}
