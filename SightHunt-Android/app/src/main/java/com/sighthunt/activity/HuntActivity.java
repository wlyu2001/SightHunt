package com.sighthunt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.sighthunt.R;
import com.sighthunt.data.model.Sight;
import com.sighthunt.fragment.SightFragment;

public class HuntActivity extends LocationConnectivityAwareActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hunt);

		getActionBar().setIcon(R.drawable.close);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(null);

		Bundle args = getIntent().getExtras();
		Sight sight = args.getParcelable(Sight.ARG);

		SightFragment fragment = SightFragment.createInstance(sight);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
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

	}

	@Override
	public void onLocationDisconnected() {

	}

	public static Intent getIntent(Context context, Sight sight) {
		Intent intent = new Intent(context, HuntActivity.class);
		intent.putExtra(Sight.ARG, sight);
		return intent;
	}

}
