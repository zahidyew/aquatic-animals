package com.example.aquaticanimals.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogHelper {

    // Msg pop-up and exit/finish the Activity
    public void buildMsgAndFinish(final String MESSAGE, Context context, Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(MESSAGE);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                activity.finish();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
