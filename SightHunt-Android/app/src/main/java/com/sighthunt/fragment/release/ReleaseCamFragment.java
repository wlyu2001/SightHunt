package com.sighthunt.fragment.release;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sighthunt.R;
import com.sighthunt.view.CapturePreview;

import java.io.File;

public class ReleaseCamFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_release_cam, container, false);

		File folder = new File(Environment.getExternalStorageDirectory() + "/sighthunt/image");
		if (!folder.exists()) {
			folder.mkdir();
		}
		String image = "image.png";
		String thumb = "thumb.png";

		final File imageFile = new File(folder, image);
		final File thumbFile = new File(folder, thumb);
		Button button = (Button) view.findViewById(R.id.button_take);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CapturePreview.takeAPicture(imageFile, thumbFile, new CapturePreview.CapturePreviewObserver() {
					@Override
					public void pictureCaptured(boolean success) {
						if (success) {
							Fragment fragment = ReleaseDescriptionFragment.createInstance(imageFile.getPath(), thumbFile.getPath());
							getFragmentManager().beginTransaction().replace(R.id.container, fragment, null).addToBackStack(null).commit();
						}
					}
				});
			}
		});


		return view;
	}
}
