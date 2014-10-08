package com.sighthunt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.sighthunt.network.SightHuntService;
import com.sighthunt.network.model.Sight;
import com.sighthunt.network.model.SightSortType;

import java.util.List;

public class SightHuntProvider extends ContentProvider {

	public static enum ContentType {
		UNKNOWN,
		SIGHT,
		SIGHT_LIST_BY_REGION_REMOTE,
		SIGHT_LIST_BY_REGION_LOCAL,
		CREATE_SIGHT_REMOTE,
		CREATE_SIGHT_LOCAL,
		SIGHT_LIST_BY_USER_REMOTE,
		SIGHT_LIST_BY_USER_LOCAL,
		INSERT_HUNT_LOCAL,
		INSERT_HUNT_REMOTE,
		CHECK_HUNT;

		private static final ContentType[] VALUES = values();

		public static ContentType fromUriMatch(int index) {
			return VALUES[index];
		}
	}

	private static final UriMatcher sUriMatcher;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(Contract.AUTHORITY, "sight/*", ContentType.SIGHT.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight_by_region/*/type/*/local", ContentType.SIGHT_LIST_BY_REGION_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight_by_region/*/type/*/remote/offset/*/limit/*", ContentType.SIGHT_LIST_BY_REGION_REMOTE.ordinal());

		sUriMatcher.addURI(Contract.AUTHORITY, "sight_by_user/*/type/*/local", ContentType.SIGHT_LIST_BY_USER_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight_by_user/*/type/*/remote/offset/*/limit/*", ContentType.SIGHT_LIST_BY_USER_REMOTE.ordinal());

		sUriMatcher.addURI(Contract.AUTHORITY, "create_sight/local", ContentType.CREATE_SIGHT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "create_sight/remote", ContentType.CREATE_SIGHT_REMOTE.ordinal());

		sUriMatcher.addURI(Contract.AUTHORITY, "hunt/local", ContentType.INSERT_HUNT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "hunt/remote", ContentType.INSERT_HUNT_REMOTE.ordinal());

		sUriMatcher.addURI(Contract.AUTHORITY, "hunt/*/sight/*", ContentType.CHECK_HUNT.ordinal());
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


		Cursor cursor = null;

		switch (getContentType(uri)) {
			case SIGHT: {

				String key = getKey(uri);
				String selection = Contract.Sight.KEY + " == ?";
				String[] selectionArgs = new String[]{key};
				cursor = mDb.query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
				break;
			}
			case SIGHT_LIST_BY_REGION_REMOTE: {

				String region = getRegion(uri);
				String type = getSightListType(uri);
				int offset = getOffset(uri);
				int limit = getLimit(uri);
				Intent dataRefreshIntent = SightHuntService.getFetchSightsByRegionIntent(getContext(), region, type, offset, limit);
				if (dataRefreshIntent != null) {
					getContext().startService(dataRefreshIntent);
				}
				break;
			}
			case SIGHT_LIST_BY_REGION_LOCAL: {

				String region = getRegion(uri);
				String type = getSightListType(uri);
				String sortOrder = "";
				String selection = Contract.Sight.REGION + " = ?";
				String[] selectionArgs = new String[]{region};

				if (SightSortType.MOST_VOTED.equals(type)) {
					sortOrder = Contract.Sight.VOTES + " DESC";
				} else if (SightSortType.MOST_HUNTED.equals(type)) {
					sortOrder = Contract.Sight.HUNTS + " DESC";
				} else if (SightSortType.NEW.equals(type)) {
					sortOrder = Contract.Sight.TIME_CREATED + " DESC";
				}

				cursor = mDb.query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);

				break;
			}
			case SIGHT_LIST_BY_USER_REMOTE: {

				String user = getUser(uri);
				String type = getSightListType(uri);
				int limit = getLimit(uri);
				int offset = getOffset(uri);
				Intent dataRefreshIntent = SightHuntService.getFetchSightsByUserIntent(getContext(), user, type, offset, limit);
				if (dataRefreshIntent != null) {
					getContext().startService(dataRefreshIntent);
				}
				break;
			}
			case SIGHT_LIST_BY_USER_LOCAL: {
				String user = getUser(uri);
				String type = getSightListType(uri);

				if (SightSortType.CREATED_BY.equals(type)) {
					String selection = Contract.Sight.CREATOR + " = ?";
					String[] selectionArgs = new String[]{user};
					String sortOrder = Contract.Sight.TIME_CREATED + " DESC";

					cursor = mDb.query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
				} else if (SightSortType.HUNTED_BY.equals(type)) {

					String sql = "SELECT " + projectionToString(projection) + " FROM " + Contract.Hunt.TABLE_NAME +
							" AS hunt JOIN " + Contract.Sight.TABLE_NAME + " AS sight" + " ON hunt." + Contract.Hunt.SIGHT +
							"=" + "sight." + Contract.Sight.KEY + " WHERE hunt." + Contract.Hunt.USER + "=?";

					cursor = mDb.rawQuery(sql, new String[]{user});
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
				}
				break;
			}
			case CHECK_HUNT: {
				List<String> segments = uri.getPathSegments();
				String user = segments.get(1);
				String sight = segments.get(3);

				String selection = Contract.Hunt.USER + " = ? AND " + Contract.Hunt.SIGHT + " = ?";
				String[] selectionArgs = new String[]{user, sight};

				cursor = mDb.query(Contract.Hunt.TABLE_NAME, null, selection, selectionArgs, null, null, null);
			}
			default: {
			}
		}

		return cursor;

	}
	private String projectionToString(String[] projection) {
		StringBuilder projectionStringBuilder = new StringBuilder();
		String prefix = "";

		for (String column : projection) {
			projectionStringBuilder.append(prefix);
			prefix = ", ";
			projectionStringBuilder.append("sight." + column + " AS " + column);
		}

		return projectionStringBuilder.toString();
	}

	public int getOffset(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return Integer.parseInt(segments.get(6));
	}

	public int getLimit(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return Integer.parseInt(segments.get(8));
	}

	public String getRegion(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public String getUser(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public String getKey(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public String getSightListType(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(3);
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
			case CREATE_SIGHT_REMOTE: {
				// insert initialized from the client to the server
				Sight sight = Contract.Sight.createSightFromContentValues(values);

				getContext().startService(SightHuntService.getInsertSightIntent(getContext(), sight));
				break;
			}
			case CREATE_SIGHT_LOCAL: {
				//insert initiated from the server to the client
				mDb.insertWithOnConflict(Contract.Sight.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				break;
			}
			case INSERT_HUNT_LOCAL: {
				mDb.insertWithOnConflict(Contract.Hunt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				break;
			}
			case INSERT_HUNT_REMOTE: {
				String user = values.getAsString(Contract.Hunt.USER);
				String sight = values.getAsString(Contract.Hunt.SIGHT);
				int vote = values.getAsInteger(Contract.Hunt.VOTE);
				getContext().startService(SightHuntService.getInsertHuntIntent(getContext(), user, sight, vote));
				break;
			}

			default: {
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
