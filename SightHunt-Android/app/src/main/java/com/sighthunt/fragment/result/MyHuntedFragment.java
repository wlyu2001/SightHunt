package com.sighthunt.fragment.result;

import com.sighthunt.R;
import com.sighthunt.network.model.SightSortType;

public class MyHuntedFragment extends BaseMySightsFragment {
	@Override
	public String getType() {
		return SightSortType.HUNTED_BY;
	}

	@Override
	public int getLoaderId() {
		return R.id.loader_my_hunted_sights;
	}
}
