package com.sighthunt.network;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.sighthunt.data.Contract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightSortType;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.util.PreferenceUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SightHuntService extends Service {

	public static final String SIGHTHUNT_SERVICE_URI = "com.sighthunt.service";
	public static final String ACTION_FETCH_SIGHTS_BY_REGION = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_region";
	public static final String ACTION_FETCH_SIGHTS_BY_USER = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_user";

	public static final String ACTION_INSERT_SIGHT = SIGHTHUNT_SERVICE_URI + ".action.insert_sight";
	public static final String ACTION_INSERT_HUNT = SIGHTHUNT_SERVICE_URI + ".action.insert_hunt";

	private static final java.lang.String ARG_SIGHT_TYPE = "arg_sight_type";
	private static final java.lang.String ARG_USER = "arg_user";
	private static final java.lang.String ARG_LIMIT = "arg_limit";
	private static final java.lang.String ARG_OFFSET = "arg_offset";


	private SharedPreferences mPrefs;

	private static final String PREF_LAST_MODIFIED_PREFIX = "prefs_last_modified_";

	ApiManager mApiManager = Injector.get(ApiManager.class);
	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);


	@Override
	public void onCreate() {
		mPrefs = PreferenceUtil.getDataSharedPreferences(this);
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
		} else if (ACTION_INSERT_SIGHT.equals(action)) {
			insertSight(args);
		} else if (ACTION_INSERT_HUNT.equals(action)) {
			insertHunt(args);
		}

		return START_STICKY;
	}

	private void insertHunt(Bundle args) {
		final String key = args.getString(Contract.Hunt.SIGHT);
		final String username = args.getString(Contract.Hunt.USER);
		final int vote = args.getInt(Contract.Hunt.VOTE);

		mApiManager.getSightService().huntSight(username, key, vote, new Callback<Integer>() {
			@Override
			public void success(Integer result, Response response) {

				if (result > 0) {
					ContentValues values = new ContentValues();
					values.put(Contract.Hunt.USER, username);
					values.put(Contract.Hunt.SIGHT, key);
					values.put(Contract.Hunt.VOTE, vote);
					getContentResolver().insert(Contract.Hunt.getInsertHuntLocalUri(), values);
					getContentResolver().notifyChange(Contract.Hunt.getInsertHuntRemoteUri(), null);

				}
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});

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
		mApiManager.getSightService().createSight(sight, new Callback<Sight>() {
			@Override
			public void success(Sight s, Response response) {
				if (s == null) return;
				Log.i("Sight key", s.key);
				String uploadUrl = s.image_key;
				mApiManager.uploadImage(uploadUrl, s.key, ImageFiles.NEW_IMAGE.getAbsolutePath(),
						ImageFiles.NEW_IMAGE_THUMB.getAbsolutePath(), new Callback<Sight>() {
							@Override
							public void success(Sight sight, Response response) {
								getContentResolver().notifyChange(Contract.Sight.getCreateSightRemoteUri(), null);
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

	private long getRegionLastModified(String type, String region) {
		return mPrefs.getLong(PREF_LAST_MODIFIED_PREFIX + type + "_region_" + region, 0);
	}

	private void saveRegionLastModified(long lastModified, String type, String region) {
		mPrefs.edit().putLong(PREF_LAST_MODIFIED_PREFIX + type + "_region_" + region, lastModified).commit();
	}

	private long getUserLastModified(String type, String user) {
		return mPrefs.getLong(PREF_LAST_MODIFIED_PREFIX + type + "_user_" + user, 0);
	}

	private void saveUserLastModified(long lastModified, String type, String user) {
		mPrefs.edit().putLong(PREF_LAST_MODIFIED_PREFIX + type + "_user_" + user, lastModified).commit();
	}

	private void fetchSightsByRegion(Bundle args) {
		final String region = args.getString(Contract.Sight.REGION);
		final String type = args.getString(ARG_SIGHT_TYPE);
		final int limit = args.getInt(ARG_LIMIT);
		final int offset = args.getInt(ARG_OFFSET);

		final long lastModified = getRegionLastModified(type, region);

		Log.i("SightHuntService", "FetchSightsByRegion");

		mApiManager.getSightService().getSightsByRegion(region, lastModified, type, offset, limit, new Callback<List<Sight>>() {
			@Override
			public void success(List<Sight> sights, Response response) {

				long newLastModified = Long.MIN_VALUE;
				for (Sight sight : sights) {
					if (sight.last_modified > lastModified)
						newLastModified = sight.last_modified;
					ContentValues values = Contract.Sight.createContentValues(sight);
					getContentResolver().insert(Contract.Sight.getCreateSightLocalUri(), values);
				}
				if (sights.size() > 0) {
					saveRegionLastModified(newLastModified, type, region);
				}
				getContentResolver().notifyChange(Contract.Sight.getFetchSightsByRegionLocalUri(region, type), null);
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});
	}

	private void fetchSightsByUser(Bundle args) {
		final String user = args.getString(ARG_USER);
		final String type = args.getString(ARG_SIGHT_TYPE);
		final int limit = args.getInt(ARG_LIMIT);
		final int offset = args.getInt(ARG_OFFSET);

		final long lastModified = getUserLastModified(type, user);
		mApiManager.getSightService().getSightsByUser(user, lastModified, type, offset, limit, new Callback<List<Sight>>() {
			@Override
			public void success(List<Sight> sights, Response response) {
				long lastModified = Long.MIN_VALUE;
				for (Sight sight : sights) {
					if (sight.last_modified > lastModified)
						lastModified = sight.last_modified;
					ContentValues values = Contract.Sight.createContentValues(sight);
					if (SightSortType.HUNTED_BY.equals(type)) {
						insertHuntLocally(mAccountUtils.getUsername(), sight.key);
					}
					getContentResolver().insert(Contract.Sight.getCreateSightLocalUri(), values);
				}
				if (sights.size() > 0) {
					saveUserLastModified(lastModified, type, user);
				}
				// notify the local uri..
				getContentResolver().notifyChange(Contract.Sight.getFetchSightsByUserLocalUri(user, type), null);
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});
	}

	private void insertHuntLocally(String user, String sight) {
		ContentValues values = new ContentValues();
		values.put(Contract.Hunt.USER, user);
		values.put(Contract.Hunt.SIGHT, sight);
		getContentResolver().insert(Contract.Hunt.getInsertHuntLocalUri(), values);
	}

	public static Intent getFetchSightsByRegionIntent(Context context, @NotNull String region, String type, int offset, int limit) {
		Intent i = new Intent(ACTION_FETCH_SIGHTS_BY_REGION);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.REGION, region);
		i.putExtra(ARG_SIGHT_TYPE, type);
		i.putExtra(ARG_OFFSET, offset);
		i.putExtra(ARG_LIMIT, limit);
		return i;
	}

	public static Intent getInsertHuntIntent(Context context, String user, String sight, int vote) {
		Intent i = new Intent(ACTION_INSERT_HUNT);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Hunt.USER, user);
		i.putExtra(Contract.Hunt.SIGHT, sight);
		i.putExtra(Contract.Hunt.VOTE, vote);
		return i;
	}

	public static Intent getInsertSightIntent(Context context, Sight sight) {
		Intent i = new Intent(ACTION_INSERT_SIGHT);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.REGION, sight.region);
		i.putExtra(Contract.Sight.CREATOR, sight.creator);
		i.putExtra(Contract.Sight.TITLE, sight.title);
		i.putExtra(Contract.Sight.DESCRIPTION, sight.description);
		i.putExtra(Contract.Sight.LON, sight.lon);
		i.putExtra(Contract.Sight.LAT, sight.lat);
		i.putExtra(Contract.Sight.TIME_CREATED, sight.time_created);
		i.putExtra(Contract.Sight.LAST_MODIFIED, sight.last_modified);
		return i;
	}

	public static Intent getFetchSightsByUserIntent(Context context, @NotNull String user, String type, int offset, int limit) {
		Intent i = new Intent(ACTION_FETCH_SIGHTS_BY_USER);
		i.setClass(context, SightHuntService.class);
		i.putExtra(ARG_USER, user);
		i.putExtra(ARG_SIGHT_TYPE, type);
		i.putExtra(ARG_LIMIT, limit);
		i.putExtra(ARG_OFFSET, offset);
		return i;
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
