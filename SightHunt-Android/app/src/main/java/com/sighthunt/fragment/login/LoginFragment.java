package com.sighthunt.fragment.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sighthunt.R;
import com.sighthunt.activity.LoginActivity;
import com.sighthunt.util.PasswordHash;

public class LoginFragment extends Fragment {

	EditText mEditTextUserName;
	EditText mEditTextPassword;
	Button mLoginButton;
	LoginActivity mLoginActivity;

	public static LoginFragment createInstance() {
		LoginFragment fragment = new LoginFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLoginActivity = (LoginActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_login, container, false);
		mEditTextUserName = (EditText) view.findViewById(R.id.username);
		mEditTextPassword = (EditText) view.findViewById(R.id.password);
		mLoginButton = (Button) view.findViewById(R.id.loginButton);

		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mEditTextUserName.getText().toString();
				String password = mEditTextPassword.getText().toString();
				if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					Toast.makeText(getActivity(), getString(R.string.empty_input), Toast.LENGTH_LONG).show();
				} else {
					mLoginActivity.login(username, PasswordHash.hash(password));
				}
			}
		});
		return view;
	}

}
