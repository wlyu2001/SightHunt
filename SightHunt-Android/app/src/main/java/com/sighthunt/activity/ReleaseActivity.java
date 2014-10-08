package com.sighthunt.activity;

import android.os.Bundle;
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

		mReleaseCamFragment = new ReleaseCamFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mReleaseCamFragment)
				.commit();

		Button closeButton = (Button) findViewById(R.id.button_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void onLocationConnected() {
		if (mReleaseCamFragment != null)
			mReleaseCamFragment.onLocationUpdated();
	}

	@Override
	public void onLocationUpdated() {
		if (mReleaseCamFragment != null)
			mReleaseCamFragment.onLocationUpdated();

	}

	@Override
	public void onLocationDisconnected() {

	}

}
