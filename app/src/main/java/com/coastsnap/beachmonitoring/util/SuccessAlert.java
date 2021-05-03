package com.coastsnap.beachmonitoring.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class SuccessAlert implements OnKeyListener {

    private final Context mContext;

    public SuccessAlert(final Context context){
        this.mContext = context;
    }

    public void successDialog(String title, String message, int iconId){
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setIcon(iconId)
                .setNeutralButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.cancel())
                .create()
                .show();
    }


    @Override
    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        return false;
    }
}
