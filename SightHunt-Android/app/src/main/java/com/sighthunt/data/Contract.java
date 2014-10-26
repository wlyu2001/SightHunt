package com.sighthunt.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.sighthunt.BuildConfig;

public final class Contract {
	public static final String AUTHORITY = BuildConfig.PACKAGE_NAME;

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Sight implements BaseColumns {

		public static final String TABLE_NAME = "sight";

		// have to be called _id for cursor to work
		public static final String KEY = "key";

		public static final String TITLE = "title";

		public static final String IMAGE_KEY = "image_key";

		public static final String THUMB_KEY = "thumb_key";

		public static final String DESCRIPTION = "description";

		public static final String CREATOR = "creator";

		// client time
		public static final String TIME_CREATED = "time_created";

		// server time
		public static final String LAST_MODIFIED = "last_modified";

		public static final String REGION = "region";

		public static final String LON = "lon";

		public static final String LAT = "lat";

		public static final String VOTES = "votes";

		public static final String HUNTS = "hunts";

		public static final String UUID = "_id";

		public static final Uri getFetchSightsByRegionLocalUri(String region, String type, int count) {
			return Uri.parse(CONTENT_URI + "/sight_by_region/" + region + "/type/" + type + "/count/" + count);
		}

		public static final Uri getBasicTypeUri(String type) {
			return Uri.parse(CONTENT_URI + "/sight_by_type/" + type);
		}

		public static final Uri getFetchSightsByUserLocalUri(String user, String type, int count) {
			return Uri.parse(CONTENT_URI + "/sight_by_user/" + user + "/type/" + type + "/count/" + count);
		}

		public static final Uri getCreateSightLocalUri() {
			return Uri.parse(CONTENT_URI + "/create_sight");
		}

		public static final Uri getFetchSightByUUIDUri(long uuid) {
			return Uri.parse(CONTENT_URI + "/sight/" + uuid);
		}

		public static final Uri getDeleteSightLocalUri(long uuid) {
			return Uri.parse(CONTENT_URI + "/delete_sight/" + uuid);
		}

		public static final Uri getEditSightLocalUri(long uuid) {
			return Uri.parse(CONTENT_URI + "/edit_sight/" + uuid);
		}

		public static final ContentValues createContentValues(com.sighthunt.network.model.Sight sight) {
			ContentValues values = new ContentValues();
			values.put(KEY, sight.key);
			values.put(TITLE, sight.title);
			values.put(DESCRIPTION, sight.description);
			values.put(REGION, sight.region);
			values.put(CREATOR, sight.creator);
			values.put(TIME_CREATED, sight.time_created);
			values.put(LAST_MODIFIED, sight.last_modified);
			values.put(IMAGE_KEY, sight.image_key);
			values.put(THUMB_KEY, sight.thumb_key);
			values.put(VOTES, sight.votes);
			values.put(HUNTS, sight.hunts);
			values.put(LON, sight.lon);
			values.put(LAT, sight.lat);
			values.put(UUID, sight.uuid);
			return values;
		}

		public static final com.sighthunt.network.model.Sight createSightFromContentValues(ContentValues values) {
			com.sighthunt.network.model.Sight sight = new com.sighthunt.network.model.Sight();
			sight.title = values.getAsString(TITLE);
			sight.description = values.getAsString(DESCRIPTION);
			sight.creator = values.getAsString(CREATOR);
			sight.region = values.getAsString(REGION);
			sight.time_created = values.getAsLong(TIME_CREATED);
			sight.last_modified = values.getAsLong(LAST_MODIFIED);
			sight.image_key = values.getAsString(IMAGE_KEY);
			sight.thumb_key = values.getAsString(THUMB_KEY);
			sight.votes = values.getAsInteger(VOTES);
			sight.hunts = values.getAsInteger(HUNTS);
			sight.lon = values.getAsFloat(LON);
			sight.lat = values.getAsFloat(LAT);
			sight.uuid = values.getAsLong(UUID);
			return sight;
		}

	}

	public static final class Region implements BaseColumns {

		public static final String _ID = "_id";

		public static final String NAME = "name";
	}

	public static final class User implements BaseColumns {


		public static final String TABLE_NAME = "user";

		public static final String _ID = "_id";

		public static final String NAME = "name";

		public static final String PASSWORD = "password";

		public static final String EMAIL = "email";

		public static final String FB = "fb";

	}

	public static final class Hunt implements BaseColumns {


		public static final String TABLE_NAME = "hunt";

		public static final String USER = "user";

		public static final String SIGHT_KEY = "key";

		// uuid of the sight
		public static final String SIGHT_UUID = "uuid";

		public static final String VOTE = "vote";

		public static final Uri getInsertHuntLocalUri() {
			return Uri.parse(CONTENT_URI + "/create_hunt");
		}

		public static Uri getCheckHuntUri(String user, long uuid) {
			return Uri.parse(CONTENT_URI + "/hunt/" + user + "/sight/" + uuid);
		}
	}

	public static final class Report implements BaseColumns {

		public static final String TABLE_NAME = "report";

		public static final String REPORTER = "reporter";

		public static final String REASON = "reason";

		public static final String SIGHT_UUID = "uuid";

		public static final Uri getInsertReportLocalUri() {
			return Uri.parse(CONTENT_URI + "/report_sight");
		}
	}

}
