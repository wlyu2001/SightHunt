package com.sighthunt.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SyncRequest;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class SightHuntProvider extends ContentProvider{

	public static enum ContentType {
		SIGHT,
		SIGHTS_NEW,
		SIGHTS_MOST_HUNTED,
		SIGHTS_MOST_VOTED,
	}

	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(SightHuntContract.AUTHORITY, "sight/*", ContentType.SIGHT.ordinal());
		sUriMatcher.addURI(SightHuntContract.AUTHORITY, "sights/*/new", ContentType.SIGHTS_NEW.ordinal());
		sUriMatcher.addURI(SightHuntContract.AUTHORITY, "sights/*/most-hunted", ContentType.SIGHTS_MOST_HUNTED.ordinal());
		sUriMatcher.addURI(SightHuntContract.AUTHORITY, "sights/*/most-voted", ContentType.SIGHTS_MOST_VOTED.ordinal());
	}
	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
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
