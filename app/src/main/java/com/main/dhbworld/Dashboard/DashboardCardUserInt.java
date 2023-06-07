package com.main.dhbworld.Dashboard;

import android.app.Activity;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.DashboardActivity;

public class DashboardCardUserInt extends DashboardCard {
    public DashboardCardUserInt(CardType cardType, LinearLayout outerLayout, MaterialCardView materialCard, LinearLayout coloredButtonBox, LinearLayout innerlayout) {
        super(cardType, outerLayout, materialCard, coloredButtonBox, innerlayout);
    }


    @Override
    public void makeCardVisible() {


    }

    @Override
    public boolean cardIsVisible() {
        return card_isVisible;
    }

    @Override
    public void setCardVisibility(boolean cardVisibility) {

    }


    @Override
    public void syncVisibility() {

    }

    @Override
    public void configurateClickers(DashboardActivity currentActivity, Class<Activity> nextActivity) {
        materialCard.setOnClickListener(v -> {
            if (!configurationModus) {
                moveToAnotherActivity(currentActivity, nextActivity);

            }
        });

    }

    @Override
    public void coloreVisibleCard(int intensity) {

    }

    @Override
    public void setColor(int setAlphaComponent) {

    }

    @Override
    public void setConfigurationModus(Boolean configurationModus) {
        this.configurationModus = configurationModus;
        materialCard.setClickable(!this.configurationModus);


    }

}
