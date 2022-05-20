package com.main.dhbworld;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ProgressIndicator  {
    CircularProgressIndicator indicator;

    public ProgressIndicator(@NonNull Context context, LinearLayout layout) {
        indicator= new CircularProgressIndicator(context);

        indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        indicator.setIndeterminate(true);
        indicator.setPadding(400,0,400,0);
        layout.addView(indicator);
    }

    public void hide(){
        indicator.setVisibility(View.GONE);
    }
    public void show(){
        indicator.setVisibility(View.VISIBLE);
    }

}
