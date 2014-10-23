package com.sighthunt.fragment.browse;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;
import com.sighthunt.adapter.SightCardViewAdapter;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.fragment.LocationAwareFragment;
import com.sighthunt.network.SightHuntService;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public abstract class BaseBrowseSightsFragment extends LocationAwareFragment {

	public abstract String getType();

	public abstract int getLoaderId();

	private static final int LIMIT = 25;
	private static final int NEXT_LOADING_SIGHTS_COUNT = 3;
	private static final int TOTAL_DISPLAY_SIGHTS_COUNT = 100;
	private int mColumnsCount;
	private int mCurrentOffset;
	private PullToRefreshLayout mPullToRefreshLayout;
	private boolean mUserScrolled;

	SightCardViewAdapter mAdapter;
	GridView mGridView;
	private boolean mHasMoreSights = true;

	private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallbacks(final String region, final String type, final int count) {
		return new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

				return new CursorLoader(getActivity(),
						Contract.Sight.getFetchSightsByRegionLocalUri(region, type, count),
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
			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		};
	}

	private void loadSights(String region) {
		getLoaderManager().restartLoader(getLoaderId(), null, getLoaderCallbacks(region, getType(), LIMIT));
		mCurrentOffset = 0;
		mHasMoreSights = true;
		getActivity().startService(SightHuntService.getFetchSightsByRegionIntent(getActivity(), region, getType(), 0, LIMIT));

	}

	@Override
	public void onRegionUpdated(String region, boolean changed) {
		if (changed) {
			loadSights(region);
		} else {
			mPullToRefreshLayout.setRefreshComplete();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String region = getRegion();
		if (region != null) {
			loadSights(region);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_browse_sights, container, false);

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().headerLayout(R.layout.header_layout).build())
				.allChildrenArePullable()
				.listener(new OnRefreshListener() {
					@Override
					public void onRefreshStarted(View view) {
						String region = getRegion();
						if (region != null) {
							loadSights(region);
						} else {
							requestRegion();
						}

					}
				})
				.setup(mPullToRefreshLayout);

		mColumnsCount = getResources().getInteger(R.integer.numColumns);
		mGridView = (GridView) view.findViewById(R.id.gridView);

		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
					if (totalItemCount > 0 && totalItemCount < TOTAL_DISPLAY_SIGHTS_COUNT) {
						final int offset = totalItemCount;
						// have we run this query already?
						if (offset > mCurrentOffset) {
							String region = getRegion();
							if (region != null) {
								getLoaderManager().restartLoader(getLoaderId(), null, getLoaderCallbacks(region, getType(), mCurrentOffset + 2 * LIMIT));
								getActivity().startService(SightHuntService.getFetchSightsByRegionIntent(getActivity(), region, getType(), offset, LIMIT));
								mCurrentOffset = offset;
							}
						}
					}
				}
			}
		});

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) mAdapter.getItem(position);
				Sight sight = Sight.fromCursor(cursor);
				startActivity(HuntActivity.getIntent(getActivity(), sight).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		mAdapter = new SightCardViewAdapter(getActivity());
		mGridView.setAdapter(mAdapter);

		return view;
	}

}
