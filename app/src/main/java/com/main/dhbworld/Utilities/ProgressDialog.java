package com.main.dhbworld.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.main.dhbworld.R;

/**
 * Original ProgressDialog is depricated. This is a replacement
 */
public class ProgressDialog {

    MaterialAlertDialogBuilder builder;
    AlertDialog alertDialog;
    Context context;

    public ProgressDialog(Context context) {
        this.context = context;
        builder = new MaterialAlertDialogBuilder(context)
                .setView(R.layout.dialog_progress);
    }

    public ProgressDialog setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public ProgressDialog setTitle(int resId) {
        builder.setTitle(resId);
        return this;
    }

    public ProgressDialog setMessage(String message) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        TextView textView = view.findViewById(R.id.progress_message);
        textView.setText(message);
        builder.setView(view);
        return this;
    }

    public ProgressDialog setMessage(int resId) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        TextView textView = view.findViewById(R.id.progress_message);
        textView.setText(context.getResources().getString(resId));
        builder.setView(view);
        return this;
    }

    public ProgressDialog setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    public ProgressDialog show() {
        alertDialog = builder.show();
        return this;
    }

    public void dismiss() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
