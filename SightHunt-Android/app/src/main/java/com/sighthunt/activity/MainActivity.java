package com.sighthunt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sighthunt.R;
import com.sighthunt.fragment.BrowseFragment;
import com.sighthunt.fragment.LocationAwareFragment;
import com.sighthunt.fragment.ResultsFragment;
import com.sighthunt.inject.Injector;
import com.sighthunt.util.AccountUtils;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends LocationAwareActivity {

	View mButtonList;
	View mButtonBrowse;
	View mButtonCam;

	LocationAwareFragment mBrowseFragment;
	private String TAG_BROWSE_FRAGMENT = "tag_browse_fragment";
	private String TAG_RESULT_FRAGMENT = "tag_result_fragment";
	ResultsFragment mResultsFragment;

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setIcon(android.R.color.transparent);

		mButtonList = findViewById(R.id.button_list);
		mButtonBrowse = findViewById(R.id.button_browse);
		mButtonCam = findViewById(R.id.button_cam);

		mBrowseFragment = BrowseFragment.createInstance();
		mResultsFragment = ResultsFragment.createInstance();

		showBrowseFragment();

		mButtonList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showListFragment();
			}
		});

		mButtonBrowse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showBrowseFragment();
			}
		});

		requestToken();

		mButtonCam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, ReleaseActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});
	}

	private void requestToken() {
		boolean inProcess = mAccountUtils.getToken(this, new AccountUtils.TokenRequestCallback() {
			@Override
			public void onTokenRequestCompleted(String token) {
			}

			@Override
			public void onTokenRequestFailed() {
				finish();
			}
		});
		if (!inProcess) {
			finish();
		}
	}

	boolean mBrowseShown;

	private void showBrowseFragment() {
		FragmentTransaction ft =
				getSupportFragmentManager().beginTransaction();
		if (mBrowseFragment.isAdded()) {
			ft.show(mBrowseFragment);
		} else {
			ft.add(R.id.container, mBrowseFragment, TAG_BROWSE_FRAGMENT);
		}
		if (mResultsFragment.isAdded()) {
			ft.hide(mResultsFragment);
		}
		ft.commit();

		getActionBar().setTitle(getString(R.string.title_explore).toUpperCase() +
				(getLocationHelper().isConnected() ? "  " + getLocationHelper().getRegion() : ""));
		mBrowseShown = true;

	}

	private void showListFragment() {
		FragmentTransaction ft =
				getSupportFragmentManager().beginTransaction();
		if (mResultsFragment.isAdded()) {
			ft.show(mResultsFragment);
		} else {
			ft.add(R.id.container, mResultsFragment, TAG_RESULT_FRAGMENT);
		}
		if (mBrowseFragment.isAdded()) {
			ft.hide(mBrowseFragment);
		}
		ft.commit();

		getActionBar().setTitle(getString(R.string.title_results).toUpperCase());
		mBrowseShown = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_logout) {
			mAccountUtils.logoutAndClearAccounts(new AccountUtils.ClearAccountCallback() {
				@Override
				public void onClearAccount() {
					requestToken();
				}
			});
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationConnected() {
	}

	@Override
	public void onLocationUpdated() {
		if (mBrowseShown) {
			getActionBar().setTitle(getString(R.string.title_explore).toUpperCase() +
					(getLocationHelper().isConnected() ? "  " + getLocationHelper().getRegion() : ""));
		}
		if (mBrowseFragment != null) {
			mBrowseFragment.onLocationUpdated();
		}
	}

	@Override
	public void onLocationDisconnected() {

	}

}
