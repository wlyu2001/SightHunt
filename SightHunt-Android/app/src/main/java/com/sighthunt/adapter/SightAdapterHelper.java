package com.sighthunt.adapter;

import com.sighthunt.data.model.Sight;
import com.sighthunt.view.CardView;

public class SightAdapterHelper {

	public static void bindSightToCardView(Sight sight, final CardView cardView) {
		cardView.setImage(sight.imageUri);
	}
}
