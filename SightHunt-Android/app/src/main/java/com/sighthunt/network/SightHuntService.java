package com.sighthunt.network;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.sighthunt.data.Contract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightSortType;
import com.sighthunt.util.AccountUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class SightHuntService extends Service {

	public static final String SIGHTHUNT_SERVICE_URI = "com.sighthunt.service";
	public static final String ACTION_FETCH_SIGHTS_BY_REGION = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_region";
	public static final String ACTION_FETCH_SIGHTS_BY_USER = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_user";

	public static final String ACTION_INSERT_SIGHTS = SIGHTHUNT_SERVICE_URI + ".action.insert_sight";

	private static final java.lang.String ARG_SIGHT_TYPE = "arg_sight_type";

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);

	ApiManager mApiManager = Injector.get(ApiManager.class);

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent == null) {
			return START_NOT_STICKY;
		}

		String action = intent.getAction();
		Bundle args = intent.getExtras();
		if (ACTION_FETCH_SIGHTS_BY_REGION.equals(action)) {
			fetchSightsByRegion(args);
		} else if (ACTION_FETCH_SIGHTS_BY_USER.equals(action)) {
			fetchSightsByUser(args);
		} else if (ACTION_INSERT_SIGHTS.equals(action)) {
			insertSight(args);
		}

		return START_STICKY;
	}


	private void insertSight(Bundle args) {
		final Sight sight = new Sight();
		sight.region = args.getString(Contract.Sight.REGION);
		sight.description = args.getString(Contract.Sight.DESCRIPTION);
		sight.title = args.getString(Contract.Sight.TITLE);
		sight.lon = args.getFloat(Contract.Sight.LON);
		sight.lat = args.getFloat(Contract.Sight.LAT);
		final String imageUri = args.getString(Contract.Sight.IMAGE_URI);
		sight.creator = mAccountUtils.getUsername();
		TypedFile file = new TypedFile("image/jpeg", new File(imageUri));
		Log.i("Image file", file.fileName());
		mApiManager.getSightService().createSight(sight, file, new Callback<Sight>() {
			@Override
			public void success(Sight s, Response response) {
//				sight.key = s.key;
//				sight.image_uri = s.image_uri;
//				sight.time_created = s.time_created;
//				sight.votes = 0;
//				sight.hunts = 0;
//				ContentValues values = Contract.Sight.createContentValues(sight);
//				getContentResolver().insert(Contract.Sight.getCreateSightServerUri(), values);
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});
	}

	private void fetchSightsByRegion(Bundle args) {
		final String region = args.getString(Contract.Sight.REGION);
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
		final String user = args.getString(Contract.Sight.CREATOR);
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
		i.putExtra(Contract.Sight.REGION, region);
		i.putExtra(ARG_SIGHT_TYPE, type.toString());
		return i;
	}

	public static Intent getInsertSightIntent(Context context, String region, String title, String description, String image, float lon, float lat) {
		Intent i = new Intent(ACTION_INSERT_SIGHTS);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.REGION, region);
		i.putExtra(Contract.Sight.TITLE, title);
		i.putExtra(Contract.Sight.DESCRIPTION, description);
		i.putExtra(Contract.Sight.IMAGE_URI, image);
		i.putExtra(Contract.Sight.LON, lon);
		i.putExtra(Contract.Sight.LAT, lat);
		return i;
	}

	public static Intent getFetchSightsByUserIntent(Context context, @NotNull String user, SightSortType.ByUser type) {
		Intent i = new Intent(ACTION_FETCH_SIGHTS_BY_USER);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.CREATOR, user);
		i.putExtra(ARG_SIGHT_TYPE, type.toString());
		return i;
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
