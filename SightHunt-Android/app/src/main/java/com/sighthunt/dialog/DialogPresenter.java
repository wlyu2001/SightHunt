package com.sighthunt.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class DialogPresenter extends DialogFragment {

	private Dialog mDialog;

	public DialogPresenter() {
		super();
		mDialog = null;
	}

	public void setDialog(Dialog dialog) {
		mDialog = dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return mDialog;
	}

	public static void showDialog(FragmentManager fragmentManager, Dialog dialog, String tag) {
		if (dialog != null) {
			DialogPresenter errorFragment = new DialogPresenter();
			errorFragment.setDialog(dialog);
			errorFragment.show(fragmentManager, tag);
		}
	}
}