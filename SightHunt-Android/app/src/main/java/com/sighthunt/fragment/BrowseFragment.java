package com.sighthunt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sighthunt.R;
import com.sighthunt.fragment.browse.MostHuntedSightsFragment;
import com.sighthunt.fragment.browse.MostVotedSightsFragment;
import com.sighthunt.fragment.browse.NewSightsFragment;
import com.sighthunt.view.SlidingTabLayout;

import java.util.Locale;

public class BrowseFragment extends LocationAwareFragment {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private static final String ARG_REGION = "arg_region";

	ViewPager mViewPager;
	public LocationAwareFragment mNewSightsFragment;
	public LocationAwareFragment mMostVotedSightsFragment;
	public LocationAwareFragment mMostHuntedSightsFragment;

	private String mRegion;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mRegion = savedInstanceState.getString(ARG_REGION);
		}
	}

	public static BrowseFragment createInstance() {
		BrowseFragment fragment = new BrowseFragment();
		return fragment;
	}


	public void onSaveInstanceState(Bundle outState) {
		outState.putString(ARG_REGION, mRegion);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onLocationUpdated() {
		super.onLocationUpdated();
		if (getRegion() != null && !getRegion().equals(mRegion)) {
			if (mNewSightsFragment != null) mNewSightsFragment.onLocationUpdated();
			if (mMostVotedSightsFragment != null) mMostVotedSightsFragment.onLocationUpdated();
			if (mMostHuntedSightsFragment != null) mMostHuntedSightsFragment.onLocationUpdated();
			mRegion = getRegion();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_browse, container, false);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

			}
		});

		SlidingTabLayout tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);

		return view;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return mNewSightsFragment == null ? mNewSightsFragment = new NewSightsFragment() : mNewSightsFragment;
				case 1:
					return mMostVotedSightsFragment == null ? mMostVotedSightsFragment = new MostVotedSightsFragment() : mMostVotedSightsFragment;
				case 2:
					return mMostHuntedSightsFragment == null ? mMostHuntedSightsFragment = new MostHuntedSightsFragment() : mMostHuntedSightsFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_new).toUpperCase(l);
				case 1:
					return getString(R.string.title_most_voted).toUpperCase(l);
				case 2:
					return getString(R.string.title_most_hunted).toUpperCase(l);
			}
			return null;
		}
	}

}
