package com.sighthunt.network.model;

public class SightSortType {

	public enum ByRegion {
		NEW("new"),
		MOST_VOTED("most_voted"),
		MOST_HUNTED("most_hunted");

		private final String mText;

		private ByRegion(final String text) {
			mText = text;
		}

		@Override
		public String toString() {
			return mText;
		}
	}

	public enum ByUser {
		NEW("created"),
		MOST_VOTED("hunted");

		private final String mText;

		private ByUser(final String text) {
			mText = text;
		}

		@Override
		public String toString() {
			return mText;
		}
	}
}
