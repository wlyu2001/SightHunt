package com.sighthunt.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.sighthunt.data.SightHuntContract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightSortType;
import com.sighthunt.network.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SightHuntService extends Service {

	public static final String SIGHTHUNT_SERVICE_URI = "com.sighthunt.service";
	public static final String ACTION_FETCH_SIGHTS_BY_REGION = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_region";
	public static final String ACTION_FETCH_SIGHTS_BY_USER = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_user";
	public static final String ACTION_LOG_IN = SIGHTHUNT_SERVICE_URI + ".action.verify_or_create_user";

	public static final String ARG_REGION = "arg_region";
	public static final String ARG_USER = "arg_user";

	private static final java.lang.String ARG_SIGHT_TYPE = "arg_sight_type";

	ApiManager mApiManager = Injector.get(ApiManager.class);

	protected void onStartCommand(Intent intent) {
		String action = intent.getAction();
		Bundle args = intent.getExtras();
		if (ACTION_FETCH_SIGHTS_BY_REGION.equals(action)) {
			fetchSightsByRegion(args);
		} else if (ACTION_FETCH_SIGHTS_BY_USER.equals(action)) {
			fetchSightsByUser(args);
		}
	}

	private void fetchSightsByRegion(Bundle args) {
		final String region = args.getString(ARG_REGION);
		final String type = args.getString(ARG_SIGHT_TYPE);
		mApiManager.getSightService().getSightsByRegion(region, type, new Callback<List<Sight>>() {
			@Override
			public void success(List<Sight> sights, Response response) {
				// getContentResolver().insert();
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});
	}

	private void fetchSightsByUser(Bundle args) {
		final String user = args.getString(ARG_USER);
		final String type = args.getString(ARG_SIGHT_TYPE);
		mApiManager.getSightService().getSightsByUser(user, type, new Callback<List<Sight>>() {
			@Override
			public void success(List<Sight> sights, Response response) {
				// getContentResolver().insert();

				//getContext().getContentResolver().notifyChange(tweetUri, null);
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});
	}

	public static Intent getFetchSightsByRegionIntent(Context context, @NotNull String region, SightSortType.ByRegion type) {
		Intent i = new Intent(ACTION_FETCH_SIGHTS_BY_REGION);
		i.setClass(context, SightHuntService.class);
		i.putExtra(ARG_REGION, region);
		i.putExtra(ARG_SIGHT_TYPE, type.toString());
		return i;
	}

	public static Intent getFetchSightsByUserIntent(Context context, @NotNull String user, SightSortType.ByUser type) {
		Intent i = new Intent(ACTION_FETCH_SIGHTS_BY_USER);
		i.setClass(context, SightHuntService.class);
		i.putExtra(ARG_USER, user);
		i.putExtra(ARG_SIGHT_TYPE, type.toString());
		return i;
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
