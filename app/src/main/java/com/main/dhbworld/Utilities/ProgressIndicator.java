package com.main.dhbworld.Utilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ProgressIndicator  {
    private CircularProgressIndicator indicator;
    private LinearLayout[] competitors;

    public ProgressIndicator(@NonNull Context context, LinearLayout layout, LinearLayout[] competitors) {
        indicator= new CircularProgressIndicator(context);
        indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        indicator.setIndeterminate(true);
        indicator.setPadding(400,0,400,0);
        layout.addView(indicator);
        this.competitors=competitors;
    }
    public ProgressIndicator(@NonNull Context context, LinearLayout layout) {
        indicator= new CircularProgressIndicator(context);
        indicator.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        indicator.setIndeterminate(true);
        indicator.setPadding(400,0,400,0);
        layout.addView(indicator);
        this.competitors=new LinearLayout[0];
    }

    public void hide(){

        for (LinearLayout competitor : competitors) {
            competitor.setVisibility(View.VISIBLE);
        }
        indicator.setVisibility(View.GONE);
    }
    public void show(){
        for (LinearLayout competitor : competitors) {
            competitor.setVisibility(View.GONE);
        }
        indicator.setVisibility(View.VISIBLE);
    }
}
