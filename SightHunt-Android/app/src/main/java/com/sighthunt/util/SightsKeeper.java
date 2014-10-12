package com.sighthunt.util;

import com.sighthunt.inject.Injectable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SightsKeeper implements Injectable {
	private Set<Long> mSightKeys = new HashSet<Long>();
	private Set<Long> mSightUUIDs = new HashSet<Long>();

	public boolean containsKey(long key) {
		return mSightKeys.contains(key);
	}

	public boolean  containUUID(long uuid) {
		return mSightUUIDs.contains(uuid);
	}

	public void addCachedSightKey(long key, long uuid) {
		mSightKeys.add(key);
		mSightUUIDs.add(uuid);
	}
}
