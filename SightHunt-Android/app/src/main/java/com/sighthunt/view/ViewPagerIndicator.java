package com.sighthunt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.sighthunt.R;
import com.sighthunt.util.Dimensions;

public class ViewPagerIndicator extends View implements ViewPager.OnPageChangeListener {

	private int mIndicatorSize;
	private int mIndicatorSpacing;

	private float mPositionOffset;

	private Paint mInactivePaint;
	private Paint mActivePaint;

	private ViewPager mViewPager;
	private ViewPager.OnPageChangeListener mListener;

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		int defaultSize = Dimensions.dipToPixelSize(10, context.getResources());
		int defaultSpacing = Dimensions.dipToPixelOffset(6, context.getResources());

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator, defStyle, 0);

		mIndicatorSize = a.getDimensionPixelSize(R.styleable.ViewPagerIndicator_indicatorSize, defaultSize);
		mIndicatorSpacing = a.getDimensionPixelOffset(R.styleable.ViewPagerIndicator_indicatorSpacing, defaultSpacing);

		int inactiveColor = a.getColor(R.styleable.ViewPagerIndicator_indicatorInactiveColor, Color.GRAY);
		int activeColor = a.getColor(R.styleable.ViewPagerIndicator_indicatorActiveColor, Color.WHITE);

		a.recycle();


		mInactivePaint = new Paint();
		mInactivePaint.setAntiAlias(true);
		mInactivePaint.setStyle(Paint.Style.FILL);
		mInactivePaint.setColor(inactiveColor);

		mActivePaint = new Paint();
		mActivePaint.setAntiAlias(true);
		mActivePaint.setStyle(Paint.Style.FILL);
		mActivePaint.setColor(activeColor);
	}

	public void setViewPager(ViewPager pager) {
		mViewPager = pager;
		mViewPager.setOnPageChangeListener(this);

		mPositionOffset = mViewPager.getCurrentItem();
		invalidate();
	}

	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
		mListener = listener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int count = (mViewPager != null) ? mViewPager.getAdapter().getCount() : 0;

		if (count <= 1) {
			setVisibility(GONE);
			setMeasuredDimension(0, 0);
			return;
		}

		int width = count * mIndicatorSize + (count - 1) * mIndicatorSpacing;
		int height = mIndicatorSize;

		width += getPaddingLeft() + getPaddingRight();
		height += getPaddingTop() + getPaddingBottom();

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int count = (mViewPager != null) ? mViewPager.getAdapter().getCount() : 0;
		if (count <= 1) {
			return;
		}

		int saveCount = canvas.save();
		canvas.translate(getPaddingLeft(), getPaddingTop());

		float radius = mIndicatorSize / 2;

		for (int i = 0; i < count; i++) {
			canvas.drawCircle(i * (mIndicatorSize + mIndicatorSpacing) + radius, radius, radius * 0.9f, mInactivePaint);
		}

		canvas.drawCircle(mPositionOffset * (mIndicatorSize + mIndicatorSpacing) + radius, radius, radius, mActivePaint);

		canvas.restoreToCount(saveCount);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		mPositionOffset = position + positionOffset;
		postInvalidate();
		if (mListener != null) {
			mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		mPositionOffset = position;
		postInvalidate();
		if (mListener != null) {
			mListener.onPageSelected(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(state);
		}
	}
}
