package com.sighthunt.fragment.browse;

import com.sighthunt.R;
import com.sighthunt.network.model.SightFetchType;

public class MostHuntedSightsFragment extends BaseBrowseSightsFragment {

	@Override
	public String getType() {
		return SightFetchType.MOST_HUNTED;
	}

	@Override
	public int getLoaderId() {
		return R.id.loader_most_hunted_sights;
	}
}
