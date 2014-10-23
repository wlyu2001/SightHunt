package com.sighthunt.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sighthunt.R;
import com.sighthunt.fragment.release.ReleaseCamFragment;

public class ReleaseActivity extends LocationAwareActivity {

	ReleaseCamFragment mReleaseCamFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_release);


		getActionBar().setIcon(R.drawable.close);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(null);

		mReleaseCamFragment = new ReleaseCamFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mReleaseCamFragment)
				.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLocationConnected() {
	}

	@Override
	public void onRegionUpdated(String region, boolean changed) {
		if (mReleaseCamFragment != null)
			mReleaseCamFragment.onRegionUpdated(region, changed);

	}

	@Override
	public void onLocationDisconnected() {

	}

}
