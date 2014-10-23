package com.sighthunt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.sighthunt.network.model.SightFetchType;

import java.util.List;

public class SightHuntProvider extends ContentProvider {

	public static enum ContentType {
		UNKNOWN,
		SIGHT,
		SIGHT_LIST_BY_REGION_LOCAL,
		CREATE_SIGHT_LOCAL,
		SIGHT_LIST_BY_USER_LOCAL,
		CREATE_HUNT_LOCAL,
		DELETE_SIGHT_LOCAL,
		REPORT_SIGHT_LOCAL,
		EDIT_SIGHT_LOCAL,
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
		sUriMatcher.addURI(Contract.AUTHORITY, "sight_by_region/*/type/*/count/*", ContentType.SIGHT_LIST_BY_REGION_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "sight_by_user/*/type/*/count/*", ContentType.SIGHT_LIST_BY_USER_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "create_sight", ContentType.CREATE_SIGHT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "delete_sight/*", ContentType.DELETE_SIGHT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "report_sight", ContentType.REPORT_SIGHT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "edit_sight/*", ContentType.EDIT_SIGHT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "create_hunt", ContentType.CREATE_HUNT_LOCAL.ordinal());
		sUriMatcher.addURI(Contract.AUTHORITY, "hunt/*/sight/*", ContentType.CHECK_HUNT.ordinal());
	}

	SightHuntDatabaseHelper mDb;

	@Override
	public boolean onCreate() {
		mDb = new SightHuntDatabaseHelper(getContext());
		return true;
	}

	private SQLiteDatabase getDb() {
		return mDb.getReadableDatabase();
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String sel, String[] args, String order) {

		Cursor cursor = null;

		switch (getContentType(uri)) {
			case SIGHT: {

				String uuid = getUUID(uri);
				String selection = Contract.Sight.UUID + " == ?";
				String[] selectionArgs = new String[]{uuid};
				cursor = getDb().query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
				break;
			}
			case SIGHT_LIST_BY_REGION_LOCAL: {

				String region = getRegion(uri);
				String type = getSightListType(uri);
				String count = getCount(uri);

				String sortOrder = "";
				String selection = Contract.Sight.REGION + " = ?";
				String[] selectionArgs = new String[]{region};

				if (SightFetchType.MOST_VOTED.equals(type)) {
					sortOrder = Contract.Sight.VOTES + " DESC";
				} else if (SightFetchType.MOST_HUNTED.equals(type)) {
					sortOrder = Contract.Sight.HUNTS + " DESC";
				} else if (SightFetchType.NEW.equals(type)) {
					sortOrder = Contract.Sight.TIME_CREATED + " DESC";
				}

				String sql = "SELECT " + projectionToString(projection) + " FROM " + Contract.Sight.TABLE_NAME +
						" WHERE " + Contract.Sight.UUID + " NOT IN " +
						" (SELECT " + Contract.Report.SIGHT_UUID + " FROM " + Contract.Report.TABLE_NAME + ")" +
						" AND " + selection + " ORDER BY " + sortOrder + " LIMIT " + count;
//
//				cursor = getDb().query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

				cursor = getDb().rawQuery(sql, selectionArgs);
				cursor.setNotificationUri(getContext().getContentResolver(), Contract.Sight.getBasicTypeUri(type));

				break;
			}
			case SIGHT_LIST_BY_USER_LOCAL: {
				String user = getUser(uri);
				String type = getSightListType(uri);
				String count = getCount(uri);

				if (SightFetchType.CREATED_BY.equals(type)) {
					String selection = Contract.Sight.CREATOR + " = ?";
					String[] selectionArgs = new String[]{user};
					String sortOrder = Contract.Sight.TIME_CREATED + " DESC";


					cursor = getDb().query(Contract.Sight.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, count);
					cursor.setNotificationUri(getContext().getContentResolver(), Contract.Sight.getBasicTypeUri(type));
				} else if (SightFetchType.HUNTED_BY.equals(type)) {

					String sql = "SELECT " + projectionToString(projection) + " FROM " + Contract.Hunt.TABLE_NAME +
							" AS hunt JOIN " + Contract.Sight.TABLE_NAME + " AS sight" + " ON hunt." + Contract.Hunt.SIGHT_UUID +
							"=" + "sight." + Contract.Sight.UUID + " WHERE hunt." + Contract.Hunt.USER + " =? ";

					cursor = getDb().rawQuery(sql, new String[]{user});
					cursor.setNotificationUri(getContext().getContentResolver(), Contract.Sight.getBasicTypeUri(type));
				}
				break;
			}
			case CHECK_HUNT: {
				List<String> segments = uri.getPathSegments();
				String user = segments.get(1);
				String uuid = segments.get(3);

				String selection = Contract.Hunt.USER + " = ? AND " + Contract.Hunt.SIGHT_UUID + " = ?";
				String[] selectionArgs = new String[]{user, uuid};

				cursor = getDb().query(Contract.Hunt.TABLE_NAME, null, selection, selectionArgs, null, null, null);
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

	public String getRegion(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public String getCount(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(5);
	}

	public String getUser(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public String getUUID(Uri uri) {
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
			case CREATE_SIGHT_LOCAL: {
				getDb().insertWithOnConflict(Contract.Sight.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				break;
			}
			case CREATE_HUNT_LOCAL: {
				getDb().insertWithOnConflict(Contract.Hunt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				break;
			}
			case REPORT_SIGHT_LOCAL: {
				getDb().insertWithOnConflict(Contract.Report.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				break;
			}

			default: {
			}
		}

		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (getContentType(uri)) {
			case DELETE_SIGHT_LOCAL: {
				selection = Contract.Sight.UUID + " == ? ";
				selectionArgs = new String[]{String.valueOf(getUUID(uri))};
				getDb().delete(Contract.Sight.TABLE_NAME, selection, selectionArgs);
				break;
			}
			default: {
			}
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		switch (getContentType(uri)) {
			case EDIT_SIGHT_LOCAL: {
				selection = Contract.Sight.UUID + " == ? ";
				selectionArgs = new String[]{String.valueOf(getUUID(uri))};
				getDb().update(Contract.Sight.TABLE_NAME, values, selection, selectionArgs);
				break;
			}
			default: {
			}
		}
		return 0;
	}
}
