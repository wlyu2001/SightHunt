package com.sighthunt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sighthunt.R;
import com.sighthunt.data.Contract;
import com.sighthunt.fragment.SightFragment;

public class HuntActivity extends LocationAwareActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hunt);

		Bundle args = getIntent().getExtras();
		String key = args.getString(Contract.Sight.KEY);

		SightFragment fragment = SightFragment.createInstance(key);
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

	@Override
	public void onLocationConnected() {

	}

	@Override
	public void onLocationUpdated() {

	}

	@Override
	public void onLocationDisconnected() {

	}

	public static Intent getIntent(Context context, String key) {
		Intent intent = new Intent(context, HuntActivity.class);
		intent.putExtra(Contract.Sight.KEY, key);
		return intent;
	}

}
