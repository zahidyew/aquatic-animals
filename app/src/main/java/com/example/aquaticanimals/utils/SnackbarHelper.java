package com.example.aquaticanimals.utils;

import android.app.Activity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import android.view.View;


// Helper to manage the sample snackbar. Hides the Android boilerplate code, and exposes simpler methods.
public final class SnackbarHelper {
    private static final int BACKGROUND_COLOR = 0xbf323232;
    private static final SnackbarHelper THE_INSTANCE = new SnackbarHelper();
    private Snackbar messageSnackbar;

    private enum DismissBehavior {
        HIDE, SHOW, FINISH
    }

    public static SnackbarHelper getInstance() {
        return THE_INSTANCE;
    }

    public boolean isShowing() {
        return messageSnackbar != null;
    }

    public void showMessage(Activity activity, String message) {
        show(activity, message, DismissBehavior.HIDE);
    }

    public void showMessageWithDismiss(Activity activity, String message) {
        show(activity, message, DismissBehavior.SHOW);
    }

    // Shows a snackbar with a given error message. When dismissed, will finish the activity. Useful
    // for notifying errors, where no further interaction with the activity is possible.
    public void showError(Activity activity, String errorMessage) {
        show(activity, errorMessage, DismissBehavior.FINISH);
    }

    // Hides the currently showing snackbar, if there is one. Safe to call from any thread. Safe to
    // call even if snackbar is not shown.
    public void hide(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (messageSnackbar != null) {
                    messageSnackbar.dismiss();
                }
                messageSnackbar = null;
            }
        });
    }

    private void show(final Activity activity, final String message, final DismissBehavior dismissBehavior) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageSnackbar = Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_INDEFINITE);

                messageSnackbar.getView().setBackgroundColor(BACKGROUND_COLOR);

                if (dismissBehavior != DismissBehavior.HIDE) {
                    messageSnackbar.setAction("Dismiss",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    messageSnackbar.dismiss();
                                }
                            });
                    if (dismissBehavior == DismissBehavior.FINISH) {
                        messageSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                activity.finish();
                            }
                        });
                    }
                }
                messageSnackbar.show();
            }
        });
    }
}

