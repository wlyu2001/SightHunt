package com.sighthunt.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;

import java.util.List;

public class MatchOverlayView extends View {
	Paint mPaint;

	public MatchOverlayView(Context context) {
		super(context);
		initialize();
	}

	public MatchOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public MatchOverlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	private void initialize() {
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(1);
	}

	KeyPoint[] mKeyPoints1;
	KeyPoint[] mKeyPoints2;
	List<DMatch> mMatches;

	float mWidth;
	float mHeight;

	public void setMatches(MatOfKeyPoint keyPoints1, MatOfKeyPoint keyPoints2, List<DMatch> matches, float width, float height) {
		mKeyPoints1 = keyPoints1.toArray();
		mKeyPoints2 = keyPoints2.toArray();
		mMatches = matches;
		mWidth = width;
		mHeight = height;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mMatches == null) return;

		float imageSize = getHeight() / 2;
		float offsetX = (getWidth() - getHeight() / 2) / 2;

		float widthRatio = imageSize / mWidth;
		float heightRatio = imageSize / mHeight;

		for (DMatch match : mMatches) {

			float x1 = (float)mKeyPoints1[match.queryIdx].pt.x * widthRatio + offsetX;
			float y1 = (float)mKeyPoints1[match.queryIdx].pt.y * heightRatio;
			float x2 = (float)mKeyPoints2[match.trainIdx].pt.x * widthRatio + offsetX;
			float y2 = (float)mKeyPoints2[match.trainIdx].pt.y * heightRatio + imageSize;
			canvas.drawLine(x1, y1, x2, y2, mPaint);

		}
	}
}
