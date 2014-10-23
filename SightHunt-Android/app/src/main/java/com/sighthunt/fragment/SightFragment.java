package com.sighthunt.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

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

	private AccountUtils mAccountUtils = Injector.get(AccountUtils.class);
	private Button mButtonHunt;
	private Button mButtonEdit;
	private Button mButtonShare;
	private EditText mDescriptionEditText;
	private EditText mTitleEditText;
	private Drawable mOriginalDrawable;
	private boolean mEditMode;
	private boolean mOwnSight;
	private MenuItem mMenuItemFlag;
	private MenuItem mMenuItemDelete;


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
		final Sight s = args.getParcelable(Sight.ARG);

		setHasOptionsMenu(true);

		mButtonHunt = (Button) view.findViewById(R.id.buttonHunt);
		mButtonEdit = (Button) view.findViewById(R.id.buttonEdit);
		mButtonShare = (Button) view.findViewById(R.id.buttonShare);
		mButtonHunt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().replace(R.id.container, HuntCamFragment.createInstance(s), null).addToBackStack(null).commit();
			}
		});

		mButtonShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Sight hunt in " + s.region);
				sharingIntent.putExtra(Intent.EXTRA_TEXT, s.title + "\n" + s.description);
				sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ImageFiles.ORIGINAL_IMAGE));
				startActivity(Intent.createChooser(sharingIntent, "Share via"));
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

					String description = mDescriptionEditText.getText().toString();
					String title = mTitleEditText.getText().toString();

					if (!TextUtils.equals(description, s.description) || !TextUtils.equals(title, s.title)) {
						com.sighthunt.network.model.Sight sight = new com.sighthunt.network.model.Sight();
						sight.uuid = s.uuid;
						sight.title = title;
						sight.description = description;
						getActivity().startService(SightHuntService.getEditSightIntent(getActivity(), sight));
					}
					mButtonEdit.setText("Edit");

					mDescriptionEditText.setBackground(null);
					mTitleEditText.setBackground(null);
					mDescriptionEditText.setEnabled(false);
					mTitleEditText.setEnabled(false);
					mEditMode = false;
				}
			}
		});


		final Uri uri = Contract.Sight.getFetchSightByUUIDUri(s.uuid);

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

	private DialogInterface.OnClickListener reportDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Sight s = getArguments().getParcelable(Sight.ARG);
			ListView lw = ((AlertDialog) dialog).getListView();
			int position = lw.getCheckedItemPosition();
			getActivity().startService(SightHuntService.getReportSightIntent(getActivity(), s.uuid, mAccountUtils.getUsername(), position));
			getActivity().finish();
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!mOwnSight) {
			mMenuItemFlag = menu.add(R.string.button_flag);
			mMenuItemFlag.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle(R.string.title_report);
					builder.setSingleChoiceItems(R.array.report_reasons, 0, null);
					builder.setPositiveButton(R.string.button_report_ok, reportDialogListener);
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
					return false;
				}
			});
		} else {
			mMenuItemDelete = menu.add(R.string.button_delete);
			mMenuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Sight s = getArguments().getParcelable(Sight.ARG);
					getActivity().startService(SightHuntService.getDeleteSightIntent(getActivity(), s.uuid));
					getActivity().finish();
					return true;
				}
			});
		}
		super.onCreateOptionsMenu(menu, inflater);
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
					mButtonShare.setVisibility(View.VISIBLE);
					mOwnSight = true;
				} else {
					mDescriptionEditText.setHint(R.string.hint_description_hunt);
					mTitleEditText.setHint(R.string.hint_title_hunt);
					if (!isHunted) {
						mButtonHunt.setVisibility(View.VISIBLE);
					} else {
						mButtonHunt.setVisibility(View.GONE);
					}
					mButtonEdit.setVisibility(View.GONE);
					mButtonShare.setVisibility(View.VISIBLE);
					mOwnSight = false;
				}
				getActivity().invalidateOptionsMenu();
			}

			@Override
			public void onLoaderReset(Loader<Cursor> cursorLoader) {

			}
		});
	}

	public static SightFragment createInstance(Sight sight) {
		SightFragment fragment = new SightFragment();
		Bundle args = new Bundle();
		args.putParcelable(Sight.ARG, sight);
		fragment.setArguments(args);
		return fragment;
	}
}
