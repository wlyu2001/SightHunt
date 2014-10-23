package com.sighthunt.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.sighthunt.R;

public class ImageHelper {
	private static final String IMAGE_URL_ROOT = "http://sight-hunt.appspot.com/image/serve";

	public static String getImageUrl(String imageKey) {
		return IMAGE_URL_ROOT + "?image_key=" + imageKey;
	}

	public static void mark(Resources res, final Bitmap src) {
		Bitmap logo = BitmapFactory.decodeResource(res, R.drawable.watermark);

		Canvas canvas = new Canvas(src);
		canvas.drawBitmap(src, 0, 0, null);

		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setAlpha(70);
		int textHeight = 30;
		int _left = 350;
		int _bottom = 480;

		paint.setTextSize(textHeight);
		canvas.drawText(res.getString(R.string.app_name_lower), _left, _bottom, paint);
		int w = logo.getWidth();
		int h = logo.getHeight();
		int height = 50;
		int bottom = _bottom + 10;
		int top = bottom - height;
		int right = _left;
		int left = right - height * w / h;
		canvas.drawBitmap(logo, new Rect(0, 0, w, h), new Rect(left, top, right, bottom), paint);

//		left = 10;
//		top = 10;
//		bottom = top + height;
//		right = left + height * w / h;
//
//		canvas.drawBitmap(logo, new Rect(0, 0, w, h), new Rect(left, top, right, bottom), paint);
//
//		_left = right;
//		_bottom = bottom - 10;
//		canvas.drawText(res.getString(R.string.app_name_lower), _left, _bottom, paint);


	}
}
