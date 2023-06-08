package com.main.dhbworld.Dashboard.Cards;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.graphics.ColorUtils;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.DashboardActivity;

public class DashboardCard extends Activity {

    LinearLayout outerLayout;
    MaterialCardView materialCard;
    LinearLayout coloredButtonBox;
    LinearLayout innerlayout;
    Boolean card_isVisible;
    Boolean configurationModus;
    CardType cardType;

    public DashboardCard(CardType cardType, LinearLayout outerLayout, MaterialCardView materialCard, LinearLayout coloredButtonBox, LinearLayout innerlayout) {
        this.cardType = cardType;
        this.outerLayout = outerLayout;
        this.materialCard = materialCard;
        this.coloredButtonBox = coloredButtonBox;
        this.innerlayout = innerlayout;
        card_isVisible = true;
        configurationModus = false;


    }


    public void makeCardVisible() {
        materialCard.setVisibility(View.VISIBLE);
    }

    public boolean cardIsVisible() {
        return card_isVisible;
    }

    public void setCardVisibility(boolean cardVisibility) {
        this.card_isVisible = cardVisibility;
        syncVisibility();
    }

    public void setConfigurationModus(Boolean configurationModus) {
        this.configurationModus = configurationModus;
    }

    public void syncVisibility() {
        if (!this.card_isVisible) {
            materialCard.setVisibility(View.GONE);
        }
    }


    //TODO integrate onClickListeners more easily
    public void configurateClickers(DashboardActivity currentActivity, Class<Activity> nextActivity) {
        materialCard.setOnClickListener(v -> {
            if (configurationModus) {
                if (card_isVisible) {
                    card_isVisible = false; //True = Card is visible
                    coloreCard(50);
                } else {
                    card_isVisible = true;//True = Card is visible
                    coloreCard(255);
                }

            } else {
                moveToAnotherActivity(currentActivity, nextActivity);

            }
        });

    }

    public void moveToAnotherActivity(DashboardActivity currentActivity, Class<Activity> nextActivity) {
        Intent intent = new Intent(currentActivity, nextActivity);
        currentActivity.startActivity(intent);
    }

    public void coloreVisibleCard(int intensity) {
        if (!card_isVisible) {
            coloreCard(intensity);
        }
    }

    public void coloreCard(int intensity) {
        materialCard.setStrokeColor(ColorUtils.setAlphaComponent(materialCard.getStrokeColor(), intensity));
        innerlayout.setBackgroundColor(ColorUtils.setAlphaComponent(materialCard.getStrokeColor(), intensity));
    }

    public void setColor(int setAlphaComponent) {
        coloredButtonBox.setBackgroundColor((setAlphaComponent));
    }

    public CardType getCardType() {
        return cardType;
    }
}
