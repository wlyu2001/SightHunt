package com.sighthunt.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sighthunt.R;
import com.sighthunt.fragment.hunt.HuntCamFragment;

public class SightFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_sight, container, false);

		Button button = (Button)view.findViewById(R.id.go_to_cam_button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			getFragmentManager().beginTransaction().replace(R.id.container, new HuntCamFragment(), null).addToBackStack(null).commit();

			}
		});

		return view;
	}
}
