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
			Contract.Sight.VOTES,
			Contract.Sight.HUNTS,
			Contract.Sight.UUID,
	};

	public static final int SIGHT_KEY = 0;
	public static final int SIGHT_TITLE = 1;
	public static final int SIGHT_DESCRIPTION = 2;
	public static final int SIGHT_IMAGE_KEY = 3;
	public static final int SIGHT_THUMB_KEY = 4;
	public static final int SIGHT_CREATOR = 5;
	public static final int SIGHT_REGION = 6;
	public static final int SIGHT_TIME_CREATED = 7;
	public static final int SIGHT_LON = 8;
	public static final int SIGHT_LAT = 9;
	public static final int SIGHT_VOTES = 10;
	public static final int SIGHT_HUNTS = 11;
	public static final int SIGHT_UUID = 12;


	public long key;
	public String title;
	public String description;
	public String imageKey;
	public String thumbKey;
	public String creator;
	public String region;
	public float lon;
	public float lat;
	public long timeCreated;
	public int votes;
	public int hunts;
	public long uuid;


	public static Sight fromCursor(Cursor cursor) {
		Sight sight = new Sight();
		sight.key = cursor.getLong(SIGHT_KEY);
		sight.title = cursor.getString(SIGHT_TITLE);
		sight.description = cursor.getString(SIGHT_DESCRIPTION);
		sight.imageKey = cursor.getString(SIGHT_IMAGE_KEY);
		sight.thumbKey = cursor.getString(SIGHT_THUMB_KEY);
		sight.creator = cursor.getString(SIGHT_CREATOR);
		sight.region = cursor.getString(SIGHT_REGION);
		sight.lon = cursor.getFloat(SIGHT_LON);
		sight.lat = cursor.getFloat(SIGHT_LAT);
		sight.timeCreated = cursor.getLong(SIGHT_TIME_CREATED);
		sight.votes = cursor.getInt(SIGHT_VOTES);
		sight.hunts = cursor.getInt(SIGHT_HUNTS);
		sight.uuid = cursor.getLong(SIGHT_UUID);

		return sight;
	}
}
