package com.sighthunt.fragment.browse;

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
import android.widget.Button;
import android.widget.GridView;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;
import com.sighthunt.adapter.SightCardViewAdapter;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.network.model.SightSortType;

public class NewSightsFragment extends Fragment {


	SightCardViewAdapter mAdapter;


	private LoaderManager.LoaderCallbacks<Cursor> getLoaderCallbacks(final String region) {
		return new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

				return new CursorLoader(getActivity(),
						Contract.Sight.getFetchSightsContentUri(region, SightSortType.ByRegion.NEW.toString()),
						Sight.PROJECTION, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
				mAdapter.swapCursor(cursor);
			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_new_sights, container, false);

		GridView gridView = (GridView) view.findViewById(R.id.gridView);

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Sight sight = (Sight)mAdapter.getItem(position);
				startActivity(new Intent(getActivity(), HuntActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		mAdapter = new SightCardViewAdapter(getActivity());
		gridView.setAdapter(mAdapter);

		String region = "";
		getLoaderManager().restartLoader(R.id.loader_new_sights, null, getLoaderCallbacks(region));

		return view;
	}
}
