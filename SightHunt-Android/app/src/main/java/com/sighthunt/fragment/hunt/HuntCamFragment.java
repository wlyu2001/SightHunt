package com.sighthunt.fragment.hunt;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sighthunt.R;
import com.sighthunt.view.CapturePreview;
import com.squareup.picasso.Picasso;

import java.io.File;

public class HuntCamFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_hunt_cam, container, false);

		File folder = new File(Environment.getExternalStorageDirectory() + "/sighthunt/image");
		if (!folder.exists()) {
			folder.mkdir();
		}
		String s = "tmp.png";
		String s1 = "tmp1.png";

		final File file = new File(folder, s);
		final File file1 = new File(folder, s1);
		ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
		imageView.setAlpha(0.5f);
		// skip cache for testing.. of course we want to cache images
		Picasso.with(getActivity()).load(file).skipMemoryCache().into(imageView);
		Button button = (Button) view.findViewById(R.id.button_take);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CapturePreview.takeAPicture(file1, new CapturePreview.CapturePreviewObserver() {
					@Override
					public void pictureCaptured(boolean success) {
						if (success) {
							Fragment fragment = HuntResultFragment.createInstance(file.getPath(), file1.getPath());
							getFragmentManager().beginTransaction().replace(R.id.container, fragment, null).addToBackStack(null).commit();
						}
					}
				});
			}
		});


		return view;
	}
}
