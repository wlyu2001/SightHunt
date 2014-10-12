package com.sighthunt.network;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.sighthunt.data.Contract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightFetchType;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.util.PreferenceUtil;
import com.sighthunt.util.Scores;
import com.sighthunt.util.SightsKeeper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SightHuntService extends Service {

	private static final String SIGHTHUNT_SERVICE_URI = "com.sighthunt.service";
	private static final String ACTION_FETCH_SIGHTS_BY_REGION = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_region";
	private static final String ACTION_FETCH_SIGHTS_BY_USER = SIGHTHUNT_SERVICE_URI + ".action.fetch_sights_by_user";

	private static final String ACTION_EDIT_SIGHT = SIGHTHUNT_SERVICE_URI + ".action.edit_sight";
	private static final String ACTION_DELETE_SIGHT = SIGHTHUNT_SERVICE_URI + ".action.delete_sight";
	private static final String ACTION_INSERT_SIGHT = SIGHTHUNT_SERVICE_URI + ".action.insert_sight";
	private static final String ACTION_INSERT_HUNT = SIGHTHUNT_SERVICE_URI + ".action.insert_hunt";

	private static final String ACTION_FETCH_HUNTS = SIGHTHUNT_SERVICE_URI + ".action.fetch_hunts";

	private static final String ARG_SIGHT_TYPE = "arg_sight_type";
	private static final String ARG_USER = "arg_user";
	private static final String ARG_LIMIT = "arg_limit";
	private static final String ARG_OFFSET = "arg_offset";

	ApiManager mApiManager = Injector.get(ApiManager.class);
	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);
	SightsKeeper mSightsKeeper = Injector.get(SightsKeeper.class);


	@Override
	public void onCreate() {

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
			insertSightRemotely(args);
		} else if (ACTION_INSERT_HUNT.equals(action)) {
			insertHunt(args);
		} else if (ACTION_EDIT_SIGHT.equals(action)) {
			editSight(args);
		} else if (ACTION_DELETE_SIGHT.equals(action)) {
			deleteSight(args);
		} else if (ACTION_FETCH_HUNTS.equals(action)) {
			fetchHunts(args);
		}

		return START_STICKY;
	}

	private String mCurrentRegion;
	private String mCurrentUser;

	public void notifyRegionBrowse() {

		getContentResolver().notifyChange(Contract.Sight.getFetchSightsByRegionLocalUri(mCurrentRegion, SightFetchType.NEW), null);
		getContentResolver().notifyChange(Contract.Sight.getFetchSightsByRegionLocalUri(mCurrentRegion, SightFetchType.MOST_HUNTED), null);
		getContentResolver().notifyChange(Contract.Sight.getFetchSightsByRegionLocalUri(mCurrentRegion, SightFetchType.MOST_VOTED), null);
		getContentResolver().notifyChange(Contract.Sight.getFetchSightsByUserLocalUri(mCurrentUser, SightFetchType.CREATED_BY), null);
		getContentResolver().notifyChange(Contract.Sight.getFetchSightsByUserLocalUri(mCurrentUser, SightFetchType.HUNTED_BY), null);

	}

	private void deleteSight(Bundle args) {
		final long uuid = args.getLong(Contract.Sight.UUID);
		mApiManager.getSightService().deleteSight(uuid, new Callback<Integer>() {
			@Override
			public void success(Integer integer, Response response) {
				if (integer > 0) {
					getContentResolver().delete(Contract.Sight.getDeleteSightLocalUri(uuid), Contract.Sight.UUID + " == ? ", new String[]{String.valueOf(uuid)});

					notifyRegionBrowse();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				Log.i("Failed to edit sight.", error.getMessage());
			}
		});
	}

	private void editSight(Bundle args) {
		final String title = args.getString(Contract.Sight.TITLE);
		final String description = args.getString(Contract.Sight.DESCRIPTION);
		final long uuid = args.getLong(Contract.Sight.UUID);
		Sight sight = new Sight();
		sight.uuid = uuid;
		sight.description = description;
		sight.title = title;
		mApiManager.getSightService().editSight(sight, new Callback<Long>() {

			@Override
			public void success(Long key, Response response) {
				ContentValues values = new ContentValues();
				values.put(Contract.Sight.TITLE, title);
				values.put(Contract.Sight.DESCRIPTION, description);
				values.put(Contract.Sight.KEY, key);

				getContentResolver().update(Contract.Sight.getEditSightLocalUri(), values, Contract.Sight.UUID + " == ? ", new String[]{String.valueOf(uuid)});
				mSightsKeeper.addCachedSightKey(key, uuid);
			}

			@Override
			public void failure(RetrofitError error) {
				Log.i("Failed to edit sight.", error.getMessage());
			}
		});
	}

	private void insertHunt(Bundle args) {
		final long uuid = args.getLong(Contract.Hunt.SIGHT_UUID);
		final long key = args.getLong(Contract.Hunt.SIGHT_KEY);
		final String username = args.getString(Contract.Hunt.USER);
		final int vote = args.getInt(Contract.Hunt.VOTE);

		mApiManager.getSightService().huntSight(username, uuid, key, vote, new Callback<Integer>() {
			@Override
			public void success(Integer result, Response response) {

				if (result > 0) {
					insertHuntLocally(username, uuid);
					getContentResolver().notifyChange(Contract.Hunt.getInsertHuntRemoteUri(), null);
					mAccountUtils.changePoints(1);
				}
			}

			@Override
			public void failure(RetrofitError error) {

				Log.i("Failed to insert hunt.", error.getMessage());
			}
		});

	}


	private void insertSightRemotely(Bundle args) {
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
			public void success(final Sight s1, Response response) {
				if (s1 == null) {
					return;
				}
				sight.key = s1.key;
				sight.uuid = s1.uuid;
				String uploadUrl = s1.image_key;
				mApiManager.uploadImage(uploadUrl, s1.key, ImageFiles.NEW_IMAGE.getAbsolutePath(),
						ImageFiles.NEW_IMAGE_THUMB.getAbsolutePath(), new Callback<Sight>() {
							@Override
							public void success(Sight s2, Response response) {
								sight.thumb_key = s2.thumb_key;
								sight.image_key = s2.image_key;
								insertSightLocally(sight);
								notifyRegionBrowse();
								mAccountUtils.changePoints(-Scores.NEW_SIGHT_COST);
							}

							@Override
							public void failure(RetrofitError error) {
								Log.i("Failed to release sight.", error.getMessage());
							}
						});
			}

			@Override
			public void failure(RetrofitError error) {

				Log.i("Failed to release sight.", error.getMessage());
			}
		});
	}


	private void fetchSightsByRegion(Bundle args) {
		final String region = args.getString(Contract.Sight.REGION);
		mCurrentRegion = region;
		final String type = args.getString(ARG_SIGHT_TYPE);
		final int limit = args.getInt(ARG_LIMIT);
		final int offset = args.getInt(ARG_OFFSET);
		final Uri uri = Contract.Sight.getFetchSightsByRegionLocalUri(region, type);

		mApiManager.getSightService().getSightsByRegion(region, type, offset, limit, new Callback<List<Long>>() {
			@Override
			public void success(List<Long> keys, Response response) {
				fetchSightsNotInCache(keys, uri, SightFetchType.BY_KEY);
			}

			@Override
			public void failure(RetrofitError error) {
				getContentResolver().notifyChange(uri, null);
			}
		});
	}

	private void fetchDeletes(Bundle args) {
	}

	private void fetchHunts(Bundle args) {
		final String user = args.getString(ARG_USER);
		mCurrentUser = user;
		long lastUpdate = PreferenceUtil.getHuntsLastUpdate(this, user);

		mApiManager.getSightService().fetchHunts(user, lastUpdate, new Callback<List<Long>>() {
			@Override
			public void success(List<Long> uuids, Response response) {
				// put last update in the end of uuids
				for (int i = 0; i < uuids.size() - 1; i++) {
					insertHuntLocally(user, uuids.get(i));
				}

				PreferenceUtil.saveHuntsLastUpdate(SightHuntService.this, user, uuids.get(uuids.size() - 1));
			}

			@Override
			public void failure(RetrofitError error) {
			}
		});
	}

	private void fetchSightsNotInCache(List<Long> ids, final Uri uri, String type) {
		List<Long> sightsToFetch = new ArrayList<Long>();
		for (long id : ids) {
			if (SightFetchType.BY_KEY.equals(type)) {
				if (!mSightsKeeper.containsKey(id)) {
					sightsToFetch.add(id);
				}
			} else if (SightFetchType.BY_UUID.equals(type)) {
				if (!mSightsKeeper.containUUID(id)) {
					sightsToFetch.add(id);
				}
			}
		}
		if (sightsToFetch.size() > 0) {
			mApiManager.getSightService().getSights(sightsToFetch, type, new Callback<List<Sight>>() {
				@Override
				public void success(List<Sight> sights, Response response) {
					for (Sight sight : sights) {
						insertSightLocally(sight);
					}
					getContentResolver().notifyChange(uri, null);
				}

				@Override
				public void failure(RetrofitError error) {
					getContentResolver().notifyChange(uri, null);
				}
			});
		} else {
			getContentResolver().notifyChange(uri, null);
		}
	}

	private void fetchSightsByUser(Bundle args) {
		final String user = args.getString(ARG_USER);
		mCurrentUser = user;
		final String type = args.getString(ARG_SIGHT_TYPE);
		final int limit = args.getInt(ARG_LIMIT);
		final int offset = args.getInt(ARG_OFFSET);
		final Uri uri = Contract.Sight.getFetchSightsByUserLocalUri(user, type);

		mApiManager.getSightService().getSightsByUser(user, type, offset, limit, new Callback<List<Long>>() {
			@Override
			public void success(List<Long> ids, Response response) {
				if (SightFetchType.CREATED_BY.equals(type)) {
					fetchSightsNotInCache(ids, uri, SightFetchType.BY_KEY);
				} else if (SightFetchType.HUNTED_BY.equals(type)) {
					for (long id : ids) {
						insertHuntLocally(user, id);
					}
					fetchSightsNotInCache(ids, uri, SightFetchType.BY_UUID);
				}
			}

			@Override
			public void failure(RetrofitError error) {
				getContentResolver().notifyChange(uri, null);
			}
		});
	}

	private void insertHuntLocally(String user, long uuid) {
		ContentValues values = new ContentValues();
		values.put(Contract.Hunt.USER, user);
		values.put(Contract.Hunt.SIGHT_UUID, uuid);
		getContentResolver().insert(Contract.Hunt.getInsertHuntLocalUri(), values);
	}

	private void insertSightLocally(Sight sight) {

		ContentValues values = Contract.Sight.createContentValues(sight);
		getContentResolver().insert(Contract.Sight.getCreateSightLocalUri(), values);
		mSightsKeeper.addCachedSightKey(sight.key, sight.uuid);
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

	public static Intent getFetchHuntsIntent(Context context, String user) {
		Intent i = new Intent(ACTION_FETCH_HUNTS);
		i.setClass(context, SightHuntService.class);
		i.putExtra(ARG_USER, user);
		return i;
	}

	public static Intent getInsertHuntIntent(Context context, String user, long uuid, long key, int vote) {
		Intent i = new Intent(ACTION_INSERT_HUNT);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Hunt.USER, user);
		i.putExtra(Contract.Hunt.SIGHT_KEY, key);
		i.putExtra(Contract.Hunt.SIGHT_UUID, uuid);
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

	public static Intent getEditSightIntent(Context context, Sight sight) {
		Intent i = new Intent(ACTION_EDIT_SIGHT);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.TITLE, sight.title);
		i.putExtra(Contract.Sight.DESCRIPTION, sight.description);
		i.putExtra(Contract.Sight.UUID, sight.uuid);
		return i;
	}

	public static Intent getDeleteSightIntent(Context context, long uuid) {
		Intent i = new Intent(ACTION_DELETE_SIGHT);
		i.setClass(context, SightHuntService.class);
		i.putExtra(Contract.Sight.UUID, uuid);
		return i;
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
