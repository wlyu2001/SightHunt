package com.sighthunt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sighthunt.R;
import com.sighthunt.fragment.browse.MostHuntedSightsFragment;
import com.sighthunt.fragment.browse.MostVotedSightsFragment;
import com.sighthunt.fragment.browse.NewSightsFragment;
import com.sighthunt.fragment.result.MyCreatedFragment;
import com.sighthunt.fragment.result.MyHuntedFragment;
import com.sighthunt.view.SlidingTabLayout;

import java.util.Locale;

public class ResultsFragment extends Fragment {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	public Fragment mMyCreatedFragment;
	public Fragment mMyHuntedFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static ResultsFragment createInstance() {
		ResultsFragment fragment = new ResultsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_results, container, false);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

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
					return mMyCreatedFragment == null ? mMyCreatedFragment = new MyCreatedFragment() : mMyCreatedFragment;
				case 1:
					return mMyHuntedFragment == null ? mMyHuntedFragment = new MyHuntedFragment() : mMyHuntedFragment;
				}
			return null;

		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_created).toUpperCase(l);
				case 1:
					return getString(R.string.title_hunted).toUpperCase(l);
			}
			return null;
		}
	}


	public static class PlaceholderFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);
			TextView textView = (TextView) rootView.findViewById(R.id.section_label);
			textView.setText("SECTION NUMBER " + getArguments().getInt(ARG_SECTION_NUMBER));
			return rootView;
		}
	}

}
