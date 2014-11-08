package com.sighthunt.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;
import com.sighthunt.activity.MainActivity;
import com.sighthunt.adapter.SightListViewAdapter;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.SightHuntService;
import com.sighthunt.network.model.SightFetchType;
import com.sighthunt.util.AccountUtils;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ResultsFragment extends Fragment {

	private static final int LIMIT = 25;

	SightListViewAdapter mAdapter;
	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);
	private static final int NEXT_LOADING_SIGHTS_COUNT = 3;
	private boolean mHasMoreSights = true;
	private int mCurrentOffset;
	private boolean mUserScrolled;
	private View mEmptyLayout;

	private PullToRefreshLayout mPullToRefreshLayout;

	private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallbacks(final String user, final String type, final int count) {

		return new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

				return new CursorLoader(getActivity(),
						Contract.Sight.getFetchSightsByUserLocalUri(user, type, count),
						Sight.PROJECTION, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
				mPullToRefreshLayout.setRefreshComplete();
				mAdapter.swapCursor(cursor);
				if (cursor.getCount() < mCurrentOffset + LIMIT) {
					mHasMoreSights = false;
				} else {
					mHasMoreSights = true;
				}
				if (cursor.getCount() == 0) {
					mEmptyLayout.setVisibility(View.VISIBLE);
				} else {
					mEmptyLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		};
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reloadSights();
	}

	private void reloadSights() {
		getLoaderManager().restartLoader(R.id.loader_my_created_sights, null, getLoaderCallbacks(mAccountUtils.getUsername(), SightFetchType.CREATED_BY, LIMIT));

		mCurrentOffset = 0;
		mHasMoreSights = true;
		getActivity().startService(SightHuntService.getFetchSightsByUserIntent(getActivity(),
				mAccountUtils.getUsername(),
				SightFetchType.CREATED_BY,
				0, LIMIT));
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_my_sights, container, false);
		mEmptyLayout = view.findViewById(R.id.emptyLayout);
		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().headerLayout(R.layout.header_layout).build())
				.allChildrenArePullable()
				.listener(new OnRefreshListener() {
					@Override
					public void onRefreshStarted(View view) {
						reloadSights();
					}
				})
				.setup(mPullToRefreshLayout);


		ListView listView = (ListView) view.findViewById(R.id.listView);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) mAdapter.getItem(position);
				Sight sight = Sight.fromCursor(cursor);
				startActivity(HuntActivity.getIntent(getActivity(), sight).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					mUserScrolled = true;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (!mUserScrolled) return;

				if (!mHasMoreSights) return;

				boolean endOfListReached = (firstVisibleItem + visibleItemCount) >= (totalItemCount - NEXT_LOADING_SIGHTS_COUNT + 1);
				if (endOfListReached) {
					if (totalItemCount > 0) {
						final int offset = totalItemCount;
						// have we run this query already?
						if (offset > mCurrentOffset) {
							getLoaderManager().restartLoader(R.id.loader_my_created_sights, null, getLoaderCallbacks(mAccountUtils.getUsername(), SightFetchType.CREATED_BY, mCurrentOffset + 2 * LIMIT));
							getActivity().startService(SightHuntService.getFetchSightsByUserIntent(getActivity(), mAccountUtils.getUsername(), SightFetchType.CREATED_BY, offset, LIMIT));
							mCurrentOffset = offset;

						}
					}
				}
			}
		});

		mAdapter = new SightListViewAdapter(getActivity());
		listView.setAdapter(mAdapter);
		((MainActivity) getActivity()).updateUIForResult();

		return view;
	}

	public static ResultsFragment createInstance() {
		ResultsFragment fragment = new ResultsFragment();
		return fragment;
	}
}
