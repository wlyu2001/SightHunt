package com.sighthunt.fragment;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.sighthunt.R;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.fragment.hunt.HuntCamFragment;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.SightHuntService;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.util.ImageHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SightFragment extends Fragment {

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);
	Button mButtonHunt;
	Button mButtonEdit;
	Button mButtonDelete;
	EditText mDescriptionEditText;
	EditText mTitleEditText;
	Drawable mOriginalDrawable;
	private boolean mEditMode;

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
		mDescriptionEditText = (EditText) view.findViewById(R.id.descriptionEditText);
		mTitleEditText = (EditText) view.findViewById(R.id.titleEditText);
		mOriginalDrawable = mDescriptionEditText.getBackground();
		mDescriptionEditText.setBackground(null);
		mTitleEditText.setBackground(null);
		Bundle args = getArguments();
		final long key = args.getLong(Contract.Sight.KEY);
		final long uuid = args.getLong(Contract.Sight.UUID);

		mButtonHunt = (Button) view.findViewById(R.id.buttonHunt);
		mButtonDelete = (Button) view.findViewById(R.id.buttonDelete);
		mButtonEdit = (Button) view.findViewById(R.id.buttonEdit);
		mButtonHunt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().replace(R.id.container, HuntCamFragment.createInstance(key, uuid), null).addToBackStack(null).commit();
			}
		});
		mButtonDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().startService(SightHuntService.getDeleteSightIntent(getActivity(),uuid));
				getActivity().finish();
			}
		});

		mButtonEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mEditMode) {
					mButtonEdit.setText("Save");
					mDescriptionEditText.setBackground(mOriginalDrawable);
					mTitleEditText.setBackground(mOriginalDrawable);
					mDescriptionEditText.setEnabled(true);
					mTitleEditText.setEnabled(true);
					mEditMode = true;
				} else {

					com.sighthunt.network.model.Sight sight = new com.sighthunt.network.model.Sight();
					sight.uuid = uuid;
					sight.title = mTitleEditText.getText().toString();
					sight.description = mDescriptionEditText.getText().toString();
					getActivity().startService(SightHuntService.getEditSightIntent(getActivity(), sight));

					mButtonEdit.setText("Edit");

					mDescriptionEditText.setBackground(null);
					mTitleEditText.setBackground(null);
					mDescriptionEditText.setEnabled(false);
					mTitleEditText.setEnabled(false);
					mEditMode = false;
				}
			}
		});


		final Uri uri = Contract.Sight.getFetchSightByUUIDUri(uuid);

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

				mDescriptionEditText.setText(sight.description);
				mTitleEditText.setText(sight.title);

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
				return new CursorLoader(getActivity(), Contract.Hunt.getCheckHuntUri(mAccountUtils.getUsername(), sight.uuid), null, null, null, null);
			}

			@Override
			public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
				boolean isHunted = cursor.moveToFirst();

				if (mAccountUtils.getUsername().equals(sight.creator)) {
					mButtonHunt.setVisibility(View.GONE);
					mButtonEdit.setVisibility(View.VISIBLE);
					mButtonDelete.setVisibility(View.VISIBLE);
				} else {
					if (!isHunted) {
						mButtonHunt.setVisibility(View.VISIBLE);
					} else {
						mButtonHunt.setVisibility(View.GONE);
					}
					mButtonEdit.setVisibility(View.GONE);
					mButtonDelete.setVisibility(View.GONE);
				}
			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		});
	}

	public static SightFragment createInstance(long key, long uuid) {
		SightFragment fragment = new SightFragment();
		Bundle args = new Bundle();
		args.putLong(Contract.Sight.KEY, key);
		args.putLong(Contract.Sight.UUID, uuid);
		fragment.setArguments(args);
		return fragment;
	}
}
