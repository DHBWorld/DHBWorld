package com.main.dhbworld.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.ColorUtils;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.Dashboard.Cards.DashboardCard;
import com.main.dhbworld.DashboardActivity;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.List;

public class Dashboard {
    private SharedPreferences sp;
    public static final String dashboardSettings = "dashboardSettings";
    private final List<DashboardCard> cards;
    private Boolean configurationModus;



    public Dashboard() {
        cards = new ArrayList<>();
        configurationModus = false;


    }

    public void addCard(DashboardCard card) {
        this.cards.add(card);
    }

    public void setConfigurationModus(boolean b) {
        this.configurationModus = b;
        for (DashboardCard card : cards) {
            card.setConfigurationModus(b);
        }
    }

    public Boolean getConfigurationModus() {
        return configurationModus;
    }

    public void setColorIntensity(int intensity) {
        for (DashboardCard card : cards) {
            card.coloreVisibleCard(intensity);
        }
    }


    public void changeCardVisibility(Context context) {
        sp = context.getSharedPreferences(dashboardSettings, Context.MODE_PRIVATE);

        for (DashboardCard card : cards) {
            card.setCardVisibility(sp.getBoolean(card.getCardType().getSavedIn(), true));
        }
    }

    public void configurateClickers(DashboardActivity dashboardActivity) {
        for (DashboardCard card : cards) {
            card.configurateClickers(dashboardActivity, card.getCardType().getLink());
        }
    }

    public void showAll() {
        for (DashboardCard card : cards) {
            card.makeCardVisible();
            card.coloreVisibleCard(50);
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
            editor.putBoolean(card.getCardType().getSavedIn(), card.cardIsVisible());
        }

        editor.apply();
    }

    public void configurationIsOn() {
        showAll();
        setConfigurationModus(true);
    }

    public void configurationIsOff(Context applicationContext) {
        setColorIntensity(255);
        syncVisibility();
        saveChanges(applicationContext);
        setConfigurationModus(false);

    }

    public void configurationClick(@NonNull MenuItem item, Context context, View snackbarContent, String message) throws NullPointerException {
        if (!configurationModus) { //User can configure his dashboard
            item.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_done));
            Snackbar.make(snackbarContent, message, BaseTransientBottomBar.LENGTH_SHORT).show();
            configurationIsOn();
        } else {
            item.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_construction));
            configurationIsOff(context);
        }
    }

    public void setUp(Context context, DashboardActivity dashboardActivity) {
       setConfigurationModus(false);
        setColorIntensity((ColorUtils.setAlphaComponent(context.getResources().getColor(R.color.black), 0)));
       changeCardVisibility(context);
        configurateClickers(dashboardActivity);
    }
}
