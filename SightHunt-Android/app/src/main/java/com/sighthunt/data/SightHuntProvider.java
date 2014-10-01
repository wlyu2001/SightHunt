package com.sighthunt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.sighthunt.inject.Injector;
import com.sighthunt.network.SightHuntService;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightSortType;
import com.sighthunt.util.AccountUtils;

import java.util.List;

public class SightHuntProvider extends ContentProvider {

	public static enum ContentType {
		UNKNOWN,
		SIGHT,
		SIGHTS_NEW,
		SIGHTS_MOST_HUNTED,
		SIGHTS_MOST_VOTED,
		NEW_SIGHT_CLIENT,
		NEW_SIGHT_SERVER;

		private static final ContentType[] VALUES = values();

		public static ContentType fromUriMatch(int index) {
			return VALUES[index];
		}
	}

	private static final UriMatcher sUriMatcher;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(Contract.AUTHORITY, "sight/*", ContentType.SIGHT.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight/*/new", ContentType.SIGHTS_NEW.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight/*/most_hunted", ContentType.SIGHTS_MOST_HUNTED.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight/*/most_voted", ContentType.SIGHTS_MOST_VOTED.ordinal());

		sUriMatcher.addURI(Contract.AUTHORITY, "new_sight/server", ContentType.NEW_SIGHT_SERVER.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "new_sight/client", ContentType.NEW_SIGHT_CLIENT.ordinal());
	}

	SQLiteDatabase mDb;

	@Override
	public boolean onCreate() {
		SightHuntDatabase db = new SightHuntDatabase(getContext());
		mDb = db.getReadableDatabase();
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String sel, String[] args, String order) {

		Intent dataRefreshIntent = null;

		switch (getContentType(uri)) {
			case SIGHT: {

			}
			case SIGHTS_MOST_HUNTED: {
				dataRefreshIntent = SightHuntService.getFetchSightsByRegionIntent(getContext(), getRegion(uri), SightSortType.ByRegion.MOST_HUNTED);
				break;
			}
			case SIGHTS_MOST_VOTED: {
				dataRefreshIntent = SightHuntService.getFetchSightsByRegionIntent(getContext(), getRegion(uri), SightSortType.ByRegion.MOST_VOTED);
				break;
			}
			case SIGHTS_NEW: {
				dataRefreshIntent = SightHuntService.getFetchSightsByRegionIntent(getContext(), getRegion(uri), SightSortType.ByRegion.NEW);
				break;
			}
			default: {
			}
		}

		if (dataRefreshIntent != null) {
			getContext().startService(dataRefreshIntent);
		}

		String sortOrder = "";
		String selection = Contract.Sight.CREATOR + " <> ?";
		String[] selectionArgs = new String[]{Injector.get(AccountUtils.class).getUsername()};

		switch (getContentType(uri)) {
			case SIGHT: {

			}
			case SIGHTS_MOST_HUNTED: {
				sortOrder = Contract.Sight.VOTES + " DESC";
				break;
			}
			case SIGHTS_MOST_VOTED: {
				sortOrder = Contract.Sight.HUNTS + " DESC";
				break;
			}
			case SIGHTS_NEW: {
				sortOrder = Contract.Sight.TIME_CREATED + " DESC";
				break;
			}
			default: {
			}
		}
		Cursor cursor = mDb.query(Contract.Sight.TABLE_NAME, projection, null, null, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		//return mDb.query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;

	}

	public String getRegion(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public ContentType getContentType(Uri uri) {
		int match = sUriMatcher.match(uri);
		if (match == UriMatcher.NO_MATCH)
			return ContentType.UNKNOWN;
		return ContentType.fromUriMatch(match);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (getContentType(uri)) {
			case NEW_SIGHT_CLIENT: {
				// insert initialized from the client to the server
				Sight sight = Contract.Sight.createSightFromContentValues(values);

				getContext().startService(SightHuntService.getInsertSightIntent(getContext(), sight));
			}
			case NEW_SIGHT_SERVER: {
				//insert initiated from the server to the client
				mDb.insertWithOnConflict(Contract.Sight.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			}
		}

		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}
