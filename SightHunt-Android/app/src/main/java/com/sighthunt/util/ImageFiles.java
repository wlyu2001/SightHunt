package com.sighthunt.util;

import android.os.Environment;

import java.io.File;

public class ImageFiles {

	private static final File IMAGE_FOLDER = new File(Environment.getExternalStorageDirectory() + "/sighthunt/image");

	public static final File ORIGINAL_IMAGE = new File(IMAGE_FOLDER, "original.jpg");
	public static final File MATCH_IMAGE = new File(IMAGE_FOLDER, "match.jpg");
	public static final File MATCH_IMAGE_THUMB = new File(IMAGE_FOLDER, "match_thumb.jpg");


	public static final File ORIGINAL_IMAGE_PREPRO = new File(IMAGE_FOLDER, "original_prepro.jpg");
	public static final File MATCH_IMAGE_PREPRO = new File(IMAGE_FOLDER, "match_prepro.jpg");


	public static final File NEW_IMAGE = new File(IMAGE_FOLDER, "new.jpg");
	public static final File NEW_IMAGE_THUMB = new File(IMAGE_FOLDER, "new_thumb.jpg");

	static {
		if (!IMAGE_FOLDER.exists()) {
			IMAGE_FOLDER.mkdirs();
		}
	}

}
