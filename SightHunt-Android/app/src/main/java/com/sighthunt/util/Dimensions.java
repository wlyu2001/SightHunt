package com.sighthunt.util;

import android.content.res.Resources;

public final class Dimensions {
	private Dimensions() {}

	public static int dipToPixelOffset(float dip, Resources resources) {
		return (int)(dip * resources.getDisplayMetrics().density);
	}

	public static int dipToPixelSize(float dip, Resources resources) {
		final int res = Math.round(dip * resources.getDisplayMetrics().density);
		if (res != 0) return res;
		if (dip == 0) return 0;
		if (dip > 0) return 1;
		return -1;
	}
}
