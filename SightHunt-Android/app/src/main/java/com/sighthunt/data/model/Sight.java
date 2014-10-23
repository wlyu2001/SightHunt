package com.sighthunt.data.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.sighthunt.data.Contract;

public class Sight implements Parcelable {
	public static final String ARG = "arg";
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

	@Override
	public int describeContents() {
		return 0;
	}

	public Sight() {
	}

	public Sight(Parcel in) {
		String[] data = new String[13];

		in.readStringArray(data);
		this.key = Long.parseLong(data[0]);
		this.title = data[1];
		this.description = data[2];
		this.imageKey = data[3];
		this.thumbKey = data[4];
		this.creator = data[5];
		this.region = data[6];
		this.lon = Float.parseFloat(data[7]);
		this.lat = Float.parseFloat(data[8]);
		this.timeCreated = Long.parseLong(data[9]);
		this.votes = Integer.parseInt(data[10]);
		this.hunts = Integer.parseInt(data[11]);
		this.uuid = Long.parseLong(data[12]);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[]{
				String.valueOf(this.key),
				this.title,
				this.description,
				this.imageKey,
				this.thumbKey,
				this.creator,
				this.region,
				String.valueOf(this.lon),
				String.valueOf(this.lat),
				String.valueOf(this.timeCreated),
				String.valueOf(this.votes),
				String.valueOf(this.hunts),
				String.valueOf(this.uuid)});
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Sight createFromParcel(Parcel in) {
			return new Sight(in);
		}

		public Sight[] newArray(int size) {
			return new Sight[size];
		}
	};
}
