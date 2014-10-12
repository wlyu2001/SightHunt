package com.sighthunt.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sighthunt.activity.LocationAwareActivity;
import com.sighthunt.location.LocationHelper;


public abstract class LocationAwareFragment extends Fragment {

	LocationHelper mHelper;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mHelper = ((LocationAwareActivity) getActivity()).getLocationHelper();
	}

	public void onLocationUpdated() {
	}

	public String getRegion() {
		if (!mHelper.isConnected()) return null;
		return mHelper.getCurrentRegion();
	}

	public Location getCurrentLocation() {
		if (!mHelper.isConnected()) return null;
		return mHelper.getLocation();
	}
}
