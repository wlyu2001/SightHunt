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
import android.widget.Button;
import android.widget.GridView;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;
import com.sighthunt.adapter.SightCardViewAdapter;
import com.sighthunt.data.Contract;

public class MostHuntedSightsFragment extends Fragment {

	SightCardViewAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
			return null;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
			mAdapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> cursorLoader) {

		}
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_most_hunted_sights, container, false);

		GridView gridView = (GridView) view.findViewById(R.id.gridView);

		mAdapter = new SightCardViewAdapter(getActivity());
		gridView.setAdapter(mAdapter);
		getLoaderManager().initLoader(R.id.loader_most_hunted_sights, null, mLoaderCallbacks);

		Button button = (Button) view.findViewById(R.id.go_to_hunt_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), HuntActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		return view;
	}
}
