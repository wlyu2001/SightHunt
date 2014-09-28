package com.sighthunt.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
	public static final String AUTHORITY = "com.sighthunt.provider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Sight implements BaseColumns {

		public static final String KEY = "key";

		public static final String TITLE = "title";

		public static final String IMAGE_URI = "image_uri";

		public static final String DESCRIPTION = "description";

		public static final String CREATOR = "creator";

		public static final String TIME_CREATED = "time_created";

		public static final String REGION = "region";

		public static final String LON = "lon";

		public static final String LAT = "lat";

		public static final String VOTES = "votes";

		public static final String HUNTS = "hunts";

		public static final Uri getFetchNewSightsContentUri(String region) {
			return Uri.parse(CONTENT_URI + "/sights/" + region + "/new");
		}

		public static final Uri getFetchMostVotedSightsContentUri(String region) {
			return Uri.parse(CONTENT_URI + "/sights/" + region + "/most_voted");
		}

		public static final Uri getFetchMostHuntedSightsContentUri(String region) {
			return Uri.parse(CONTENT_URI + "/sights/" + region + "/most_hunted");
		}

		public static final Uri getCreateSightClientUri() {
			return Uri.parse(CONTENT_URI+"/new_sight/client");
		}

		public static final Uri getCreateSightServerUri() {
			return Uri.parse(CONTENT_URI+"/new_sight/server");
		}

		public static final ContentValues createContentValues(com.sighthunt.network.model.Sight sight) {
			ContentValues values = new ContentValues();
			values.put(KEY, sight.key);
			values.put(TITLE, sight.title);
			values.put(DESCRIPTION, sight.description);
			values.put(CREATOR, sight.creator);
			values.put(TIME_CREATED, sight.time_created);
			values.put(IMAGE_URI, sight.image_uri);
			values.put(VOTES, sight.votes);
			values.put(HUNTS, sight.hunts);
			values.put(LON, sight.lon);
			values.put(LAT, sight.lat);
			return values;
		}

	}

	public static final class Region implements BaseColumns {

		public static final String _ID = "_id";

		public static final String NAME = "name";
	}

	public static final class User implements BaseColumns {

		public static final String _ID = "_id";

		public static final String NAME = "name";

		public static final String PASSWORD = "password";

		public static final String EMAIL = "email";

		public static final String FB = "fb";

	}

}
