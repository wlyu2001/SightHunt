package com.sighthunt.fragment.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sighthunt.R;
import com.sighthunt.activity.HuntActivity;

public class NewSightsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_new_sights, container, false);

		Button button = (Button)view.findViewById(R.id.go_to_hunt_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), HuntActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});

		return view;
	}
}
