package com.sighthunt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sighthunt.R;
import com.squareup.picasso.Picasso;

public class ListItemView extends LinearLayout {

	ImageView mImageView;
	TextView mTitleView;
	TextView mSubtitleView;

	public ListItemView(Context context) {
		this(context, null);
	}

	public ListItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflate(context, R.layout.listitemview, this);
		mImageView = (ImageView)findViewById(R.id.imageView);
		mTitleView = (TextView)findViewById(R.id.titleView);
		mSubtitleView = (TextView)findViewById(R.id.subtitleView);
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}
	public void setSubtitle(String subtitle) {
		mTitleView.setText(subtitle);
	}

	public void setImage(String imageUri) {
		Picasso.with(getContext()).load(imageUri).into(mImageView);
	}

	public static ListItemView createInstance(Context context) {
		return new ListItemView(context);
	}
}
