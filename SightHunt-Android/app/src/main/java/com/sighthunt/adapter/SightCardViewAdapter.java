package com.sighthunt.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.sighthunt.data.model.Sight;
import com.sighthunt.util.ImageHelper;
import com.sighthunt.view.CardView;

public class SightCardViewAdapter extends CursorAdapter {
	Context mContext;
	public SightCardViewAdapter(Context context) {
		super(context, null, 0);
		mContext = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return CardView.createInstance(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CardView cardView = (CardView) view;
		if (cardView == null)
			cardView = CardView.createInstance(context);

		Sight sight = Sight.fromCursor(cursor);

		cardView.setImage(ImageHelper.getImageUrl(sight.thumbKey));
	}
}
