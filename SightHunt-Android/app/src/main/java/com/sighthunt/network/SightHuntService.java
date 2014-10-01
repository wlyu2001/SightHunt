package com.sighthunt.network;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sighthunt.data.Contract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightSortType;
import com.sighthunt.util.AccountUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SightHuntService extends Service {

	public static final String SIGHTHUNT_SERVICE_URI = "com.sighthunt.service";
	public static final String ACTION_FETCH_SIGHTS_BY_REGION = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_region";
	public static final String ACTION_FETCH_SIGHTS_BY_USER = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_user";

	public static final String ACTION_INSERT_SIGHTS = SIGHTHUNT_SERVICE_URI + ".action.insert_sight";

	private static final java.lang.String ARG_SIGHT_TYPE = "arg_sight_type";


	private SharedPreferences mPrefs;

	private static final String PREF_LAST_MODIFIED_NEW = "prefs_last_modified_new";
	private static final String PREF_LAST_MODIFIED_MOST_VOTED = "prefs_last_modified_most_voted";
	private static final String PREF_LAST_MODIFIED_MOST_HUNTED = "prefs_last_modified_most_hunted";

	ApiManager mApiManager = Injector.get(ApiManager.class);


	@Override
	public void onCreate() {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

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
		sight.time_created = args.getLong(Contract.Sight.TIME_CREATED);
		sight.last_modified = args.getLong(Contract.Sight.LAST_MODIFIED);
		sight.creator = args.getString(Contract.Sight.CREATOR);
		// here the key is actually the image path
		final String imageKey = args.getString(Contract.Sight.IMAGE_KEY);
		final String thumbKey = args.getString(Contract.Sight.THUMB_KEY);
		mApiManager.getSightService().createSight(sight, new Callback<Sight>() {
			@Override
			public void success(Sight s, Response response) {
				if (s == null) return;
				Log.i("Sight key", s.key);
				String uploadUrl = s.image_key;
				mApiManager.uploadImage(uploadUrl, s.key, imageKey, thumbKey, new Callback<Sight>() {
					@Override
					public void success(Sight sight, Response response) {
					}

					@Override
					public void failure(RetrofitError error) {
					}
				});
			}

			@Override
			public void failure(RetrofitError error) {

				Log.i("post sight failed", error.getMessage());
			}
		});
	}

	private long getLastModified(String type) {
		if (SightSortType.ByRegion.NEW.toString().equals(type)) {
			return mPrefs.getLong(PREF_LAST_MODIFIED_NEW, 0);
		} else if (SightSortType.ByRegion.MOST_HUNTED.toString().equals(type)) {
			return mPrefs.getLong(PREF_LAST_MODIFIED_MOST_HUNTED, 0);
		} else if (SightSortType.ByRegion.MOST_VOTED.toString().equals(type)) {
			return mPrefs.getLong(PREF_LAST_MODIFIED_MOST_VOTED, 0);
		} else {
			return 0;
		}
	}

	private void saveLastModified(long lastModified, String type) {
		if (SightSortType.ByRegion.NEW.toString().equals(type)) {
			mPrefs.edit().putLong(PREF_LAST_MODIFIED_NEW, lastModified).commit();
		} else if (SightSortType.ByRegion.MOST_HUNTED.toString().equals(type)) {
			mPrefs.edit().putLong(PREF_LAST_MODIFIED_MOST_HUNTED, lastModified).commit();
		} else if (SightSortType.ByRegion.MOST_VOTED.toString().equals(type)) {
			mPrefs.edit().putLong(PREF_LAST_MODIFIED_MOST_VOTED, lastModified).commit();
		}
	}

	private void fetchSightsByRegion(Bundle args) {
		final String region = args.getString(Contract.Sight.REGION);
		final String type = args.getString(ARG_SIGHT_TYPE);

		long lastModified = getLastModified(type);

		Log.i("SightHuntService", "FetchSightsByRegion");

		mApiManager.getSightService().getSightsByRegion(region, lastModified, type, new Callback<List<Sight>>() {
			@Override
			public void success(List<Sight> sights, Response response) {
				for(Sight sight : sights) {
					ContentValues values = Contract.Sight.createContentValues(sight);
					getContentResolver().insert(Contract.Sight.getCreateSightServerUri(), values);
				}

				saveLastModified(new Date().getTime(), type);
				// only update content provider when there is new sights..
				if (sights.size() > 0) {
					getContentResolver().notifyChange(Contract.Sight.getFetchSightsContentUri(region, type), null);
				}
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

	public static Intent getInsertSightIntent(Context context, Sight sight) {
		Intent i = new Intent(ACTION_INSERT_SIGHTS);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.REGION, sight.region);
		i.putExtra(Contract.Sight.CREATOR, sight.creator);
		i.putExtra(Contract.Sight.TITLE, sight.title);
		i.putExtra(Contract.Sight.DESCRIPTION, sight.description);
		i.putExtra(Contract.Sight.IMAGE_KEY, sight.image_key);
		i.putExtra(Contract.Sight.THUMB_KEY, sight.thumb_key);
		i.putExtra(Contract.Sight.LON, sight.lon);
		i.putExtra(Contract.Sight.LAT, sight.lat);
		i.putExtra(Contract.Sight.TIME_CREATED, sight.time_created);
		i.putExtra(Contract.Sight.LAST_MODIFIED, sight.last_modified);
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
