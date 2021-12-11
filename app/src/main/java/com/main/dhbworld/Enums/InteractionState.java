package com.main.dhbworld.Enums;
// Zustände von dem Drücker, Kaffemaschine und Kantine

import com.main.dhbworld.R;

public enum InteractionState {
    NORMAL(0, "Funktioniert", R.color.grey_dark),
    DEFECT(1, "Defekt", R.color.grey_defect),
    CLEANING(2, "wird sauber gemacht", R.color.blue_cleaning),

    QUEUE_LONG(6, "lange Schlange", R.color.red_dark),
    QUEUE_MIDDLE(5, "mittlere Schlange", R.color.orange_queue),
    QUEUE_SHORT(4, "kurze Schlange", R.color.green_queue),
    QUEUE_ABCENT(3, "keine Schlange", R.color.grey_dark);




    private int id;
    private int color;
    private String text;

    InteractionState(int id, String text, int color){
        this.id = id;
        this.color= color;
        this.text=text;
    }

    public int getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public static InteractionState parseId(int id) {
        switch (id) {
            case 0:
                return NORMAL;
            case 1:
                return DEFECT;
            case 2:
                return CLEANING;
            case 3:
                return QUEUE_ABCENT;
            case 4:
                return QUEUE_SHORT;
            case 5:
                return QUEUE_MIDDLE;
            case 6:
                return QUEUE_LONG;
            default:
                return null;
        }

    }
}
