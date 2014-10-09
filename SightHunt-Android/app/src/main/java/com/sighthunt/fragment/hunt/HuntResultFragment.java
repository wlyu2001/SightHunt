package com.sighthunt.fragment.hunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.algorithm.ImageMatcher;
import com.sighthunt.data.Contract;
import com.sighthunt.inject.Injector;
import com.sighthunt.network.ApiManager;
import com.sighthunt.util.AccountUtils;
import com.sighthunt.util.ImageFiles;
import com.sighthunt.util.PreferenceUtil;
import com.sighthunt.view.MatchOverlayView;
import com.squareup.picasso.Picasso;

public class HuntResultFragment extends Fragment {

	AccountUtils mAccountUtils = Injector.get(AccountUtils.class);
	ApiManager mApiManager = Injector.get(ApiManager.class);

	ImageMatcher mImageMatcher;
	ImageView mImageView1;
	ImageView mImageView2;
	MatchOverlayView mMatchOverlayView;
	EditText mThreshold;

	SharedPreferences mPrefs;

	private static final String PREF_SELECTED_DETECTOR = "pref_selected_detector";
	private static final String PREF_SELECTED_DESCRIPTOR = "pref_selected_descriptor";
	private static final String PREF_SELECTED_MATCHER = "pref_selected_matcher";
	private static final String PREF_MATCH_THRESHOLD = "pref_match_threshold";


	private static final String[] MATCHERS = new String[]{"FLANNBASED", "BRUTEFORCE", "BRUTEFORCE_L1", "BRUTEFORCE_HAMMING", "BRUTEFORCE_HAMMINGLUT", "BRUTEFORCE_SL2"};
	private static final String[] DETECTOR = new String[]{"FAST", "START", "SIFT", "SURF", "ORB", "MSER", "GFTT", "HARRIS", "SIMPLEBLOB", "DENSE", "BRISK"};
	private static final String[] DESCRIPTOR = new String[]{"SIFT", "SURF", "ORB", "BRIEF", "BRISK", "FREAK"};
	private static final int[] MATCHERS_MAP = new int[]{1, 2, 3, 4, 5, 6};
	private static final int[] DETECTOR_MAP = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
	private static final int[] DESCRIPTOR_MAP = new int[]{1, 2, 3, 4, 5, 6};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mImageMatcher = new ImageMatcher();
		mPrefs = PreferenceUtil.getSettingSharedPreferences(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_hunt_result, container, false);

		final View failedLayout = view.findViewById(R.id.layoutFailed);
		final View successLayout = view.findViewById(R.id.layoutSuccess);

		final Button buttonUpVote = (Button) view.findViewById(R.id.buttonUpVote);
		final Button buttonDownVote = (Button) view.findViewById(R.id.buttonDownVote);
		final Button buttonTryAgain = (Button) view.findViewById(R.id.buttonTryAgain);

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


		final Button buttonDescriptor = (Button) view.findViewById(R.id.button_descriptor);
		buttonDescriptor.setText(DESCRIPTOR[mPrefs.getInt(PREF_SELECTED_DESCRIPTOR, 0)]);
		buttonDescriptor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = mPrefs.getInt(PREF_SELECTED_DESCRIPTOR, 0);
				showChoserDialog("Descriptor", DESCRIPTOR, selected, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPrefs.edit().putInt(PREF_SELECTED_DESCRIPTOR, which).commit();
						buttonDescriptor.setText(DESCRIPTOR[which]);
					}
				});
			}
		});

		final Button buttonDetector = (Button) view.findViewById(R.id.button_detector);
		buttonDetector.setText(DETECTOR[mPrefs.getInt(PREF_SELECTED_DETECTOR, 0)]);
		buttonDetector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = mPrefs.getInt(PREF_SELECTED_DETECTOR, 0);
				showChoserDialog("Detector", DETECTOR, selected, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPrefs.edit().putInt(PREF_SELECTED_DETECTOR, which).commit();
						buttonDetector.setText(DETECTOR[which]);
					}
				});
			}
		});

		final Button buttonMatcher = (Button) view.findViewById(R.id.button_matcher);
		buttonMatcher.setText(MATCHERS[mPrefs.getInt(PREF_SELECTED_MATCHER, 0)]);
		buttonMatcher.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int selected = mPrefs.getInt(PREF_SELECTED_MATCHER, 0);
				showChoserDialog("Matcher", MATCHERS, selected, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPrefs.edit().putInt(PREF_SELECTED_MATCHER, which).commit();
						buttonMatcher.setText(MATCHERS[which]);
					}
				});
			}
		});

		mThreshold = (EditText) view.findViewById(R.id.text_threshold);
		mThreshold.setText(mPrefs.getFloat(PREF_MATCH_THRESHOLD, 0) + "");

		Button buttonCompute = (Button) view.findViewById(R.id.button_compute);
		buttonCompute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (compute()) {
					successLayout.setVisibility(View.VISIBLE);
					failedLayout.setVisibility(View.GONE);
				} else {
					successLayout.setVisibility(View.GONE);
					failedLayout.setVisibility(View.VISIBLE);
				}
			}
		});

		mImageView1 = (ImageView) view.findViewById(R.id.imageView1);
		mImageView2 = (ImageView) view.findViewById(R.id.imageView2);

		Picasso.with(getActivity()).load(ImageFiles.ORIGINAL_IMAGE).skipMemoryCache().into(mImageView1);
		Picasso.with(getActivity()).load(ImageFiles.MATCH_IMAGE).skipMemoryCache().into(mImageView2);

		mMatchOverlayView = (MatchOverlayView) view.findViewById(R.id.matchOverlayView);

		return view;
	}

	private void voteAndSendToServer(final int vote) {
		final long key = getArguments().getLong(Contract.Sight.KEY);
		final String username = mAccountUtils.getUsername();

		ContentValues values = new ContentValues();
		values.put(Contract.Hunt.USER, username);
		values.put(Contract.Hunt.SIGHT, key);
		values.put(Contract.Hunt.VOTE, vote);
		getActivity().getContentResolver().insert(Contract.Hunt.getInsertHuntRemoteUri(), values);

		getActivity().getContentResolver().registerContentObserver(Contract.Hunt.getInsertHuntRemoteUri(), false, new ContentObserver(new Handler(Looper.getMainLooper())) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				Activity activity = getActivity();
				if (activity != null) {
					Toast.makeText(activity, "Successfully hunted", Toast.LENGTH_LONG).show();
					activity.finish();
				}
			}
		});
	}

	private void showChoserDialog(String title, String[] items, int selected, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setItems(items, listener);
		AlertDialog alertDialog = builder.create();
		alertDialog.getListView().setSelection(selected);
		alertDialog.show();
	}


	private boolean compute() {
		mPrefs.edit().putFloat(PREF_MATCH_THRESHOLD, Float.parseFloat(mThreshold.getText().toString())).commit();


		int matcher = MATCHERS_MAP[mPrefs.getInt(PREF_SELECTED_MATCHER, 0)];
		int detector = DETECTOR_MAP[mPrefs.getInt(PREF_SELECTED_DETECTOR, 0)];
		int descriptor = DESCRIPTOR_MAP[mPrefs.getInt(PREF_SELECTED_DESCRIPTOR, 0)];

		mImageMatcher = new ImageMatcher(detector, descriptor, matcher, Float.parseFloat(mThreshold.getText().toString()));
		//mImageMatcher = new ImageMatcher(FeatureDetector.ORB, DescriptorExtractor.ORB, DescriptorMatcher.BRUTEFORCE_HAMMING, 40);

		mImageMatcher.getImageMatchingScore(ImageFiles.ORIGINAL_IMAGE, ImageFiles.MATCH_IMAGE);

		Log.i("lingyu matches", mImageMatcher.getMatches().size() + "");

		mMatchOverlayView.setMatches(mImageMatcher.getKeyPoints1(), mImageMatcher.getKeyPoints2(), mImageMatcher.getMatches(), mImageMatcher.getWidth(), mImageMatcher.getHeight());
		mMatchOverlayView.invalidate();


		// if match succeeds

		return true;


	}

	public static HuntResultFragment createInstance(long key, float lon, float lat) {
		HuntResultFragment fragment = new HuntResultFragment();

		Bundle arguments = new Bundle();
		arguments.putLong(Contract.Sight.KEY, key);
		arguments.putFloat(Contract.Sight.LON, lon);
		arguments.putFloat(Contract.Sight.LAT, lat);
		fragment.setArguments(arguments);

		return fragment;
	}
}
