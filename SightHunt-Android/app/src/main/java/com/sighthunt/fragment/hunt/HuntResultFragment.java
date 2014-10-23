package com.sighthunt.fragment.hunt;

import android.app.Activity;
import android.database.ContentObserver;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.algorithm.ImageMatcher;
import com.sighthunt.data.Contract;
import com.sighthunt.data.model.Sight;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.SightHuntService;
import com.sighthunt.util.AccountUtils;

import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

public class HuntResultFragment extends Fragment {

	private AccountUtils mAccountUtils = Injector.get(AccountUtils.class);

	private ImageMatcher mImageMatcher;
	private View mFailedLayout;
	private View mSuccessLayout;

	private static final float DISTANCE_THRESHOLD = 100;
	private static final int IMAGE_MATCH_THRESHOLD = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_hunt_result, container, false);

		float lon = getArguments().getFloat(Contract.Sight.LON);
		float lat = getArguments().getFloat(Contract.Sight.LAT);
		Sight sight = getArguments().getParcelable(Sight.ARG);

		mFailedLayout = view.findViewById(R.id.layoutFailed);
		mSuccessLayout = view.findViewById(R.id.layoutSuccess);

		final Button buttonUpVote = (Button) view.findViewById(R.id.buttonUpVote);
		final Button buttonDownVote = (Button) view.findViewById(R.id.buttonDownVote);
		final Button buttonTryAgain = (Button) view.findViewById(R.id.buttonTryAgain);

		final TextView textViewFailed = (TextView) view.findViewById(R.id.textViewFailed);
		final TextView textViewSuccess = (TextView) view.findViewById(R.id.textViewSuccess);
		final TextView textViewVote = (TextView) view.findViewById(R.id.textViewVote);
		final TextView textViewTryAgain = (TextView) view.findViewById(R.id.textViewTryAgain);

		buttonTryAgain.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				fm.popBackStack("HuntCamFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		});

		buttonUpVote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				voteAndSendToServer(1);
			}
		});

		buttonDownVote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				voteAndSendToServer(0);
			}
		});

		float distance = matchLocation(lon, lat, sight.lon, sight.lat);
		int matches = matchImage();
		if (distance > DISTANCE_THRESHOLD) {
			mSuccessLayout.setVisibility(View.GONE);
			mFailedLayout.setVisibility(View.VISIBLE);
			textViewFailed.setText(R.string.text_fail_wrong_place);
			textViewTryAgain.setText(R.string.text_try_again_wrong_place);
		} else {
			if (matches < IMAGE_MATCH_THRESHOLD) {

				mSuccessLayout.setVisibility(View.GONE);
				mFailedLayout.setVisibility(View.VISIBLE);
				textViewFailed.setText(R.string.text_fail_right_place);
				textViewTryAgain.setText(R.string.text_try_again_right_place);
			} else {

				mSuccessLayout.setVisibility(View.VISIBLE);
				mFailedLayout.setVisibility(View.GONE);

			}
		}
		return view;
	}

	private void voteAndSendToServer(final int vote) {
		final Sight sight = getArguments().getParcelable(Sight.ARG);
		final String username = mAccountUtils.getUsername();

		getActivity().startService(SightHuntService.getInsertHuntIntent(getActivity(), username, sight.uuid, sight.key, vote));

		getActivity().getContentResolver().registerContentObserver(Contract.Hunt.getInsertHuntLocalUri(), false, new ContentObserver(new Handler(Looper.getMainLooper())) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				super.onChange(selfChange);
				Activity activity = getActivity();
				if (activity != null) {
					Toast.makeText(activity, "Successfully hunted!", Toast.LENGTH_LONG).show();
					activity.finish();
				}
			}
		});
	}

	private float matchLocation(float lon1, float lat1, float lon2, float lat2) {
		Location locationA = new Location("A");
		locationA.setLatitude(lat1);
		locationA.setLongitude(lon1);
		Location locationB = new Location("B");
		locationB.setLatitude(lat2);
		locationB.setLongitude(lon2);
		float distance = locationA.distanceTo(locationB);

		return distance;
	}

	private int matchImage() {
		mImageMatcher = new ImageMatcher(FeatureDetector.ORB, DescriptorExtractor.ORB, DescriptorMatcher.BRUTEFORCE_HAMMING, 40, 0);
		return mImageMatcher.getImageMatchingScore();
	}

	public static HuntResultFragment createInstance(Sight sight, float lon, float lat) {
		HuntResultFragment fragment = new HuntResultFragment();

		Bundle arguments = new Bundle();

		arguments.putParcelable(Sight.ARG, sight);
		arguments.putFloat(Contract.Sight.LON, lon);
		arguments.putFloat(Contract.Sight.LAT, lat);
		fragment.setArguments(arguments);

		return fragment;
	}
}
