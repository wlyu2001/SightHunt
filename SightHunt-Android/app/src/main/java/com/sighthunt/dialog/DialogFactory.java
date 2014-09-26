package com.sighthunt.dialog;

import android.app.Dialog;
import android.content.Context;

public class DialogFactory {

	public static Dialog getConnectionFailedDialog(Context context) {
		Dialog connectionFailedDialog = new Dialog(context);
		return connectionFailedDialog;
	}
}
