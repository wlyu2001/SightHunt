package com.sighthunt.fragment;

import android.app.Activity;
import android.location.Location;
import android.support.v4.app.Fragment;

import com.sighthunt.activity.LocationConnectivityAwareActivity;
import com.sighthunt.location.LocationHelper;


public abstract class LocationAwareFragment extends Fragment {

	LocationHelper mHelper;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mHelper = ((LocationConnectivityAwareActivity) getActivity()).getLocationHelper();
	}

	public abstract void onRegionUpdated(String region, boolean changed);

	public void requestRegion() {
		mHelper.requestRegion();
	}

	public String getRegion() {
		return mHelper.getCurrentRegion();
	}

	public Location getCurrentLocation() {
		return mHelper.getLocation();
	}
}
