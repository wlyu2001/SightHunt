package com.sighthunt.location;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.sighthunt.activity.LocationAwareActivity;
import com.sighthunt.dialog.DialogFactory;
import com.sighthunt.dialog.DialogPresenter;
import com.sighthunt.inject.Injector;
import com.sighthunt.util.PreferenceUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

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
	private boolean mIsConnected;

	private FragmentActivity mActivity;
	private LocationClient mLocationClient;
	private SharedPreferences mPrefs;

	private Geocoder geocoder;

	private OnLocationObserver mObserver;

	public interface OnLocationObserver {
		public void onLocationConnected();

		public void onLocationUpdated();

		public void onLocationDisconnected();
	}

	public boolean isConnected() {
		return mIsConnected;
	}

	public LocationHelper(LocationAwareActivity activity) {
		mObserver = activity;
		final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (result != ConnectionResult.SUCCESS) {
			Toast.makeText(activity, "Google Play service is not available (status=" + result + ")", Toast.LENGTH_LONG).show();
			activity.finish();
		}
		mActivity = activity;
		mLocationClient = new LocationClient(mActivity, this, this);
		mPrefs = PreferenceUtil.getSettingSharedPreferences(mActivity);

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		geocoder = new Geocoder(activity, Locale.getDefault());
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

		LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			DialogPresenter.showDialog(mActivity.getSupportFragmentManager(), DialogFactory.getLocationDisabledDialog(mActivity), "Location Disabled");
		}

		//if (mUpdatesRequested) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		//}
		mIsConnected = true;
		mObserver.onLocationConnected();

	}

	@Override
	public void onDisconnected() {
		mIsConnected = false;
		mObserver.onLocationDisconnected();
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
		mIsConnected = false;
	}

	public Location getLocation() {
		return mLocationClient.getLastLocation();
	}

	String mRegion;

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			getRegionForLocation(location);
		}

		mObserver.onLocationUpdated();
	}

	public String getLastKnownRegion() {
		return mRegion;
	}

	public String getRegionForLocation(@NotNull Location location) {
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (addresses.size() > 0) {
				mRegion = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mRegion;
	}

	public String getCurrentRegion() {
		String region = null;
		Location location = mLocationClient.getLastLocation();
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (addresses.size() > 0) {
				region = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return region;
	}
}
