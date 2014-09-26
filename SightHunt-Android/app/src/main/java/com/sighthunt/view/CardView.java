package com.sighthunt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sighthunt.R;
import com.squareup.picasso.Picasso;

public class CardView extends LinearLayout {

	ImageView mImageView;

	public CardView(Context context) {
		this(context, null);
	}

	public CardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflate(context, R.layout.cardview, this);
		mImageView = (ImageView)findViewById(R.id.imageView);
	}

	public void setImage(String imageUri) {
		Picasso.with(getContext()).load(imageUri).into(mImageView);
	}

	public static CardView createInstance(Context context) {
		return new CardView(context);
	}
}
