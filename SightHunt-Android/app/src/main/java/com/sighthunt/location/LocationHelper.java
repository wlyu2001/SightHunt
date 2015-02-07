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
import com.sighthunt.activity.LocationConnectivityAwareActivity;
import com.sighthunt.dialog.DialogFactory;
import com.sighthunt.dialog.DialogPresenter;
import com.sighthunt.util.NetworkHelper;
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

	private FragmentActivity mActivity;
	private LocationClient mLocationClient;
	private SharedPreferences mPrefs;

	private Geocoder geocoder;

	private OnLocationObserver mObserver;

	public interface OnLocationObserver {
		public void onLocationConnected();

		public void onLocationDisconnected();

		public void onRegionUpdated(String region, boolean changed);
	}

	public boolean isConnected() {
		return mLocationClient.isConnected();
	}

	public LocationHelper(LocationConnectivityAwareActivity activity) {
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
		geocoder = new Geocoder(activity, Locale.US);
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

	public void connect() {
		mLocationClient.connect();
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
		mObserver.onLocationConnected();

	}

	@Override
	public void onDisconnected() {
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
	}

	public Location getLocation() {
		if (isConnected())
			return mLocationClient.getLastLocation();
		else return null;
	}

	String mRegion;

	@Override
	public void onLocationChanged(Location location) {
		String region = null;
		if (location != null) {
			region = getRegionForLocation(location);
		}
		if (region != null && !region.equals(mRegion)) {
			mRegion = region;
			mObserver.onRegionUpdated(region, true);
		} else {
			mObserver.onRegionUpdated(mRegion, false);
		}
	}

//	public String getLastKnownRegion() {
//		return mRegion;
//	}

	private String getRegionForLocation(@NotNull Location location) {
		String region = null;
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				if (address.getLocality() != null) {
					region = address.getLocality() + ", " + address.getCountryName();
				} else if (address.getAdminArea() != null) {
					region = address.getAdminArea() + ", " + address.getCountryName();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return region;
	}

	public void requestRegion() {
		String region = null;
		if (isConnected() && NetworkHelper.isConnected(mActivity)) {
			Location location = mLocationClient.getLastLocation();
			region = getRegionForLocation(location);
		}
		if (region != null && !region.equals(mRegion)) {
			mRegion = region;
			mObserver.onRegionUpdated(region, true);
		} else {
			mObserver.onRegionUpdated(mRegion, false);
		}
	}

	public String getCurrentRegion() {
		return mRegion;
	}
}
