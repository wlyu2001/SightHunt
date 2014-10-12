package com.sighthunt.fragment.browse;

import com.sighthunt.R;
import com.sighthunt.network.model.SightFetchType;

public class MostVotedSightsFragment extends BaseBrowseSightsFragment {

	@Override
	public String getType() {
		return SightFetchType.MOST_VOTED;
	}

	@Override
	public int getLoaderId() {
		return R.id.loader_most_voted_sights;
	}
}
