package com.sighthunt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.fragment.BrowseFragment;
import com.sighthunt.fragment.ResultsFragment;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.SightHuntService;
import com.sighthunt.network.model.User;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.Scores;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends LocationConnectivityAwareActivity implements AccountUtils.UserUpdatedCallback {

	ImageButton mButtonList;
	ImageButton mButtonBrowse;
	ImageButton mButtonCam;

	BrowseFragment mBrowseFragment;
	private String TAG_BROWSE_FRAGMENT = "tag_browse_fragment";
	private String TAG_RESULT_FRAGMENT = "tag_result_fragment";
	ResultsFragment mResultsFragment;

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setIcon(android.R.color.transparent);

		mButtonList = (ImageButton) findViewById(R.id.button_list);
		mButtonBrowse = (ImageButton) findViewById(R.id.button_browse);
		mButtonCam = (ImageButton) findViewById(R.id.button_cam);
		mBrowseFragment = BrowseFragment.createInstance();
		mResultsFragment = ResultsFragment.createInstance();

		FragmentTransaction ft =
				getSupportFragmentManager().beginTransaction();
		ft.add(R.id.container, mBrowseFragment, TAG_BROWSE_FRAGMENT).commit();
		String region = getLocationHelper().getCurrentRegion();
		getActionBar().setTitle(getString(R.string.title_explore).toUpperCase() + "  " + (region == null ? "Unknown region" : region));
		mButtonBrowse.setSelected(true);

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
				if (mAccountUtils.getUer() != null) {
					tryOpenReleaseActivity(mAccountUtils.getUer());
				} else {
					mRequestToOpenReleaseActivity = true;
					mAccountUtils.fetchUser();
				}
			}
		});
	}

	private boolean mRequestToOpenReleaseActivity = false;

	private void tryOpenReleaseActivity(@NotNull User user) {
		if (getLocationHelper().getCurrentRegion() == null) {
			Toast.makeText(MainActivity.this, getString(R.string.release_in_unknown_region), Toast.LENGTH_LONG).show();
			return;
		}
		if (user.points > Scores.NEW_SIGHT_COST) {
			startActivity(new Intent(MainActivity.this, ReleaseActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		} else {
			Toast.makeText(MainActivity.this, getString(R.string.not_enough_points), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mAccountUtils.addUserUpdatedCallback(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		mAccountUtils.removeUserUpdatedCallback(this);
	}

	private void requestToken() {
		boolean inProcess = mAccountUtils.getToken(this, new AccountUtils.TokenRequestCallback() {
			@Override
			public void onTokenRequestCompleted(String token) {
				startService(SightHuntService.getFetchHuntsIntent(MainActivity.this, mAccountUtils.getUsername()));
				mAccountUtils.fetchUser();
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

	private void showBrowseFragment() {
		FragmentTransaction ft =
				getSupportFragmentManager().beginTransaction();

		ft.replace(R.id.container, mBrowseFragment, TAG_BROWSE_FRAGMENT).addToBackStack(TAG_BROWSE_FRAGMENT);
		ft.commit();
	}

	public void updateUIForBrowse() {
		showRegion();
		mButtonBrowse.setSelected(true);
		mButtonList.setSelected(false);
	}

	public void updateUIForResult() {
		showPoints();
		mButtonList.setSelected(true);
		mButtonBrowse.setSelected(false);
	}

	private void showListFragment() {
		FragmentTransaction ft =
				getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.container, mResultsFragment, TAG_RESULT_FRAGMENT).addToBackStack(TAG_RESULT_FRAGMENT);
		ft.commit();
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
		if (id == R.id.action_logout) {
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
	public void onRegionUpdated(String region, boolean changed) {
		if (mBrowseFragment.isVisible()) {
			getActionBar().setTitle(getString(R.string.title_explore).toUpperCase() + "  " + (region == null ? getString(R.string.unknown_region) : region));
		}
		if (mBrowseFragment != null) {
			mBrowseFragment.onRegionUpdated(region, changed);
		}
	}

	@Override
	public void onLocationDisconnected() {

	}

	@Override
	public void userUpdated() {
		if (!mBrowseFragment.isVisible()) {
			showPoints();
		}
		if (mRequestToOpenReleaseActivity) {
			if (mAccountUtils.getUer() != null) {
				tryOpenReleaseActivity(mAccountUtils.getUer());
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.user_info_unavailable), Toast.LENGTH_LONG).show();
			}
			mRequestToOpenReleaseActivity = false;
		}
	}

	private void showRegion() {
		String region = getLocationHelper().getCurrentRegion();
		getActionBar().setTitle(getString(R.string.title_explore).toUpperCase() + "  " + (region == null ? getString(R.string.unknown_region) : region));
	}

	private void showPoints() {
		User user = mAccountUtils.getUer();
		getActionBar().setTitle(getString(R.string.title_results).toUpperCase() +
				(user == null ? "" : "  " + user.points + " points"));
	}
}
