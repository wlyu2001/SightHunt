package com.sighthunt.fragment.browse;

import com.sighthunt.R;
import com.sighthunt.network.model.SightFetchType;

public class NewSightsFragment extends BaseBrowseSightsFragment {

	@Override
	public String getType() {
		return SightFetchType.NEW;
	}

	@Override
	public int getLoaderId() {
		return R.id.loader_new_sights;
	}
}
