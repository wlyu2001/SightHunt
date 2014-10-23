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
import com.sighthunt.util.EmailValidator;
import com.sighthunt.util.PasswordHash;

public class SignUpFragment extends Fragment {


	EditText mEditTextUserName;
	EditText mEditTextPassword;
	EditText mEditTextEmail;
	EditText mEditTextNick;
	Button mSignUpButton;
	LoginActivity mLoginActivity;

	public static SignUpFragment createInstance() {
		SignUpFragment fragment = new SignUpFragment();
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLoginActivity = (LoginActivity) getActivity();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_signup, container, false);
		mEditTextUserName = (EditText) view.findViewById(R.id.username);
		mEditTextPassword = (EditText) view.findViewById(R.id.password);
		mEditTextEmail = (EditText) view.findViewById(R.id.email);
		mEditTextNick = (EditText) view.findViewById(R.id.nick);
		mSignUpButton = (Button) view.findViewById(R.id.signUpButton);

		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mEditTextUserName.getText().toString();
				String password = mEditTextPassword.getText().toString();
				String email = mEditTextEmail.getText().toString();
				String nick = mEditTextNick.getText().toString();

				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
					Toast.makeText(getActivity(), getString(R.string.empty_input), Toast.LENGTH_LONG).show();
				} else {
					if (EmailValidator.isEmailValid(email)) {
						mLoginActivity.signup(username, PasswordHash.hash(password), email, nick);
					} else {
						Toast.makeText(getActivity(), getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		return view;
	}

}
