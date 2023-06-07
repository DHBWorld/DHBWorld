package com.main.dhbworld.Dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.DashboardActivity;

public class DashboardCardInfo extends DashboardCard {
    public DashboardCardInfo(CardType cardType, LinearLayout outerLayout, MaterialCardView materialCard, LinearLayout coloredButtonBox, LinearLayout innerlayout) {
        super(cardType, outerLayout, materialCard, coloredButtonBox, innerlayout);
    }

    @Override
    public void moveToAnotherActivity(DashboardActivity currentActivity, Class<Activity> nextActivity) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/inFumumVerti/DHBWorld/releases/latest"));
        currentActivity.startActivity(browserIntent);
    }


}
