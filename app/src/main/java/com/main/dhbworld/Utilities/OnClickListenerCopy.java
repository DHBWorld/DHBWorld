package com.main.dhbworld.Utilities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

public class OnClickListenerCopy implements View.OnClickListener {
    Context context;
    String textToCopy;
    View snackbarView;

    public OnClickListenerCopy(Context context, String textToCopy, View snackbarView) {
        this.context = context;
        this.textToCopy = textToCopy;
        this.snackbarView = snackbarView;

    }


    @Override
    public void onClick(View view) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", textToCopy);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(snackbarView, context.getResources().getString(R.string.copied), BaseTransientBottomBar.LENGTH_SHORT).show();
    }

}
