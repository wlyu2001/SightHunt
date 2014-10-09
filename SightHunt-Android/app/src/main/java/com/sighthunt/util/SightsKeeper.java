package com.sighthunt.util;

import com.sighthunt.inject.Injectable;

import java.util.HashSet;
import java.util.Set;

public class SightsKeeper implements Injectable {
	private Set<Long> mSightIds = new HashSet<Long>();

	public boolean containSightId(long sightId) {
		return mSightIds.contains(sightId);
	}

	public void addCachedSightId(long id) {
		mSightIds.add(id);
	}
}
