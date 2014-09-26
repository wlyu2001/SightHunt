package com.sighthunt.fragment.hunt;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.sighthunt.R;
import com.sighthunt.algorithm.ImageMatcher;
import com.sighthunt.view.MatchOverlayView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class HuntResultFragment extends Fragment {

	ImageMatcher mImageMatcher;
	ImageView mImageView1;
	ImageView mImageView2;
	MatchOverlayView mMatchOverlayView;

	public static final String ARG_IMAGE_1 = "image_1";
	public static final String ARG_IMAGE_2 = "image_2";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageMatcher = new ImageMatcher();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_hunt_result, container, false);


		final File file1 = new File(getArguments().getString(ARG_IMAGE_1));
		final File file2 = new File(getArguments().getString(ARG_IMAGE_2));

		Button upButton = (Button)view.findViewById(R.id.button_up_vote);
		upButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// delete only temp image and

				file1.delete();
				file2.renameTo(file1);
				getActivity().finish();
			}
		});

		mImageView1 = (ImageView)view.findViewById(R.id.imageView1);
		mImageView2 = (ImageView)view.findViewById(R.id.imageView2);

		Picasso.with(getActivity()).load(file1).skipMemoryCache().into(mImageView1);
		Picasso.with(getActivity()).load(file2).skipMemoryCache().into(mImageView2);

		mMatchOverlayView = (MatchOverlayView)view.findViewById(R.id.matchOverlayView);

		mImageMatcher.getImageMatchingScore(file1, file2);

		Log.i("lingyu matches", mImageMatcher.getMatches().size()+"");

		mMatchOverlayView.setMatches(mImageMatcher.getKeyPoints1(), mImageMatcher.getKeyPoints2(), mImageMatcher.getMatches(), mImageMatcher.getWidth(), mImageMatcher.getHeight());

		return view;
	}

	public static HuntResultFragment createInstance(String file1, String file2) {
		HuntResultFragment fragment = new HuntResultFragment();

		Bundle arguments = new Bundle();
		arguments.putString(ARG_IMAGE_1, file1);
		arguments.putString(ARG_IMAGE_2, file2);
		fragment.setArguments(arguments);

		return fragment;
	}
}
