package com.main.dhbworld.Dashboard;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

public class DashboardRefresh {
    public static boolean statusCheck(boolean configurationModus, boolean refreshIsEnable, View view, Context context){
        if (configurationModus) {
            Snackbar.make(view.findViewById(android.R.id.content), context.getResources().getString(R.string.youAreInConfigModus), BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }
        if (!refreshIsEnable) {
            Snackbar.make(view.findViewById(android.R.id.content), context.getResources().getString(R.string.refreshIsOnlyIn10Min), BaseTransientBottomBar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }




}
