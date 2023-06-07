package com.main.dhbworld.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;

import com.main.dhbworld.DashboardActivity;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {
    SharedPreferences sp;
    public static final String dashboardSettings = "dashboardSettings";
    private List<DashboardCard> cards;


    public Dashboard() {
        cards = new ArrayList<>();

    }

    public void addCard(DashboardCard card) {
        this.cards.add(card);
    }

    public void setConfigurationModus(boolean b) {
        for (DashboardCard card : cards) {
            card.setConfigurationModus(b);
        }
    }

    public void setColorIntensity(int intensity) {
        for (DashboardCard card : cards) {
            card.coloreCard(intensity);
        }
    }


    public void changeCardVisibility(Context context) {
        sp = context.getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);

        for (DashboardCard card : cards) {
            card.setCardVisibility(sp.getBoolean(card.cardType.getSavedIn(), true));
        }
    }

    public void configurateClickers(DashboardActivity dashboardActivity) {
        for (DashboardCard card : cards) {
            card.configurateClickers(dashboardActivity, card.cardType.getLink());
        }
    }

    public void showAll() {
        for (DashboardCard card : cards) {
            card.makeCardVisible();
        }
    }

    public void syncVisibility() {
        for (DashboardCard card : cards) {
            card.syncVisibility();
        }
    }

    public void saveChanges(Context context) {
        sp = context.getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        for (DashboardCard card : cards) {
            editor.putBoolean(card.cardType.getSavedIn(), card.cardIsVisible());
        }

        editor.apply();
    }
}
