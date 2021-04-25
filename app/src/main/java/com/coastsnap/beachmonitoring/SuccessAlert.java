package com.coastsnap.beachmonitoring;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.Icon;
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
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }


    @Override
    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        return false;
    }
}
