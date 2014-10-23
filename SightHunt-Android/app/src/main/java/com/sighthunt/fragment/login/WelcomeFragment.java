package com.sighthunt.fragment.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.sighthunt.R;
import com.sighthunt.activity.LoginActivity;
import com.sighthunt.view.ViewPagerIndicator;
import com.thehayro.view.InfinitePagerAdapter;
import com.thehayro.view.InfiniteViewPager;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeFragment extends Fragment {


	ViewPager mViewPager;
	ViewPagerIndicator mViewPagerIndicator;
	AdapterViewFlipper mAdapterViewFlipper;
	private int mFocusedPage = 0;

	private PagerAdapter mAdapter;

	private List<String> READ_PERMISSIONS = Arrays.asList(
			"email",
			"user_friends");

	public static WelcomeFragment createInstance() {
		WelcomeFragment fragment = new WelcomeFragment();
		return fragment;
	}

	private UiLifecycleHelper uiHelper;
	LoginActivity mLoginActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLoginActivity = (LoginActivity) getActivity();

		uiHelper = new UiLifecycleHelper(getActivity(), new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {

				Log.i("onSessionStateChanged", "UiLifecycleHelper");
				onSessionStateChanged(session, state, exception);
			}
		});
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_welcome, container, false);
		// Connect with facebook button
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		authButton.setReadPermissions(READ_PERMISSIONS);

		// Email sign up button
		Button signUpButton = (Button) view.findViewById(R.id.signUpButton);
		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoginActivity.showSignUp();
			}
		});

		// Login button
		TextView loginTextView = (TextView) view.findViewById(R.id.loginText);
		loginTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoginActivity.showLogin();
			}
		});

		mAdapterViewFlipper = (AdapterViewFlipper)view.findViewById(R.id.adapterViewFlipper);

		BaseAdapter adapter = new BaseAdapter() {

			@Override
			public int getCount() {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return null;
			}
		}
		mAdapterViewFlipper.setAdapter(adapter);

		mViewPager = (InfiniteViewPager) view.findViewById(R.id.viewPager);
		mViewPagerIndicator = (ViewPagerIndicator) view.findViewById(R.id.viewPagerIndicator);
		mViewPagerIndicator.setViewPager(mViewPager);
		mAdapter = new IntroPagerAdapter(0);
		mViewPager.setAdapter(mAdapter);
		Timer timer = new Timer();
		timer.schedule(new UpdateTimeTask(), 1000, 2000);

		return view;
	}

	Session mSession;

	private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			if (mSession == null || isSessionChanged(session)) {
				mSession = session;
				Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						mLoginActivity.login(user.getId(), "");
					}
				}).executeAsync();
			}
		} else if (state.isClosed()) {
		}
	}

	private boolean isSessionChanged(Session session) {

		// Check if session state changed
		if (mSession.getState() != session.getState())
			return true;

		// Check if accessToken changed
		if (mSession.getAccessToken() != null) {
			if (!mSession.getAccessToken().equals(session.getAccessToken()))
				return true;
		} else if (session.getAccessToken() != null) {
			return true;
		}

		// Nothing changed
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();

		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed())) {
			onSessionStateChanged(session, session.getState(), null);
		}
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private class IntroPagerAdapter extends InfinitePagerAdapter<Integer> {

		public IntroPagerAdapter(final Integer initValue) {
			super(initValue);
		}

		private int[] titles = new int[]{
				R.string.intro_your_city_title,
				R.string.intro_travel_title,
				R.string.intro_myth_title,
				R.string.intro_compete_title};

		private int[] bodies = new int[]{
				R.string.intro_your_city_body,
				R.string.intro_travel_body,
				R.string.intro_myth_body,
				R.string.intro_compete_body};

		@Override
		public Integer getNextIndicator() {
			return getCurrentIndicator() + 1;
		}

		@Override
		public Integer getPreviousIndicator() {
			return getCurrentIndicator() - 1;
		}

		@Override
		public String getStringRepresentation(final Integer currentIndicator) {
			return String.valueOf(currentIndicator);
		}

		@Override
		public Integer convertToIndicator(final String representation) {
			return Integer.valueOf(representation);
		}

		@Override
		public ViewGroup instantiateItem(Integer indicator) {

			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup view = (ViewGroup) inflater.inflate(R.layout.intro_layout, null);
			TextView titleView = (TextView) view.findViewById(R.id.title);
			TextView bodyView = (TextView) view.findViewById(R.id.body);
			int index = ((indicator % 4) + 4) % 4;
			titleView.setText(titles[index]);
			bodyView.setText(bodies[index]);
			return view;
		}
	}


	class UpdateTimeTask extends TimerTask {
		public void run() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mViewPager.setCurrentItem(mFocusedPage++ % 4, true);
			}
		});
		}
	}


}
