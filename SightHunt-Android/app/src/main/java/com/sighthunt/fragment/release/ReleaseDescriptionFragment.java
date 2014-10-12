package com.sighthunt.fragment.release;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.data.Contract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.model.Sight;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.ImageFiles;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

public class ReleaseDescriptionFragment extends Fragment {


	ImageView mImageView;
	EditText mEditTextTitle;
	EditText mEditTextDescription;

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_release_description, container, false);

		mImageView = (ImageView) view.findViewById(R.id.imageView);
		mEditTextTitle = (EditText) view.findViewById(R.id.title);
		mEditTextDescription = (EditText) view.findViewById(R.id.description);

		final String region = getArguments().getString(Contract.Sight.REGION);
		final float lon = getArguments().getFloat(Contract.Sight.LON);
		final float lat = getArguments().getFloat(Contract.Sight.LAT);

		Picasso.with(getActivity()).load(ImageFiles.NEW_IMAGE).skipMemoryCache().into(mImageView);

		Button button = (Button) view.findViewById(R.id.buttonCreateSight);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Sight sight = new Sight();
				sight.title = mEditTextTitle.getText().toString();
				sight.region = region;
				sight.description = mEditTextDescription.getText().toString();
				sight.creator = mAccountUtils.getUsername();
				sight.last_modified = new Date().getTime();
				sight.time_created = new Date().getTime();
				sight.lon = lon;
				sight.lat = lat;
				ContentValues values = Contract.Sight.createContentValues(sight);
				getActivity().getContentResolver().insert(Contract.Sight.getCreateSightRemoteUri(), values);
				getActivity().finish();
				Toast.makeText(getActivity(), "Releasing new sight...", Toast.LENGTH_LONG).show();
//				getActivity().getContentResolver().registerContentObserver(Contract.Sight.getCreateSightRemoteUri(), false, new ContentObserver(new Handler(Looper.getMainLooper())) {
//					@Override
//					public void onChange(boolean selfChange) {
//						super.onChange(selfChange);
//						Activity activity = getActivity();
//						if (activity != null) {
//							Toast.makeText(activity, "Sight created", Toast.LENGTH_LONG).show();
//							activity.finish();
//						}
//					}
//				});
			}
		});


		return view;
	}

	public static ReleaseDescriptionFragment createInstance(String region, float lon, float lat) {
		ReleaseDescriptionFragment fragment = new ReleaseDescriptionFragment();

		Bundle arguments = new Bundle();
		arguments.putString(Contract.Sight.REGION, region);
		arguments.putFloat(Contract.Sight.LON, lon);
		arguments.putFloat(Contract.Sight.LAT, lat);

		fragment.setArguments(arguments);

		return fragment;
	}
}
