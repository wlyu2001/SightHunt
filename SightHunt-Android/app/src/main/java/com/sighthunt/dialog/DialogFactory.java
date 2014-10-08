package com.sighthunt.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.sighthunt.R;

public class DialogFactory {

	public static Dialog getConnectionFailedDialog(Context context) {
		Dialog dialog = new Dialog(context);
		return dialog;
	}

	public static Dialog getLocationDisabledDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
		.setTitle(R.string.dialog_location_title)
		.setMessage(R.string.dialog_location_message)
		.setNegativeButton(R.string.dialog_ignore, null)
		.setPositiveButton(R.string.dialog_settings, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});
		return builder.create();
	}
}
