package com.sighthunt.util;

public class ImageHelper {
	private static final String IMAGE_URL_ROOT = "http://sight-hunt.appspot.com/image/serve/";
	public static String getImageUrl(String imageKey) {
		return IMAGE_URL_ROOT + imageKey;
	}
}
