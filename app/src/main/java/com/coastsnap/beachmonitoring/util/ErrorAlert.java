package com.coastsnap.beachmonitoring.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class ErrorAlert implements OnKeyListener {

    private final Context mContext;

    public ErrorAlert(final Context context){
        this.mContext = context;
    }

    public void showErrorDialog(String title, String message){
        AlertDialog errorDialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNeutralButton("Close", (dialogInterface, i) -> {
                    if (!title.equalsIgnoreCase("About") && !title.equalsIgnoreCase("Directory Error") && !title.equalsIgnoreCase("View")) {
                        ((Activity) mContext).finish();
                    }
                }).create();
        errorDialog.setOnKeyListener(this);
        errorDialog.show();
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            //disable the back button
        }
        return true;
    }
}
