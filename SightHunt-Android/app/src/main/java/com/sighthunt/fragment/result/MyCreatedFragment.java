package com.sighthunt.fragment.result;

import com.sighthunt.R;
import com.sighthunt.network.model.SightFetchType;

public class MyCreatedFragment extends BaseMySightsFragment {

	@Override
	public String getType() {
		return SightFetchType.CREATED_BY;
	}

	@Override
	public int getLoaderId() {
		return R.id.loader_my_created_sights;
	}
}
