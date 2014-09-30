package com.sighthunt.data.model;

import android.database.Cursor;

import com.sighthunt.data.Contract;

public class Sight {
	public static final String[] PROJECTION = {
			Contract.Sight.KEY,
			Contract.Sight.TITLE,
			Contract.Sight.DESCRIPTION,
			Contract.Sight.IMAGE_KEY,
			Contract.Sight.THUMB_KEY,
			Contract.Sight.CREATOR,
			Contract.Sight.REGION,
			Contract.Sight.TIME_CREATED,
			Contract.Sight.LON,
			Contract.Sight.LAT,
	};

	public static final int SIGHT_KEY = 0;
	public static final int SIGHT_TITLE = 1;
	public static final int SIGHT_DESCRIPTION = 2;
	public static final int SIGHT_IMAGE_KEY = 3;
	public static final int SIGHT_THUMB_KEY = 4;
	public static final int SIGHT_CREATOR = 5;
	public static final int SIGHT_REGION = 6;
	public static final int SIGHT_LON = 7;
	public static final int SIGHT_LAT = 8;

	public String key;
	public String title;
	public String description;
	public String imageKey;
	public String thumbKey;
	public String creator;
	public String region;
	public float lon;
	public float lat;


	public void bindCursor(Cursor cursor) {
		key = cursor.getString(SIGHT_KEY);
		title = cursor.getString(SIGHT_TITLE);
		description = cursor.getString(SIGHT_DESCRIPTION);
		imageKey = cursor.getString(SIGHT_IMAGE_KEY);
		thumbKey = cursor.getString(SIGHT_THUMB_KEY);
		creator = cursor.getString(SIGHT_CREATOR);
		region = cursor.getString(SIGHT_REGION);
		lon = cursor.getFloat(SIGHT_LON);
		lat = cursor.getFloat(SIGHT_LAT);
	}
}
