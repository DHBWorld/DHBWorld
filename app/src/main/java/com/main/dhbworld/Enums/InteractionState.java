package com.main.dhbworld.Enums;
// Zustände von dem Drücker, Kaffemaschine und Kantine

import android.graphics.Color;

import com.main.dhbworld.R;

public enum InteractionState {
    NORMAL("Funktioniert", R.color.black),
    DEFECT("Defekt", R.color.grey_light),
    CLEANING("wird sauber gemacht", R.color.grey_light),
    QUEUE_LONG("lange Schlange", R.color.red_dark),
    QUEUE_MIDDLE("mittlere Schlange", R.color.red),
    QUEUE_KURZ("kurze Schlange", R.color.teal_700),
    QUEUE_ABCENT("keine Schlange", R.color.black);





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
