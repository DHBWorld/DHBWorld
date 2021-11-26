package com.main.dhbworld.Enums;
// Zustände von dem Drücker, Kaffemaschine und Kantine

import android.graphics.Color;

import com.main.dhbworld.R;

public enum InteractionState {
    NORMAL("Funktioniert", R.color.grey_dark),
    DEFECT("Defekt", R.color.grey_defect),
    CLEANING("wird sauber gemacht", R.color.blue_cleaning),

    QUEUE_LONG("lange Schlange", R.color.red_dark),
    QUEUE_MIDDLE("mittlere Schlange", R.color.orange_queue),
    QUEUE_KURZ("kurze Schlange", R.color.green_queue),
    QUEUE_ABCENT("keine Schlange", R.color.grey_dark);





    private int color;
    private String text;

    InteractionState(String text, int color){
        this.color= color;
        this.text=text;

    }

    public int getColor() {
        return color;
    }

    public String getText() {
        return text;
    }
}
