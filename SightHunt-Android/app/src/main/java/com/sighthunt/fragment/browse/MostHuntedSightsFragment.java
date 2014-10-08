package com.sighthunt.fragment.browse;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;
import com.sighthunt.adapter.SightCardViewAdapter;
import com.sighthunt.fragment.LocationAwareFragment;
import com.sighthunt.network.model.SightSortType;

public class MostHuntedSightsFragment extends BaseBrowseSightsFragment {

	@Override
	public String getType() {
		return SightSortType.MOST_HUNTED;
	}

	@Override
	public int getLoaderId() {
		return R.id.loader_most_hunted_sights;
	}
}
