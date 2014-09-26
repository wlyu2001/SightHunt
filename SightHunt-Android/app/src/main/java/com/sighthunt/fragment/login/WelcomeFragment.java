package com.sighthunt.fragment.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Arrays;
import java.util.List;

public class WelcomeFragment extends Fragment {

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
						mLoginActivity.loginWithFacebook(user.getId());
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
}
