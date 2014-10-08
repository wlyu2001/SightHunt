package com.sighthunt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback {

	public static Bitmap mBitmap;
	SurfaceHolder holder;
	static Camera mCamera;
	private float mDist;

	public interface CapturePreviewObserver {
		public void pictureCaptured(boolean success);
	}

	public CapturePreview(Context context, AttributeSet attrs) {
		super(context, attrs);

		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		Camera.Parameters parameters = mCamera.getParameters();
		parameters.getSupportedPreviewSizes();
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		try {
			mCamera = Camera.open();
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(90);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
	}

	/**
	 * Take a picture and and convert it from bytes[] to Bitmap.
	 */
	public static void takeAPicture(final File image, final File thumb, final CapturePreviewObserver observer) {

		Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				BitmapFactory.Options options = new BitmapFactory.Options();
				mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				int h = mBitmap.getHeight();
				int w = mBitmap.getWidth();
				int size = Math.min(h, w);
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap processed = Bitmap.createBitmap(mBitmap, (w - size) / 2, (h - size) / 2, size, size, matrix, true);
				Bitmap scaled = Bitmap.createScaledBitmap(processed, 500, 500, false);
				Bitmap scaled1 = Bitmap.createScaledBitmap(processed, 200, 200, false);
				try {
					FileOutputStream out = new FileOutputStream(image);
					scaled.compress(Bitmap.CompressFormat.JPEG, 100, out);

					out = new FileOutputStream(thumb);
					scaled1.compress(Bitmap.CompressFormat.JPEG, 100, out);
					observer.pictureCaptured(true);
				} catch (IOException e) {
					e.printStackTrace();
					observer.pictureCaptured(false);
				}
			}
		};
		mCamera.takePicture(null, null, mPictureCallback);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Get the pointer ID
		Camera.Parameters params = mCamera.getParameters();
		int action = event.getAction();

		if (event.getPointerCount() > 1) {
			// handle multi-touch events
			if (action == MotionEvent.ACTION_POINTER_DOWN) {
				mDist = getFingerSpacing(event);
			} else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
				mCamera.cancelAutoFocus();
				handleZoom(event, params);
			}
		} else {
			// handle single touch events
			if (action == MotionEvent.ACTION_UP) {
				handleFocus(event, params);
			}
		}
		return true;
	}

	private void handleZoom(MotionEvent event, Camera.Parameters params) {
		int maxZoom = params.getMaxZoom();
		int zoom = params.getZoom();
		float newDist = getFingerSpacing(event);
		if (newDist > mDist) {
			//zoom in
			if (zoom < maxZoom)
				zoom++;
		} else if (newDist < mDist) {
			//zoom out
			if (zoom > 0)
				zoom--;
		}
		mDist = newDist;
		params.setZoom(zoom);
		mCamera.setParameters(params);
	}

	public void handleFocus(MotionEvent event, Camera.Parameters params) {
		List<String> supportedFocusModes = params.getSupportedFocusModes();
		if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {

			mCamera.cancelAutoFocus();
			int pointerId = event.getPointerId(0);
			int pointerIndex = event.findPointerIndex(pointerId);
			// Get the pointer's current position
			float x = event.getX(pointerIndex);
			float y = event.getY(pointerIndex);

			float centerX = x / getWidth() * 2000 - 1000;
			float centerY = y / getHeight() * 2000 - 1000;

			int size = 50;
			int x1 = (int) (centerX - size);
			int x2 = (int) (centerX + size);
			int y1 = (int) (centerY - size);
			int y2 = (int) (centerY + size);


			// need to do this transformation, due to rotation of display
			int _x1 = y1;
			int _y1 = -x1 - 2 * size;
			int _x2 = y2;
			int _y2 = -x2 + 2 * size;

			_x1 = _x1 >= -1000 ? _x1 : -1000;
			_x2 = _x2 <= 1000 ? _x2 : 1000;
			_y1 = _y1 >= -1000 ? _y1 : -1000;
			_y2 = _y2 <= 1000 ? _y2 : 1000;

			List<Camera.Area> areas = new ArrayList<Camera.Area>();
			Rect areaRect = new Rect(_x1, _y1, _x2, _y2);
			areas.add(new Camera.Area(areaRect, 1000));
			params.setFocusAreas(areas);

			mCamera.setParameters(params);
			mCamera.autoFocus(null);
		}
	}

	/**
	 * Determine the space between the first two fingers
	 */
	private float getFingerSpacing(MotionEvent event) {
		// ...
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
}