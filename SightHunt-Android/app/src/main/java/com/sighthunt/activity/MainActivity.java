package com.sighthunt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sighthunt.R;
import com.sighthunt.fragment.BaseFragment;
import com.sighthunt.fragment.BrowseFragment;
import com.sighthunt.fragment.ResultsFragment;
import com.sighthunt.inject.Injector;
import com.sighthunt.location.LocationHelper;
import com.sighthunt.util.AccountUtils;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends FragmentActivity {

	View mButtonList;
	View mButtonBrowse;
	View mButtonCam;

	BaseFragment mBrowseFragment;
	BaseFragment mListFragment;

	LocationHelper mLocationHelper;
	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLocationHelper = new LocationHelper(this);

		Log.i("mainactivity", "created");

		getActionBar().setIcon(android.R.color.transparent);

		showFragment(new BrowseFragment());

		mButtonList = findViewById(R.id.button_list);
		mButtonBrowse = findViewById(R.id.button_browse);
		mButtonCam = findViewById(R.id.button_cam);

		mBrowseFragment = BrowseFragment.createInstance();
		mListFragment = ResultsFragment.createInstance();

		mButtonList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFragment(mListFragment);
			}
		});

		mButtonBrowse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFragment(mBrowseFragment);
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

	private void showFragment(BaseFragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment, null)
				.commit();
		getActionBar().setTitle(fragment.getDefaultTitle().toUpperCase());
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

	protected void onStart() {
		super.onStart();
		mLocationHelper.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationHelper.onStop();
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
