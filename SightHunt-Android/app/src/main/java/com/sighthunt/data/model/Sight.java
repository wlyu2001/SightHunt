package com.sighthunt.data.model;

import android.database.Cursor;

import com.sighthunt.data.SightHuntContract;

public class Sight {
	public static final String[] PROJECTION = {
			SightHuntContract.Sight.KEY,
			SightHuntContract.Sight.TITLE,
			SightHuntContract.Sight.DESCRIPTION,
			SightHuntContract.Sight.IMAGE_URI,
			SightHuntContract.Sight.CREATOR,
			SightHuntContract.Sight.REGION,
	};

	public static final int SIGHT_KEY = 0;
	public static final int SIGHT_TITLE = 1;
	public static final int SIGHT_DESCRIPTION = 2;
	public static final int SIGHT_IMAGE_URI = 3;
	public static final int SIGHT_CREATOR = 4;
	public static final int SIGHT_REGION = 5;

	public String key;
	public String title;
	public String description;
	public String imageUri;
	public String creator;
	public String region;


	public void bindCursor(Cursor cursor) {
		key = cursor.getString(SIGHT_KEY);
		title = cursor.getString(SIGHT_TITLE);
		description = cursor.getString(SIGHT_DESCRIPTION);
		imageUri = cursor.getString(SIGHT_IMAGE_URI);
		creator = cursor.getString(SIGHT_CREATOR);
		region = cursor.getString(SIGHT_REGION);
	}
}
