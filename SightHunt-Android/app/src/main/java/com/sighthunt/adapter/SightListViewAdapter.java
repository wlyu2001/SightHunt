package com.sighthunt.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.sighthunt.data.model.Sight;
import com.sighthunt.util.ImageHelper;
import com.sighthunt.view.CardView;
import com.sighthunt.view.ListItemView;

public class SightListViewAdapter extends CursorAdapter {
	Context mContext;
	public SightListViewAdapter(Context context) {
		super(context, null, 0);
		mContext = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return ListItemView.createInstance(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ListItemView listItemView = (ListItemView) view;
		if (listItemView == null)
			listItemView = ListItemView.createInstance(context);

		Sight sight = Sight.fromCursor(cursor);

		listItemView.setImage(ImageHelper.getImageUrl(sight.thumbKey));
		listItemView.setTitle(sight.title);
		listItemView.setSubtitle(sight.region + "\n" + sight.votes + " votes  " + sight.hunts + " hunts.");
	}
}
