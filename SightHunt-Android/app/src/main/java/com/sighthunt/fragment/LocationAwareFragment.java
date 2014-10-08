package com.sighthunt.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sighthunt.activity.LocationAwareActivity;
import com.sighthunt.location.LocationHelper;


public abstract class LocationAwareFragment extends Fragment {

	private String mRegion;
	private Location mLocation;
	LocationHelper mHelper;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mHelper = ((LocationAwareActivity) getActivity()).getLocationHelper();
		if (mHelper.isConnected()) {
			mRegion = mHelper.getRegion();
			mLocation = mHelper.getLocation();
		}
	}

	public void onLocationUpdated() {
		if (mHelper.isConnected()) {
			mRegion = mHelper.getRegion();
			mLocation = mHelper.getLocation();
		}
	}

	public String getRegion() {
		return mRegion;
	}

	public Location getCurrentLocation() {
		return mLocation;
	}
}
