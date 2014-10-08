package com.sighthunt.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sighthunt.R;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.fragment.hunt.HuntCamFragment;
import com.sighthunt.inject.Injector;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.util.ImageHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SightFragment extends Fragment {

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);
	Button mButton;

	Target mTarget = new Target() {
		@Override
		public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						FileOutputStream out = new FileOutputStream(ImageFiles.ORIGINAL_IMAGE);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

		@Override
		public void onBitmapFailed(Drawable errorDrawable) {
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_sight, container, false);

		final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
		final TextView descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
		final TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);


		Bundle args = getArguments();
		final String key = args.getString(Contract.Sight.KEY);

		mButton = (Button) view.findViewById(R.id.go_to_cam_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().replace(R.id.container, HuntCamFragment.createInstance(key), null).addToBackStack(null).commit();
			}
		});


		final Uri uri = Contract.Sight.getFetchSightByKeyUri(key);

		getLoaderManager().initLoader(R.id.loader_sight, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
				return new CursorLoader(getActivity(), uri, Sight.PROJECTION, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
				if (!cursor.moveToFirst())
					return;
				Sight sight = Sight.fromCursor(cursor);

				checkHunt(sight);

				Picasso.with(getActivity()).load(ImageHelper.getImageUrl(sight.imageKey)).into(mTarget);

				Picasso.with(getActivity()).load(ImageHelper.getImageUrl(sight.imageKey)).into(imageView);

				descriptionTextView.setText(sight.description);
				titleTextView.setText(sight.title);

			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		});

		return view;
	}

	private void checkHunt(final Sight sight) {
		getLoaderManager().initLoader(R.id.loader_check_hunt, null, new LoaderManager.LoaderCallbacks<Cursor>() {
			@Override
			public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
				return new CursorLoader(getActivity(), Contract.Hunt.getCheckHuntUri(mAccountUtils.getUsername(), sight.key), null, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
			boolean isHunted = cursor.moveToFirst();

				if (mAccountUtils.getUsername().equals(sight.creator) || isHunted) {
					mButton.setVisibility(View.GONE);
				} else {
					mButton.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		});
	}

	public static SightFragment createInstance(String key) {
		SightFragment fragment = new SightFragment();
		Bundle args = new Bundle();
		args.putString(Contract.Sight.KEY, key);
		fragment.setArguments(args);
		return fragment;
	}
}
