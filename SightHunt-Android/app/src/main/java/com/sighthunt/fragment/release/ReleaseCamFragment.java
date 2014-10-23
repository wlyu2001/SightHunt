package com.sighthunt.fragment.release;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.activity.LocationAwareActivity;
import com.sighthunt.data.Contract;
import com.sighthunt.fragment.LocationAwareFragment;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.view.CapturePreview;

public class ReleaseCamFragment extends LocationAwareFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_release_cam, container, false);

		Button button = (Button) view.findViewById(R.id.button_take);
		final CapturePreview capturePreview = (CapturePreview) view.findViewById(R.id.capturePreview);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				capturePreview.takeAPicture(ImageFiles.NEW_IMAGE, ImageFiles.NEW_IMAGE_THUMB, new CapturePreview.CapturePreviewObserver() {
					@Override
					public void pictureCaptured(boolean success) {
						if (success) {
							Location location = getCurrentLocation();
							if (location == null) {
								Toast.makeText(getActivity(), getString(R.string.location_unavailable), Toast.LENGTH_LONG).show();
								return;
							}
							String region = getRegion();
							if (region == null) {
								Toast.makeText(getActivity(), getString(R.string.release_in_unknown_region), Toast.LENGTH_LONG).show();
								return;
							}
							Fragment fragment = ReleaseDescriptionFragment.createInstance(region,
									(float) location.getLongitude(),
									(float) location.getLatitude());
							getFragmentManager().beginTransaction().replace(R.id.container, fragment, null).addToBackStack(null).commit();
						}
					}
				});
			}
		});

		return view;
	}

	@Override
	public void onRegionUpdated(String region, boolean changed) {

	}
}
