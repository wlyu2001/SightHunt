package com.sighthunt.location;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.sighthunt.dialog.DialogFactory;
import com.sighthunt.dialog.DialogPresenter;

public class LocationHelper implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private final String KEY_UPDATES_ON = "key_updates_on";
	private static final long UPDATE_INTERVAL = 60 * 60 * 1000;
	private static final long FASTEST_INTERVAL = 10 * 60 * 1000;
	private boolean mUpdatesRequested;
	private LocationRequest mLocationRequest;

	FragmentActivity mActivity;
	LocationClient mLocationClient;
	private SharedPreferences mPrefs;

	public LocationHelper(FragmentActivity activity) {
		final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (result != ConnectionResult.SUCCESS) {
			Toast.makeText(activity, "Google Play service is not available (status=" + result + ")", Toast.LENGTH_LONG).show();
			activity.finish();
		}
		mActivity = activity;
		mLocationClient = new LocationClient(mActivity, this, this);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	}

	public void onStart() {
		mLocationClient.connect();
	}

	public void onStop() {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
	}

	public void onResume() {
		if (mPrefs.contains(KEY_UPDATES_ON)) {
			mUpdatesRequested = mPrefs.getBoolean(KEY_UPDATES_ON, false);
		} else {
			mPrefs.edit().putBoolean(KEY_UPDATES_ON, false).commit();
		}
	}

	public void onPause() {
		mPrefs.edit().putBoolean(KEY_UPDATES_ON, mUpdatesRequested).commit();
	}

	@Override
	public void onConnected(Bundle bundle) {
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			DialogPresenter.showDialog(mActivity.getSupportFragmentManager(), DialogFactory.getConnectionFailedDialog(mActivity), "Connection failed");
		}
	}

	public Location getCurrentLocation() {
		return mLocationClient.getLastLocation();
	}

	@Override
	public void onLocationChanged(Location location) {

	}
}
