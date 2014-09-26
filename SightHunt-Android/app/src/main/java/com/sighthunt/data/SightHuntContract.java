package com.sighthunt.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class SightHuntContract {
	public static final String AUTHORITY = "com.sighthunt";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final class Sight implements BaseColumns {

		public static final String KEY = "key";

		public static final String TITLE = "title";

		public static final String IMAGE_URI = "image_uri";

		public static final String DESCRIPTION = "description";

		public static final String CREATOR = "creator";

		public static final String REGION = "region";

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
