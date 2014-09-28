package com.sighthunt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.sighthunt.network.SightHuntService;
import com.sighthunt.network.model.SightSortType;

import java.util.List;

public class SightHuntProvider extends ContentProvider {

	public static enum ContentType {
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

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

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



		// query sqlite

		return null;
	}

	public String getRegion(Uri uri) {
		List<String> segments = uri.getPathSegments();
		return segments.get(1);
	}

	public ContentType getContentType(Uri uri) {
		int result = sUriMatcher.match(uri);
		return ContentType.fromUriMatch(result);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (getContentType(uri)) {
			case NEW_SIGHT_CLIENT: {
				String title = values.getAsString(Contract.Sight.TITLE);
				String description = values.getAsString(Contract.Sight.DESCRIPTION);
				String image = values.getAsString(Contract.Sight.IMAGE_URI);
				String region = values.getAsString(Contract.Sight.REGION);
				float lon = values.getAsFloat(Contract.Sight.LON);
				float lat = values.getAsFloat(Contract.Sight.LAT);

				getContext().startService(SightHuntService.getInsertSightIntent(getContext(), region, title, description, image, lon, lat));
			} case NEW_SIGHT_SERVER: {
			// write sqlite
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
