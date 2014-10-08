package com.sighthunt.fragment.hunt;

import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sighthunt.R;
import com.sighthunt.data.Contract;
import com.sighthunt.fragment.LocationAwareFragment;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.view.CapturePreview;
import com.squareup.picasso.Picasso;

import java.io.File;

public class HuntCamFragment extends LocationAwareFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_hunt_cam, container, false);

		Bundle args = getArguments();
		final String key = args.getString(Contract.Sight.KEY);

		ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
		imageView.setAlpha(0.5f);
		Picasso.with(getActivity()).load(ImageFiles.ORIGINAL_IMAGE).skipMemoryCache().into(imageView);
		Button button = (Button) view.findViewById(R.id.button_take);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CapturePreview.takeAPicture(ImageFiles.MATCH_IMAGE, ImageFiles.MATCH_IMAGE_THUMB, new CapturePreview.CapturePreviewObserver() {
					@Override
					public void pictureCaptured(boolean success) {
						if (success) {
							Location location = getCurrentLocation();
							Fragment fragment = HuntResultFragment.createInstance(key, (float)location.getLongitude(), (float)location.getLatitude());
							getFragmentManager().beginTransaction().replace(R.id.container, fragment, null).addToBackStack("HuntCamFragment").commit();
						}
					}
				});
			}
		});


		return view;
	}

	public static HuntCamFragment createInstance(String key) {
		HuntCamFragment fragment = new HuntCamFragment();
		Bundle args = new Bundle();
		args.putString(Contract.Sight.KEY, key);
		fragment.setArguments(args);
		return fragment;
	}
}
