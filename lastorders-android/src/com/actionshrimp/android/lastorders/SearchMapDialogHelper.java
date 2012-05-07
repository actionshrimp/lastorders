package com.actionshrimp.android.lastorders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

public class SearchMapDialogHelper {
	
	private SearchMapActivity activity;
	
	AlertDialog errorDialog;
	ProgressDialog progressDialog;

	public SearchMapDialogHelper(SearchMapActivity activity) {
		this.activity = activity;		
	}
	
	public void displayProgressDialog(String message) {
		dismissAllDialogs();
		
		progressDialog = ProgressDialog.show(activity, "", message, true, true, new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				activity.finish();
			}
		});
		
	}
	
	public void displayErrorDialog(String title, String detail) {
		dismissAllDialogs();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(detail + " Press OK to return to the search page.");
		builder.setTitle(title);
		
		builder.setCancelable(true);
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				activity.finish();
			}
		});
		
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
		});
		
		builder.setIcon(R.drawable.alert_dialog_icon);
		
		errorDialog = builder.show();
	}

	public void displayLocatorDialog(DialogInterface.OnClickListener listener, String source, double accuracy) {
		dismissAllDialogs();
		
		String message = "Current location accurate to within " + String.valueOf(accuracy) + "m (source: " + source + " signal). Looking for a more accurate location..."; 
				
		progressDialog = new ProgressDialog(activity);
		progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Accept accuracy", listener);
		progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
		});
		
		progressDialog.setMessage(message);
		progressDialog.setCancelable(true);
		progressDialog.show();
		
	}
	
	public void dismissAllDialogs() {
		if (errorDialog != null) {
			errorDialog.dismiss();
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
}
