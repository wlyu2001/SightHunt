package com.sighthunt.fragment.release;

import android.content.ContentValues;
import android.os.Bundle;
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
	public static final String ARG_THUMB = "thumb";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_release_description, container, false);


		mImageView = (ImageView)view.findViewById(R.id.imageView);
		mEditTextTitle = (EditText) view.findViewById(R.id.title);
		mEditTextDescription = (EditText) view.findViewById(R.id.description);


		final File imageFile = new File(getArguments().getString(ARG_IMAGE));
		final File thumbFile = new File(getArguments().getString(ARG_THUMB));

		Picasso.with(getActivity()).load(imageFile).skipMemoryCache().into(mImageView);

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
				sight.image_key = imageFile.getAbsolutePath();
				sight.thumb_key = thumbFile.getAbsolutePath();
				ContentValues values = Contract.Sight.createContentValues(sight);
				getActivity().getContentResolver().insert(Contract.Sight.getCreateSightClientUri(), values);
			}
		});


		return view;
	}

	public static ReleaseDescriptionFragment createInstance(String image, String thumb) {
		ReleaseDescriptionFragment fragment = new ReleaseDescriptionFragment();

		Bundle arguments = new Bundle();
		arguments.putString(ARG_IMAGE, image);
		arguments.putString(ARG_THUMB, thumb);
		fragment.setArguments(arguments);

		return fragment;
	}
}
