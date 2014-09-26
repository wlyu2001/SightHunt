package com.sighthunt.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.sighthunt.R;
import com.sighthunt.fragment.SightFragment;
import com.sighthunt.location.LocationHelper;

public class HuntActivity extends FragmentActivity {

	LocationHelper mLocationHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hunt);

		mLocationHelper = new LocationHelper(this);

		SightFragment fragment = new SightFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.commit();

		Button closeButton = (Button) findViewById(R.id.button_close);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	protected void onStart() {
		super.onStart();
		mLocationHelper.onStart();
	}
	@Override
	protected void onStop() {
		mLocationHelper.onStop();
		super.onStop();
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
