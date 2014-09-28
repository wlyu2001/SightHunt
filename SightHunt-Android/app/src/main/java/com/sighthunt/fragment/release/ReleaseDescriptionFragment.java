package com.sighthunt.fragment.release;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sighthunt.R;
import com.sighthunt.data.Contract;
import com.sighthunt.network.model.Sight;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ReleaseDescriptionFragment extends Fragment{


	ImageView mImageView;
	EditText mEditTextTitle;
	EditText mEditTextDescription;


	public static final String ARG_IMAGE = "image";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_release_description, container, false);


		mImageView = (ImageView)view.findViewById(R.id.imageView);
		mEditTextTitle = (EditText) view.findViewById(R.id.title);
		mEditTextDescription = (EditText) view.findViewById(R.id.description);


		final File file = new File(getArguments().getString(ARG_IMAGE));

		Picasso.with(getActivity()).load(file).skipMemoryCache().into(mImageView);

		Button button = (Button) view.findViewById(R.id.buttonCreateSight);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Sight sight = new Sight();
				sight.title = mEditTextTitle.getText().toString();
				sight.description = mEditTextDescription.getText().toString();
				sight.region = "";
				sight.lon = 0;
				sight.lat = 0;
				sight.image_uri = file.getAbsolutePath();
				ContentValues values = Contract.Sight.createContentValues(sight);
				getActivity().getContentResolver().insert(Contract.Sight.getCreateSightClientUri(), values);
			}
		});


		return view;
	}

	public static ReleaseDescriptionFragment createInstance(String file) {
		ReleaseDescriptionFragment fragment = new ReleaseDescriptionFragment();

		Bundle arguments = new Bundle();
		arguments.putString(ARG_IMAGE, file);
		fragment.setArguments(arguments);

		return fragment;
	}
}
