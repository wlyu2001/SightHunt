package com.sighthunt.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sighthunt.R;
import com.sighthunt.util.ImageHelper;

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
	private Context mContext;

	public interface CapturePreviewObserver {
		public void pictureCaptured(boolean success);
	}

	public CapturePreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
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
	public void takeAPicture(final File image, final File thumb, final CapturePreviewObserver observer) {

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

				ImageHelper.mark(mContext.getResources(), scaled);
				Bitmap small = Bitmap.createScaledBitmap(processed, 200, 200, false);
				try {
					FileOutputStream out = new FileOutputStream(image);
					scaled.compress(Bitmap.CompressFormat.JPEG, 100, out);

					out = new FileOutputStream(thumb);
					small.compress(Bitmap.CompressFormat.JPEG, 100, out);
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

	boolean zoomOn = true;

	private void handleZoom(MotionEvent event, Camera.Parameters params) {
		if (zoomOn) {
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
	}


	public void handleFocus(MotionEvent event, Camera.Parameters params) {
		List<String> supportedFocusModes = params.getSupportedFocusModes();
		if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {

			mCamera.cancelAutoFocus();
			int pointerId = event.getPointerId(0);
			int pointerIndex = event.findPointerIndex(pointerId);
			// Get the pointer's current position
			float x = event.getX();
			float y = event.getY();


			float touchMajor = event.getTouchMajor();
			float touchMinor = event.getTouchMinor();

			RectF touchRect = new RectF(
					x - touchMajor / 2,
					y - touchMinor / 2,
					x + touchMajor / 2,
					y + touchMinor / 2);

			int w = getWidth();
			int h = getHeight();

			// the camera sensor direction is 90 degree counter-clockwise relative to portrait direction
			float left = touchRect.top / h;
			float top = 1 - touchRect.right / w;
			float right = touchRect.bottom / h;
			float bottom = 1 - touchRect.left / w;

			int _left = (int) (left * 2000 - 1000);
			int _top = (int) (top * 2000 - 1000);
			int _right = (int) (right * 2000 - 1000);
			int _bottom = (int) (bottom * 2000 - 1000);

			_left = _left >= -1000 ? _left : -1000;
			_top = _top >= -1000 ? _top : -1000;
			_right = _right <= 1000 ? _right : 1000;
			_bottom = _bottom <= 1000 ? _bottom : 1000;
			final Rect targetFocusRect = new Rect(_left, _top, _right, _bottom);


			List<Camera.Area> areas = new ArrayList<Camera.Area>();
			//Rect areaRect = new Rect(x1, y1, x2, y2);
			//Rect areaRect = new Rect(_x1, _y1, _x2, _y2);
			//Log.i("focus_area", _x1 + ", " + _y1 + ", " + _x2 + "," + y2);
			areas.add(new Camera.Area(targetFocusRect, 1000));
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