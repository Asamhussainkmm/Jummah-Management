package com.sltj.jummah;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class MyLoading {
    private AlertDialog alertDialog;
    private boolean isLoading = false;

    public MyLoading(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.loading_view, null); // Replace with your custom loading layout

        builder.setView(dialogView);
        builder.setCancelable(false);

        alertDialog = builder.create();
    }

    public void loadingStart() {
        if (!isLoading && alertDialog != null && !alertDialog.isShowing()) {
            isLoading = true;
            alertDialog.show();
        }
    }

    public void loadingDismiss() {
        if (isLoading && alertDialog != null && alertDialog.isShowing()) {
            isLoading = false;
            alertDialog.dismiss();
        }
    }
}
