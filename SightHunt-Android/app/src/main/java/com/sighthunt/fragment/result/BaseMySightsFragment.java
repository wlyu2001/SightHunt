package com.sighthunt.fragment.result;

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
import android.widget.AdapterView;
import android.widget.ListView;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;
import com.sighthunt.adapter.SightListViewAdapter;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.inject.Injector;
import com.sighthunt.util.AccountUtils;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public abstract class BaseMySightsFragment extends Fragment {

	public abstract String getType();

	public abstract int getLoaderId();

	private static final int LIMIT = 25;

	SightListViewAdapter mAdapter;
	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);

	private PullToRefreshLayout mPullToRefreshLayout;

	LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

			return new CursorLoader(getActivity(),
					Contract.Sight.getFetchSightsByUserLocalUri(mAccountUtils.getUsername(), getType()),
					Sight.PROJECTION, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
			mPullToRefreshLayout.setRefreshComplete();
			mAdapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> cursorLoader) {

		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().restartLoader(getLoaderId(), null, mLoaderCallback);
		getActivity().getContentResolver().query(Contract.Sight.getFetchSightsByUserRemoteUri(mAccountUtils.getUsername(), getType(), 0, LIMIT), null, null, null, null);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_my_sights, container, false);

		mPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.ptr_layout);

		ActionBarPullToRefresh.from(getActivity())
				.options(Options.create().headerLayout(R.layout.header_layout).build())
				.allChildrenArePullable()
				.listener(new OnRefreshListener() {
					@Override
					public void onRefreshStarted(View view) {
						getActivity().getContentResolver().query(Contract.Sight.getFetchSightsByUserRemoteUri(mAccountUtils.getUsername(), getType(), 0, LIMIT), null, null, null, null);
					}
				})
				.setup(mPullToRefreshLayout);


		ListView listView = (ListView) view.findViewById(R.id.listView);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor cursor = (Cursor) mAdapter.getItem(position);
				Sight sight = Sight.fromCursor(cursor);
				startActivity(HuntActivity.getIntent(getActivity(), sight.key).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		mAdapter = new SightListViewAdapter(getActivity());
		listView.setAdapter(mAdapter);

		return view;
	}

}
