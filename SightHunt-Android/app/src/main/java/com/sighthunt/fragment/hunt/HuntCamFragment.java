package com.sighthunt.fragment.hunt;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sighthunt.R;
import com.sighthunt.data.model.Sight;
import com.sighthunt.fragment.LocationAwareFragment;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.view.CapturePreview;
import com.squareup.picasso.Picasso;

public class HuntCamFragment extends LocationAwareFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_hunt_cam, container, false);

		Bundle args = getArguments();
		final Sight sight = args.getParcelable(Sight.ARG);

		ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
		final CapturePreview capturePreview = (CapturePreview) view.findViewById(R.id.capturePreview);
		imageView.setAlpha(0.5f);
		Picasso.with(getActivity()).load(ImageFiles.ORIGINAL_IMAGE).skipMemoryCache().into(imageView);
		Button button = (Button) view.findViewById(R.id.button_take);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				capturePreview.takeAPicture(ImageFiles.MATCH_IMAGE, ImageFiles.MATCH_IMAGE_THUMB, new CapturePreview.CapturePreviewObserver() {
					@Override
					public void pictureCaptured(boolean success) {
						if (success) {

							// disable hunting if location is not ready...
							Location location = getCurrentLocation();
							float lat = 0;
							float lon = 0;
							if (location != null) {
								lat = (float) location.getLatitude();
								lon = (float) location.getLongitude();
							}
							Fragment fragment = HuntResultFragment.createInstance(sight, lon, lat);
							getFragmentManager().beginTransaction().replace(R.id.container, fragment, null).addToBackStack("HuntCamFragment").commit();
						}
					}
				});
			}
		});


		return view;
	}

	public static HuntCamFragment createInstance(Sight sight) {
		HuntCamFragment fragment = new HuntCamFragment();
		Bundle args = new Bundle();
		args.putParcelable(Sight.ARG, sight);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onRegionUpdated(String region, boolean changed) {

	}
}
